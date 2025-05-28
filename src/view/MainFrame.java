/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.*;
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
    private JDesktopPane desktopPane;
    private JInternalFrame welcomeInternalFrame;
    private JSplitPane splitPane;
    private JPanel navigationPanel;
    private JPanel userInfoPanel;

    public MainFrame() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        initializeComponents();
        // setupMenu(); // Đã bỏ
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Phần mềm quản lý chuỗi cửa hàng Xe đạp");

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
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.LIGHT_GRAY);
        welcomeInternalFrame = createWelcomeInternalFrame();
        // navigationTree = createNavigationTree(); // Thay thế dòng này
        navigationPanel = createButtonNavigationPanel(); // Gọi phương thức mới
        userInfoPanel = createUserInfoPanel(); // Thêm panel thông tin user
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

    private JPanel createButtonNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(220, 220, 220));

        Font categoryFont = new Font("Segoe UI", Font.BOLD, 16);
        Font itemFont = new Font("Segoe UI", Font.PLAIN, 14);
        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 35);

        // Sales Category
        JLabel salesLabel = new JLabel("Sales");
        salesLabel.setFont(categoryFont);
        salesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        salesLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        panel.add(salesLabel);

        String[] salesItems = { "Quản lý khách hàng", "Quản lý đơn hàng", "Quản lý nhân viên", "Quản lý cửa hàng" };
        for (String itemName : salesItems) {
            JButton button = new JButton(itemName);
            configureNavButton(button, itemFont, buttonSize, itemName);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Production Category
        JLabel productionLabel = new JLabel("Production");
        productionLabel.setFont(categoryFont);
        productionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        productionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        panel.add(productionLabel);

        String[] productionItems = { "Quản lý sản phẩm", "Quản lý danh mục", "Quản lý nhãn hàng", "Quản lý kho" };
        for (String itemName : productionItems) {
            JButton button = new JButton(itemName);
            configureNavButton(button, itemFont, buttonSize, itemName);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Thêm một empty component để đẩy các button chức năng lên trên
        panel.add(Box.createVerticalGlue());

        // Log Out Button
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(itemFont);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.setFocusPainted(false);
        logoutButton.setMargin(new Insets(8, 15, 8, 15));

        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);

        logoutButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn đăng xuất không?",
                    "Đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmation == JOptionPane.YES_OPTION) {
                SessionManager.getInstance().setCurrentUser(null);
                this.dispose();

                SwingUtilities.invokeLater(() -> {
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                });
            }
        });
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(logoutButton);

        return panel;
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
        
        switch (role) {
            case EMPLOYEE:
                return "Nhân viên";
            case STORE_MANAGER:
                return "Quản lý cửa hàng";
            case CHIEF_MANAGER:
                return "Quản lý tổng";
            default:
                return "Không xác định";
        }
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

    private void configureNavButton(JButton button, Font font, Dimension size, String itemName) {
        button.setFont(font);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(size);
        button.setFocusPainted(false);
        button.setMargin(new Insets(5, 15, 5, 15));

        button.addActionListener(e -> handleNavigation(itemName));
    }

    private void handleNavigation(String menuItemName) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame != welcomeInternalFrame) { // Không đóng welcome frame
                try {
                    frame.setClosed(true);
                } catch (PropertyVetoException ex) {
                    // ex.printStackTrace();
                }
            }
        }
        // Ẩn welcome frame khi một cửa sổ quản lý được mở
        if (welcomeInternalFrame != null && welcomeInternalFrame.isVisible() && !menuItemName.isEmpty()) {
            welcomeInternalFrame.setVisible(false);
        }

        switch (menuItemName) {
            case "Quản lý khách hàng":
                openCustomerManagement();
                break;
            case "Quản lý đơn hàng":
                openOrderManagement();
                break;
            case "Quản lý nhân viên":
                openStaffManagement();
                break;
            case "Quản lý cửa hàng":
                openStoreManagement();
                break;
            case "Quản lý sản phẩm":
                openProductManagement();
                break;
            case "Quản lý danh mục":
                openCategoryManagement();
                break;
            case "Quản lý nhãn hàng":
                openBrandManagement();
                break;
            case "Quản lý kho":
                openStockManagement();
                break;
            default:

                if (welcomeInternalFrame != null && !welcomeInternalFrame.isVisible()) {
                    // Đảm bảo welcome frame được thêm vào desktopPane nếu chưa có
                    if (!desktopPane.isAncestorOf(welcomeInternalFrame)) {
                        desktopPane.add(welcomeInternalFrame);
                    }
                    welcomeInternalFrame.setVisible(true);
                    centerWelcomeFrame();
                }
                break;
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
            // Kiểm tra xem welcomeInternalFrame đã được thêm vào desktopPane chưa
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

    // Phương thức tiện ích để thêm và phóng to JInternalFrame
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