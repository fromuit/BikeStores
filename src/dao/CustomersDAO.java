/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import model.Sales.Customers;
import utils.DatabaseUtil;

/**
 *
 * @author duyng
 */
public class CustomersDAO {
    
    // Create - Add new customer
    public boolean addCustomer(Customers customer) {
        String query = "INSERT INTO sales.customers (first_name, last_name, email, phone, street, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getStreet());
            pstmt.setString(6, customer.getCity());
            pstmt.setString(7, customer.getState());
            pstmt.setString(8, customer.getZipCode());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Read - Get all customers
    public ArrayList<Customers> getAllCustomers() {
        ArrayList<Customers> customers = new ArrayList<>();
        String query = "SELECT * FROM sales.customers";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
        }
        return customers;
    }
    
    // Read - Get customer by ID
    public Customers getCustomerById(int customerId) {
        String query = "SELECT * FROM sales.customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Update - Update existing customer
    public boolean updateCustomer(Customers customer) {
        String query = "UPDATE sales.customers SET first_name=?, last_name=?, email=?, phone=?, street=?, city=?, state=?, zip_code=? WHERE customer_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getStreet());
            pstmt.setString(6, customer.getCity());
            pstmt.setString(7, customer.getState());
            pstmt.setString(8, customer.getZipCode());
            pstmt.setInt(9, customer.getPersonID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Delete - Delete customer
    public boolean deleteCustomer(int customerId) {
        String query = "DELETE FROM sales.customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    //Search customer
    public ArrayList<Customers> searchCustomers(String searchTerm) {
        ArrayList<Customers> customers = new ArrayList<>();
        String query = "SELECT * FROM sales.customers WHERE " +
                       "LOWER(first_name) LIKE LOWER(?) OR " +
                       "LOWER(last_name) LIKE LOWER(?) OR " +
                       "LOWER(email) LIKE LOWER(?) OR " +
                       "phone LIKE ? OR " +
                       "LOWER(city) LIKE LOWER(?) OR " +
                       "LOWER(state) LIKE LOWER(?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            pstmt.setString(6, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
        return customers;
    }    
    
    public ArrayList<Customers> getCustomersByState(String state) {
        ArrayList<Customers> customers = new ArrayList<>();
        String query = "SELECT * FROM sales.customers WHERE LOWER(state) = LOWER(?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, state);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting customers by state: " + e.getMessage());
        }
        return customers;
    }
    
    public ArrayList<Customers> getCustomersByCity(String city) {
        ArrayList<Customers> customers = new ArrayList<>();
        String query = "SELECT * FROM sales.customers WHERE LOWER(city) = LOWER(?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, city);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting customers by city: " + e.getMessage());
        }
        return customers;
    }

    public ArrayList<String> getCitiesForState(String state) {
        ArrayList<String> cities = new ArrayList<>();
        String query = "SELECT DISTINCT city FROM sales.customers WHERE LOWER(state) = LOWER(?) AND city IS NOT NULL AND city != '' ORDER BY city";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, state);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting cities for state: " + e.getMessage());
        }
        return cities;
    }

    public boolean existsByEmail(String email) {
        String query = "SELECT COUNT(*) FROM sales.customers WHERE LOWER(email) = LOWER(?)";
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

    public boolean existsByEmailExcluding(String email, int excludeCustomerId) {
        String query = "SELECT COUNT(*) FROM sales.customers WHERE LOWER(email) = LOWER(?) AND customer_id != ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, excludeCustomerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    public boolean existsByPhone(String phone) {
        String query = "SELECT COUNT(*) FROM sales.customers WHERE phone = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone existence: " + e.getMessage());
        }
        return false;
    }

    public boolean existsByPhoneExcluding(String phone, int excludeCustomerId) {
        String query = "SELECT COUNT(*) FROM sales.customers WHERE phone = ? AND customer_id != ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setInt(2, excludeCustomerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone existence: " + e.getMessage());
        }
        return false;
    }

    public boolean hasActiveOrders(int customerId) {
        String query = "SELECT COUNT(*) FROM sales.orders WHERE customer_id = ? AND order_status IN (1, 2)"; // Assuming 1=Pending, 2=Processing
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking active orders: " + e.getMessage());
        }
        return false;
    }

    public ArrayList<String> getDistinctStates() {
        ArrayList<String> states = new ArrayList<>();
        String query = "SELECT DISTINCT state FROM sales.customers WHERE state IS NOT NULL AND state != '' ORDER BY state";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                states.add(rs.getString("state"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting distinct states: " + e.getMessage());
        }
        return states;
    }

    public ArrayList<String> getDistinctCities() {
        ArrayList<String> cities = new ArrayList<>();
        String query = "SELECT DISTINCT city FROM sales.customers WHERE city IS NOT NULL AND city != '' ORDER BY city";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting distinct cities: " + e.getMessage());
        }
        return cities;
    }    
    // Helper method to map ResultSet to Customer object
    private Customers mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customers(
            rs.getInt("customer_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("street"),
            rs.getString("city"),
            rs.getString("state"),
            rs.getString("zip_code")
        );
    }
}