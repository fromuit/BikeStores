/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import java.awt.*;
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
    
    public MainFrame() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        initializeComponents();
        setupMenu();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("BikeStores Management System");
    }
    
    private void initializeComponents() {
        desktopPane = new JDesktopPane();
        menuBar = new JMenuBar();
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
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            }
            new MainFrame().setVisible(true);
        });
    }
}