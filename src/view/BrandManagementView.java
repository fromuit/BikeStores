/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.BrandController;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
        super("Quản lý nhãn hàng", true, true, true, true);
        controller = new BrandController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadBrands();
        setSize(800, 500);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID Nhãn hàng", "Tên nhãn hàng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        brandTable = new JTable(tableModel);
        brandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        brandTable.setRowSorter(sorter);
        

        txtBrandName = new JTextField(20);
        

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnRefresh = new JButton("Làm mới");
        btnClear = new JButton("Xoá trường");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(brandTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Tên nhãn hàng:"));
        formPanel.add(txtBrandName);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(southPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        brandTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = brandTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedBrand(selectedRow);
                }
            }
        });
        
        btnAdd.addActionListener(e -> addBrand());
        btnUpdate.addActionListener(e -> updateBrand());
        btnDelete.addActionListener(e -> deleteBrand());
        btnRefresh.addActionListener(e -> loadBrands());
        btnClear.addActionListener(e -> clearForm());
    }
    
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
            showError("Hãy chọn một nhãn hàng để tiến hành cập nhật");
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
            showError("Hãy chọn một nhãn hàng để xoá");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xoá nhãn hàng này?", 
            "Xác nhận xoá", 
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteBrand(selectedBrandId);
        }
    }
    
    private void loadSelectedBrand(int row) {
        int modelRow = brandTable.convertRowIndexToModel(row);
        selectedBrandId = (int) tableModel.getValueAt(modelRow, 0);
        txtBrandName.setText((String) tableModel.getValueAt(modelRow, 1));
    }
    
    private void clearForm() {
        selectedBrandId = -1;
        txtBrandName.setText("");
        brandTable.clearSelection();
    }
    
    private boolean validateInput() {
        if (txtBrandName.getText().trim().isEmpty()) {
            showError("Phải có tên nhãn hàng!");
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
