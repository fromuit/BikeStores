/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.OrderController;
import dao.CustomersDAO;
import dao.StaffsDAO;
import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Administration.User;
import model.Sales.Customers;
import model.Sales.OrderItems;
import model.Sales.Orders;
import model.Sales.Staffs;
import model.Production.Products;
import utils.SessionManager;

/**
 *
 * @author duyng
 */
public class OrderManagementView extends JInternalFrame {
    private final OrderController controller;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField txtOrderDate, txtRequiredDate, txtShippedDate;
    private JComboBox<String> cmbCustomer, cmbStaff, cmbStore, cmbOrderStatus;
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private JComboBox<String> cmbStatusFilter, cmbStoreFilter;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedOrderId = -1;
    private JButton btnViewDetails;

    private final SessionManager sessionManager;
    private final CustomersDAO customerDAO;
    private final StaffsDAO staffDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OrderManagementView() {
        super("Order Management", true, true, true, true);
        this.sessionManager = SessionManager.getInstance();
        this.customerDAO = new CustomersDAO();
        this.staffDAO = new StaffsDAO();
        controller = new OrderController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadOrders();
        setSize(1400, 650); // Reduced height from 800 to 650

        applyRoleBasedPermissions();
    }

    private void initializeComponents() {
        // Table setup
        String[] columnNames = { "Order ID", "Customer", "Status", "Order Date", "Required Date", "Shipped Date",
                "Store ID", "Staff" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setAutoCreateRowSorter(true);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set preferred table size to show fewer rows
        orderTable.setPreferredScrollableViewportSize(new Dimension(1200, 200)); // Reduced height

        // Input fields
        txtOrderDate = new JTextField(15);
        txtRequiredDate = new JTextField(15);
        txtShippedDate = new JTextField(15);
        txtSearch = new JTextField(20);

        // Placeholder text to date fields
        txtOrderDate.setToolTipText("Format: yyyy-MM-dd HH:mm:ss (e.g., 2024-01-15 10:30:00)");
        txtRequiredDate.setToolTipText("Format: yyyy-MM-dd HH:mm:ss (e.g., 2024-01-15 10:30:00)");
        txtShippedDate.setToolTipText("Format: yyyy-MM-dd HH:mm:ss (e.g., 2024-01-15 10:30:00)");

        // Combo boxes
        cmbCustomer = new JComboBox<>();
        cmbStaff = new JComboBox<>();
        cmbStore = new JComboBox<>();
        cmbOrderStatus = new JComboBox<>(new String[] { "Pending", "Processing", "Rejected", "Completed" });

        // Filter combo boxes
        cmbStatusFilter = new JComboBox<>(
                new String[] { "All Statuses", "Pending", "Processing", "Rejected", "Completed" });
        cmbStoreFilter = new JComboBox<>();

        // Buttons
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnClear = new JButton("Clear");
        btnSearch = new JButton("Search");
        btnClearSearch = new JButton("Clear Search");
        btnViewDetails = new JButton("View Details");

        // Populate dropdowns
        populateDropdowns();

        // Set current date as default
        txtOrderDate.setText(dateFormat.format(new Date()));
        txtRequiredDate.setText(dateFormat.format(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))); // 7
                                                                                                                    // days
                                                                                                                    // from
                                                                                                                    // now
    }

