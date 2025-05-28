/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.interfaces.IUserDAO;
import java.sql.*;
import java.util.ArrayList;
import model.Administration.User;
import utils.DatabaseUtil;

/**
 * User Data Access Object Implementation
 */
public class UserDAO implements IUserDAO {
    
    @Override
    public boolean insert(User user) {
        String query = "INSERT INTO administration.users (username, password, role, staff_id, active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole().toString());
            if (user.getStaffID() != null) {
                pstmt.setInt(4, user.getStaffID());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setBoolean(5, user.isActive());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean update(User user) {
        String query = "UPDATE administration.users SET username=?, password=?, role=?, staff_id=?, active=? WHERE user_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole().toString());
            if (user.getStaffID() != null) {
                pstmt.setInt(4, user.getStaffID());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setBoolean(5, user.isActive());
            pstmt.setInt(6, user.getUserID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(Integer userId) {
        String query = "DELETE FROM administration.users WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public ArrayList<User> selectAll() {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM administration.users";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }
    
    @Override
    public User selectById(Integer userId) {
        return getUserById(userId);
    }
    
    @Override
    public ArrayList<User> search(String searchTerm) {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM administration.users WHERE LOWER(username) LIKE LOWER(?) OR LOWER(role) LIKE LOWER(?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return users;
    }
    
    @Override
    public User getUserByUsername(String username) {
        String query = "SELECT * FROM administration.users WHERE LOWER(username) = LOWER(?) AND active = 1";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }
        return null;
    }
    
    public User getUserById(int userId) {
        String query = "SELECT * FROM administration.users WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public boolean updateLastLogin(int userId) {
        String query = "UPDATE administration.users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updatePassword(int userId, String hashedPassword) {
        String query = "UPDATE administration.users SET password = ?, password_changed_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            User.UserRole.valueOf(rs.getString("role")),
            rs.getObject("staff_id") != null ? rs.getInt("staff_id") : null
        );
        user.setActive(rs.getBoolean("active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}
