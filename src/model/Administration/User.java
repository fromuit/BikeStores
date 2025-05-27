/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Administration;

import java.sql.Timestamp;

/**
 *
 * @author duyng
 */
public class User {
    private int userID;
    private String username;
    private String password;
    private UserRole role;
    private Integer staffID; // Link to existing staff if applicable
    private boolean active = true;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private Timestamp passwordChangedAt;
    
    public enum UserRole {
        CHIEF_MANAGER(3),
        STORE_MANAGER(2),
        EMPLOYEE(1);
        
        private final int level;
        
        UserRole(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }

    public User(int userID, String username, String password, UserRole role, Integer staffID) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.staffID = staffID;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = UserRole.EMPLOYEE;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Integer getStaffID() {
        return staffID;
    }

    public void setStaffID(Integer staffID) {
        this.staffID = staffID;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public Timestamp getPasswordChangedAt() {
        return passwordChangedAt;
    }
    
    public void setPasswordChangedAt(Timestamp passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }
}