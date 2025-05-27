/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.Sales.Staffs;
import utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class StaffsDAO {
    
    // Create - Add new staff
    public boolean addStaff(Staffs staff) {
        String query = "INSERT INTO sales.staffs (first_name, last_name, email, phone, active, store_id, manager_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, staff.getFirstName());
            pstmt.setString(2, staff.getLastName());
            pstmt.setString(3, staff.getEmail());
            pstmt.setString(4, staff.getPhone());
            pstmt.setInt(5, staff.getActive());
            pstmt.setInt(6, staff.getStoreID());
            if (staff.getManagerID() != null) {
                pstmt.setInt(7, (int)staff.getManagerID());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Read - Get all staffs
    public ArrayList<Staffs> getAllStaffs() {
        ArrayList<Staffs> staffs = new ArrayList<>();
        String query = "SELECT * FROM sales.staffs";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                staffs.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
        }
        return staffs;
    }
    
    // Read - Get staff by ID
    public Staffs getStaffById(int staffId) {
        String query = "SELECT * FROM sales.staffs WHERE staff_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    public ArrayList<Staffs> getStaffsByStore(int storeId) {
        ArrayList<Staffs> staffs = new ArrayList<>();
        String query = "SELECT * FROM sales.staffs WHERE store_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                staffs.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting staffs by store: " + e.getMessage());
        }
        return staffs;
    }
    
    // Update - Update existing staff
    public boolean updateStaff(Staffs staff) {
        String query = "UPDATE sales.staffs SET first_name=?, last_name=?, email=?, phone=?, active=?, store_id=?, manager_id=? WHERE staff_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, staff.getFirstName());
            pstmt.setString(2, staff.getLastName());
            pstmt.setString(3, staff.getEmail());
            pstmt.setString(4, staff.getPhone());
            pstmt.setInt(5, staff.getActive());
            pstmt.setInt(6, staff.getStoreID());
            if (staff.getManagerID() != null) {
                pstmt.setInt(7, (int)staff.getManagerID());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            pstmt.setInt(8, staff.getPersonID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Delete - Delete staff
    public boolean deleteStaff(int staffId) {
        String query = "DELETE FROM sales.staffs WHERE staff_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    //Search staffs
    public ArrayList<Staffs> searchStaffs(String searchTerm) {
        ArrayList<Staffs> staffs = new ArrayList<>();
        String query = "SELECT * FROM sales.staffs WHERE " +
                       "LOWER(first_name) LIKE LOWER(?) OR " +
                       "LOWER(last_name) LIKE LOWER(?) OR " +
                       "LOWER(email) LIKE LOWER(?) OR " +
                       "phone LIKE ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                staffs.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching staffs: " + e.getMessage());
        }
        return staffs;
    }
    
    public boolean existsByEmail(String email) {
        String query = "SELECT COUNT(*) FROM sales.staffs WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    public boolean existsByEmailExcluding(String email, int excludeStaffId) {
        String query = "SELECT COUNT(*) FROM sales.staffs WHERE LOWER(email) = LOWER(?) AND staff_id != ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, excludeStaffId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }
    
    public boolean storeExists(int storeId) {
        String query = "SELECT COUNT(*) FROM sales.stores WHERE store_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking store existence: " + e.getMessage());
        }
        return false;
    }

    public boolean hasActiveOrders(int staffId) {
        String query = "SELECT COUNT(*) FROM sales.orders WHERE staff_id = ? AND order_status IN (1, 2)"; // Assuming 1=Pending, 2=Processing
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking active orders: " + e.getMessage());
        }
        return false;
    }

    public boolean isManagingOtherStaff(int staffId) {
        String query = "SELECT COUNT(*) FROM sales.staffs WHERE manager_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking managed staff: " + e.getMessage());
        }
        return false;
    }    
    // Helper method to map ResultSet to Staff object
    private Staffs mapResultSetToStaff(ResultSet rs) throws SQLException {
        return new Staffs(
            rs.getInt("staff_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getInt("active"),
            rs.getInt("store_id"),
            rs.getObject("manager_id") != null ? rs.getInt("manager_id") : null
        );
    }
}
