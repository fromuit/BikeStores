/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.ProductsDAO;
import java.util.ArrayList;
import model.Production.Products;


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
    
    public boolean addProduct(Products product) {
        // Add business logic validation here if needed
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            return false;
        }
        if (product.getListPrice() < 0) {
            return false;
        }
        return productDAO.addProduct(product);
    }
    
    public boolean updateProduct(Products product) {
        return productDAO.updateProduct(product);
    }
    
    public boolean deleteProduct(int id) {
        return productDAO.deleteProduct(id);
    }
}