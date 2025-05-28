package view;

import controller.StockController;
import java.awt.*;
import java.util.ArrayList; // For product selection if needed
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel; // To get list of stores for dropdown
import javax.swing.table.TableRowSorter;
import model.Administration.User;
import model.Production.Stocks;
import model.Sales.Stores;
import service.StoreService;
import utils.SessionManager;

public class StockManagementView extends JInternalFrame {
    private final StockController controller;
    private JTable stockTable;
    private DefaultTableModel tableModel;

    private JComboBox<StoreItem> cmbStoreSelector;
    private JTextField txtSelectedProductName, txtSelectedProductId, txtNewQuantity;
    private JButton btnUpdateQuantity, btnRefreshStocks;

    private int selectedStoreIdForTable = -1;
    private int selectedProductIdForUpdate = -1;

    private final SessionManager sessionManager;
    private final StoreService storeService; // To populate store dropdown

    // Helper class for JComboBox store items
    private static class StoreItem {
        private final int id;
        private final String name;

        public StoreItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public StockManagementView() {
        super("Quản lý kho", true, true, true, true);
        this.sessionManager = SessionManager.getInstance();
        this.storeService = new StoreService(); // Initialize StoreService
        controller = new StockController(this);
        initializeUI();
        populateStoreDropdown();
        applyRoleBasedPermissions();
    }

    private void initializeUI() {
        setSize(900, 650);
        setLayout(new BorderLayout(10, 10));

        // --- Store Selection Panel ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel storeSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        storeSelectionPanel.add(new JLabel("Chọn cửa hàng:"));
        cmbStoreSelector = new JComboBox<>();
        storeSelectionPanel.add(cmbStoreSelector);
        btnRefreshStocks = new JButton("Tải/Làm mới kho hàng");
        storeSelectionPanel.add(btnRefreshStocks);
        topPanel.add(storeSelectionPanel, BorderLayout.NORTH);

        // --- Stock Update Panel (within topPanel or separate) ---
        JPanel updatePanel = new JPanel(new GridBagLayout());
        updatePanel.setBorder(BorderFactory.createTitledBorder("Cập nhật số lượng hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        updatePanel.add(new JLabel("Mã sản phẩm:"), gbc);
        txtSelectedProductId = new JTextField(5);
        txtSelectedProductId.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 0;
        updatePanel.add(txtSelectedProductId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        updatePanel.add(new JLabel("Tên sản phẩm:"), gbc);
        txtSelectedProductName = new JTextField(20);
        txtSelectedProductName.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        updatePanel.add(txtSelectedProductName, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 2;
        updatePanel.add(new JLabel("Số lượng mới:"), gbc);
        txtNewQuantity = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 2;
        updatePanel.add(txtNewQuantity, gbc);

        btnUpdateQuantity = new JButton("Cập nhật");
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        updatePanel.add(btnUpdateQuantity, gbc);
        btnUpdateQuantity.setEnabled(false); // Enabled when a product is selected

        topPanel.add(updatePanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columnNames = { "Mã sản phẩm", "Tên sản phẩm", "Số lượng trong kho", "Giá niêm yết" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(tableModel);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(2, Comparator.comparingInt(o -> Integer.valueOf(o.toString()))); 
        sorter.setComparator(3, Comparator.comparingDouble(o -> Double.valueOf(o.toString())));
        stockTable.setRowSorter(sorter);
        
        
        JScrollPane scrollPane = new JScrollPane(stockTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Event Listeners ---
        cmbStoreSelector.addActionListener(e -> {
            StoreItem selectedStore = (StoreItem) cmbStoreSelector.getSelectedItem();
            if (selectedStore != null && selectedStore.getId() != 0) {
                selectedStoreIdForTable = selectedStore.getId();
                // controller.loadStocksByStore(selectedStoreIdForTable); // Load on selection
                // or via button
            } else {
                selectedStoreIdForTable = -1;
                tableModel.setRowCount(0); // Clear table if "Select Store" is chosen
                clearUpdateForm();
            }
        });

        btnRefreshStocks.addActionListener(e -> {
            if (selectedStoreIdForTable > 0) {
                controller.loadStocksByStore(selectedStoreIdForTable);
            } else {
                showError("Hãy chọn một cửa hàng");
            }
        });

        stockTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && stockTable.getSelectedRow() != -1) {
                loadSelectedStockItemToForm();
            }
        });

        btnUpdateQuantity.addActionListener(e -> updateStockQuantity());
    }

    private void populateStoreDropdown() {
        cmbStoreSelector.removeAllItems();
        cmbStoreSelector.addItem(new StoreItem(0, "Chọn một cửa hàng")); 
        try {
            ArrayList<Stores> stores = storeService.getAllStores();
            if (stores != null) {
                for (Stores store : stores) {
                    cmbStoreSelector.addItem(new StoreItem(store.getStoreID(), store.getStoreName()));
                }
            }
        } catch (SecurityException e) {
            showError("Permission Denied: Could not load stores. " + e.getMessage());
        } catch (Exception e) {
            showError("Error loading stores: " + e.getMessage());
            System.err.println("Error populating store dropdown: " + e.getMessage());
        }
    }

    private void applyRoleBasedPermissions() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            showError("Không có tài khoản! Quyền truy cập bị từ chối");
            cmbStoreSelector.setEnabled(false);
            btnRefreshStocks.setEnabled(false);
            btnUpdateQuantity.setEnabled(false);
            txtNewQuantity.setEditable(false);
            return;
        }

        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            btnUpdateQuantity.setEnabled(false);
            txtNewQuantity.setEditable(false);
            // Employees might only view stock, not update it.
        } else {
            // Managers can update
            // btnUpdateQuantity is enabled/disabled based on table selection too
            txtNewQuantity.setEditable(true);
        }
    }

    private void loadSelectedStockItemToForm() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = stockTable.convertRowIndexToModel(selectedRow);
            selectedProductIdForUpdate = (Integer) tableModel.getValueAt(modelRow, 0);
            String productName = (String) tableModel.getValueAt(modelRow, 1);
            // int currentQuantity = (Integer) tableModel.getValueAt(selectedRow, 2);

            txtSelectedProductId.setText(String.valueOf(selectedProductIdForUpdate));
            txtSelectedProductName.setText(productName);
            txtNewQuantity.setText(tableModel.getValueAt(modelRow, 2).toString()); // Set current quantity as
                                                                                      // starting point
            txtNewQuantity.requestFocus();
            btnUpdateQuantity.setEnabled(true && txtNewQuantity.isEditable()); // Enable if form is editable
        } else {
            clearUpdateForm();
        }
    }

