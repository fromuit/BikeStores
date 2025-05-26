/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;
import dao.CustomersDAO;
import dao.ProductsDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Production.Products;
import model.Sales.Customers;
/**
 *
 * @author duyng
 */
public class TestDAO {
    
    public static void main(String[] args) {
        System.out.println("=== Testing DAO Layer ===");
        
        // Test database connection first
        testConnection();
        
        // Test Customer DAO
        testCustomerDAO();
        
        // Test Product DAO  
        testProductDAO();
    }
    
    private static void testConnection() {
        System.out.println("\n--- Testing Database Connection ---");
        try {
            Connection conn = DatabaseUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                conn.close();
            } else {
                System.out.println("✗ Database connection failed!");
            }
        } catch (SQLException e) {
            System.out.println("✗ Connection error: " + e.getMessage());
        }
    }
    
    private static void testCustomerDAO() {
        System.out.println("\n--- Testing Customer DAO ---");
        CustomersDAO customerDAO = new CustomersDAO();
        
        try {
            // Test READ - Get all customers
            System.out.println("Testing getAllCustomers()...");
            ArrayList<Customers> customers = customerDAO.getAllCustomers();
            System.out.println("✓ Found " + customers.size() + " customers in database");
            
            // Display first few customers
            for (int i = 0; i < Math.min(3, customers.size()); i++) {
                Customers c = customers.get(i);
                System.out.println("  Customer " + (i+1) + ": " + c.getFirstName() + " " + c.getLastName());
            }
            
            // Test CREATE - Add a test customer
            System.out.println("\nTesting addCustomer()...");
            Customers testCustomer = new Customers(
                0, "Test", "Customer", "123-456-7890", "test@email.com",
                "123 Test St", "Test City", "TS", "12345"
            );
            
            boolean added = customerDAO.addCustomer(testCustomer);
            if (added) {
                System.out.println("✓ Test customer added successfully!");
                
                // Test READ by getting the updated list
                ArrayList<Customers> updatedCustomers = customerDAO.getAllCustomers();
                System.out.println("✓ Customer count after add: " + updatedCustomers.size());
                
                // Find and test UPDATE on the test customer
                Customers addedCustomer = null;
                for (Customers c : updatedCustomers) {
                    if ("Test".equals(c.getFirstName()) && "Customer".equals(c.getLastName())) {
                        addedCustomer = c;
                        break;
                    }
                }
                
                if (addedCustomer != null) {
                    System.out.println("✓ Test customer found with ID: " + addedCustomer.getPersonID());
                    
                    // Test UPDATE
                    addedCustomer.setFirstName("Updated Test");
                    boolean updated = customerDAO.updateCustomer(addedCustomer);
                    if (updated) {
                        System.out.println("✓ Customer updated successfully!");
                    } else {
                        System.out.println("✗ Failed to update customer");
                    }
                    
                    // Test DELETE
                    boolean deleted = customerDAO.deleteCustomer(addedCustomer.getPersonID());
                    if (deleted) {
                        System.out.println("✓ Test customer deleted successfully!");
                    } else {
                        System.out.println("✗ Failed to delete test customer");
                    }
                } else {
                    System.out.println("✗ Could not find added test customer");
                }
            } else {
                System.out.println("✗ Failed to add test customer");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Customer DAO test failed: " + e.getMessage());
        }
    }
    
    private static void testProductDAO() {
        System.out.println("\n--- Testing Product DAO ---");
        ProductsDAO productDAO = new ProductsDAO();
        
        try {
            // Test READ - Get all products
            System.out.println("Testing getAllProducts()...");
            ArrayList<Products> products = productDAO.getAllProducts();
            System.out.println("✓ Found " + products.size() + " products in database");
            
            // Display first few products
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                Products p = products.get(i);
                System.out.println("  Product " + (i+1) + ": " + p.getProductName() + " - $" + p.getListPrice());
            }
            
        } catch (Exception e) {
            System.out.println("✗ Product DAO test failed: " + e.getMessage());
        }
    }
}