    // Add helper method to set current date/time in fields
    private void setCurrentDateTime(JTextField dateField) {
        dateField.setText(dateFormat.format(new Date()));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Filter by Status:"));
        searchPanel.add(cmbStatusFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Filter by Store:"));
        searchPanel.add(cmbStoreFilter);

        // Table panel with limited height
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setPreferredSize(new Dimension(1200, 220)); // Set fixed height for table area

        // Combine search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Form panel with more compact layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5); // Reduced vertical spacing

        // Order Information Section Header
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel orderInfoLabel = new JLabel("Order Information");
        orderInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        orderInfoLabel.setForeground(new Color(0, 102, 204));
        formPanel.add(orderInfoLabel, gbc);

        // Assignment Information Section Header
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel assignmentInfoLabel = new JLabel("Assignment Information");
        assignmentInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        assignmentInfoLabel.setForeground(new Color(0, 102, 204));
        formPanel.add(assignmentInfoLabel, gbc);

        // Reset gridwidth for form fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Order Information Fields
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbCustomer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Order Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbOrderStatus, gbc);

        // Order Date with helper button
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Order Date:"), gbc);
        gbc.gridx = 1;
        JPanel orderDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        orderDatePanel.add(txtOrderDate);
        JButton btnSetOrderDateNow = new JButton("Now");
        btnSetOrderDateNow.setPreferredSize(new Dimension(50, txtOrderDate.getPreferredSize().height));
        btnSetOrderDateNow.addActionListener(e -> setCurrentDateTime(txtOrderDate));
        orderDatePanel.add(btnSetOrderDateNow);
        formPanel.add(orderDatePanel, gbc);

        // Required Date with helper button
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Required Date:"), gbc);
        gbc.gridx = 1;
        JPanel requiredDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        requiredDatePanel.add(txtRequiredDate);
        JButton btnSetRequiredDateNow = new JButton("Now");
        btnSetRequiredDateNow.setPreferredSize(new Dimension(50, txtRequiredDate.getPreferredSize().height));
        btnSetRequiredDateNow.addActionListener(e -> setCurrentDateTime(txtRequiredDate));
        requiredDatePanel.add(btnSetRequiredDateNow);
        formPanel.add(requiredDatePanel, gbc);

        // Shipped Date with helper button
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Shipped Date:"), gbc);
        gbc.gridx = 1;
        JPanel shippedDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        shippedDatePanel.add(txtShippedDate);
        JButton btnSetShippedDateNow = new JButton("Now");
        btnSetShippedDateNow.setPreferredSize(new Dimension(50, txtShippedDate.getPreferredSize().height));
        btnSetShippedDateNow.addActionListener(e -> setCurrentDateTime(txtShippedDate));
        shippedDatePanel.add(btnSetShippedDateNow);
        formPanel.add(shippedDatePanel, gbc);

        // Assignment Information Fields
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Store:"), gbc);
        gbc.gridx = 3;
        formPanel.add(cmbStore, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Staff:"), gbc);
        gbc.gridx = 3;
        formPanel.add(cmbStaff, gbc);

        // Add some spacing between sections
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 6;
        formPanel.add(Box.createHorizontalStrut(30), gbc);

        // Add date format hints - more compact
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel dateHintLabel = new JLabel("Date format: yyyy-MM-dd HH:mm:ss");
        dateHintLabel.setFont(new Font("Arial", Font.ITALIC, 11)); // Smaller font
        dateHintLabel.setForeground(Color.GRAY);
        formPanel.add(dateHintLabel, gbc);

        // Button panel with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Reduced vertical padding
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnViewDetails);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);

