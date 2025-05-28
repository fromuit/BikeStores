/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.CustomerController;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Administration.User;
import model.Sales.Customers;
import utils.SessionManager;

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
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private JComboBox<String> cmbStateFilter, cmbCityFilter;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedCustomerId = -1;

    private final SessionManager sessionManager;

    public CustomerManagementView() {
        super("Quản lý khách hàng", true, true, true, true);
        this.sessionManager = SessionManager.getInstance();
        controller = new CustomerController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCustomers();
        setSize(1400, 700);

        applyRoleBasedPermissions();
    }

    private void initializeComponents() {
        String[] columnNames = { "ID", "Tên", "Họ", "Email", "SĐT", "Đường", "TP", "Bang",
                "Mã ZIP" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(8, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        customerTable.setRowSorter(sorter);

        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtPhone = new JTextField(15);
        txtEmail = new JTextField(15);
        txtStreet = new JTextField(15);
        txtCity = new JTextField(15);
        txtState = new JTextField(15);
        txtZipCode = new JTextField(15);
        txtSearch = new JTextField(20);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnRefresh = new JButton("Làm mới");
        btnClear = new JButton("Xoá trường");
        btnSearch = new JButton("Tìm");
        btnClearSearch = new JButton("Xoá");

        cmbStateFilter = new JComboBox<>();
        cmbCityFilter = new JComboBox<>();
        populateFilters();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Lọc theo Bang:"));
        searchPanel.add(cmbStateFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Lọc theo Thành phố:"));
        searchPanel.add(cmbCityFilter);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(customerTable);

        // Combine search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Personal Information Section Header
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel personalInfoLabel = new JLabel("Thông tin cá nhân");
        personalInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        personalInfoLabel.setForeground(new Color(0, 102, 204));
        formPanel.add(personalInfoLabel, gbc);

        // Address Information Section Header
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel addressInfoLabel = new JLabel("Địa chỉ");
        addressInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressInfoLabel.setForeground(new Color(0, 102, 204));
        formPanel.add(addressInfoLabel, gbc);

        // Reset gridwidth for form fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Personal Information Fields
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFirstName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Họ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtLastName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        // Address Information Fields
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Đường:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtStreet, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Thành phố:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtCity, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Bang:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtState, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Mã ZIP:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtZipCode, gbc);

        // Add some spacing between sections
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 5;
        formPanel.add(Box.createHorizontalStrut(30), gbc);

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

        // Search listeners
        btnSearch.addActionListener(e -> performSearch());
        btnClearSearch.addActionListener(e -> clearSearch());
        txtSearch.addActionListener(e -> performSearch()); // Search on Enter

        // Filter listeners
        cmbStateFilter.addActionListener(e -> filterByState());
        cmbCityFilter.addActionListener(e -> filterByCity());

        // Button listeners
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnRefresh.addActionListener(e -> loadCustomers());
        btnClear.addActionListener(e -> clearForm());

        txtFirstName.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
            }
        });
    }

    private void applyRoleBasedPermissions() {
        User currentUser = sessionManager.getCurrentUser();

        switch (currentUser.getRole()) {
            case EMPLOYEE -> {
                // Employees can only view customers
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(false);
                setFormFieldsEnabled(true);
            }
            case STORE_MANAGER, CHIEF_MANAGER -> {
                // Managers can manage customers
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                setFormFieldsEnabled(true);
            }
        }
    }

    private void setFormFieldsEnabled(boolean enabled) {
        txtFirstName.setEnabled(enabled);
        txtLastName.setEnabled(enabled);
        txtPhone.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        txtStreet.setEnabled(enabled);
        txtCity.setEnabled(enabled);
        txtState.setEnabled(enabled);
        txtZipCode.setEnabled(enabled);
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

    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        if (!searchTerm.isEmpty()) {
            controller.searchCustomers(searchTerm);
        } else {
            loadCustomers();
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbStateFilter.setSelectedIndex(0);
        cmbCityFilter.setSelectedIndex(0);
        loadCustomers();
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
        int modelRow = customerTable.convertRowIndexToModel(row);
        selectedCustomerId = (int) tableModel.getValueAt(modelRow, 0);
        txtFirstName.setText((String) tableModel.getValueAt(modelRow, 1));
        txtLastName.setText((String) tableModel.getValueAt(modelRow, 2));
        txtEmail.setText((String) tableModel.getValueAt(modelRow, 3));
        txtPhone.setText((String) tableModel.getValueAt(modelRow, 4));
        txtStreet.setText((String) tableModel.getValueAt(modelRow, 5));
        txtCity.setText((String) tableModel.getValueAt(modelRow, 6));
        txtState.setText((String) tableModel.getValueAt(modelRow, 7));
        txtZipCode.setText((String) tableModel.getValueAt(modelRow, 8));
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
            showError("Phải cung cấp tên");
            txtFirstName.requestFocus();
            return false;
        }
        if (txtLastName.getText().trim().isEmpty()) {
            showError("Phải cung cấp họ");
            txtLastName.requestFocus();
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            showError("Phải có email khách hàng");
            txtEmail.requestFocus();
            return false;
        }

        // Basic email validation
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Địa chỉ email không hợp lệ!");
            txtEmail.requestFocus();
            return false;
        }

        // Phone validation (if provided)
        if (!txtPhone.getText().trim().isEmpty()) {
            String phone = txtPhone.getText().replaceAll("[^\\d]", "");
            if (phone.length() < 10) {
                showError("SĐT không hợp lệ! (ít nhất 10 chữ số)");
                txtPhone.requestFocus();
                return false;
            }
        }

        // State validation (if provided)
        if (!txtState.getText().trim().isEmpty() && txtState.getText().trim().length() != 2) {
            showError("Mã bang không hợp lệ! (Mã 2 kí tự như CA, NY, TX)");
            txtState.requestFocus();
            return false;
        }

        // Zip code validation (if provided)
        if (!txtZipCode.getText().trim().isEmpty()) {
            if (!txtZipCode.getText().matches("^\\d{5}(-\\d{4})?$")) {
                showError("Mã ZIP không hợp lệ! (12345 hay 12345-6789)");
                txtZipCode.requestFocus();
                return false;
            }
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
                txtState.getText().trim().toUpperCase(),
                txtZipCode.getText().trim());
    }

    private void populateFilters() {
        populateStateFilter();
        populateCityFilter();
    }

    private void populateStateFilter() {
        cmbStateFilter.removeAllItems();
        cmbStateFilter.addItem("Tất cả");

        // Get actual states from database
        try {
            ArrayList<String> states = controller.getDistinctStates();
            for (String state : states) {
                cmbStateFilter.addItem(state);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu mã Bang: " + e.getMessage());
            // Fallback to hardcoded states if database fails
            String[] fallbackStates = { "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL",
                    "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH",
                    "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT",
                    "VA", "WA", "WV", "WI", "WY" };
            for (String state : fallbackStates) {
                cmbStateFilter.addItem(state);
            }
        }
    }

    private void populateCityFilter() {
        cmbCityFilter.removeAllItems();
        cmbCityFilter.addItem("Tất cả");

        // Get actual cities from database
        try {
            ArrayList<String> cities = controller.getDistinctCities();
            for (String city : cities) {
                cmbCityFilter.addItem(city);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu thành phố: " + e.getMessage());
        }
    }

    private void filterByState() {
        int selectedIndex = cmbStateFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            loadCustomers();
            populateCityFilter();
        } else {
            String state = (String) cmbStateFilter.getSelectedItem();
            controller.loadCustomersByState(state);
            populateCitiesForState(state);
        }
    }

    private void filterByCity() {
        int selectedIndex = cmbCityFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            int stateIndex = cmbStateFilter.getSelectedIndex();
            if (stateIndex == 0) {
                loadCustomers(); 
            } else {
                String state = (String) cmbStateFilter.getSelectedItem();
                controller.loadCustomersByState(state); // Show all customers in selected state
            }
        } else {
            String city = (String) cmbCityFilter.getSelectedItem();
            controller.loadCustomersByCity(city);
        }
    }

    private void populateCitiesForState(String state) {
        cmbCityFilter.removeAllItems();
        cmbCityFilter.addItem("Tất cả");

        try {
            ArrayList<String> cities = controller.getCitiesForState(state);
            for (String city : cities) {
                cmbCityFilter.addItem(city);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu thành phố cho bang: " + e.getMessage());
        }
    }

    public void refreshFilters() {
        populateFilters();
    }

}