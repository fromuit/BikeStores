/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Sales.Customers;
import java.util.ArrayList;
import utils.DatabaseUtil;
import java.sql.*;

/**
 *
 * @author duyng
 */
public class CustomersDAO {
    
    // Create - Add new customer
    public boolean addCustomer(Customers customer) {
        String query = "INSERT INTO sales.customers (first_name, last_name, phone, email, street, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
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
        String query = "UPDATE sales.customers SET first_name=?, last_name=?, phone=?, email=?, street=?, city=?, state=?, zip_code=? WHERE customer_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
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
    
    // Helper method to map ResultSet to Customer object
    private Customers mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customers(
            rs.getInt("customer_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("street"),
            rs.getString("city"),
            rs.getString("state"),
            rs.getString("zip_code")
        );
    }
}