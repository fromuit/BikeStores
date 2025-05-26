/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import controller.ProductController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Production.Products;


/**
 *
 * @author duyng
 */
public class ProductManagementView extends JInternalFrame {
    private final ProductController controller;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtProductName, txtBrandID, txtCategoryID, txtModelYear, txtListPrice;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedProductId = -1;
    
    public ProductManagementView() {
        super("Product Management", true, true, true, true);
        controller = new ProductController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadProducts();
        setSize(1000, 600);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Product Name", "Brand ID", "Category ID", "Model Year", "List Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Input fields
        txtProductName = new JTextField(20);
        txtBrandID = new JTextField(10);
        txtCategoryID = new JTextField(10);
        txtModelYear = new JTextField(10);
        txtListPrice = new JTextField(10);
        
        // Buttons
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnClear = new JButton("Clear");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtProductName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Brand ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtBrandID, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Category ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCategoryID, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Model Year:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtModelYear, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
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
    }
    
    // Methods called by controller
    public void displayProducts(List<Products> products) {
        tableModel.setRowCount(0);
        for (Products product : products) {
            Object[] row = {
                product.getProductID(),
                product.getProductName(),
                product.getBrandID(),
                product.getCategoryID(),
                product.getModelYear(),
                String.format("$%.2f", product.getListPrice())
            };
            tableModel.addRow(row);
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
        
        // Remove $ and format from price
        String priceStr = (String) tableModel.getValueAt(row, 5);
        String cleanPrice = priceStr.replace("$", "");
        txtListPrice.setText(cleanPrice);
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
    
    private boolean validateInput() {
        if (txtProductName.getText().trim().isEmpty()) {
            showError("Product name is required");
            return false;
        }
        
        try {
            Integer.valueOf(txtBrandID.getText().trim());
        } catch (NumberFormatException e) {
            showError("Brand ID must be a valid number");
            return false;
        }
        
        try {
            Integer.valueOf(txtCategoryID.getText().trim());
        } catch (NumberFormatException e) {
            showError("Category ID must be a valid number");
            return false;
        }
        
        try {
            int year = Integer.parseInt(txtModelYear.getText().trim());
            if (year < 1900 || year > 2030) {
                showError("Model year must be between 1900 and 2030");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Model year must be a valid number");
            return false;
        }
        
        try {
            double price = Double.parseDouble(txtListPrice.getText().trim());
            if (price < 0) {
                showError("List price cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("List price must be a valid number");
            return false;
        }
        
        return true;
    }
    
    private Products createProductFromForm() {
        return new Products(
            0, // ID will be auto-generated
            txtProductName.getText().trim(),
            Integer.parseInt(txtBrandID.getText().trim()),
            Integer.parseInt(txtCategoryID.getText().trim()),
            Integer.parseInt(txtModelYear.getText().trim()),
            Double.parseDouble(txtListPrice.getText().trim())
        );
    }
}