/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.BrandController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Production.Brands;
/**
 *
 * @author duyng
 */
public class BrandManagementView extends JInternalFrame {
    private final BrandController controller;
    private JTable brandTable;
    private DefaultTableModel tableModel;
    private JTextField txtBrandName;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedBrandId = -1;
    
    public BrandManagementView() {
        super("Brand Management", true, true, true, true);
        controller = new BrandController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadBrands();
        setSize(800, 500);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Brand Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        brandTable = new JTable(tableModel);
        brandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Input fields
        txtBrandName = new JTextField(20);
        
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
        JScrollPane scrollPane = new JScrollPane(brandTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Brand Name:"));
        formPanel.add(txtBrandName);
        
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
        brandTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = brandTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedBrand(selectedRow);
                }
            }
        });
        
        // Button listeners
        btnAdd.addActionListener(e -> addBrand());
        btnUpdate.addActionListener(e -> updateBrand());
        btnDelete.addActionListener(e -> deleteBrand());
        btnRefresh.addActionListener(e -> loadBrands());
        btnClear.addActionListener(e -> clearForm());
    }
    
    // Methods called by controller
    public void displayBrands(ArrayList<Brands> brands) {
        tableModel.setRowCount(0);
        for (Brands brand : brands) {
            Object[] row = {
                brand.getBrandID(),
                brand.getBrandName()
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
    private void loadBrands() {
        controller.loadBrands();
    }
    
    private void addBrand() {
        if (validateInput()) {
            Brands brand = createBrandFromForm();
            controller.addBrand(brand);
        }
    }
    
    private void updateBrand() {
        if (selectedBrandId == -1) {
            showError("Please select a brand to update");
            return;
        }
        if (validateInput()) {
            Brands brand = createBrandFromForm();
            brand.setBrandID(selectedBrandId);
            controller.updateBrand(brand);
        }
    }
    
    private void deleteBrand() {
        if (selectedBrandId == -1) {
            showError("Please select a brand to delete");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this brand?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteBrand(selectedBrandId);
        }
    }
    
    private void loadSelectedBrand(int row) {
        selectedBrandId = (int) tableModel.getValueAt(row, 0);
        txtBrandName.setText((String) tableModel.getValueAt(row, 1));
    }
    
    private void clearForm() {
        selectedBrandId = -1;
        txtBrandName.setText("");
        brandTable.clearSelection();
    }
    
    private boolean validateInput() {
        if (txtBrandName.getText().trim().isEmpty()) {
            showError("Brand name is required");
            return false;
        }
        return true;
    }
    
    private Brands createBrandFromForm() {
        return new Brands(
            0, // ID will be auto-generated
            txtBrandName.getText().trim()
        );
    }
}
