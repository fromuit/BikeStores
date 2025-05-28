/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.ProductController;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Administration.User;
import model.Production.Products;
import utils.SessionManager;
import dao.BrandsDAO;
import dao.CategoriesDAO;
import model.Production.Categories;
import model.Production.Brands;

/**
 *
 * @author duyng
 */
public class ProductManagementView extends JInternalFrame {
    // Helper class to store Category ID and Name for ComboBox

    // Helper class to store Brand ID and Name for ComboBox

    private final ProductController controller;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtProductName, txtBrandID, txtCategoryID, txtModelYear, txtListPrice;
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private JComboBox<Categories> cmbCategoryFilter;
    private JComboBox<Brands> cmbBrandFilter;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedProductId = -1;

    private final SessionManager sessionManager;

    public ProductManagementView() {
        super("Quản lý sản phẩm", true, true, true, true);
        this.sessionManager = SessionManager.getInstance();
        this.controller = new ProductController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadProducts();
        setSize(1200, 600);
        applyRoleBasedPermissions();
    }

    private void initializeComponents() {
        // Table setup
        String[] columnNames = { "Mã SP", "Tên sản phẩm", "Mã nhãn hàng", "Mã danh mục", "Năm sản xuất", "Giá niêm yết", "Danh mục",
                "Nhãn hàng" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // productTable.setAutoCreateRowSorter(true);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(2, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(3, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(4, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(5, Comparator.comparingDouble(o -> Double.valueOf(o.toString()))); 
        productTable.setRowSorter(sorter);

        // Input fields
        txtProductName = new JTextField(15);
        txtBrandID = new JTextField(10);
        txtCategoryID = new JTextField(10);
        txtModelYear = new JTextField(5);
        txtListPrice = new JTextField(10);
        txtSearch = new JTextField(20);

        // Buttons
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnRefresh = new JButton("Làm mới");
        btnClear = new JButton("Xoá trường");
        
        btnSearch = new JButton("Tìm");
        btnClearSearch = new JButton("Xoá");

        cmbCategoryFilter = new JComboBox<>();
        populateCategoryFilter();

        cmbBrandFilter = new JComboBox<>();
        populateBrandFilter();
    }

    private void populateCategoryFilter() {
        cmbCategoryFilter.removeAllItems();
        CategoriesDAO category = new CategoriesDAO();
        ArrayList<Categories> categoriesList = category.getAllCategories();
        cmbCategoryFilter.addItem(new Categories(-1, "Tất cả"));
        int index = 1;
        for (Categories ctgr : categoriesList){
            cmbCategoryFilter.addItem(ctgr);
        }
        

    }

    private void populateBrandFilter() {
        cmbBrandFilter.removeAllItems();
        BrandsDAO brand = new BrandsDAO();
        ArrayList<Brands> brandsList = brand.getAllBrands();
        cmbBrandFilter.addItem(new Brands(-1, "Tất cả"));
        int index = 1;
        for (Brands br : brandsList){
            cmbBrandFilter.addItem(br);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm theo tên:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Lọc theo Danh mục:"));
        searchPanel.add(cmbCategoryFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Lọc theo Nhãn hàng:"));
        searchPanel.add(cmbBrandFilter);

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
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtProductName, gbc);

        gbc.gridx = 2;
        gbc.gridy = y;
        formPanel.add(new JLabel("Năm sản xuất:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtModelYear, gbc);
        y++;

        gbc.gridx = 0;
        gbc.gridy = y;
        formPanel.add(new JLabel("Mã nhãn hàng:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtBrandID, gbc);

        gbc.gridx = 2;
        gbc.gridy = y;
        formPanel.add(new JLabel("Giá niêm yết:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtListPrice, gbc);
        y++;

        gbc.gridx = 0;
        gbc.gridy = y;
        formPanel.add(new JLabel("Mã danh mục:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCategoryID, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
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
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                loadSelectedProduct(productTable.getSelectedRow());
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
        cmbBrandFilter.addActionListener(e -> filterByBrand());
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
                        product.getCategory(),
                        product.getBrand()
                };
                tableModel.addRow(row);
            }
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
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
            clearForm();
        }
    }

    private void updateProduct() {
        if (selectedProductId == -1) {
            showError("Chọn sản phẩm để cập nhật.");
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
            showError("Chọn sản phẩm để xoá.");
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn xoá sản phẩm này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            controller.deleteProduct(selectedProductId);
            clearForm();
        }
    }

    private void loadSelectedProduct(int selectedRow) {
        int modelRow = productTable.convertRowIndexToModel(selectedRow);    
        selectedProductId = (int) tableModel.getValueAt(modelRow, 0);
        txtProductName.setText((String) tableModel.getValueAt(modelRow, 1));
        txtBrandID.setText(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        txtCategoryID.setText(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        txtModelYear.setText(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        txtListPrice.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
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
        controller.searchProducts(searchTerm);
    }

    private void clearSearch() {
        txtSearch.setText("");
        if (cmbCategoryFilter.getItemCount() > 0) {
            cmbCategoryFilter.setSelectedIndex(0);
        }
        if (cmbBrandFilter.getItemCount() > 0) {
            cmbBrandFilter.setSelectedIndex(0);
        }
        loadProducts();
    }

    private void filterByCategory() {
        Object selectedItem = cmbCategoryFilter.getSelectedItem();
        if (selectedItem instanceof Categories) {
            Categories categoryItem = (Categories) selectedItem;
            int categoryId = categoryItem.getCategoryID();
            if (categoryId == -1) { // ID 0 for "All Categories"
                loadProducts();
            } else {
                controller.loadProductsByCategory(categoryId);
            }
        } else {
            if (cmbCategoryFilter.getItemCount() == 0) {
                showError("Không có danh sách danh mục.");
            }
            loadProducts();
        }
    }

    private void filterByBrand() {
        Object selectedItem = cmbBrandFilter.getSelectedItem();
        if (selectedItem instanceof Brands) {
            Brands brandItem = (Brands) selectedItem;
            int brandId = brandItem.getBrandID();
            if (brandId == 0) { // ID 0 for "All Brands"
                loadProducts();
            } else {
                controller.loadProductsByBrand(brandId);
            }
        } else {
            if (cmbBrandFilter.getItemCount() == 0) {
                showError("Không có danh sách nhãn hàng.");
            }
            loadProducts(); // Fallback to load all products
        }
    }

    private void applyRoleBasedPermissions() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            showError("Không có tài khoản đăng nhập. Truy cập bị từ chối.");
            setFormAndButtonsEnabled(false);
            return;
        }

        boolean canAdd = false;
        boolean canUpdate = false;
        boolean canDelete = false;
        boolean formEnabled = false;

        switch (currentUser.getRole()) {
            case EMPLOYEE:
                break;
            case STORE_MANAGER:
                canAdd = true;
                canUpdate = true;
                formEnabled = true;
                break;
            case CHIEF_MANAGER:
                canAdd = true;
                canUpdate = true;
                canDelete = true;
                formEnabled = true;
                break;
        }
        setFormAndButtonsEnabled(formEnabled, canAdd, canUpdate, canDelete);
    }

    private void setFormAndButtonsEnabled(boolean formEnabled, boolean canAdd, boolean canUpdate, boolean canDelete) {
        txtProductName.setEnabled(formEnabled);
        txtBrandID.setEnabled(formEnabled);
        txtCategoryID.setEnabled(formEnabled);
        txtModelYear.setEnabled(formEnabled);
        txtListPrice.setEnabled(formEnabled);

        btnAdd.setEnabled(canAdd);
        btnUpdate.setEnabled(canUpdate);
        btnDelete.setEnabled(canDelete);
    }

    private void setFormAndButtonsEnabled(boolean allDisabled) {
        setFormAndButtonsEnabled(allDisabled, allDisabled, allDisabled, allDisabled);
    }

    private boolean validateInput() {
        if (txtProductName.getText().trim().isEmpty()) {
            showError("Tên sản phẩm là bắt buộc.");
            return false;
        }
        try {
            Integer.valueOf(txtBrandID.getText().trim());
        } catch (NumberFormatException e) {
            showError("Mã nhãn hàng phải là số hợp lệ.");
            return false;
        }
        try {
            Integer.valueOf(txtCategoryID.getText().trim());
        } catch (NumberFormatException e) {
            showError("Mã danh mục phải là số hợp lệ.");
            return false;
        }
        try {
            int modelYear = Integer.parseInt(txtModelYear.getText().trim());
            if (modelYear < 1900 || modelYear > java.time.Year.now().getValue() + 5) {
                showError("Năm sản xuất không hợp lệ.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Năm sản xuất phải là số hợp lệ");
            return false;
        }
        try {
            double listPrice = Double.parseDouble(txtListPrice.getText().trim());
            if (listPrice < 0) {
                showError("Giá sản phẩm không được âm.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Giá sản phẩm phải là số hợp lệ.");
            return false;
        }
        return true;
    }

    private Products createProductFromForm() {
        return new Products(
                0,
                txtProductName.getText().trim(),
                Integer.parseInt(txtBrandID.getText().trim()),
                Integer.parseInt(txtCategoryID.getText().trim()),
                Integer.parseInt(txtModelYear.getText().trim()),
                Double.parseDouble(txtListPrice.getText().trim()));
    }
}