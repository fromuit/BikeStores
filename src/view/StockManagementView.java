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
    private final StoreService storeService; 

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
        this.storeService = new StoreService();
        controller = new StockController(this);
        initializeUI();
        populateStoreDropdown();
        applyRoleBasedPermissions();
    }

    private void initializeUI() {
        setSize(900, 650);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel storeSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        storeSelectionPanel.add(new JLabel("Chọn cửa hàng:"));
        cmbStoreSelector = new JComboBox<>();
        storeSelectionPanel.add(cmbStoreSelector);
        btnRefreshStocks = new JButton("Tải/Làm mới kho hàng");
        storeSelectionPanel.add(btnRefreshStocks);
        topPanel.add(storeSelectionPanel, BorderLayout.NORTH);

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
        btnUpdateQuantity.setEnabled(false); 

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
            if (selectedStore != null && selectedStore.getId() > 0) {
                selectedStoreIdForTable = selectedStore.getId();

                tableModel.setRowCount(0);
                clearUpdateForm();

                updateButtonStateBasedOnPermissions();
            } else {
                selectedStoreIdForTable = -1;
                tableModel.setRowCount(0);
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
        
        try {
            ArrayList<Stores> stores = controller.getAccessibleStoresForStockView();
            
            User currentUser = sessionManager.getCurrentUser();
            
            if (stores != null && !stores.isEmpty()) {
                if (currentUser != null && currentUser.getRole() == User.UserRole.EMPLOYEE) {
                    if (stores.size() == 1) {
                        Stores store = stores.get(0);
                        cmbStoreSelector.addItem(new StoreItem(store.getStoreID(), store.getStoreName()));
                        selectedStoreIdForTable = store.getStoreID(); 
                    } else {
                        cmbStoreSelector.addItem(new StoreItem(0, "Chọn một cửa hàng"));
                        for (Stores store : stores) {
                            cmbStoreSelector.addItem(new StoreItem(store.getStoreID(), store.getStoreName()));
                        }
                    }
                } else {
                    cmbStoreSelector.addItem(new StoreItem(0, "Chọn một cửa hàng"));
                    for (Stores store : stores) {
                        cmbStoreSelector.addItem(new StoreItem(store.getStoreID(), store.getStoreName()));
                    }
                }
            } else {
                cmbStoreSelector.addItem(new StoreItem(0, "Không có cửa hàng khả dụng"));
                cmbStoreSelector.setEnabled(false);
            }
            
        } catch (SecurityException e) {
            showError("Permission Denied: Could not load stores. " + e.getMessage());
            cmbStoreSelector.addItem(new StoreItem(0, "Lỗi quyền truy cập"));
            cmbStoreSelector.setEnabled(false);
        } catch (Exception e) {
            showError("Error loading stores: " + e.getMessage());
            System.err.println("Error populating store dropdown: " + e.getMessage());
            cmbStoreSelector.addItem(new StoreItem(0, "Lỗi tải dữ liệu"));
            cmbStoreSelector.setEnabled(false);
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

        switch (currentUser.getRole()) {
            case EMPLOYEE -> {
                cmbStoreSelector.setEnabled(true);
                btnRefreshStocks.setEnabled(true);
                txtNewQuantity.setEditable(true);
                
                if (selectedStoreIdForTable > 0) {
                    updateButtonStateBasedOnPermissions();
                }
            }
            case STORE_MANAGER -> {
                cmbStoreSelector.setEnabled(true);
                btnRefreshStocks.setEnabled(true);
                txtNewQuantity.setEditable(true);
            }
            case CHIEF_MANAGER -> {
                cmbStoreSelector.setEnabled(true);
                btnRefreshStocks.setEnabled(true);
                txtNewQuantity.setEditable(true);
            }
        }
    }

    private void loadSelectedStockItemToForm() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = stockTable.convertRowIndexToModel(selectedRow);
            selectedProductIdForUpdate = (Integer) tableModel.getValueAt(modelRow, 0);
            String productName = (String) tableModel.getValueAt(modelRow, 1);

            txtSelectedProductId.setText(String.valueOf(selectedProductIdForUpdate));
            txtSelectedProductName.setText(productName);
            txtNewQuantity.setText(tableModel.getValueAt(modelRow, 2).toString());
            txtNewQuantity.requestFocus();

            updateButtonStateBasedOnPermissions();
        } else {
            clearUpdateForm();
        }
    }

    private void updateButtonStateBasedOnPermissions() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null || selectedStoreIdForTable <= 0) {
            btnUpdateQuantity.setEnabled(false);
            return;
        }

        boolean canUpdate = false;
        boolean canView = true;
        
        switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> {
                canUpdate = true;
                canView = true;
            }
            case STORE_MANAGER -> {
                canView = true; 
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                canUpdate = accessibleStores.contains(selectedStoreIdForTable);
            }
            case EMPLOYEE -> {
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                canView = accessibleStores.contains(selectedStoreIdForTable);
                canUpdate = accessibleStores.contains(selectedStoreIdForTable);
            }
        }


        btnUpdateQuantity.setEnabled(canUpdate && selectedProductIdForUpdate > 0);
        
        if (canUpdate) {
            txtNewQuantity.setEditable(true);
            txtNewQuantity.setBackground(Color.WHITE);
            txtNewQuantity.setToolTipText(null);
        } else if (canView) {
            txtNewQuantity.setEditable(false);
            txtNewQuantity.setBackground(Color.LIGHT_GRAY);
            txtNewQuantity.setToolTipText("Bạn chỉ có thể xem thông tin kho của cửa hàng này");
        } else {
            txtNewQuantity.setEditable(false);
            txtNewQuantity.setBackground(Color.LIGHT_GRAY);
            txtNewQuantity.setToolTipText("Không có quyền truy cập cửa hàng này");
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
            showError("Vui lòng chọn cửa hàng và sản phẩm từ bảng.");
            return;
        }

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            boolean canUpdate = false;
            
            switch (currentUser.getRole()) {
                case CHIEF_MANAGER -> canUpdate = true;
                case STORE_MANAGER, EMPLOYEE -> {
                    ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                    canUpdate = accessibleStores.contains(selectedStoreIdForTable);
                }
            }
            
            if (!canUpdate) {
                String message = currentUser.getRole() == User.UserRole.STORE_MANAGER 
                    ? "Bạn chỉ có thể cập nhật kho cho cửa hàng mình quản lý"
                    : "Bạn chỉ có thể cập nhật kho cho cửa hàng mình làm việc";
                showError(message);
                return;
            }
        }

        try {
            int newQuantity = Integer.parseInt(txtNewQuantity.getText().trim());
            if (newQuantity < 0) {
                showError("Số lượng không được phép âm!");
                return;
            }
            controller.updateStockQuantity(selectedStoreIdForTable, selectedProductIdForUpdate, newQuantity);
        } catch (NumberFormatException e) {
            showError("Số lượng không hợp lệ!");
        }
    }

    public void displayStocks(ArrayList<Stocks> stocks) {
        tableModel.setRowCount(0); 
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
        clearUpdateForm();
    }

    public void refreshCurrentView() {
        if (selectedStoreIdForTable > 0) {
            controller.loadStocksByStore(selectedStoreIdForTable);
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}