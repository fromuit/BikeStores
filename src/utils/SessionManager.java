/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import model.Administration.User;
/**
 *
 * @author duyng
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean hasPermission(String action) {
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER -> action.startsWith("VIEW_") ||
                action.equals("MANAGE_STAFF") ||
                action.equals("MANAGE_INVENTORY") ||
                action.equals("MANAGE_ORDERS");
            case EMPLOYEE -> action.startsWith("VIEW_") ||
                action.equals("MANAGE_ORDERS");
            default -> false;
        }; // Chief Manager has all permissions
        // Store Managers can manage their own store's data
        // Employees have limited permissions
    }
    
    public boolean canAccessStore(int storeId) {
        if (currentUser == null) return false;
        if (currentUser.getRole() == User.UserRole.CHIEF_MANAGER) return true;
        
        // For Store Managers, check if it's their store
        if (currentUser.getRole() == User.UserRole.STORE_MANAGER) {
            // Get store ID from staff record
            return getStaffStoreId(currentUser.getStaffID()) == storeId;
        }
        
        return false;
    }
    
    private int getStaffStoreId(Integer staffID) {
        // Implementation to get store ID from staff record
        return 0; // Placeholder
    }
}
