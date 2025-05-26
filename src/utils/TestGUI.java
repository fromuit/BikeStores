/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;
import javax.swing.*;
import view.MainFrame;
/**
 *
 * @author duyng
 */
public class TestGUI {
    
    public static void main(String[] args) {
        System.out.println("=== Testing GUI Components ===");
        
        // Test main application
         testMainFrame();
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
