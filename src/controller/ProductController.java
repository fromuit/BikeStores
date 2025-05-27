/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.ArrayList;
import model.Production.Products;
import service.ProductService;
import utils.ValidationException;
import view.ProductManagementView;

/**
 *
 * @author duyng
 */
public class ProductController {
    private final ProductService productService;
    private final ProductManagementView view;

    public ProductController(ProductManagementView view) {
        this.view = view;
        this.productService = new ProductService();
    }

    public void loadProducts() {
        try {
            ArrayList<Products> products = productService.getAllProducts();
            view.displayProducts(products);
        } catch (Exception e) {
            view.showError("Error loading products: " + e.getMessage());
        }
    }

    public void loadProductsByCategory(int categoryId) {
        try {
            ArrayList<Products> products = productService.getProductsByCategory(categoryId);
            view.displayProducts(products);
        } catch (Exception e) {
            view.showError("Error loading products by category: " + e.getMessage());
        }
    }

    public void loadProductsByBrand(int brandId) {
        try {
            ArrayList<Products> products = productService.getProductsByBrand(brandId);
            view.displayProducts(products);
        } catch (Exception e) {
            view.showError("Error loading products by brand: " + e.getMessage());
            System.err.println("Error in loadProductsByBrand: " + e.getMessage());
        }
    }

    public void searchProducts(String searchTerm) {
        try {
            ArrayList<Products> products = productService.searchProducts(searchTerm);
            view.displayProducts(products);
        } catch (Exception e) {
            view.showError("Error searching products: " + e.getMessage());
        }
    }

    public void addProduct(Products product) {
        try {
            if (productService.addProduct(product)) {
                view.showMessage("Product added successfully!");
                loadProducts(); // Refresh the table
            } else {
                view.showError("Failed to add product. Check input data and logs.");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error adding product: " + e.getMessage());
            System.err.println("Error in addProduct: " + e.getMessage());
        }
    }

    public void updateProduct(Products product) {
        try {
            if (productService.updateProduct(product)) {
                view.showMessage("Product updated successfully!");
                loadProducts(); // Refresh the table
            } else {
                view.showError("Failed to update product. Check input data and logs.");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error updating product: " + e.getMessage());
            System.err.println("Error in updateProduct: " + e.getMessage());
        }
    }

    public void deleteProduct(int productId) {
        try {
            if (productService.deleteProduct(productId)) {
                view.showMessage("Product deleted successfully!");
                loadProducts(); // Refresh the table
            } else {
                view.showError("Failed to delete product. It might be in use or not exist.");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error deleting product: " + e.getMessage());
            System.err.println("Error in deleteProduct: " + e.getMessage());
        }
    }
}