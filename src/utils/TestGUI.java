/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;
import java.awt.HeadlessException;
import view.CustomerManagementView;
import view.MainFrame;
import javax.swing.*;
/**
 *
 * @author duyng
 */
public class TestGUI {
    
    public static void main(String[] args) {
        System.out.println("=== Testing GUI Components ===");
        
        // Test individual component

        
        // Test main application
         testMainFrame();
    }
    
    private static void testCustomerManagementView() {
        System.out.println("\n--- Testing Customer Management View ---");
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set Look and Feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    // Create and show the customer management view in a frame
                    JFrame testFrame = new JFrame("Test Customer Management");
                    CustomerManagementView customerView = new CustomerManagementView();
                    
                    testFrame.add(customerView);
                    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    testFrame.setSize(1000, 600);
                    testFrame.setLocationRelativeTo(null);
                    testFrame.setVisible(true);
                    
                    System.out.println("✓ Customer Management View opened successfully!");
                    System.out.println("  - Check if the table loads with data");
                    System.out.println("  - Try adding, updating, and deleting customers");
                    System.out.println("  - Test form validation");
                    
                } catch (HeadlessException | ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                    System.out.println("✗ GUI test failed: " + e.getMessage());
                }
            }
        });
    }
    
    private static void testMainFrame() {
        System.out.println("\n--- Testing Main Application Frame ---");
        
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                System.out.println("✓ Main application started successfully!");
            } catch (Exception e) {
                System.out.println("✗ Main frame test failed: " + e.getMessage());
            }
        });
    }
}
