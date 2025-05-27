/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.ProductsDAO;
import java.util.ArrayList;
import model.Production.Products;
import utils.ValidationException; // Assuming you have or will create this

/**
 *
 * @author duyng
 */
public class ProductService {
    private final ProductsDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductsDAO();
    }

    public ArrayList<Products> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public Products getProductById(int id) {
        return productDAO.getProductById(id);
    }

    public ArrayList<Products> getProductsByCategory(int categoryId) throws Exception {
        // TODO: Implement logic to fetch products by category from DAO
        // This might involve specific queries in ProductsDAO (e.g.,
        // productDAO.getProductsByCategoryId(categoryId))
        System.out.println("ProductService: getProductsByCategory called with category ID " + categoryId
                + ". Not implemented yet. Returning all products.");
        // For now, returning all products as a fallback until DAO layer is implemented
        return productDAO.getAllProducts();
    }

    public ArrayList<Products> searchProducts(String searchTerm) throws Exception {
        // TODO: Implement logic to search products in DAO (e.g., by name, description
        // using LIKE queries)
        // Example: return productDAO.searchProductsByName(searchTerm);
        
        ArrayList<Products>allProducts = productDAO.searchProductsByName(searchTerm);
        
        // For now, returning all products as a fallback until DAO layer is implemented
        return allProducts;
    }
    

    public boolean addProduct(Products product) throws ValidationException {
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

    public boolean updateProduct(Products product) throws ValidationException {
        validateProduct(product);
        if (product.getProductID() <= 0) {
            throw new ValidationException("Product ID for update is invalid.");
        }
        // Add other business logic for update if needed
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(int id) throws ValidationException {
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