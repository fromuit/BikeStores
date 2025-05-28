/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.interfaces.IStoresDAO;
import java.sql.*;
import java.util.ArrayList;
import model.Sales.Stores;
import utils.DatabaseUtil;

/**
 * Store Data Access Object Implementation
 */
public class StoresDAO implements IStoresDAO {
    
    @Override
    public boolean insert(Stores store) {
        return addStore(store);
    }
    
    @Override
    public boolean update(Stores store) {
        return updateStore(store);
    }
    
    @Override
    public boolean delete(Integer storeId) {
        return deleteStore(storeId);
    }
    
    @Override
    public ArrayList<Stores> selectAll() {
        return getAllStores();
    }
    
    @Override
    public Stores selectById(Integer storeId) {
        return getStoreById(storeId);
    }
    
    @Override
    public ArrayList<Stores> search(String searchTerm) {
        return searchStores(searchTerm);
    }
    
    // Existing methods (keeping for backward compatibility)
    public boolean addStore(Stores store) {
        String query = "INSERT INTO sales.stores (store_name, phone, email, street, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, store.getStoreName());
            pstmt.setString(2, store.getPhone());
            pstmt.setString(3, store.getEmail());
            pstmt.setString(4, store.getStreet());
            pstmt.setString(5, store.getCity());
            pstmt.setString(6, store.getState());
            pstmt.setString(7, store.getZipCode());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding store: " + e.getMessage());
            return false;
        }
    }

    public Stores getStoreById(int storeId) {
        String query = "SELECT * FROM sales.stores WHERE store_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStore(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting store by ID: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Stores> getAllStores() {
        ArrayList<Stores> stores = new ArrayList<>();
        String query = "SELECT * FROM sales.stores ORDER BY store_name";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                stores.add(mapResultSetToStore(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all stores: " + e.getMessage());
        }
        return stores;
    }

    public boolean updateStore(Stores store) {
        String query = "UPDATE sales.stores SET store_name=?, phone=?, email=?, street=?, city=?, state=?, zip_code=? WHERE store_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, store.getStoreName());
            pstmt.setString(2, store.getPhone());
            pstmt.setString(3, store.getEmail());
            pstmt.setString(4, store.getStreet());
            pstmt.setString(5, store.getCity());
            pstmt.setString(6, store.getState());
            pstmt.setString(7, store.getZipCode());
            pstmt.setInt(8, store.getStoreID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating store: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStore(int storeId) {
        String query = "DELETE FROM sales.stores WHERE store_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, storeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Check for foreign key constraint violation (e.g., if store is in use by
            // orders or staff)
            if (e.getSQLState().startsWith("23")) { // SQLState for integrity constraint violation
                System.err.println("Error deleting store: Store is currently in use and cannot be deleted. SQLState: "
                        + e.getSQLState());
                // Optionally, throw a custom exception or return a specific error code/message
                // to the service layer
                // throw new DataIntegrityViolationException("Cannot delete store as it is in
                // use.");
            } else {
                System.err.println("Error deleting store: " + e.getMessage());
            }
            return false;
        }
    }

    public ArrayList<Stores> searchStores(String searchTerm) {
        ArrayList<Stores> stores = new ArrayList<>();
        String query = "SELECT * FROM sales.stores WHERE LOWER(store_name) LIKE LOWER(?) OR LOWER(city) LIKE LOWER(?) OR LOWER(state) LIKE LOWER(?) OR phone LIKE ? OR email LIKE ? ORDER BY store_name";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, searchPattern);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stores.add(mapResultSetToStore(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching stores: " + e.getMessage());
        }
        return stores;
    }

    private Stores mapResultSetToStore(ResultSet rs) throws SQLException {
        return new Stores(
                rs.getInt("store_id"),
                rs.getString("store_name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("street"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("zip_code"));
    }
}
