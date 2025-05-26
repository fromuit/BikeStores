/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.CategoryController;
import model.Production.Categories;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class CategoryManagementView extends JInternalFrame {
    private final CategoryController controller;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField txtCategoryName;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedCategoryId = -1;
    
    public CategoryManagementView() {
        super("Category Management", true, true, true, true);
        controller = new CategoryController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCategories();
        setSize(800, 500);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Category Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Input fields
        txtCategoryName = new JTextField(20);
        
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
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Category Name:"));
        formPanel.add(txtCategoryName);
        
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
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = categoryTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedCategory(selectedRow);
                }
            }
        });
        
        // Button listeners
        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
        btnRefresh.addActionListener(e -> loadCategories());
        btnClear.addActionListener(e -> clearForm());
    }
    
    // Methods called by controller
    public void displayCategories(ArrayList<Categories> categories) {
        tableModel.setRowCount(0);
        for (Categories category : categories) {
            Object[] row = {
                category.getCategoryID(),
                category.getCategoryName()
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
    private void loadCategories() {
        controller.loadCategories();
    }
    
    private void addCategory() {
        if (validateInput()) {
            Categories category = createCategoryFromForm();
            controller.addCategory(category);
        }
    }
    
    private void updateCategory() {
        if (selectedCategoryId == -1) {
            showError("Please select a category to update");
            return;
        }
        if (validateInput()) {
            Categories category = createCategoryFromForm();
            category.setCategoryID(selectedCategoryId);
            controller.updateCategory(category);
        }
    }
    
    private void deleteCategory() {
        if (selectedCategoryId == -1) {
            showError("Please select a category to delete");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this category?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteCategory(selectedCategoryId);
        }
    }
    
    private void loadSelectedCategory(int row) {
        selectedCategoryId = (int) tableModel.getValueAt(row, 0);
        txtCategoryName.setText((String) tableModel.getValueAt(row, 1));
    }
    
    private void clearForm() {
        selectedCategoryId = -1;
        txtCategoryName.setText("");
        categoryTable.clearSelection();
    }
    
    private boolean validateInput() {
        if (txtCategoryName.getText().trim().isEmpty()) {
            showError("Category name is required");
            return false;
        }
        return true;
    }
    
    private Categories createCategoryFromForm() {
        return new Categories(
            0, // ID will be auto-generated
            txtCategoryName.getText().trim()
        );
    }
}