/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import dao.StaffsDAO;
import java.util.ArrayList;
import model.Administration.User;
import model.Sales.Orders;
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
                action.equals("MANAGE_CUSTOMERS"); 
            case EMPLOYEE -> action.startsWith("VIEW_") ||
                action.equals("MANAGE_ORDERS") ||
                action.equals("MANAGE_CUSTOMERS");
            default -> false;
        };
    }
    
    public boolean canAccessStore(int storeId) {
        if (currentUser == null) return false;
        if (currentUser.getRole() == User.UserRole.CHIEF_MANAGER) return true;
        
        // For Store Managers and Employees, check if it's their store
        if (currentUser.getRole() == User.UserRole.STORE_MANAGER || 
            currentUser.getRole() == User.UserRole.EMPLOYEE) {
            // Get store ID from staff record
            return getStaffStoreId(currentUser.getStaffID()) == storeId;
        }
        
        return false;
    }

    public boolean canAddOrder(Orders order) {
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER -> canAccessStore(order.getStoreID());
            case EMPLOYEE -> true; 
            default -> false;
        };
    }

    public boolean canUpdateOrder(Orders order) {
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER -> canAccessStore(order.getStoreID());
            case EMPLOYEE -> order.getStaffID() == currentUser.getStaffID(); 
            default -> false;
        };
    }

    public boolean canDeleteOrder(Orders order) {
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER -> canAccessStore(order.getStoreID());
            case EMPLOYEE -> false;
            default -> false;
        };
    }

    public boolean canViewOrder(Orders order) {
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER -> canAccessStore(order.getStoreID());
            case EMPLOYEE -> order.getStaffID() == currentUser.getStaffID(); 
            default -> false;
        };
    }
    
    private int getStaffStoreId(Integer staffID) {
        if (staffID == null) {
            System.err.println("Staff ID is null");
            return -1;
        }

        try {
            StaffsDAO staffDAO = new StaffsDAO();
            Staffs staff = staffDAO.getStaffById(staffID);
            if (staff != null) {
                System.out.println("Found staff: " + staff.getFirstName() + " " + staff.getLastName() + 
                                 " in store: " + staff.getStoreID());
                return staff.getStoreID();
            } else {
                System.err.println("No staff found with ID: " + staffID);
                return -1;
            }
        } catch (Exception e) {
            System.err.println("Error getting staff store ID: " + e.getMessage());
            e.printStackTrace();
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
        }; 
    }    
    
    public ArrayList<Integer> getAccessibleStoreIds() {
        ArrayList<Integer> storeIds = new ArrayList<>();
        if (currentUser == null) return storeIds;

        switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> {
                for (int i = 1; i <= 5; i++) { 
                    storeIds.add(i);
                }
            }
            case STORE_MANAGER, EMPLOYEE -> {
                int storeId = getStaffStoreId(currentUser.getStaffID());
                if (storeId > 0) {
                    storeIds.add(storeId);
                }
            }
        }
        return storeIds;
    }
}
