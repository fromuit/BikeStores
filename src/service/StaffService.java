/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.StaffsDAO;
import dao.interfaces.IStaffsDAO;
import java.util.ArrayList;
import model.Administration.User;
import model.Sales.Staffs;
import utils.SessionManager;
import utils.ValidationException;
/**
 *
 * @author duyng
 */
public class StaffService {
    private final IStaffsDAO staffDAO;
    private final SessionManager sessionManager;
    
    public StaffService() {
        this.staffDAO = new StaffsDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public StaffService(IStaffsDAO staffDAO) {
        this.staffDAO = staffDAO;
        this.sessionManager = SessionManager.getInstance();
    }
    
    public ArrayList<Staffs> getAllStaffs() {
        ArrayList<Staffs> allStaffs = staffDAO.selectAll();
        return filterStaffsByAccess(allStaffs);
    }
    
    public Staffs getStaffById(int id) {
        return staffDAO.selectById(id);
    }
    
    public ArrayList<Staffs> getStaffsByStore(int storeId) {
        if (!sessionManager.canAccessStore(storeId)) {
            throw new SecurityException("Access denied to store " + storeId);
        }
        return staffDAO.getStaffsByStore(storeId);
    }
    
    public ArrayList<Staffs> searchStaffs(String searchTerm) {
        ArrayList<Staffs> allStaffs = staffDAO.search(searchTerm);
        return filterStaffsByAccess(allStaffs);
    }
    
    private ArrayList<Staffs> filterStaffsByAccess(ArrayList<Staffs> staffs) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getRole() == User.UserRole.CHIEF_MANAGER) {
            return staffs; 
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

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER && !sessionManager.canManageStaff(staff)) {
            throw new SecurityException("You don't have permission to add staff to store " + staff.getStoreID());
        }

        if (staffDAO.existsByEmail(staff.getEmail())) {
            throw new ValidationException("Email already exists in the system");
        }
        
        return staffDAO.insert(staff);
    }
    
    public boolean updateStaff(Staffs staff) throws ValidationException {
        validateStaff(staff);
        validateBusinessRules(staff);

        Staffs existingStaff = staffDAO.selectById(staff.getPersonID());
        if (existingStaff == null) {
            throw new ValidationException("Staff not found");
        }

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER && !sessionManager.canManageStaff(existingStaff)) {
            throw new SecurityException("You don't have permission to update this staff member");
        }

        if (staffDAO.existsByEmailExcluding(staff.getEmail(), staff.getPersonID())) {
            throw new ValidationException("Email already exists in the system");
        }
        
        return staffDAO.update(staff);
    }
    
    public boolean deleteStaff(int staffId) throws ValidationException {
        Staffs staff = staffDAO.selectById(staffId);
        if (staff == null) {
            throw new ValidationException("Staff not found");
        }

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required");
        }

        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER && !sessionManager.canManageStaff(staff)) {
            throw new SecurityException("You don't have permission to delete this staff member");
        }

        if (staffDAO.hasActiveOrders(staffId)) {
            throw new ValidationException("Cannot delete staff with active orders");
        }
        
        if (staffDAO.isManagingOtherStaff(staffId)) {
            throw new ValidationException("Cannot delete staff who is managing other staff members");
        }
        
        return staffDAO.delete(staffId);
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
        if (!staffDAO.storeExists(staff.getStoreID())) {
            throw new ValidationException("Invalid store ID");
        }

        if (staff.getManagerID() != null) {
            Staffs manager = staffDAO.selectById((Integer) staff.getManagerID());
            if (manager == null) {
                throw new ValidationException("Invalid manager ID");
            }

            User currentUser = sessionManager.getCurrentUser();
            if (currentUser != null && currentUser.getRole() != User.UserRole.CHIEF_MANAGER) {
                if (manager.getStoreID() != staff.getStoreID()) {
                    throw new ValidationException("Manager must be in the same store");
                }
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
