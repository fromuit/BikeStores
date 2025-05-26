/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Production.Products;
import service.ProductService;
import view.ProductManagementView;
import java.util.ArrayList;

/**
 *
 * @author duyng
 */
public class ProductController {
    private ProductService productService;
    private ProductManagementView view;
    
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
    
    public void addProduct(Products product) {
        try {
            if (productService.addProduct(product)) {
                view.showMessage("Product added successfully!");
                loadProducts(); // Refresh the table
            } else {
                view.showError("Failed to add product");
            }
        } catch (Exception e) {
            view.showError("Error adding product: " + e.getMessage());
        }
    }
    
    public void updateProduct(Products product) {
        try {
            if (productService.updateProduct(product)) {
                view.showMessage("Product updated successfully!");
                loadProducts(); // Refresh the table
            } else {
                view.showError("Failed to update product");
            }
        } catch (Exception e) {
            view.showError("Error updating product: " + e.getMessage());
        }
    }
    
    public void deleteProduct(int productId) {
        try {
            if (productService.deleteProduct(productId)) {
                view.showMessage("Product deleted successfully!");
                loadProducts(); // Refresh the table
            } else {
                view.showError("Failed to delete product");
            }
        } catch (Exception e) {
            view.showError("Error deleting product: " + e.getMessage());
        }
    }
}