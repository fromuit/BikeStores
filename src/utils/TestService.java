/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;
import service.CustomerService;
import service.ProductService;
import model.Sales.Customers;
import model.Production.Products;
import java.util.ArrayList;
import service.CustomerService;
import service.ProductService;
/**
 *
 * @author duyng
 */
public class TestService {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Service Layer ===");
        
        testCustomerService();
        testProductService();
    }
    
    private static void testCustomerService() {
        System.out.println("\n--- Testing Customer Service ---");
        CustomerService customerService = new CustomerService();
        
        try {
            // Test getting all customers
            ArrayList<Customers> customers = customerService.getAllCustomers();
            System.out.println("✓ CustomerService.getAllCustomers() returned " + customers.size() + " customers");
            
            // Test business logic validation
            Customers invalidCustomer = new Customers(0, "", "Test", "123", "email", "st", "city", "state", "zip");
            boolean addResult = customerService.addCustomer(invalidCustomer);
            if (!addResult) {
                System.out.println("✓ Service validation working - rejected customer with empty first name");
            } else {
                System.out.println("✗ Service validation failed - should reject empty first name");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Customer Service test failed: " + e.getMessage());
        }
    }
    
    private static void testProductService() {
        System.out.println("\n--- Testing Product Service ---");
        ProductService productService = new ProductService();
        
        try {
            // Test getting all products
            ArrayList<Products> products = productService.getAllProducts();
            System.out.println("✓ ProductService.getAllProducts() returned " + products.size() + " products");
            
            // Test business logic validation
            Products invalidProduct = new Products(0, "", 1, 1, 2023, -100.0);
            boolean addResult = productService.addProduct(invalidProduct);
            if (!addResult) {
                System.out.println("✓ Service validation working - rejected product with negative price");
            } else {
                System.out.println("✗ Service validation failed - should reject negative price");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Product Service test failed: " + e.getMessage());
        }
    }
}