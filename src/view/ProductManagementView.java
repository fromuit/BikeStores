/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.ProductController;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Production.Products;
import java.util.ArrayList;
import model.Administration.User;
import utils.SessionManager;
import dao.CategoriesDAO;
import java.lang.reflect.Array;
import model.Production.Categories;

/**
 *
 * @author duyng
 */
public class ProductManagementView extends JInternalFrame {
    private final ProductController controller;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtProductName, txtBrandID, txtCategoryID, txtModelYear, txtListPrice;
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private JComboBox<String> cmbCategoryFilter;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedProductId = -1;

    private final SessionManager sessionManager;

    public ProductManagementView() {
        super("Product Management", true, true, true, true);
        this.sessionManager = SessionManager.getInstance();
        controller = new ProductController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadProducts();
        setSize(1200, 600);
        applyRoleBasedPermissions();
    }

    private void initializeComponents() {
        // Table setup
        String[] columnNames = { "ID", "Name", "Brand ID", "Category ID", "Model Year", "List Price", "Category" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Input fields
        txtProductName = new JTextField(15);
        txtBrandID = new JTextField(10);
        txtCategoryID = new JTextField(10);
        txtModelYear = new JTextField(5);
        txtListPrice = new JTextField(10);
        txtSearch = new JTextField(20);

        // Buttons
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnClear = new JButton("Clear");
        btnSearch = new JButton("Search");
        btnClearSearch = new JButton("Clear Search");

        cmbCategoryFilter = new JComboBox<>();
        populateCategoryFilter();
    }

    private void populateCategoryFilter() {
        cmbCategoryFilter.removeAllItems();
        cmbCategoryFilter.addItem("All Categories");
        // TODO: Populate with actual categories from your data source
        // Example using a hypothetical CategoryService and Category class:
        // CategoryService categoryService = new CategoryService();
        // ArrayList<Category> categories = categoryService.getAllCategories();
        // for (Category category : categories) {
        // cmbCategoryFilter.addItem(category.getCategoryName()); // Or better, store
        // Category objects and use a custom renderer
        // }
        // For now, adding some dummy categories for UI demonstration
        CategoriesDAO category = new CategoriesDAO();
        ArrayList<Categories> categories = new ArrayList<>();
        categories=category.getAllCategories();
        for (Categories ctgr : categories){
        cmbCategoryFilter.addItem(ctgr.getCategoryName());
        }

    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by Name:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Filter by Category:"));
        searchPanel.add(cmbCategoryFilter);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Combine search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtProductName, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Brand ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtBrandID, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Category ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCategoryID, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Model Year:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtModelYear, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("List Price:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtListPrice, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);

        // Combine form and button panels
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        // Table selection listener
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedProduct(selectedRow);
                }
            }
        });

        // Button listeners
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnRefresh.addActionListener(e -> loadProducts());
        btnClear.addActionListener(e -> clearForm());

        btnSearch.addActionListener(e -> performSearch());
        btnClearSearch.addActionListener(e -> clearSearch());
        txtSearch.addActionListener(e -> performSearch()); // Search on Enter in text field

        cmbCategoryFilter.addActionListener(e -> filterByCategory());
    }

    // Methods called by controller
    public void displayProducts(ArrayList<Products> products) {
        tableModel.setRowCount(0);
        if (products != null) {
            for (Products product : products) {
                Object[] row = {
                        product.getProductID(),
                        product.getProductName(),
                        product.getBrandID(),
                        product.getCategoryID(),
                        product.getModelYear(),
                        product.getListPrice(),
                        product.getCategory()
                };
                tableModel.addRow(row);
            }
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Private helper methods
    private void loadProducts() {
        controller.loadProducts();
    }

    private void addProduct() {
        if (validateInput()) {
            Products product = createProductFromForm();
            controller.addProduct(product);
        }
    }

    private void updateProduct() {
        if (selectedProductId == -1) {
            showError("Please select a product to update");
            return;
        }
        if (validateInput()) {
            Products product = createProductFromForm();
            product.setProductID(selectedProductId);
            controller.updateProduct(product);
        }
    }

    private void deleteProduct() {
        if (selectedProductId == -1) {
            showError("Please select a product to delete");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteProduct(selectedProductId);
        }
    }

    private void loadSelectedProduct(int row) {
        selectedProductId = (int) tableModel.getValueAt(row, 0);
        txtProductName.setText((String) tableModel.getValueAt(row, 1));
        txtBrandID.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtCategoryID.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtModelYear.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtListPrice.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        selectedProductId = -1;
        txtProductName.setText("");
        txtBrandID.setText("");
        txtCategoryID.setText("");
        txtModelYear.setText("");
        txtListPrice.setText("");
        productTable.clearSelection();
    }

    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        if (!searchTerm.isEmpty()) {
            controller.searchProducts(searchTerm);
        } else {
            loadProducts(); // If search term is empty, load all products
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbCategoryFilter.setSelectedIndex(0); // Reset category filter as well
        loadProducts();
    }

    private void filterByCategory() {
        int selectedIndex = cmbCategoryFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            loadProducts(); // "All Categories" selected
        } else {
            // This is a placeholder. In a real app, you would get the Category ID.
            // For now, we'll just show a message and reload all products.
            // String categoryName = (String) cmbCategoryFilter.getSelectedItem();
            // int categoryId = getCategoryIdFromName(categoryName); // This method would
            // need to be implemented
            // if (categoryId != -1) { // -1 if not found
            // controller.loadProductsByCategory(categoryId);
            // }
            showMessage(
                    "Filtering by category (using actual Category IDs) is not yet fully implemented in the view. Displaying all products.");
            loadProducts();
        }
    }

    private void applyRoleBasedPermissions() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            // Handle case where user is not logged in, perhaps disable all controls
            showError("No user logged in. Access denied.");
            setFormFieldsEnabled(false);
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            return;
        }

        switch (currentUser.getRole()) { // Assuming getRole() returns an enum or a comparable type
            case EMPLOYEE: // Or whatever your role definition is
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                setFormFieldsEnabled(false);
                break;
            case STORE_MANAGER:
                // Example: Store managers can only view products or manage a limited set.
                // For full mirroring of Staff, they might have more permissions.
                // This depends on your specific business logic for product management by store
                // managers.
                // For now, let's assume they can add/update but not delete, and only for their
                // store's category perhaps.
                btnAdd.setEnabled(true); // Or false, depending on requirements
                btnUpdate.setEnabled(true); // Or false
                btnDelete.setEnabled(false);
                setFormFieldsEnabled(true); // Or restrict some fields
                // txtCategoryID.setEnabled(false); // Example: if manager is tied to a category
                break;
            case CHIEF_MANAGER:
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                setFormFieldsEnabled(true);
                break;
            default:
                // Unknown role, disable all
                setFormFieldsEnabled(false);
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                break;
        }
    }

    private void setFormFieldsEnabled(boolean enabled) {
        txtProductName.setEnabled(enabled);
        txtBrandID.setEnabled(enabled);
        txtCategoryID.setEnabled(enabled);
        txtModelYear.setEnabled(enabled);
        txtListPrice.setEnabled(enabled);
    }

    private boolean validateInput() {
        if (txtProductName.getText().trim().isEmpty()) {
            showError("Product name is required");
            return false;
        }

        try {
            Integer.parseInt(txtBrandID.getText().trim());
        } catch (NumberFormatException e) {
            showError("Brand ID must be a valid number");
            return false;
        }

        try {
            Integer.parseInt(txtCategoryID.getText().trim());
        } catch (NumberFormatException e) {
            showError("Category ID must be a valid number");
            return false;
        }

        try {
            Integer.parseInt(txtModelYear.getText().trim());
        } catch (NumberFormatException e) {
            showError("Model Year must be a valid number");
            return false;
        }

        try {
            Double.parseDouble(txtListPrice.getText().trim());
        } catch (NumberFormatException e) {
            showError("List Price must be a valid number");
            return false;
        }

        if (Double.parseDouble(txtListPrice.getText().trim()) < 0) {
            showError("List Price cannot be negative.");
            return false;
        }

        return true;
    }

    private Products createProductFromForm() {
        return new Products(
                0, // ID will be auto-generated or handled by DB
                txtProductName.getText().trim(),
                Integer.parseInt(txtBrandID.getText().trim()),
                Integer.parseInt(txtCategoryID.getText().trim()),
                Integer.parseInt(txtModelYear.getText().trim()),
                Double.parseDouble(txtListPrice.getText().trim()));
    }
}