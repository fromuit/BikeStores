/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import java.util.ArrayList;
import model.Sales.Staffs;
import service.StaffService;
import utils.ValidationException;
import view.StaffManagementView;
/**
 *
 * @author duyng
 */
public class StaffController {
    private final StaffService staffService;
    private final StaffManagementView view;
    
    public StaffController(StaffManagementView view) {
        this.view = view;
        this.staffService = new StaffService();
    }
    
    
    public void loadStaffs() {
        try {
            ArrayList<Staffs> staffs = staffService.getAllStaffs();
            view.displayStaffs(staffs);
        } catch (SecurityException e) {
            view.showError("Access denied: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error loading staffs: " + e.getMessage());
            System.err.println("Error in loadStaffs: " + e.getMessage());
        }
    }
    
    public void loadStaffsByStore(int storeId) {
        try {
            ArrayList<Staffs> staffs = staffService.getStaffsByStore(storeId);
            view.displayStaffs(staffs);
        } catch (SecurityException e) {
            view.showError("Access denied: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error loading staffs for store " + storeId + ": " + e.getMessage());
            System.err.println("Error in loadStaffsByStore: " + e.getMessage());
        }
    }
    
    public void searchStaffs(String searchTerm) {
        try {
            ArrayList<Staffs> staffs = staffService.searchStaffs(searchTerm);
            view.displayStaffs(staffs);
        } catch (Exception e) {
            view.showError("Error searching staffs: " + e.getMessage());
        }
    }
    
    public void addStaff(Staffs staff) {
        try {
            if (staffService.addStaff(staff)) {
                view.showMessage("Staff added successfully!");
                loadStaffs();
            } else {
                view.showError("Failed to add staff");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error adding staff: " + e.getMessage());
            System.err.println("Error in addStaff: " + e.getMessage());
        }
    }
    
    public void updateStaff(Staffs staff) {
        try {
            if (staffService.updateStaff(staff)) {
                view.showMessage("Staff updated successfully!");
                loadStaffs();
            } else {
                view.showError("Failed to update staff");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error updating staff: " + e.getMessage());
            System.err.println("Error in updateStaff: " + e.getMessage());
        }
    }

    public void deleteStaff(int staffId) {
        try {
            if (staffService.deleteStaff(staffId)) {
                view.showMessage("Staff deleted successfully!");
                loadStaffs();
            } else {
                view.showError("Failed to delete staff");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error deleting staff: " + e.getMessage());
            System.err.println("Error in deleteStaff: " + e.getMessage());
        }
    }
}