    private void clearUpdateForm() {
        selectedProductIdForUpdate = -1;
        txtSelectedProductId.setText("");
        txtSelectedProductName.setText("");
        txtNewQuantity.setText("");
        btnUpdateQuantity.setEnabled(false);
        stockTable.clearSelection();
    }

    private void updateStockQuantity() {
        if (selectedStoreIdForTable <= 0 || selectedProductIdForUpdate <= 0) {
            showError("Please select a store and a product from the table.");
            return;
        }
        try {
            int newQuantity = Integer.parseInt(txtNewQuantity.getText().trim());
            if (newQuantity < 0) {
                showError("Số lượng không được phép âm!");
                return;
            }
            controller.updateStockQuantity(selectedStoreIdForTable, selectedProductIdForUpdate, newQuantity);
            // The controller's updateStockQuantity should trigger a refresh of the view
        } catch (NumberFormatException e) {
            showError("Số lượng không hợp lệ!");
        }
    }

    public void displayStocks(ArrayList<Stocks> stocks) {
        tableModel.setRowCount(0); // Clear existing data
        if (stocks != null) {
            for (Stocks stockItem : stocks) {
                Object[] row = {
                        stockItem.getProductID(),
                        stockItem.getProduct() != null ? stockItem.getProduct().getProductName() : "N/A",
                        stockItem.getQuantity(),
                        stockItem.getProduct() != null ? String.format("%.2f", stockItem.getProduct().getListPrice())
                                : "N/A"
                };
                tableModel.addRow(row);
            }
        }
        clearUpdateForm(); // Clear form after refreshing table
    }

    // Method called by controller after successful update to refresh current view
    public void refreshCurrentView() {
        if (selectedStoreIdForTable > 0) {
            controller.loadStocksByStore(selectedStoreIdForTable);
        }
        // if another filter type is active (e.g. by product), handle that too.
        // For simplicity, this example assumes store filter is primary.
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}