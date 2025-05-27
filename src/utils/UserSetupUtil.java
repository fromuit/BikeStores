/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;
import dao.UserDAO;
import model.Administration.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 * @author duyng
 */
public class UserSetupUtil {
    
    public static void main(String[] args) {
        setupInitialUsers();
    }
    
    public static void setupInitialUsers() {
        try {
            // Hash passwords for initial users
            String chiefPassword = PasswordUtil.hashPassword("123");
            String managerPassword = PasswordUtil.hashPassword("123");
            String employeePassword = PasswordUtil.hashPassword("123");
            
            // Insert users with hashed passwords
            insertUser("chief", chiefPassword, "CHIEF_MANAGER", null);
            insertUser("manager", managerPassword, "STORE_MANAGER", 1);
            insertUser("employee", employeePassword, "EMPLOYEE", 2);
            
            System.out.println("Initial users created successfully!");
            System.out.println("Username: chief, Password: 123, Role: CHIEF_MANAGER");
            System.out.println("Username: manager, Password: 123, Role: STORE_MANAGER");
            System.out.println("Username: employee, Password: 123, Role: EMPLOYEE");
            
        } catch (Exception e) {
            System.err.println("Error setting up users: " + e.getMessage());
        }
    }
    
    private static void insertUser(String username, String hashedPassword, String role, Integer staffId) {
        String query = "INSERT INTO administration.users (username, password, role, staff_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            if (staffId != null) {
                pstmt.setInt(4, staffId);
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.executeUpdate();
            System.out.println("User '" + username + "' created successfully");
            
        } catch (SQLException e) {
            System.err.println("Error creating user '" + username + "': " + e.getMessage());
        }
    }
}
