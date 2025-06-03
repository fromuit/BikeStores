/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import javax.swing.*;
import model.Administration.User;
import utils.SessionManager;

/**
 *
 * @author duyng
 */
public class MainFrame extends JFrame {
    // Constants for better maintainability
    private static final String WINDOW_TITLE = "Phần mềm quản lý chuỗi cửa hàng Xe đạp";
    private static final Color NAVIGATION_BACKGROUND = new Color(220, 220, 220);
    private static final Color USER_INFO_BACKGROUND = new Color(52, 73, 94);
    private static final Color LOGOUT_BUTTON_COLOR = new Color(220, 53, 69);
    
    private static final Font CATEGORY_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font ITEM_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Dimension BUTTON_SIZE = new Dimension(Integer.MAX_VALUE, 35);
    
    // UI Components
    private JDesktopPane desktopPane;
    private JInternalFrame welcomeInternalFrame;
    private JSplitPane splitPane;
    private JPanel navigationPanel;
    private JPanel userInfoPanel;

    public MainFrame() {
        initializeComponents();
        setupLayout();
        configureWindow();
        setupEventListeners();
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle(WINDOW_TITLE);
    }

    private void setupEventListeners() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerWelcomeFrame();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                centerWelcomeFrame();
            }
        });
    }

    private void initializeComponents() {
        desktopPane = createDesktopPane();
        welcomeInternalFrame = createWelcomeInternalFrame();
        navigationPanel = createNavigationPanel();
        userInfoPanel = createUserInfoPanel();
    }

    private JDesktopPane createDesktopPane() {
        JDesktopPane desktop = new JDesktopPane();
        desktop.setBackground(Color.LIGHT_GRAY);
        return desktop;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(NAVIGATION_BACKGROUND);

        addNavigationSection(panel, "Sales", getSalesMenuItems());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        addNavigationSection(panel, "Production", getProductionMenuItems());
        
        panel.add(Box.createVerticalGlue());
        addLogoutButton(panel);

        return panel;
    }

    private String[] getSalesMenuItems() {
        return new String[]{"Quản lý khách hàng", "Quản lý đơn hàng", "Quản lý nhân viên", "Quản lý cửa hàng"};
    }

    private String[] getProductionMenuItems() {
        return new String[]{"Quản lý sản phẩm", "Quản lý danh mục", "Quản lý nhãn hàng", "Quản lý kho"};
    }

    private void addNavigationSection(JPanel panel, String sectionTitle, String[] menuItems) {
        // Section header
        JLabel sectionLabel = new JLabel(sectionTitle);
        sectionLabel.setFont(CATEGORY_FONT);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        panel.add(sectionLabel);

        // Menu items
        for (String itemName : menuItems) {
            JButton button = createNavigationButton(itemName);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
    }

    private JButton createNavigationButton(String itemName) {
        JButton button = new JButton(itemName);
        configureNavigationButton(button, itemName);
        return button;
    }

    private void configureNavigationButton(JButton button, String itemName) {
        button.setFont(ITEM_FONT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(BUTTON_SIZE);
        button.setFocusPainted(false);
        button.setMargin(new Insets(8, 15, 8, 15));
        
        button.addActionListener(e -> handleNavigation(itemName));
    }

    private void addLogoutButton(JPanel panel) {
        JButton logoutButton = createLogoutButton();
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(logoutButton);
    }

    private JButton createLogoutButton() {
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(ITEM_FONT);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.setFocusPainted(false);
        logoutButton.setMargin(new Insets(8, 15, 8, 15));
        logoutButton.setBackground(LOGOUT_BUTTON_COLOR);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        
        logoutButton.addActionListener(this::handleLogout);
        return logoutButton;
    }

    private void handleLogout(ActionEvent e) {
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn đăng xuất không?",
                "Đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            performLogout();
        }
    }

    private void performLogout() {
        SessionManager.getInstance().setCurrentUser(null);
        this.dispose();
        
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }

    private JInternalFrame createWelcomeInternalFrame() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("Quản lý chuỗi cửa hàng Xe đạp", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel projectLabel = new JLabel("IE303 - Công nghệ Java", SwingConstants.CENTER);
        projectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JLabel authorLabel = new JLabel("Tác giả: Nguyễn Hoàng Duy - Nguyễn Minh Trí", SwingConstants.CENTER);
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JLabel versionLabel = new JLabel("Version: 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Segoe UI Light", Font.ITALIC, 16));

        panel.add(titleLabel, gbc);
        panel.add(projectLabel, gbc);
        panel.add(authorLabel, gbc);
        panel.add(versionLabel, gbc);

        JInternalFrame internalFrame = new JInternalFrame("Welcome", false, false, false, false);
        internalFrame.setContentPane(panel);
        internalFrame.pack();
        internalFrame.setBorder(BorderFactory.createRaisedBevelBorder());

        internalFrame.setFocusable(false);
        if (internalFrame.getUI() instanceof javax.swing.plaf.basic.BasicInternalFrameUI) {
            ((javax.swing.plaf.basic.BasicInternalFrameUI) internalFrame.getUI()).setNorthPane(null);
        }
        return internalFrame;
    }

    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94)); // Dark blue background
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setPreferredSize(new Dimension(0, 60));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        
        // Left side - User info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        userIcon.setForeground(Color.WHITE);
        userIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        JPanel userTextPanel = new JPanel();
        userTextPanel.setLayout(new BoxLayout(userTextPanel, BoxLayout.Y_AXIS));
        userTextPanel.setOpaque(false);
        
        String username = currentUser != null ? currentUser.getUsername() : "Unknown User";
        String role = currentUser != null ? getRoleDisplayName(currentUser.getRole()) : "Unknown Role";
        
        JLabel usernameLabel = new JLabel("Xin chào, " + username);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Vai trò: " + role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(189, 195, 199)); // Light gray
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userTextPanel.add(usernameLabel);
        userTextPanel.add(roleLabel);
        
        // Add store info for employees and store managers
        if (currentUser != null && currentUser.getStaffID() != null && 
            (currentUser.getRole() == User.UserRole.EMPLOYEE || currentUser.getRole() == User.UserRole.STORE_MANAGER)) {
            String storeInfo = getStoreInfo(currentUser);
            if (!storeInfo.isEmpty()) {
                JLabel storeLabel = new JLabel(storeInfo);
                storeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                storeLabel.setForeground(new Color(149, 165, 166)); // Lighter gray
                storeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                userTextPanel.add(storeLabel);
            }
        }
        
        leftPanel.add(userIcon);
        leftPanel.add(userTextPanel);
        
        // Right side - System info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        
        JLabel systemLabel = new JLabel("BikeStores Management System");
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        systemLabel.setForeground(new Color(52, 152, 219)); // Blue
        
        rightPanel.add(systemLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private String getRoleDisplayName(User.UserRole role) {
        if (role == null) return "Không xác định";
        
        return switch (role) {
            case EMPLOYEE -> "Nhân viên";
            case STORE_MANAGER -> "Quản lý cửa hàng";
            case CHIEF_MANAGER -> "Quản lý tổng";
            default -> "Không xác định";
        };
    }
    
    private String getStoreInfo(User user) {
        try {
            if (user != null && user.getStaffID() != null) {
                java.util.ArrayList<Integer> accessibleStores = SessionManager.getInstance().getAccessibleStoreIds();
                if (!accessibleStores.isEmpty()) {
                    return "Cửa hàng: " + accessibleStores.get(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting store info: " + e.getMessage());
        }
        return "";
    }

    private void handleNavigation(String menuItemName) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame != welcomeInternalFrame) { // Không đóng welcome frame
                try {
                    frame.setClosed(true);
                } catch (PropertyVetoException ex) {
                }
            }
        }
        // Ẩn welcome frame khi một cửa sổ quản lý được mở
        if (welcomeInternalFrame != null && welcomeInternalFrame.isVisible() && !menuItemName.isEmpty()) {
            welcomeInternalFrame.setVisible(false);
        }

        switch (menuItemName) {
            case "Quản lý khách hàng" -> openCustomerManagement();
            case "Quản lý đơn hàng" -> openOrderManagement();
            case "Quản lý nhân viên" -> openStaffManagement();
            case "Quản lý cửa hàng" -> openStoreManagement();
            case "Quản lý sản phẩm" -> openProductManagement();
            case "Quản lý danh mục" -> openCategoryManagement();
            case "Quản lý nhãn hàng" -> openBrandManagement();
            case "Quản lý kho" -> openStockManagement();
            default -> {
                if (welcomeInternalFrame != null && !welcomeInternalFrame.isVisible()) {
                    if (!desktopPane.isAncestorOf(welcomeInternalFrame)) {
                        desktopPane.add(welcomeInternalFrame);
                    }
                    welcomeInternalFrame.setVisible(true);
                    centerWelcomeFrame();
                }
            }
        }
    }

    private void setupLayout() {

        JScrollPane navigationScrollPane = new JScrollPane(navigationPanel);
        navigationScrollPane.setMinimumSize(new Dimension(220, 0));
        navigationScrollPane.setBorder(BorderFactory.createEmptyBorder());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigationScrollPane, desktopPane);
        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);
        splitPane.setBorder(null);

        setLayout(new BorderLayout());
        add(userInfoPanel, BorderLayout.NORTH); // Thêm panel thông tin user ở phía trên
        add(splitPane, BorderLayout.CENTER);

        if (welcomeInternalFrame != null) {

            if (!desktopPane.isAncestorOf(welcomeInternalFrame)) {
                desktopPane.add(welcomeInternalFrame);
            }
            welcomeInternalFrame.setVisible(true);

            // SwingUtilities.invokeLater(this::centerWelcomeFrame);
        }
    }

    private void centerWelcomeFrame() {
        if (welcomeInternalFrame != null && welcomeInternalFrame.isVisible() && desktopPane.getWidth() > 0
                && desktopPane.getHeight() > 0) {
            Dimension desktopSize = desktopPane.getSize();
            Dimension welcomeSize = welcomeInternalFrame.getPreferredSize();
            // welcomeInternalFrame.pack();

            int x = (desktopSize.width - welcomeSize.width) / 2;
            int y = (desktopSize.height - welcomeSize.height) / 2;
            x = Math.max(0, x);
            y = Math.max(0, y);
            welcomeInternalFrame.setLocation(x, y);
        }
    }
    
    public void updateUserInfo() {
        if (userInfoPanel != null) {
            // Remove old panel and create new one with updated info
            Container parent = userInfoPanel.getParent();
            if (parent != null) {
                parent.remove(userInfoPanel);
                userInfoPanel = createUserInfoPanel();
                parent.add(userInfoPanel, BorderLayout.NORTH);
                parent.revalidate();
                parent.repaint();
            }
        }
    }

    private void openCustomerManagement() {
        CustomerManagementView customerView = new CustomerManagementView();
        addAndMaximizeInternalFrame(customerView);
    }

    private void openProductManagement() {
        ProductManagementView productView = new ProductManagementView();
        addAndMaximizeInternalFrame(productView);
    }

    private void openStoreManagement() {
        StoreManagementView storeView = new StoreManagementView();
        addAndMaximizeInternalFrame(storeView);
    }

    private void openStockManagement() {
        StockManagementView stockView = new StockManagementView();
        addAndMaximizeInternalFrame(stockView);
    }

    private void openBrandManagement() {
        BrandManagementView brandView = new BrandManagementView();
        addAndMaximizeInternalFrame(brandView);
    }

    private void openCategoryManagement() {
        CategoryManagementView categoryView = new CategoryManagementView();
        addAndMaximizeInternalFrame(categoryView);
    }

    private void openStaffManagement() {
        StaffManagementView staffView = new StaffManagementView();
        addAndMaximizeInternalFrame(staffView);
    }

    private void openOrderManagement() {
        OrderManagementView orderView = new OrderManagementView();
        addAndMaximizeInternalFrame(orderView);
    }

    private void addAndMaximizeInternalFrame(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setMaximum(true);
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            // e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                    | UnsupportedLookAndFeelException e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                        | UnsupportedLookAndFeelException ex) {
                    // ex.printStackTrace();
                }
            }
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}