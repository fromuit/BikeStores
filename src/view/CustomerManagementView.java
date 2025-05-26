/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import controller.CustomerController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Sales.Customers;
/**
 *
 * @author duyng
 */
public class CustomerManagementView extends JInternalFrame {
    private final CustomerController controller;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField txtFirstName, txtLastName, txtPhone, txtEmail;
    private JTextField txtStreet, txtCity, txtState, txtZipCode;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedCustomerId = -1;
    
    public CustomerManagementView() {
        super("Customer Management", true, true, true, true);
        controller = new CustomerController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCustomers();
        setSize(1000, 600);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "First Name", "Last Name", "Phone", "Email", "Street", "City", "State", "Zip Code"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Input fields
        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtPhone = new JTextField(15);
        txtEmail = new JTextField(15);
        txtStreet = new JTextField(15);
        txtCity = new JTextField(15);
        txtState = new JTextField(15);
        txtZipCode = new JTextField(15);
        
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
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFirstName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtLastName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Street:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtStreet, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("City:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtCity, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("State:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtState, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3;
        formPanel.add(new JLabel("Zip Code:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtZipCode, gbc);
        
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
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedCustomer(selectedRow);
                }
            }
        });
        
        // Button listeners
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnRefresh.addActionListener(e -> loadCustomers());
        btnClear.addActionListener(e -> clearForm());
    }
    
    // Methods called by controller
    public void displayCustomers(ArrayList<Customers> customers) {
        tableModel.setRowCount(0);
        for (Customers customer : customers) {
            Object[] row = {
                customer.getPersonID(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStreet(),
                customer.getCity(),
                customer.getState(),
                customer.getZipCode()
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
    private void loadCustomers() {
        controller.loadCustomers();
    }
    
    private void addCustomer() {
        if (validateInput()) {
            Customers customer = createCustomerFromForm();
            controller.addCustomer(customer);
        }
    }
    
    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            showError("Please select a customer to update");
            return;
        }
        if (validateInput()) {
            Customers customer = createCustomerFromForm();
            customer.setPersonID(selectedCustomerId);
            controller.updateCustomer(customer);
        }
    }
    
    private void deleteCustomer() {
        if (selectedCustomerId == -1) {
            showError("Please select a customer to delete");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this customer?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteCustomer(selectedCustomerId);
        }
    }
    
    private void loadSelectedCustomer(int row) {
        selectedCustomerId = (int) tableModel.getValueAt(row, 0);
        txtFirstName.setText((String) tableModel.getValueAt(row, 1));
        txtLastName.setText((String) tableModel.getValueAt(row, 2));
        txtEmail.setText((String) tableModel.getValueAt(row, 3));
        txtPhone.setText((String) tableModel.getValueAt(row, 4));
        txtStreet.setText((String) tableModel.getValueAt(row, 5));
        txtCity.setText((String) tableModel.getValueAt(row, 6));
        txtState.setText((String) tableModel.getValueAt(row, 7));
        txtZipCode.setText((String) tableModel.getValueAt(row, 8));
    }
    
    private void clearForm() {
        selectedCustomerId = -1;
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtStreet.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtZipCode.setText("");
        customerTable.clearSelection();
    }
    
    private boolean validateInput() {
        if (txtFirstName.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }
        if (txtLastName.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }
        return true;
    }
    
    private Customers createCustomerFromForm() {
        return new Customers(
            0, // ID will be auto-generated
            txtFirstName.getText().trim(),
            txtLastName.getText().trim(),
            txtEmail.getText().trim(),
            txtPhone.getText().trim(),
            txtStreet.getText().trim(),
            txtCity.getText().trim(),
            txtState.getText().trim(),
            txtZipCode.getText().trim()
        );
    }
}