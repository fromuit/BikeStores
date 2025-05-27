/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import model.Production.Products;
import java.sql.*;
import utils.DatabaseUtil;

/**
 *
 * @author duyng
 */
public class ProductsDAO {
    public ArrayList<Products> getAllProducts() {
        ArrayList<Products> products = new ArrayList<>();
        String query = "SELECT product_id, "
                + "product_name, brand_id, production.products.category_id, "
                + "model_year, list_price, category_name FROM production.products, "
                + "production.categories "
                + "WHERE production.products.brand_id =  production.categories. category_id";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Products product = new Products(rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getInt("model_year"),
                        rs.getDouble("list_price"),
                        rs.getString("category_name"));
                products.add(product);
            }
        } catch (SQLException e) {
        }

        return products;
    }

    public boolean addProduct(Products product) {
        String query = "INSERT INTO production.products (product_name, brand_id, category_id, model_year, list_price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getBrandID());
            pstmt.setInt(3, product.getCategoryID());
            pstmt.setInt(4, product.getModelYear());
            pstmt.setDouble(5, product.getListPrice());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateProduct(Products product) {
        String query = "UPDATE production.products SET product_name=?, brand_id=?, category_id=?, model_year=?, list_price=? WHERE product_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getBrandID());
            pstmt.setInt(3, product.getCategoryID());
            pstmt.setInt(4, product.getModelYear());
            pstmt.setDouble(5, product.getListPrice());
            pstmt.setInt(6, product.getProductID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM production.products WHERE product_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Products getProductById(int productId) {
        String query = "SELECT * FROM production.products WHERE product_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Products(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getInt("model_year"),
                        rs.getDouble("list_price"));
            }
        } catch (SQLException e) {
            // Consider logging the exception e.g.,
            // Logger.getLogger(ProductsDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    // Method to search products by name, returns a list of products
    public ArrayList<Products> searchProductsByName(String searchName) {
        ArrayList<Products> productsList = new ArrayList<>();
        // Using LIKE for partial matching, case-insensitive search might depend on DB
        // collation
        // For standard SQL, you can use LOWER() or UPPER() on both sides for
        // case-insensitivity
        String query = "SELECT * FROM production.products WHERE LOWER(product_name) LIKE LOWER(?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + searchName + "%"); // Add wildcards for partial search
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Products product = new Products(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getInt("model_year"),
                        rs.getDouble("list_price"));
                productsList.add(product);
            }
        } catch (SQLException e) {
            // Consider logging the exception properly
            // e.printStackTrace(); // For debugging, but prefer logging in production
            System.err.println("SQL Exception in searchProductsByName: " + e.getMessage());
        }
        return productsList;
    }
    

}
