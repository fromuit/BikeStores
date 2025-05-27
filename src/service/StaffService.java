/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.StaffsDAO;
import model.Sales.Staffs;
import java.util.ArrayList;
import model.Administration.User;
import utils.SessionManager;
import utils.ValidationException;
/**
 *
 * @author duyng
 */
public class StaffService {
    private final StaffsDAO staffDAO;
    private final SessionManager sessionManager;
    
    public StaffService() {
        this.staffDAO = new StaffsDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public ArrayList<Staffs> getAllStaffs() {
        ArrayList<Staffs> allStaffs = staffDAO.getAllStaffs();
        return filterStaffsByAccess(allStaffs);
    }
    
    public Staffs getStaffById(int id) {
        return staffDAO.getStaffById(id);
    }
    
    public ArrayList<Staffs> getStaffsByStore(int storeId) {
        if (!sessionManager.canAccessStore(storeId)) {
            throw new SecurityException("Access denied to store " + storeId);
        }
        return staffDAO.getStaffsByStore(storeId);
    }
    
    public ArrayList<Staffs> searchStaffs(String searchTerm) {
        ArrayList<Staffs> allStaffs = staffDAO.searchStaffs(searchTerm);
        return filterStaffsByAccess(allStaffs);
    }
    
    private ArrayList<Staffs> filterStaffsByAccess(ArrayList<Staffs> staffs) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getRole() == User.UserRole.CHIEF_MANAGER) {
            return staffs; // Chief managers see all
        }
        
        ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
        ArrayList<Staffs> filteredStaffs = new ArrayList<>();
        
        for (Staffs staff : staffs) {
            if (accessibleStores.contains(staff.getStoreID())) {
                filteredStaffs.add(staff);
            }
        }
        return filteredStaffs;
    }
    
    public boolean addStaff(Staffs staff) throws ValidationException {
        validateStaff(staff);
        validateBusinessRules(staff);
        
        if (!sessionManager.canManageStaff(staff)) {
            throw new SecurityException("You don't have permission to add staff to store " + staff.getStoreID());
        }
        
        // Check for duplicate email
        if (staffDAO.existsByEmail(staff.getEmail())) {
            throw new ValidationException("Email already exists in the system");
        }
        
        return staffDAO.addStaff(staff);
    }
    
    public boolean updateStaff(Staffs staff) throws ValidationException {
        validateStaff(staff);
        validateBusinessRules(staff);
        
        // Get existing staff to check permissions
        Staffs existingStaff = staffDAO.getStaffById(staff.getPersonID());
        if (existingStaff == null) {
            throw new ValidationException("Staff not found");
        }
        
        if (!sessionManager.canManageStaff(existingStaff)) {
            throw new SecurityException("You don't have permission to update this staff member");
        }
        
        // Check for duplicate email (excluding current staff)
        if (staffDAO.existsByEmailExcluding(staff.getEmail(), staff.getPersonID())) {
            throw new ValidationException("Email already exists in the system");
        }
        
        return staffDAO.updateStaff(staff);
    }
    
    public boolean deleteStaff(int staffId) throws ValidationException {
        Staffs staff = staffDAO.getStaffById(staffId);
        if (staff == null) {
            throw new ValidationException("Staff not found");
        }
        
        if (!sessionManager.canManageStaff(staff)) {
            throw new SecurityException("You don't have permission to delete this staff member");
        }
        
        // Check if staff has active orders or is managing other staff
        if (staffDAO.hasActiveOrders(staffId)) {
            throw new ValidationException("Cannot delete staff with active orders");
        }
        
        if (staffDAO.isManagingOtherStaff(staffId)) {
            throw new ValidationException("Cannot delete staff who is managing other staff members");
        }
        
        return staffDAO.deleteStaff(staffId);
    }
    
    private void validateStaff(Staffs staff) throws ValidationException {
        if (staff.getFirstName() == null || staff.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name is required");
        }
        if (staff.getLastName() == null || staff.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name is required");
        }
        if (staff.getEmail() == null || staff.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (!isValidEmail(staff.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        if (staff.getPhone() != null && !isValidPhone(staff.getPhone())) {
            throw new ValidationException("Invalid phone format");
        }
    }
    
    private void validateBusinessRules(Staffs staff) throws ValidationException {
        // Validate store exists
        if (!staffDAO.storeExists(staff.getStoreID())) {
            throw new ValidationException("Invalid store ID");
        }
        
        // Validate manager exists and is in same store (if manager is specified)
        if (staff.getManagerID() != null) {
            Staffs manager = staffDAO.getStaffById((Integer) staff.getManagerID());
            if (manager == null) {
                throw new ValidationException("Invalid manager ID");
            }
            if (manager.getStoreID() != staff.getStoreID()) {
                throw new ValidationException("Manager must be in the same store");
            }
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean isValidPhone(String phone) {
        return phone.matches("^[\\d\\s\\-\\(\\)\\+]+$");
    }
}
