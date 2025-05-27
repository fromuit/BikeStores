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
    private JMenuBar menuBar;
    private JInternalFrame welcomeInternalFrame;
    
    public MainFrame() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        initializeComponents();
        setupMenu();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("BikeStores Management System");

        // Add a component listener to center the welcomeInternalFrame when the frame is resized
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
        menuBar = new JMenuBar();
        welcomeInternalFrame = createWelcomeInternalFrame();
    }
    
    private JInternalFrame createWelcomeInternalFrame() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("BikeStores Management System", SwingConstants.CENTER);
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
        ((javax.swing.plaf.basic.BasicInternalFrameUI)internalFrame.getUI()).setNorthPane(null);

        return internalFrame;
    }
    
    private void setupMenu() {
        // Sales Menu
        JMenu salesMenu = new JMenu("Sales");
        JMenuItem customersItem = new JMenuItem("Manage Customers");
        JMenuItem ordersItem = new JMenuItem("Manage Orders");
        JMenuItem staffsItem = new JMenuItem("Manage Staffs");
        JMenuItem storesItem = new JMenuItem("Manage Stores");
        
        salesMenu.add(customersItem);
        salesMenu.add(ordersItem);
        salesMenu.add(staffsItem);
        salesMenu.add(storesItem);
        
        // Production Menu
        JMenu productionMenu = new JMenu("Production");
        JMenuItem productsItem = new JMenuItem("Manage Products");
        JMenuItem categoriesItem = new JMenuItem("Manage Categories");
        JMenuItem brandsItem = new JMenuItem("Manage Brands");
        JMenuItem stocksItem = new JMenuItem("Manage Stocks");
        
        productionMenu.add(productsItem);
        productionMenu.add(categoriesItem);
        productionMenu.add(brandsItem);
        productionMenu.add(stocksItem);
        
        menuBar.add(salesMenu);
        menuBar.add(productionMenu);
        
        // Add action listeners
        customersItem.addActionListener(e -> openCustomerManagement());
        productsItem.addActionListener(e -> openProductManagement());
        brandsItem.addActionListener(e -> openBrandManagement());
        categoriesItem.addActionListener(e -> openCategoryManagement());
        storesItem.addActionListener(e -> openStoreManagement());
        staffsItem.addActionListener(e -> openStaffManagement());
        ordersItem.addActionListener(e -> openOrderManagement());
        stocksItem.addActionListener(e -> openStockManagement());

        setJMenuBar(menuBar);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(desktopPane, BorderLayout.CENTER);

        if (welcomeInternalFrame != null) {
            desktopPane.add(welcomeInternalFrame);
            welcomeInternalFrame.setVisible(true);
        }
    }
    
    private void centerWelcomeFrame() {
        if (welcomeInternalFrame != null && welcomeInternalFrame.isVisible()) {
            Dimension desktopSize = desktopPane.getSize();
            Dimension welcomeSize = welcomeInternalFrame.getSize();
            int x = (desktopSize.width - welcomeSize.width) / 2;
            int y = (desktopSize.height - welcomeSize.height) / 2;
            x = Math.max(0, x);
            y = Math.max(0, y);
            welcomeInternalFrame.setLocation(x, y);
        }
    }
    
    private void openCustomerManagement() {
        CustomerManagementView customerView = new CustomerManagementView();
        desktopPane.add(customerView);
        customerView.setVisible(true);
        try {
            customerView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    
    private void openProductManagement() {
        ProductManagementView productView = new ProductManagementView();
        desktopPane.add(productView);
        productView.setVisible(true);
        try {
            productView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    
    private void openStoreManagement() {
        StoreManagementView storeView = new StoreManagementView();
        desktopPane.add(storeView);
        storeView.setVisible(true);
        try {
            storeView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    
    private void openStockManagement() {
        StockManagementView stockView = new StockManagementView();
        desktopPane.add(stockView);
        stockView.setVisible(true);
        try {
            stockView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    
    private void openBrandManagement() {
        BrandManagementView brandView = new BrandManagementView();
        desktopPane.add(brandView);
        brandView.setVisible(true);
        try {
            brandView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    private void openCategoryManagement() {
        CategoryManagementView categoryView = new CategoryManagementView();
        desktopPane.add(categoryView);
        categoryView.setVisible(true);
        try {
            categoryView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    private void openStaffManagement() {
        StaffManagementView staffView = new StaffManagementView();
        desktopPane.add(staffView);
        staffView.setVisible(true);
        try {
            staffView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }

    private void openOrderManagement() {
        OrderManagementView orderView = new OrderManagementView();
        desktopPane.add(orderView);
        orderView.setVisible(true);
        try {
            orderView.setSelected(true);
        } catch (PropertyVetoException e) {
        }
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use a modern look and feel if available
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                // Fallback to system L&F if Nimbus is not available or fails
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    // e.printStackTrace(); // Log inner exception
                }
            }
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            // Initial centering after frame is visible and has its size.
            // The component listener will handle subsequent resizes.
            // mainFrame.centerWelcomeFrame(); // This will be called by componentShown
        });
    }
}