        // Combine form and button panels
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set preferred size for the south panel to ensure buttons are visible
        southPanel.setPreferredSize(new Dimension(1200, 220)); // Increased slightly for the new buttons

        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        // Table selection listener
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedOrder(selectedRow);
                }
            }
        });

        // Search listeners
        btnSearch.addActionListener(e -> performSearch());
        btnClearSearch.addActionListener(e -> clearSearch());
        txtSearch.addActionListener(e -> performSearch()); // Search on Enter

        // Filter listeners
        cmbStatusFilter.addActionListener(e -> filterByStatus());
        cmbStoreFilter.addActionListener(e -> filterByStore());

        // Button listeners
        btnAdd.addActionListener(e -> addOrder());
        btnUpdate.addActionListener(e -> updateOrder());
        btnDelete.addActionListener(e -> deleteOrder());
        btnViewDetails.addActionListener(e -> viewOrderDetails());
        btnRefresh.addActionListener(e -> loadOrders());
        btnClear.addActionListener(e -> clearForm());

        // Order status change listener
        cmbOrderStatus.addActionListener(e -> handleStatusChange());
    }

    private void applyRoleBasedPermissions() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) { // Added null check for currentUser
            showError("No user logged in. Access denied.");
            setFormFieldsEnabled(false); // Disable all form fields
            btnAdd.setEnabled(false); // Disable all buttons
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            btnViewDetails.setEnabled(false);
            return;
        }

        switch (currentUser.getRole()) {
            case EMPLOYEE:
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(false);
                btnViewDetails.setEnabled(true);
                setFormFieldsEnabled(true);
                preselectCurrentUserAsStaff();
                cmbStaff.setEnabled(false);
                break;
            case STORE_MANAGER:
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnViewDetails.setEnabled(true);
                setFormFieldsEnabled(true);
                limitStoreSelectionToUserStore();
                break;
            case CHIEF_MANAGER:
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnViewDetails.setEnabled(true);
                setFormFieldsEnabled(true);
                break;
            default: // Handle unexpected role or no role
                setFormFieldsEnabled(false);
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnViewDetails.setEnabled(false);
                break;
        }
    }

    private void setFormFieldsEnabled(boolean enabled) {
        cmbCustomer.setEnabled(enabled);
        cmbStaff.setEnabled(enabled);
        cmbStore.setEnabled(enabled);
        cmbOrderStatus.setEnabled(enabled);
        txtOrderDate.setEnabled(enabled);
        txtRequiredDate.setEnabled(enabled);
        txtShippedDate.setEnabled(enabled);
    }

    // Methods called by controller
    public void displayOrders(ArrayList<Orders> orders) {
        tableModel.setRowCount(0);
        for (Orders order : orders) {
            // Get customer name
            String customerName = getCustomerName(order.getCustID());

            // Get staff name
            String staffName = getStaffName(order.getStaffID());

            Object[] row = {
                    order.getOrderID(),
                    customerName,
                    controller.getOrderStatusName(order.getOrderStatus()),
                    order.getOrderDate(),
                    order.getRequiredDate(),
                    order.getShippedDate(),
                    order.getStoreID(),
                    staffName
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
    private void loadOrders() {
        controller.loadOrders();
    }

    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        if (!searchTerm.isEmpty()) {
            controller.searchOrders(searchTerm);
        } else {
            loadOrders();
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbStatusFilter.setSelectedIndex(0);
        cmbStoreFilter.setSelectedIndex(0);
        loadOrders();
    }

    private void filterByStatus() {
        int selectedIndex = cmbStatusFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            loadOrders();
        } else {
            controller.loadOrdersByStatus(selectedIndex);
        }
    }

    private void filterByStore() {
        int selectedIndex = cmbStoreFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            loadOrders();
        } else {
            // Extract store ID from the selected item
            String selectedItem = (String) cmbStoreFilter.getSelectedItem();
            if (selectedItem != null && selectedItem.contains("ID: ")) {
                try {
                    int storeId = Integer.parseInt(selectedItem.split("ID: ")[1].split(" ")[0]);
                    controller.loadOrdersByStore(storeId);
                } catch (NumberFormatException e) {
                    showError("Error parsing store ID");
                }
            }
        }
    }

    private void addOrder() {
        if (validateInput()) {
            Orders order = createOrderFromForm();
            if (order != null) {
                controller.addOrder(order);
            }
        }
    }

    private void updateOrder() {
        if (selectedOrderId == -1) {
            showError("Please select an order to update");
            return;
        }

        // Additional check for employees - they can only update their own orders
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == User.UserRole.EMPLOYEE) {
            // Get the selected order to check staff assignment
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0) {
                String staffName = (String) tableModel.getValueAt(selectedRow, 7);
                String currentUserName = getCurrentUserName();
                if (!staffName.contains(currentUserName)) {
                    showError("You can only update orders assigned to you");
                    return;
                }
            }
        }

        if (validateInput()) {
            Orders order = createOrderFromForm();
            if (order != null) {
                order.setOrderID(selectedOrderId);
                controller.updateOrder(order);
            }
        }
    }

    private void deleteOrder() {
        if (selectedOrderId == -1) {
            showError("Please select an order to delete");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this order?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteOrder(selectedOrderId);
        }
    }

    // Method to pre-select current user as staff for employees
    private void preselectCurrentUserAsStaff() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getStaffID() != null) {
            for (int i = 0; i < cmbStaff.getItemCount(); i++) {
                String item = cmbStaff.getItemAt(i);
                if (item.contains("ID: " + currentUser.getStaffID())) {
                    cmbStaff.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // Method to limit store selection for store managers
    private void limitStoreSelectionToUserStore() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == User.UserRole.STORE_MANAGER) {
            ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
            if (!accessibleStores.isEmpty()) {
                int userStoreId = accessibleStores.get(0);
                for (int i = 0; i < cmbStore.getItemCount(); i++) {
                    String item = cmbStore.getItemAt(i);
                    if (item.contains("ID: " + userStoreId)) {
                        cmbStore.setSelectedIndex(i);
                        cmbStore.setEnabled(false); // Disable store selection
                        break;
                    }
                }
            }
        }
    }

    // Helper method to get current user's name
    private String getCurrentUserName() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getStaffID() != null) {
            try {
                Staffs staff = staffDAO.getStaffById(currentUser.getStaffID());
                if (staff != null) {
                    return staff.getFirstName() + " " + staff.getLastName();
                }
            } catch (Exception e) {
                System.err.println("Error getting current user name: " + e.getMessage());
            }
        }
        return "";
    }

    private void loadSelectedOrder(int row) {
        selectedOrderId = (int) tableModel.getValueAt(row, 0);

        // Find and select customer
        String customerName = (String) tableModel.getValueAt(row, 1);
        for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
            if (cmbCustomer.getItemAt(i).contains(customerName)) {
                cmbCustomer.setSelectedIndex(i);
                break;
            }
        }

        // Set status
        String status = (String) tableModel.getValueAt(row, 2);
        cmbOrderStatus.setSelectedItem(status);

        // Set dates
        Object orderDateObj = tableModel.getValueAt(row, 3);
        if (orderDateObj instanceof Timestamp) {
            txtOrderDate.setText(dateFormat.format((Timestamp) orderDateObj));
        } else if (orderDateObj != null) {
            txtOrderDate.setText(orderDateObj.toString());
        }

        Object requiredDateObj = tableModel.getValueAt(row, 4);
        if (requiredDateObj instanceof Timestamp) {
            txtRequiredDate.setText(dateFormat.format((Timestamp) requiredDateObj));
        } else if (requiredDateObj != null) {
            txtRequiredDate.setText(requiredDateObj.toString());
        }

        Object shippedDateObj = tableModel.getValueAt(row, 5);
        if (shippedDateObj instanceof Timestamp) {
            txtShippedDate.setText(dateFormat.format((Timestamp) shippedDateObj));
        } else if (shippedDateObj != null) {
            txtShippedDate.setText(shippedDateObj.toString());
        } else {
            txtShippedDate.setText("");
        }

        // Set store
        int storeId = (int) tableModel.getValueAt(row, 6);
        for (int i = 0; i < cmbStore.getItemCount(); i++) {
            if (cmbStore.getItemAt(i).contains("ID: " + storeId)) {
                cmbStore.setSelectedIndex(i);
                break;
            }
        }

        // Find and select staff
        String staffName = (String) tableModel.getValueAt(row, 7);
        for (int i = 0; i < cmbStaff.getItemCount(); i++) {
            if (cmbStaff.getItemAt(i).contains(staffName)) {
                cmbStaff.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        selectedOrderId = -1;
        cmbCustomer.setSelectedIndex(0);
        cmbStaff.setSelectedIndex(0);
        cmbStore.setSelectedIndex(0);
        cmbOrderStatus.setSelectedIndex(0);
        txtOrderDate.setText(dateFormat.format(new Date()));
        txtRequiredDate.setText(dateFormat.format(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)));
        txtShippedDate.setText("");
        orderTable.clearSelection();
    }

    private boolean validateInput() {
        if (cmbCustomer.getSelectedIndex() == 0) {
            showError("Please select a customer");
            cmbCustomer.requestFocus();
            return false;
        }

        if (cmbStaff.getSelectedIndex() == 0) {
            showError("Please select a staff member");
            cmbStaff.requestFocus();
            return false;
        }

        if (cmbStore.getSelectedIndex() == 0) {
            showError("Please select a store");
            cmbStore.requestFocus();
            return false;
        }

        if (txtOrderDate.getText().trim().isEmpty()) {
            showError("Order date is required");
            txtOrderDate.requestFocus();
            return false;
        }

        if (txtRequiredDate.getText().trim().isEmpty()) {
            showError("Required date is required");
            txtRequiredDate.requestFocus();
            return false;
        }

        // Validate date formats
        try {
            dateFormat.parse(txtOrderDate.getText().trim());
        } catch (ParseException e) {
            showError("Invalid order date format. Use: yyyy-MM-dd HH:mm:ss");
            txtOrderDate.requestFocus();
            return false;
        }

        try {
            dateFormat.parse(txtRequiredDate.getText().trim());
        } catch (ParseException e) {
            showError("Invalid required date format. Use: yyyy-MM-dd HH:mm:ss");
            txtRequiredDate.requestFocus();
            return false;
        }

        // Validate shipped date if provided
        if (!txtShippedDate.getText().trim().isEmpty()) {
            try {
                dateFormat.parse(txtShippedDate.getText().trim());
            } catch (ParseException e) {
                showError("Invalid shipped date format. Use: yyyy-MM-dd HH:mm:ss");
                txtShippedDate.requestFocus();
                return false;
            }

            // If shipped date is provided, status should be completed
            if (cmbOrderStatus.getSelectedIndex() != 3) { // 3 = Completed (0-based index)
                showError("Order status must be 'Completed' when shipped date is provided");
                cmbOrderStatus.requestFocus();
                return false;
            }
        }

        return true;
    }

    private Orders createOrderFromForm() {
        try {
            // Extract customer ID
            String customerItem = (String) cmbCustomer.getSelectedItem();
            int customerId = Integer.parseInt(customerItem.split("ID: ")[1].split(" ")[0]);

            // Extract staff ID
            String staffItem = (String) cmbStaff.getSelectedItem();
            int staffId = Integer.parseInt(staffItem.split("ID: ")[1].split(" ")[0]);

            // Extract store ID
            String storeItem = (String) cmbStore.getSelectedItem();
            int storeId = Integer.parseInt(storeItem.split("ID: ")[1].split(" ")[0]);

            // Parse dates
            Timestamp orderDate = new Timestamp(dateFormat.parse(txtOrderDate.getText().trim()).getTime());
            Timestamp requiredDate = new Timestamp(dateFormat.parse(txtRequiredDate.getText().trim()).getTime());
            Timestamp shippedDate = null;
            if (!txtShippedDate.getText().trim().isEmpty()) {
                shippedDate = new Timestamp(dateFormat.parse(txtShippedDate.getText().trim()).getTime());
            }

            // Create order
            Orders order = new Orders(
                    0, // ID will be auto-generated
                    cmbOrderStatus.getSelectedIndex() + 1, // Convert to 1-based status
                    orderDate,
                    requiredDate,
                    storeId,
                    staffId);
            order.setCustID(customerId);
            order.setShippedDate(shippedDate);

            return order;
        } catch (Exception e) {
            showError("Error creating order: " + e.getMessage());
            return null;
        }
    }

    private void populateDropdowns() {
        populateCustomerDropdown();
        populateStaffDropdown();
        populateStoreDropdown();
        populateStoreFilter();
    }

    private void populateCustomerDropdown() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("Select Customer");

        try {
            ArrayList<Customers> customers = customerDAO.getAllCustomers();
            for (Customers customer : customers) {
                String item = String.format("ID: %d - %s %s (%s)",
                        customer.getPersonID(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail());
                cmbCustomer.addItem(item);
            }
        } catch (Exception e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }

    private void populateStaffDropdown() {
        cmbStaff.removeAllItems();
        cmbStaff.addItem("Select Staff");

        try {
            ArrayList<Staffs> staffs = staffDAO.getAllStaffs();
            for (Staffs staff : staffs) {
                String item = String.format("ID: %d - %s %s (%s)",
                        staff.getPersonID(),
                        staff.getFirstName(),
                        staff.getLastName(),
                        staff.getEmail());
                cmbStaff.addItem(item);
            }
        } catch (Exception e) {
            showError("Error loading staff: " + e.getMessage());
        }
    }

    private void populateStoreDropdown() {
        cmbStore.removeAllItems();
        cmbStore.addItem("Select Store");

        // Since StoresDAO is empty, we'll add some placeholder stores
        // You should implement StoresDAO.getAllStores() method
        cmbStore.addItem("ID: 1 - Main Store");
        cmbStore.addItem("ID: 2 - Downtown Store");
        cmbStore.addItem("ID: 3 - Mall Store");
    }

    private void populateStoreFilter() {
        cmbStoreFilter.removeAllItems();
        cmbStoreFilter.addItem("All Stores");

        // Since StoresDAO is empty, we'll add some placeholder stores
        // You should implement StoresDAO.getAllStores() method
        cmbStoreFilter.addItem("ID: 1 - Main Store");
        cmbStoreFilter.addItem("ID: 2 - Downtown Store");
        cmbStoreFilter.addItem("ID: 3 - Mall Store");
    }

    private String getCustomerName(int customerId) {
        try {
            Customers customer = customerDAO.getCustomerById(customerId);
            if (customer != null) {
                return customer.getFirstName() + " " + customer.getLastName();
            }
        } catch (Exception e) {
            System.err.println("Error getting customer name: " + e.getMessage());
        }
        return "Customer ID: " + customerId;
    }

    private String getStaffName(int staffId) {
        try {
            Staffs staff = staffDAO.getStaffById(staffId);
            if (staff != null) {
                return staff.getFirstName() + " " + staff.getLastName();
            }
        } catch (Exception e) {
            System.err.println("Error getting staff name: " + e.getMessage());
        }
        return "Staff ID: " + staffId;
    }

    private void handleStatusChange() {
        int selectedStatus = cmbOrderStatus.getSelectedIndex();

        // If status is "Completed" (index 3), suggest setting shipped date
        if (selectedStatus == 3 && txtShippedDate.getText().trim().isEmpty()) {
            txtShippedDate.setText(dateFormat.format(new Date()));
        }

        // If status is not "Completed", clear shipped date
        if (selectedStatus != 3) {
            txtShippedDate.setText("");
        }
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Vui lòng chọn một đơn hàng để xem chi tiết");
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        controller.showOrderDetails(orderId);
    }

    public void showOrderDetailsDialog(int orderId, ArrayList<OrderItems> orderItems, double orderTotal,
            int itemCount) {
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết đơn hàng #" + orderId,
                true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header panel with order summary
        JPanel headerPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));
        headerPanel.add(new JLabel("Mã đơn hàng:"));
        headerPanel.add(new JLabel(String.valueOf(orderId)));
        headerPanel.add(new JLabel("Số lượng sản phẩm:"));
        headerPanel.add(new JLabel(String.valueOf(itemCount)));
        headerPanel.add(new JLabel("Tổng tiền:"));
        headerPanel.add(new JLabel(String.format("$%.2f", orderTotal)));

        // Table for order items
        String[] columnNames = { "Mã SP", "Tên sản phẩm", "Thương hiệu", "Danh mục", "Năm", "Số lượng", "Đơn giá",
                "Giảm giá", "Thành tiền" };
        DefaultTableModel itemTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable itemTable = new JTable(itemTableModel);

        // Populate table with order items
        for (OrderItems item : orderItems) {
            Object[] row = {
                    item.getProductID(),
                    item.getProductName(),
                    item.getBrandName(),
                    item.getCategoryName(),
                    item.getModelYear(),
                    item.getQuantity(),
                    String.format("$%.2f", item.getListPrice()),
                    String.format("%.1f%%", item.getDiscount() * 100),
                    String.format("$%.2f", item.getItemTotal())
            };
            itemTableModel.addRow(row);
        }

        // Scroll pane for table
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Dialog for adding items to a newly created order
    public void showAddItemToOrderDialog(int orderId) {
        JDialog addItemDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add Items to Order #" + orderId, true);
        addItemDialog.setSize(900, 700); // Adjusted size for better layout
        addItemDialog.setLocationRelativeTo(this);
        addItemDialog.setLayout(new BorderLayout(10, 10)); // Add gaps
        addItemDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for dialog

        // --- Header Panel (Order ID) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centered header
        // headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); No,
        // use dialog padding
        JLabel orderIdLabel = new JLabel("Order ID: " + orderId);
        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(orderIdLabel);
        // addItemDialog.add(headerPanel, BorderLayout.NORTH); // Will add later as part
        // of a combined top panel

        // --- Product Selection Panel ---
        JPanel productSelectionPanel = new JPanel(new GridBagLayout());
        productSelectionPanel.setBorder(BorderFactory.createTitledBorder("Add New Product to Order"));
        GridBagConstraints gbcSelection = new GridBagConstraints();
        gbcSelection.insets = new Insets(5, 5, 5, 5);
        gbcSelection.anchor = GridBagConstraints.WEST; // Align labels to left

        gbcSelection.gridx = 0;
        gbcSelection.gridy = 0;
        productSelectionPanel.add(new JLabel("Product:"), gbcSelection);
        JComboBox<Products> cmbSelectProduct = new JComboBox<>();
        ArrayList<Products> availableProducts = controller.getAllProductsForSelection();
        if (availableProducts.isEmpty()) {
            cmbSelectProduct.addItem(null); // Add a placeholder if no products
            cmbSelectProduct.setEnabled(false);
        } else {
            for (Products p : availableProducts) {
                cmbSelectProduct.addItem(p);
            }
        }
        cmbSelectProduct.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Products) {
                    Products product = (Products) value;
                    setText(product.getProductName() + " ($ " + String.format("%.2f", product.getListPrice()) + ")");
                } else {
                    setText("No products available");
                }
                return this;
            }
        });
        gbcSelection.gridx = 1;
        gbcSelection.gridy = 0;
        gbcSelection.fill = GridBagConstraints.HORIZONTAL;
        gbcSelection.weightx = 1.0;
        productSelectionPanel.add(cmbSelectProduct, gbcSelection);
        gbcSelection.fill = GridBagConstraints.NONE;
        gbcSelection.weightx = 0.0;

        gbcSelection.gridx = 0;
        gbcSelection.gridy = 1;
        productSelectionPanel.add(new JLabel("Quantity:"), gbcSelection);
        JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        gbcSelection.gridx = 1;
        gbcSelection.gridy = 1;
        productSelectionPanel.add(spnQuantity, gbcSelection);

        gbcSelection.gridx = 0;
        gbcSelection.gridy = 2;
        productSelectionPanel.add(new JLabel("Discount (%):"), gbcSelection);
        JSpinner spnDiscount = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        gbcSelection.gridx = 1;
        gbcSelection.gridy = 2;
        productSelectionPanel.add(spnDiscount, gbcSelection);

        JButton btnAddItem = new JButton("Add Product");
        gbcSelection.gridx = 1;
        gbcSelection.gridy = 3;
        gbcSelection.anchor = GridBagConstraints.EAST;
        productSelectionPanel.add(btnAddItem, gbcSelection);
        if (availableProducts.isEmpty())
            btnAddItem.setEnabled(false);

        // Combine header and product selection into a top panel
        JPanel topPanelContainer = new JPanel(new BorderLayout(0, 15)); // 0 horizontal gap, 15 vertical
        topPanelContainer.add(headerPanel, BorderLayout.NORTH);
        topPanelContainer.add(productSelectionPanel, BorderLayout.CENTER);
        addItemDialog.add(topPanelContainer, BorderLayout.NORTH);

        // --- Current Order Items Table ---
        String[] itemColumnNames = { "Item ID", "Product ID", "Name", "Brand", "Category", "Year", "Qty", "Price",
                "Discount", "Total" };
        DefaultTableModel currentItemsTableModel = new DefaultTableModel(itemColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable currentItemsTable = new JTable(currentItemsTableModel);
        JScrollPane itemsScrollPane = new JScrollPane(currentItemsTable);
        itemsScrollPane.setBorder(BorderFactory.createTitledBorder("Products in this Order"));
        addItemDialog.add(itemsScrollPane, BorderLayout.CENTER);

        // --- Footer Panel (Total and Close Button) ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Top padding

        JLabel lblOrderTotal = new JLabel("Order Total: $0.00");
        lblOrderTotal.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.add(lblOrderTotal);
        footerPanel.add(totalPanel, BorderLayout.WEST);

        JButton btnCloseDialog = new JButton("Finish & Close");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCloseDialog);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        addItemDialog.add(footerPanel, BorderLayout.SOUTH);

        // Function to refresh current items table and total
        Runnable refreshItemsTableAndTotal = () -> {
            currentItemsTableModel.setRowCount(0);
            ArrayList<OrderItems> currentItems = controller.getOrderItemsForDialog(orderId);
            double runningOrderTotal = 0;
            for (OrderItems item : currentItems) {
                currentItemsTableModel.addRow(new Object[] {
                        item.getItemID(),
                        item.getProductID(),
                        item.getProductName(),
                        item.getBrandName(), // Assuming OrderItems has these from join
                        item.getCategoryName(), // Assuming OrderItems has these from join
                        item.getModelYear(), // Assuming OrderItems has these from join
                        item.getQuantity(),
                        String.format("$%.2f", item.getListPrice()),
                        String.format("%.1f%%", item.getDiscount() * 100),
                        String.format("$%.2f", item.getItemTotal())
                });
                runningOrderTotal += item.getItemTotal();
            }
            lblOrderTotal.setText(String.format("Order Total: $%.2f", runningOrderTotal));
        };
        refreshItemsTableAndTotal.run(); // Initial load

        // Action for btnAddItem
        btnAddItem.addActionListener(e -> {
            Products selectedProduct = (Products) cmbSelectProduct.getSelectedItem();
            int quantity = (Integer) spnQuantity.getValue();
            double discountPercentage = (Double) spnDiscount.getValue();

            if (selectedProduct == null) {
                showError("Please select a product.");
                return;
            }

            OrderItems newItem = new OrderItems();
            newItem.setOrderID(orderId);
            newItem.setProductID(selectedProduct.getProductID());
            newItem.setQuantity(quantity);
            newItem.setListPrice(selectedProduct.getListPrice());
            newItem.setDiscount(discountPercentage / 100.0);

            if (controller.addItemToOrder(newItem)) {
                // showMessage("Product added to order!"); // Can be too noisy
                refreshItemsTableAndTotal.run();
                spnQuantity.setValue(1);
                spnDiscount.setValue(0.0);
                if (cmbSelectProduct.getItemCount() > 0)
                    cmbSelectProduct.setSelectedIndex(0);
            } else {
                showError("Failed to add product. It might already exist or an error occurred.");
            }
        });

        // Action for btnCloseDialog
        btnCloseDialog.addActionListener(e -> addItemDialog.dispose());

        addItemDialog.setVisible(true);
    }
}