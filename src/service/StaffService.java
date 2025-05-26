/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.StaffsDAO;
import model.Sales.Staffs;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class StaffService {
    private final StaffsDAO staffDAO;
    
    public StaffService() {
        this.staffDAO = new StaffsDAO();
    }
    
    public ArrayList<Staffs> getAllStaffs() {
        return staffDAO.getAllStaffs();
    }
    
    public Staffs getStaffById(int id) {
        return staffDAO.getStaffById(id);
    }
    
    public boolean addStaff(Staffs staff) {
        // Add business logic validation here if needed
        if (staff.getFirstName() == null || staff.getFirstName().trim().isEmpty()) {
            return false;
        }
        if (staff.getLastName() == null || staff.getLastName().trim().isEmpty()) {
            return false;
        }
        return staffDAO.addStaff(staff);
    }
    
    public boolean updateStaff(Staffs staff) {
        return staffDAO.updateStaff(staff);
    }
    
    public boolean deleteStaff(int id) {
        return staffDAO.deleteStaff(id);
    }
}
