/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.StaffController;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Administration.User;
import model.Sales.Staffs;
import utils.SessionManager;

/**
 *
 * @author duyng
 */
public class StaffManagementView extends JInternalFrame {
    private final StaffController controller;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone;
    private JTextField txtStoreID, txtManagerID;
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private JComboBox<String> cmbStoreFilter;
    private JComboBox<String> cmbActive;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private int selectedStaffId = -1;

    private final SessionManager sessionManager;

    public StaffManagementView() {
        super("Quản lý nhân viên", true, true, true, true);
        this.sessionManager = SessionManager.getInstance();
        controller = new StaffController(this);
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadStaffs();
        setSize(1200, 600);

        applyRoleBasedPermissions();
    }

    private void initializeComponents() {
        // Table setup
        String[] columnNames = { "Mã nhân viên", "Tên", "Họ", "Email", "SĐT", "Tình trạng", "Mã cửa hàng",
                "Mã quản lý" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(6, Comparator.comparingInt(o -> Integer.valueOf(o.toString())));
        sorter.setComparator(7, Comparator.comparingInt(o -> Integer.valueOf(o.toString())));
        staffTable.setRowSorter(sorter);

        // Input fields
        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtEmail = new JTextField(15);
        txtPhone = new JTextField(15);
        txtStoreID = new JTextField(10);
        txtManagerID = new JTextField(10);
        txtSearch = new JTextField(20);

        // Active dropdown
        cmbActive = new JComboBox<>(new String[] { "1 - Active", "0 - Inactive" });

        // Buttons
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnRefresh = new JButton("Làm mới");
        btnClear = new JButton("Xoá trường");
        btnSearch = new JButton("Tìm");
        btnClearSearch = new JButton("Xoá");

        cmbStoreFilter = new JComboBox<>();
        populateStoreFilter();
    }

    private void populateStoreFilter() {
        cmbStoreFilter.removeAllItems();
        cmbStoreFilter.addItem("Tất cả");

        ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
        for (Integer storeId : accessibleStores) {
            cmbStoreFilter.addItem("Cửa hàng " + storeId);
        }
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
        searchPanel.add(new JLabel("Lọc theo cửa hàng:"));
        searchPanel.add(cmbStoreFilter);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(staffTable);
        // add(scrollPane, BorderLayout.CENTER);

        // Combine search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFirstName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Họ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtLastName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tình trạng nhân viên:"), gbc);
        gbc.gridx = 3;
        formPanel.add(cmbActive, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mã cửa hàng:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtStoreID, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Mã quản lý:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtManagerID, gbc);

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
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = staffTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedStaff(selectedRow);
                }
            }
        });
        // Search listeners
        btnSearch.addActionListener(e -> performSearch());
        btnClearSearch.addActionListener(e -> clearSearch());
        txtSearch.addActionListener(e -> performSearch()); // Search on Enter
        // Button listeners
        btnAdd.addActionListener(e -> addStaff());
        btnUpdate.addActionListener(e -> updateStaff());
        btnDelete.addActionListener(e -> deleteStaff());
        btnRefresh.addActionListener(e -> loadStaffs());
        btnClear.addActionListener(e -> clearForm());

        // Store filter listener
        cmbStoreFilter.addActionListener(e -> filterByStore());
    }

    // Methods called by controller
    public void displayStaffs(ArrayList<Staffs> staffs) {
        tableModel.setRowCount(0);

        for (Staffs staff : staffs) {

            Object[] row = {
                    staff.getPersonID(),
                    staff.getFirstName(),
                    staff.getLastName(),
                    staff.getEmail(),
                    staff.getPhone(),
                    staff.getActive() == 1 ? "Active" : "Inactive",
                    staff.getStoreID(),
                    staff.getManagerID() != null ? staff.getManagerID() : "NULL"
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
    private void loadStaffs() {
        controller.loadStaffs();
    }

    private void addStaff() {
        if (validateInput()) {
            Staffs staff = createStaffFromForm();
            controller.addStaff(staff);
        }
    }

    private void updateStaff() {
        if (selectedStaffId == -1) {
            showError("Hãy chọn một nhân viên để cập nhật");
            return;
        }
        if (validateInput()) {
            Staffs staff = createStaffFromForm();
            staff.setPersonID(selectedStaffId);
            controller.updateStaff(staff);
        }
    }

    private void deleteStaff() {
        if (selectedStaffId == -1) {
            showError("Hãy chọn một nhân viên để xoá");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xoá nhân viên này?",
                "Xác nhận xoá",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.deleteStaff(selectedStaffId);
        }
    }

    private void loadSelectedStaff(int row) {
        int modelRow = staffTable.convertRowIndexToModel(row);
        selectedStaffId = (int) tableModel.getValueAt(modelRow, 0);
        txtFirstName.setText((String) tableModel.getValueAt(modelRow, 1));
        txtLastName.setText((String) tableModel.getValueAt(modelRow, 2));
        txtEmail.setText((String) tableModel.getValueAt(modelRow, 3));
        txtPhone.setText((String) tableModel.getValueAt(modelRow, 4));

        String activeStatus = (String) tableModel.getValueAt(modelRow, 5);
        cmbActive.setSelectedIndex(activeStatus.equals("Active") ? 0 : 1);

        txtStoreID.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));

        Object managerIdObj = tableModel.getValueAt(modelRow, 7);
        txtManagerID.setText(managerIdObj != null && !managerIdObj.toString().isEmpty() ? managerIdObj.toString() : "");
    }

    private void clearForm() {
        selectedStaffId = -1;
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtStoreID.setText("");
        txtManagerID.setText("");
        cmbActive.setSelectedIndex(0);
        staffTable.clearSelection();
    }

    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        if (!searchTerm.isEmpty()) {
            controller.searchStaffs(searchTerm);
        } else {
            loadStaffs();
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbStoreFilter.setSelectedIndex(0);
        loadStaffs();
    }

    private void filterByStore() {
        int selectedIndex = cmbStoreFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            // All stores
            loadStaffs();
        } else {
            // Specific store
            String storeText = (String) cmbStoreFilter.getSelectedItem();
            int storeId = Integer.parseInt(storeText.replace("Cửa hàng ", ""));
            controller.loadStaffsByStore(storeId);
        }
    }

    private void applyRoleBasedPermissions() {
        User currentUser = sessionManager.getCurrentUser();

        switch (currentUser.getRole()) {
            case EMPLOYEE ->  {
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                // Make all form fields read-only
                setFormFieldsEnabled(false);
            }
            case STORE_MANAGER ->  {
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                txtManagerID.setEnabled(false);

                // Restrict store ID to their own store
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                if (!accessibleStores.isEmpty()) {
                    txtStoreID.setText(String.valueOf(accessibleStores.get(0)));
                    txtStoreID.setEnabled(false);
                }
            }
            case CHIEF_MANAGER ->  {
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
        txtEmail.setEnabled(enabled);
        txtPhone.setEnabled(enabled);
        txtStoreID.setEnabled(enabled);
        txtManagerID.setEnabled(enabled);
        cmbActive.setEnabled(enabled);
    }

    private boolean validateInput() {
        if (txtFirstName.getText().trim().isEmpty()) {
            showError("Phải có họ và tên");
            return false;
        }
        if (txtLastName.getText().trim().isEmpty()) {
            showError("Phải có họ và tên");
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            showError("Phải cung cấp email nhân viên");
            return false;
        }

        try {
            Integer.valueOf(txtStoreID.getText().trim());
        } catch (NumberFormatException e) {
            showError("Mã cửa hàng không hợp lệ");
            return false;
        }

        // Manager ID is optional, but if provided, must be a valid number
        if (!txtManagerID.getText().trim().isEmpty()) {
            try {
                Integer.valueOf(txtManagerID.getText().trim());
            } catch (NumberFormatException e) {
                showError("Mã quản lý không hợp hệ");
                return false;
            }
        }

        return true;
    }

    private Staffs createStaffFromForm() {
        int active = cmbActive.getSelectedIndex(); // 0 = Active (1), 1 = Inactive (0)
        int activeValue = active == 0 ? 1 : 0;

        Integer managerId = null;
        if (!txtManagerID.getText().trim().isEmpty()) {
            managerId = Integer.valueOf(txtManagerID.getText().trim());
        }

        return new Staffs(
                0, // ID will be auto-generated
                txtFirstName.getText().trim(),
                txtLastName.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim(),
                activeValue,
                Integer.parseInt(txtStoreID.getText().trim()),
                managerId);
    }
}
