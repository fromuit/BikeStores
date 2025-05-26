/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.Sales.Staffs;
import service.StaffService;
import view.StaffManagementView;
import java.util.ArrayList;
import model.Administration.User;
import utils.RequiresPermission;
import utils.SessionManager;
/**
 *
 * @author duyng
 */
public class StaffController {
    private final StaffService staffService;
    private final StaffManagementView view;
    private final SessionManager sessionManager;
    
    public StaffController(StaffManagementView view) {
        this.view = view;
        this.staffService = new StaffService();
        this.sessionManager = SessionManager.getInstance();
    }
    
    
    public void loadStaffs() {
        try {
            ArrayList<Staffs> staffs = staffService.getAllStaffs();
            view.displayStaffs(staffs);
        } catch (Exception e) {
            view.showError("Error loading staffs: " + e.getMessage());
        }
    }
    
    @RequiresPermission("MANAGE_STAFF")
    public void addStaff(Staffs staff) {
            if (!sessionManager.hasPermission("MANAGE_STAFF")) {
                view.showError("You don't have permission to add staff members");
                return;
            }

            // If Store Manager, can only add staff to their own store
            if (sessionManager.getCurrentUser().getRole() == User.UserRole.STORE_MANAGER &&
                !sessionManager.canAccessStore(staff.getStoreID())) {
                view.showError("You can only add staff to your own store");
                return;
            }
                
        try {
            if (staffService.addStaff(staff)) {
                view.showMessage("Staff added successfully!");
                loadStaffs(); // Refresh the table
            } else {
                view.showError("Failed to add staff");
            }
        } catch (Exception e) {
            view.showError("Error adding staff: " + e.getMessage());
        }
    }
    
    @RequiresPermission("MANAGE_STAFF")
    public void updateStaff(Staffs staff) {
            if (!sessionManager.hasPermission("MANAGE_STAFF")) {
                view.showError("You don't have permission to update info of staff members");
                return;
            }

            // If Store Manager, can only add staff to their own store
            if (sessionManager.getCurrentUser().getRole() == User.UserRole.STORE_MANAGER &&
                !sessionManager.canAccessStore(staff.getStoreID())) {
                view.showError("You can only update info for staffs working at your store");
                return;
            }
            
        try {
            if (staffService.updateStaff(staff)) {
                view.showMessage("Staff updated successfully!");
                loadStaffs(); // Refresh the table
            } else {
                view.showError("Failed to update staff");
            }
        } catch (Exception e) {
            view.showError("Error updating staff: " + e.getMessage());
        }
    }
    
    @RequiresPermission("MANAGE_STAFF")
    public void deleteStaff(int staffId) {
            if (!sessionManager.hasPermission("MANAGE_STAFF")) {
                view.showError("You don't have permission to delete staff members");
                return;
            }

//            // If Store Manager, can only delete staff of their own store
//            if (sessionManager.getCurrentUser().getRole() == User.UserRole.STORE_MANAGER &&
//                !sessionManager.canAccessStore(staff.getStoreID())) {
//                view.showError("You can only delete staffs working at your store");
//                return;
//            }
        try {
            if (staffService.deleteStaff(staffId)) {
                view.showMessage("Staff deleted successfully!");
                loadStaffs(); // Refresh the table
            } else {
                view.showError("Failed to delete staff");
            }
        } catch (Exception e) {
            view.showError("Error deleting staff: " + e.getMessage());
        }
    }
}
