/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import dao.StaffsDAO;
import java.util.ArrayList;
import model.Administration.User;
import model.Sales.Staffs;
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
                action.equals("MANAGE_ORDERS") ||
                action.equals("MANAGE_CUSTOMERS"); // Add this line
            case EMPLOYEE -> action.startsWith("VIEW_") ||
                action.equals("MANAGE_ORDERS");
            default -> false;
        };
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
        if (staffID == null) return -1;
    
        try {
            // Get store ID from staff record using DAO
            StaffsDAO staffDAO = new StaffsDAO();
            Staffs staff = staffDAO.getStaffById(staffID);
            return staff != null ? staff.getStoreID() : -1;
        } catch (Exception e) {
            System.err.println("Error getting staff store ID: " + e.getMessage());
            return -1;
        }    
    }

    public boolean canManageStaff(Staffs staff) {
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER -> canAccessStore(staff.getStoreID());
            case EMPLOYEE -> false;
            default -> false;
        }; // Can only manage staff in their own store
    }    
    
    public ArrayList<Integer> getAccessibleStoreIds() {
        ArrayList<Integer> storeIds = new ArrayList<>();
        if (currentUser == null) return storeIds;

        switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> {
                // Chief managers can access all stores
                // You might want to fetch this from database
                for (int i = 1; i <= 5; i++) { 
                    storeIds.add(i);
                }
            }
            case STORE_MANAGER -> {
                // Store managers can only access their own store
                int storeId = getStaffStoreId(currentUser.getStaffID());
                if (storeId > 0) {
                    storeIds.add(storeId);
                }
            }
            case EMPLOYEE -> {
            }
        }
        // Employees typically don't manage staff
        return storeIds;
    }
}
