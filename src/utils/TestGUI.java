/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;
import javax.swing.*;
import view.LoginView;
/**
 *
 * @author duyng
 */
public class TestGUI {
    
    public static void main(String[] args) {
        System.out.println("=== Testing GUI Components ===");
        
        // Thiết lập Look and Feel TRƯỚC khi test
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
                    // Use default
                }
            }
            
            // Test main application
            testMainFrame();
        });
    }
    
    private static void testMainFrame() {
        System.out.println("\n--- Testing Main Application Frame ---");
        
        SwingUtilities.invokeLater(() -> {
            try {
                LoginView newLogin = new LoginView();
                newLogin.setVisible(true);
                System.out.println("✓ Main application started successfully!");
            } catch (Exception e) {
                System.out.println("✗ Main frame test failed: " + e.getMessage());
            }
        });
    }
}
