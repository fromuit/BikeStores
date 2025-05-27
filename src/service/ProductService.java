/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.ProductsDAO;
import java.util.ArrayList;
import model.Administration.User;
import model.Production.Products;
import utils.SessionManager;
import utils.ValidationException;

/**
 *
 * @author duyng
 */
public class ProductService {
    private final ProductsDAO productDAO;
    private final SessionManager sessionManager; // Added SessionManager

    public ProductService() {
        this.productDAO = new ProductsDAO();
        this.sessionManager = SessionManager.getInstance(); // Initialize SessionManager
    }

    public ArrayList<Products> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public Products getProductById(int id) {
        return productDAO.getProductById(id);
    }

    public ArrayList<Products> getProductsByCategory(int categoryId) throws Exception {
        if (categoryId <= 0) {
            // Or throw an IllegalArgumentException, or return all products, depending on
            // desired behavior
            System.out.println(
                    "ProductService: Invalid Category ID received: " + categoryId + ". Returning all products.");
            return productDAO.getAllProducts();
        }
        return productDAO.getProductsByCategoryId(categoryId);
    }

    public ArrayList<Products> getProductsByBrand(int brandId) throws Exception {
        if (brandId <= 0) {
            // If brandId is invalid or 0 (representing "All Brands"), return all products.
            // This matches the behavior of getProductsByCategory.
            System.out.println(
                    "ProductService: Invalid or 'All Brands' Brand ID received: " + brandId
                            + ". Returning all products.");
            return productDAO.getAllProducts();
        }
        return productDAO.getProductsByBrandId(brandId);
    }

    public ArrayList<Products> searchProducts(String searchTerm) throws Exception {

        ArrayList<Products> allProducts = productDAO.searchProductsByName(searchTerm);

        // For now, returning all products as a fallback until DAO layer is implemented
        return allProducts;
    }

    public boolean addProduct(Products product) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("No user logged in. Access denied.");
        }
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            throw new SecurityException("Employees do not have permission to add products.");
        }

        validateProduct(product);
        // Add business logic validation here if needed
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            // This specific check is also in validateProduct, can be removed if redundant
            throw new ValidationException("Product name cannot be empty.");
        }
        if (product.getListPrice() < 0) {
            throw new ValidationException("Product list price cannot be negative.");
        }
        // Potentially check for duplicate product name, etc.
        return productDAO.addProduct(product);
    }

    public boolean updateProduct(Products product) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("No user logged in. Access denied.");
        }
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            throw new SecurityException("Employees do not have permission to update products.");
        }

        validateProduct(product);
        if (product.getProductID() <= 0) {
            throw new ValidationException("Product ID for update is invalid.");
        }
        // Add other business logic for update if needed
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(int id) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("No user logged in. Access denied.");
        }
        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER) {
            throw new SecurityException("Only Chief Managers have permission to delete products.");
        }

        if (id <= 0) {
            throw new ValidationException("Product ID for delete is invalid.");
        }
        // Add business logic to check if product can be deleted (e.g., not part of
        // active orders)
        return productDAO.deleteProduct(id);
    }

    private void validateProduct(Products product) throws ValidationException {
        if (product == null) {
            throw new ValidationException("Product object cannot be null.");
        }
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new ValidationException("Product name is required.");
        }
        if (product.getBrandID() <= 0) { // Assuming IDs are positive
            throw new ValidationException("Valid Brand ID is required.");
        }
        if (product.getCategoryID() <= 0) { // Assuming IDs are positive
            throw new ValidationException("Valid Category ID is required.");
        }
        if (product.getModelYear() <= 1800 || product.getModelYear() > java.time.Year.now().getValue() + 1) { // Basic
                                                                                                              // year
                                                                                                              // check
            throw new ValidationException("Invalid model year.");
        }
        if (product.getListPrice() < 0) {
            throw new ValidationException("List price cannot be negative.");
        }
    }
}