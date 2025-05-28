package view;

import controller.StoreController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Administration.User;
import model.Sales.Stores;
import utils.SessionManager;

public class StoreManagementView extends JInternalFrame {
    private final StoreController controller;
    private JTable storeTable;
    private DefaultTableModel tableModel;

    private JTextField txtStoreName, txtPhone, txtEmail, txtStreet, txtCity, txtState, txtZipCode;
    private JTextField txtSearch;
    private JButton btnSearch, btnClearSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClearForm;

    private int selectedStoreId = -1;
    private final SessionManager sessionManager;

    public StoreManagementView() {
        super("Quản lý cửa hàng", true, true, true, true);
        this.sessionManager = SessionManager.getInstance();
        controller = new StoreController(this);
        initializeUI();
        controller.loadStores();
        applyRoleBasedPermissions();
    }

    private void initializeUI() {
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10)); // Add gaps between components

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(25);
        searchPanel.add(txtSearch);
        btnSearch = new JButton("Tìm");
        searchPanel.add(btnSearch);
        btnClearSearch = new JButton("Xoá");
        searchPanel.add(btnClearSearch);

        // --- Table Panel ---
        String[] columnNames = { "Mã cửa hàng", "Tên cửa hàng", "SĐT", "Email","Đường" ,"Thành phố", "Bang", "Ma zip" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        storeTable = new JTable(tableModel);
        storeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storeTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(storeTable);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết cửa hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên CH:"), gbc);
        txtStoreName = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        formPanel.add(txtStoreName, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("SĐT:"), gbc);
        txtPhone = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(txtEmail, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Đường:"), gbc);
        txtStreet = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        formPanel.add(txtStreet, gbc);
        gbc.gridwidth = 1;

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Thành phố:"), gbc);
        txtCity = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(txtCity, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Bang:"), gbc);
        txtState = new JTextField(20);
        gbc.gridx = 3;
        gbc.gridy = 3;
        formPanel.add(txtState, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Mã ZIP:"), gbc);
        txtZipCode = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(txtZipCode, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnRefresh = new JButton("Làm mới");
        btnClearForm = new JButton("Xoá trường");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClearForm);

        // --- Layout Arrangement ---
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // --- Event Listeners ---
        storeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && storeTable.getSelectedRow() != -1) {
                loadSelectedStoreToForm();
            }
        });

        btnAdd.addActionListener(e -> addStore());
        btnUpdate.addActionListener(e -> updateStore());
        btnDelete.addActionListener(e -> deleteStore());
        btnRefresh.addActionListener(e -> controller.loadStores());
        btnClearForm.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchStores());
        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            controller.loadStores();
        });
        txtSearch.addActionListener(e -> searchStores()); // Search on Enter key
    }

    private void applyRoleBasedPermissions() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            showError("Không có tài khoản! Quyền truy cập bị từ chối");
            // Disable all functionalities
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            setFormFieldsEditable(false);
            return;
        }

        switch (currentUser.getRole()) {
            case EMPLOYEE -> {
                // Employees can only view their own store
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                setFormFieldsEditable(false);
            }
            case STORE_MANAGER -> {
                // Store managers can view all stores but only edit their own
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(false);
                setFormFieldsEditable(true);
            }
            case CHIEF_MANAGER -> {
                // Chief managers have full access
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                setFormFieldsEditable(true);
            }
        }
    }

    private void setFormFieldsEditable(boolean editable) {
        txtStoreName.setEditable(editable);
        txtPhone.setEditable(editable);
        txtEmail.setEditable(editable);
        txtStreet.setEditable(editable);
        txtCity.setEditable(editable);
        txtState.setEditable(editable);
        txtZipCode.setEditable(editable);
    }

    private void loadSelectedStoreToForm() {
        int selectedRow = storeTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedStoreId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            User currentUser = sessionManager.getCurrentUser();
            
            // For store managers, check if they can edit this store
            if (currentUser != null && currentUser.getRole() == User.UserRole.STORE_MANAGER) {
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                boolean canEdit = accessibleStores.contains(selectedStoreId);
                
                // Update button state based on whether they can edit this store
                btnUpdate.setEnabled(canEdit);
                setFormFieldsEditable(canEdit);
                
                if (!canEdit) {
                    // Show a visual indicator that this store is read-only
                    showMessage("Bạn chỉ có thể xem thông tin cửa hàng này");
                }
            }
            
            // Fetch full store details to populate form
            Stores store = controller.getStoreById(selectedStoreId);
            if (store != null) {
                txtStoreName.setText(store.getStoreName());
                txtPhone.setText(store.getPhone());
                txtEmail.setText(store.getEmail());
                txtStreet.setText(store.getStreet());
                txtCity.setText(store.getCity());
                txtState.setText(store.getState());
                txtZipCode.setText(store.getZipCode());
            } else {
                clearForm();
                showError("Không thể lấy thông tin cửa hàng này!");
            }
        }
    }

    private void addStore() {
        Stores store = createStoreFromForm();
        if (store != null) {
            controller.addStore(store);
        }
    }

    private void updateStore() {
        if (selectedStoreId == -1) {
            showError("Hãy chọn một cửa hàng để cập nhật");
            return;
        }
        
        User currentUser = sessionManager.getCurrentUser();
        
        // Additional client-side check for store managers
        if (currentUser != null && currentUser.getRole() == User.UserRole.STORE_MANAGER) {
            ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
            if (!accessibleStores.contains(selectedStoreId)) {
                showError("Bạn chỉ có thể sửa thông tin cửa hàng mình quản lý");
                return;
            }
        }
        
        Stores store = createStoreFromForm();
        if (store != null) {
            store.setStoreID(selectedStoreId);
            controller.updateStore(store);
        }
    }

    private void deleteStore() {
        if (selectedStoreId == -1) {
            showError("Hãy chọn một cửa hàng để xoá");
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xoá cửa hàng có mã: " + selectedStoreId + "?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            controller.deleteStore(selectedStoreId);
        }
    }

    private void searchStores() {
        String searchTerm = txtSearch.getText().trim();
        controller.searchStores(searchTerm);
    }

    private Stores createStoreFromForm() {
        String name = txtStoreName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String street = txtStreet.getText().trim();
        String city = txtCity.getText().trim();
        String state = txtState.getText().trim();
        String zip = txtZipCode.getText().trim();

        // Basic validation (more detailed validation should be in Service layer)
        if (name.isEmpty()) {
            showError("Store Name cannot be empty.");
            txtStoreName.requestFocus();
            return null;
        }
        // You can add more client-side quick validations here if desired

        Stores store = new Stores();
        // For add, ID is set by DB. For update, selectedStoreId is used.
        store.setStoreName(name);
        store.setPhone(phone.isEmpty() ? null : phone);
        store.setEmail(email.isEmpty() ? null : email);
        store.setStreet(street.isEmpty() ? null : street);
        store.setCity(city.isEmpty() ? null : city);
        store.setState(state.isEmpty() ? null : state);
        store.setZipCode(zip.isEmpty() ? null : zip);
        return store;
    }

    private void clearForm() {
        selectedStoreId = -1;
        txtStoreName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtStreet.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtZipCode.setText("");
        storeTable.clearSelection();
    }

    public void displayStores(ArrayList<Stores> stores) {
        tableModel.setRowCount(0); // Clear existing data
        if (stores != null) {
            for (Stores store : stores) {
                Object[] row = {
                        store.getStoreID(),
                        store.getStoreName(),
                        store.getPhone(),
                        store.getEmail(),
                        store.getStreet(),
                        store.getCity(),
                        store.getState(),
                        store.getZipCode()  
                };
                tableModel.addRow(row);
            }
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}