/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.CategoryController;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Production.Categories;
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
        super("Quản lý danh mục", true, true, true, true);
        controller = new CategoryController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCategories();
        setSize(800, 500);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID Danh mục", "Tên danh mục"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        categoryTable.setRowSorter(sorter);

        txtCategoryName = new JTextField(20);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnRefresh = new JButton("Làm mới");
        btnClear = new JButton("Xoá trường");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);
 
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Tên danh mục:"));
        formPanel.add(txtCategoryName);

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
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = categoryTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedCategory(selectedRow);
                }
            }
        });

        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
        btnRefresh.addActionListener(e -> loadCategories());
        btnClear.addActionListener(e -> clearForm());
    }

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
            showError("Hãy chọn một danh mục để cập nhật");
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
            showError("Hãy chọn một danh mục để xoá");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xoá danh mục này?", 
            "Xác nhận xoá", 
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteCategory(selectedCategoryId);
        }
    }
    
    private void loadSelectedCategory(int row) {
        int modelRow = categoryTable.convertRowIndexToModel(row);
        selectedCategoryId = (int) tableModel.getValueAt(modelRow, 0);
        txtCategoryName.setText((String) tableModel.getValueAt(modelRow, 1));
    }
    
    private void clearForm() {
        selectedCategoryId = -1;
        txtCategoryName.setText("");
        categoryTable.clearSelection();
    }
    
    private boolean validateInput() {
        if (txtCategoryName.getText().trim().isEmpty()) {
            showError("Phải có tên danh mục!");
            return false;
        }
        return true;
    }
    
    private Categories createCategoryFromForm() {
        return new Categories(
            0,
            txtCategoryName.getText().trim()
        );
    }
}