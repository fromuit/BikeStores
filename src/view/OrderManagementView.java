/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.OrderController;
import dao.CustomersDAO;
import dao.StaffsDAO;
import dao.StoresDAO;
import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Administration.User;
import model.Production.Products;
import model.Sales.Customers;
import model.Sales.OrderItems;
import model.Sales.Orders;
import model.Sales.Staffs;
import model.Sales.Stores;
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
        super("Quản lý đơn hàng", true, true, true, true);
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
        String[] columnNames = { "Mã đơn hàng", "Khách hàng", "Tình trạng", "Ngày đặt hàng",
            "Ngày dự tính", "Ngày giao hàng", "Mã cửa hàng", "Nhân viên phụ trách" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(6, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        orderTable.setRowSorter(sorter);

        // Set preferred table size to show fewer rows
        orderTable.setPreferredScrollableViewportSize(new Dimension(1200, 200)); // Reduced height

        // Input fields
        txtOrderDate = new JTextField(15);
        txtRequiredDate = new JTextField(15);
        txtShippedDate = new JTextField(15);
        txtSearch = new JTextField(20);

        // Placeholder text to date fields
        txtOrderDate.setToolTipText("Định dạng: yyyy-MM-dd HH:mm:ss (VD, 2024-01-15 10:30:00)");
        txtRequiredDate.setToolTipText("Định dạng: yyyy-MM-dd HH:mm:ss (VD, 2024-01-15 10:30:00)");
        txtShippedDate.setToolTipText("Định dạng: yyyy-MM-dd HH:mm:ss (VD, 2024-01-15 10:30:00)");

        // Combo boxes
        cmbCustomer = new JComboBox<>();
        cmbStaff = new JComboBox<>();
        cmbStore = new JComboBox<>();
        cmbOrderStatus = new JComboBox<>(new String[] { "Đang chờ xử lý", "Đang xử lý", "Huỷ", "Đã hoàn tất" });

        // Filter combo boxes
        cmbStatusFilter = new JComboBox<>(
                new String[] { "Tất cả", "Đang chờ xử lý", "Đang xử lý", "Huỷ", "Đã hoàn tất"});
        cmbStoreFilter = new JComboBox<>();

        // Buttons
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnRefresh = new JButton("Làm mới");
        btnClear = new JButton("Xoá trường");
        btnViewDetails = new JButton("Chi tiết đơn hàng");
        
        btnSearch = new JButton("Tìm");
        btnClearSearch = new JButton("Xoá");


        // Populate dropdowns
        populateDropdowns();

        // Set current date as default
        txtOrderDate.setText(dateFormat.format(new Date()));
        txtRequiredDate.setText(dateFormat.format(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))); 
    }

    // Add helper method to set current date/time in fields
    private void setCurrentDateTime(JTextField dateField) {
        dateField.setText(dateFormat.format(new Date()));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Lọc theo tình trạng:"));
        searchPanel.add(cmbStatusFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Lọc theo cửa hàng:"));
        searchPanel.add(cmbStoreFilter);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setPreferredSize(new Dimension(1200, 250)); // Adjusted height

        // Center panel for search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Form Panel (South)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), // Outer padding
                BorderFactory.createEtchedBorder() // Inner border for visual separation
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // === Left Column: Order Information ===
        int currentRow = 0;
        JLabel orderInfoLabel = new JLabel("Thông tin đơn hàng");
        orderInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        orderInfoLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.gridwidth = 2; // Span two columns
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(orderInfoLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor
        gbc.gridwidth = 1; // Reset gridwidth

        currentRow++;
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        formPanel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbCustomer.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"); // Set prototype for width
        formPanel.add(cmbCustomer, gbc);
        gbc.fill = GridBagConstraints.NONE;


        currentRow++;
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        formPanel.add(new JLabel("Tình trạng:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbOrderStatus, gbc);
        
        currentRow++;
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        formPanel.add(new JLabel("Ngày đặt hàng:"), gbc);
        gbc.gridx = 1;
        formPanel.add(createDateFieldWithButton(txtOrderDate), gbc);

        currentRow++;
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        formPanel.add(new JLabel("Ngày dự tính:"), gbc);
        gbc.gridx = 1;
        formPanel.add(createDateFieldWithButton(txtRequiredDate), gbc);

        currentRow++;
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        formPanel.add(new JLabel("Ngày giao hàng:"), gbc);
        gbc.gridx = 1;
        formPanel.add(createDateFieldWithButton(txtShippedDate), gbc);
        
        // Vertical separator (empty space or a line)
        gbc.gridx = 2; 
        gbc.gridy = 0;
        gbc.gridheight = currentRow + 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createHorizontalStrut(30), gbc); // Creates a 30px horizontal gap
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1; // Reset gridheight


        // === Right Column: Order Assignment ===
        currentRow = 0; // Reset row counter for the right column
        JLabel assignmentInfoLabel = new JLabel("Phụ trách đơn hàng");
        assignmentInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        assignmentInfoLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 3; // Start at column 3
        gbc.gridy = currentRow;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(assignmentInfoLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        currentRow++;
        gbc.gridx = 3;
        gbc.gridy = currentRow;
        formPanel.add(new JLabel("Cửa hàng:"), gbc);
        gbc.gridx = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbStore.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"); // Set prototype for width
        formPanel.add(cmbStore, gbc);
        gbc.fill = GridBagConstraints.NONE;

        currentRow++;
        gbc.gridx = 3;
        gbc.gridy = currentRow;
        formPanel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbStaff.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"); // Set prototype for width
        formPanel.add(cmbStaff, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Add date format hint at the bottom of the form, spanning all form columns
        currentRow = Math.max(5, currentRow); // Ensure hint is below both columns
        currentRow++; 
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.gridwidth = 5; // Span all 5 columns (0 to 4)
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel dateHintLabel = new JLabel("Định dạng ngày: yyyy-MM-dd HH:mm:ss (Hoặc click 'Now')");
        dateHintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        dateHintLabel.setForeground(Color.GRAY);
        formPanel.add(dateHintLabel, gbc);


        // Button Panel (below the form)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnViewDetails);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClear);

        // South Panel to hold form and buttons
        JPanel southPanel = new JPanel(new BorderLayout(0,10)); // Add vertical gap between form and buttons
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(southPanel, BorderLayout.SOUTH);
    }

    // Helper method to create a panel with a JTextField and a "Now" button
    private JPanel createDateFieldWithButton(JTextField dateField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(dateField);
        JButton btnSetDateNow = new JButton("Now");
        // Adjust button size to match text field height
        Dimension textFieldSize = dateField.getPreferredSize();
        btnSetDateNow.setPreferredSize(new Dimension(60, textFieldSize.height)); 
        btnSetDateNow.setMargin(new Insets(2,2,2,2)); // Reduce padding
        btnSetDateNow.addActionListener(e -> setCurrentDateTime(dateField));
        panel.add(Box.createHorizontalStrut(5)); // Small gap
        panel.add(btnSetDateNow);
        return panel;
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
            showError("Không có tài khoản đăng nhập. Truy cập bị từ chối.");
            setFormFieldsEnabled(false); // Disable all form fields
            btnAdd.setEnabled(false); // Disable all buttons
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            btnViewDetails.setEnabled(false);
            return;
        }

        switch (currentUser.getRole()) {
            case EMPLOYEE -> {
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(false);
                btnViewDetails.setEnabled(true);
                setFormFieldsEnabled(true);
                preselectCurrentUserAsStaff();
                cmbStaff.setEnabled(false);
            }
            case STORE_MANAGER -> {
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnViewDetails.setEnabled(true);
                setFormFieldsEnabled(true);
                limitStoreSelectionToUserStore();
            }
            case CHIEF_MANAGER -> {
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnViewDetails.setEnabled(true);
                setFormFieldsEnabled(true);
            }
            default -> {
                // Handle unexpected role or no role
                setFormFieldsEnabled(false);
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnViewDetails.setEnabled(false);
            }
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
                    showError("Lỗi phân giải mã cửa hàng.");
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
            showError("Hãy chọn một đơn hàng để cập nhật");
            return;
        }

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == User.UserRole.EMPLOYEE) {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0) {
                String staffName = (String) tableModel.getValueAt(selectedRow, 7);
                String currentUserName = getCurrentUserName();
                if (!staffName.contains(currentUserName)) {
                    showError("Chỉ có thể cập nhật hoá đơn bạn được phân công.");
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
            showError("Vui lòng chọn hoá đơn để xoá.");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xoá đơn hàng này?",
                "Xác nhận xoá",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteOrder(selectedOrderId);
        }
    }
    
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
                System.err.println("Lỗi truy xuất tên hiện tại: " + e.getMessage());
            }
        }
        return "";
    }

    private void loadSelectedOrder(int row) {
        int modelRow = orderTable.convertRowIndexToModel(row);
        selectedOrderId = (int) tableModel.getValueAt(modelRow, 0);
        // Find and select customer
        String customerName = (String) tableModel.getValueAt(modelRow, 1);
        for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
            if (cmbCustomer.getItemAt(i).contains(customerName)) {
                cmbCustomer.setSelectedIndex(i);
                break;
            }
        }

        // Set status
        Object statusObj = tableModel.getValueAt(modelRow, 2);
        if (statusObj instanceof String) {
            String statusStringFromTable = ((String) statusObj).trim();
            int statusIndexToSelect = -1;

            // Map English status names (expected from controller/table) to JComboBox indices
            if (statusStringFromTable.equalsIgnoreCase("Pending")) {
                statusIndexToSelect = 0; // "Đang chờ xử lý"
            } else if (statusStringFromTable.equalsIgnoreCase("Processing")) {
                statusIndexToSelect = 1; // "Đang xử lý"
            } else if (statusStringFromTable.equalsIgnoreCase("Rejected") || statusStringFromTable.equalsIgnoreCase("Cancelled")) {
                statusIndexToSelect = 2; // "Huỷ"
            } else if (statusStringFromTable.equalsIgnoreCase("Completed")) {
                statusIndexToSelect = 3; // "Đã hoàn tất"
            } else {
                for (int i = 0; i < cmbOrderStatus.getItemCount(); i++) {
                    if (cmbOrderStatus.getItemAt(i).equalsIgnoreCase(statusStringFromTable)) {
                        statusIndexToSelect = i;
                        break;
                    }
                }
            }

            if (statusIndexToSelect != -1) {
                cmbOrderStatus.setSelectedIndex(statusIndexToSelect);
            } else {
                // If no mapping or direct match is found, print a warning and clear selection or set a default.
                System.err.println("OrderManagementView: Status '" + statusStringFromTable + "' from table could not be mapped to cmbOrderStatus. Clearing selection.");
                cmbOrderStatus.setSelectedIndex(-1);
            }
        } else {
            System.err.println("OrderManagementView: Status from table is not a String or is null. Clearing selection.");
            cmbOrderStatus.setSelectedIndex(-1); 
        }

        // Set dates
        Object orderDateObj = tableModel.getValueAt(modelRow, 3);
        if (orderDateObj instanceof Timestamp) {
            txtOrderDate.setText(dateFormat.format((Timestamp) orderDateObj));
        } else if (orderDateObj != null) {
            txtOrderDate.setText(orderDateObj.toString());
        }

        Object requiredDateObj = tableModel.getValueAt(modelRow, 4);
        if (requiredDateObj instanceof Timestamp) {
            txtRequiredDate.setText(dateFormat.format((Timestamp) requiredDateObj));
        } else if (requiredDateObj != null) {
            txtRequiredDate.setText(requiredDateObj.toString());
        }

        Object shippedDateObj = tableModel.getValueAt(modelRow, 5);
        if (shippedDateObj instanceof Timestamp) {
            txtShippedDate.setText(dateFormat.format((Timestamp) shippedDateObj));
        } else if (shippedDateObj != null) {
            txtShippedDate.setText(shippedDateObj.toString());
        } else {
            txtShippedDate.setText("");
        }

        // Set store
        int storeId = (int) tableModel.getValueAt(modelRow, 6);
        for (int i = 0; i < cmbStore.getItemCount(); i++) {
            if (cmbStore.getItemAt(i).contains("ID: " + storeId)) {
                cmbStore.setSelectedIndex(i);
                break;
            }
        }

        // Find and select staff
        String staffName = (String) tableModel.getValueAt(modelRow, 7);
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
            showError("Hãy chọn một khách hàng từ CSDL");
            cmbCustomer.requestFocus();
            return false;
        }

        if (cmbStaff.getSelectedIndex() == 0) {
            showError("Hãy chọn một nhân viên phụ trách đơn hàng");
            cmbStaff.requestFocus();
            return false;
        }

        if (cmbStore.getSelectedIndex() == 0) {
            showError("Hãy chọn một cửa hàng phụ trách");
            cmbStore.requestFocus();
            return false;
        }

        if (txtOrderDate.getText().trim().isEmpty()) {
            showError("Phải có ngày đặt hàng");
            txtOrderDate.requestFocus();
            return false;
        }

        if (txtRequiredDate.getText().trim().isEmpty()) {
            showError("Phải có ngày dự tính");
            txtRequiredDate.requestFocus();
            return false;
        }

        // Validate date formats
        try {
            dateFormat.parse(txtOrderDate.getText().trim());
        } catch (ParseException e) {
            showError("Định dạng ngày đặt hàng không hợp lệ. Sử dụng: yyyy-MM-dd HH:mm:ss");
            txtOrderDate.requestFocus();
            return false;
        }

        try {
            dateFormat.parse(txtRequiredDate.getText().trim());
        } catch (ParseException e) {
            showError("Định dạng ngày dự tính không hợp lệ. Sử dụng: yyyy-MM-dd HH:mm:ss");
            txtRequiredDate.requestFocus();
            return false;
        }

        if (!txtShippedDate.getText().trim().isEmpty()) {
            try {
                dateFormat.parse(txtShippedDate.getText().trim());
            } catch (ParseException e) {
                showError("Định dạng ngày giao không hợp lệ. Sử dụng: yyyy-MM-dd HH:mm:ss");
                txtShippedDate.requestFocus();
                return false;
            }

            if (cmbOrderStatus.getSelectedIndex() != 3) { // 3 = Completed (0-based index)
                showError("Đơn hàng đã được giao phải có tình trạng là Hoàn thành!! ");
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
                    0,
                    customerId,
                    cmbOrderStatus.getSelectedIndex() + 1, 
                    orderDate,
                    requiredDate,
                    storeId,
                    staffId);
            order.setShippedDate(shippedDate);

            return order;
        } catch (NumberFormatException | ParseException e) {
            showError("Lỗi tạo hoá đơn: " + e.getMessage());
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
        cmbCustomer.addItem("Chọn khách hàng");

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
            showError("Lỗi tải khách hàng: " + e.getMessage());
        }
    }

    private void populateStaffDropdown() {
        cmbStaff.removeAllItems();
        cmbStaff.addItem("Chọn nhân viên");

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
            showError("Lỗi tải nhân viên: " + e.getMessage());
        }
    }

    private void populateStoreDropdown() {
        cmbStore.removeAllItems();
        cmbStore.addItem("Chọn cửa hàng");

        StoresDAO storesDAO = new StoresDAO();
        ArrayList<Stores> stores = storesDAO.getAllStores();
        if (stores != null) {
            for (Stores store : stores) {
                cmbStore.addItem("ID: " + store.getStoreID() + " - " + store.getStoreName());
            }
        }
    }

    private void populateStoreFilter() {
        cmbStoreFilter.removeAllItems();
        cmbStoreFilter.addItem("Tất cả");

        StoresDAO storesDAO = new StoresDAO();
        ArrayList<Stores> stores = storesDAO.getAllStores();
        if (stores != null) {
            for (Stores store : stores) {
                cmbStoreFilter.addItem("ID: " + store.getStoreID() + " - " + store.getStoreName());
            }
        }
    }

    private String getCustomerName(int customerId) {
        try {
            Customers customer = customerDAO.getCustomerById(customerId);
            if (customer != null) {
                return customer.getFirstName() + " " + customer.getLastName();
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải tên khách hàng: " + e.getMessage());
        }
        return "Mã khách hàng: " + customerId;
    }

    private String getStaffName(int staffId) {
        try {
            Staffs staff = staffDAO.getStaffById(staffId);
            if (staff != null) {
                return staff.getFirstName() + " " + staff.getLastName();
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải tên nhân viên: " + e.getMessage());
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
        headerPanel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN ĐƠN HÀNG:"));
        headerPanel.add(new JLabel("Mã đơn hàng:"));
        headerPanel.add(new JLabel(String.valueOf(orderId)));
        headerPanel.add(new JLabel("Số lượng sản phẩm:"));
        headerPanel.add(new JLabel(String.valueOf(itemCount)));
        headerPanel.add(new JLabel("Tổng tiền:"));
        headerPanel.add(new JLabel(String.format("$%.2f", orderTotal)));

        // Table for order items
        String[] columnNames = { "Mã SP", "Tên sản phẩm", "Nhãn hàng", "Danh mục", "Năm", "Số lượng", "Đơn giá",
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
        addItemDialog.setSize(900, 700); 
        addItemDialog.setLocationRelativeTo(this);
        addItemDialog.setLayout(new BorderLayout(10, 10));
        addItemDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); 

        JLabel orderIdLabel = new JLabel("Order ID: " + orderId);
        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(orderIdLabel);

        JPanel productSelectionPanel = new JPanel(new GridBagLayout());
        productSelectionPanel.setBorder(BorderFactory.createTitledBorder("Add New Product to Order"));
        GridBagConstraints gbcSelection = new GridBagConstraints();
        gbcSelection.insets = new Insets(5, 5, 5, 5);
        gbcSelection.anchor = GridBagConstraints.WEST; 

        gbcSelection.gridx = 0;
        gbcSelection.gridy = 0;
        productSelectionPanel.add(new JLabel("Product:"), gbcSelection);
        JComboBox<Products> cmbSelectProduct = new JComboBox<>();
        ArrayList<Products> availableProducts = controller.getAllProductsForSelection();
        if (availableProducts.isEmpty()) {
            cmbSelectProduct.addItem(null);
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
        JPanel topPanelContainer = new JPanel(new BorderLayout(0, 15)); 
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

        Runnable refreshItemsTableAndTotal = () -> {
            currentItemsTableModel.setRowCount(0);
            ArrayList<OrderItems> currentItems = controller.getOrderItemsForDialog(orderId);
            double runningOrderTotal = 0;
            for (OrderItems item : currentItems) {
                currentItemsTableModel.addRow(new Object[] {
                        item.getItemID(),
                        item.getProductID(),
                        item.getProductName(),
                        item.getBrandName(), 
                        item.getCategoryName(),
                        item.getModelYear(), 
                        item.getQuantity(),
                        String.format("$%.2f", item.getListPrice()),
                        String.format("%.1f%%", item.getDiscount() * 100),
                        String.format("$%.2f", item.getItemTotal())
                });
                runningOrderTotal += item.getItemTotal();
            }
            lblOrderTotal.setText(String.format("Order Total: $%.2f", runningOrderTotal));
        };
        refreshItemsTableAndTotal.run(); 

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
                refreshItemsTableAndTotal.run();
                spnQuantity.setValue(1);
                spnDiscount.setValue(0.0);
                if (cmbSelectProduct.getItemCount() > 0)
                    cmbSelectProduct.setSelectedIndex(0);
            } else {
                showError("Failed to add product. It might already exist or an error occurred.");
            }
        });

        btnCloseDialog.addActionListener(e -> addItemDialog.dispose());

        addItemDialog.setVisible(true);
    }
}