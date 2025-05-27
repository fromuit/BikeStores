/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import model.Production.Products;
import utils.DatabaseUtil;

/**
 *
 * @author duyng
 */
public class ProductsDAO {
    public ArrayList<Products> getAllProducts() {
        ArrayList<Products> products = new ArrayList<>();
        String query = "SELECT product_id, product_name, production.products.brand_id, production.products.category_id, "
                + "model_year, list_price, category_name, brand_name FROM production.products, "
                + "production.categories, production.brands  "
                + "WHERE production.products.category_id =  production.categories. category_id "
                + "AND production.products.brand_id = production.brands.brand_id";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Products product = new Products(rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getInt("model_year"),
                        rs.getDouble("list_price"),
                        rs.getString("category_name"),
                        rs.getString("brand_name"));
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
        String query = "SELECT product_id, product_name, production.products.brand_id, "
                + "production.products.category_id, model_year, list_price, category_name, "
                + "brand_name FROM production.products, production.categories, production.brands  "
                + "WHERE production.products.category_id =  production.categories. category_id "
                + "AND production.products.brand_id = production.brands.brand_id AND product_id = ?";
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
                        rs.getDouble("list_price"),
                        rs.getString("category_id"),
                        rs.getString("brand_name"));
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
        String query = "SELECT product_id, product_name, production.products.brand_id, production.products.category_id, "
                + "model_year, list_price, category_name, brand_name "
                + "FROM production.products, production.categories, production.brands  "
                + "WHERE production.products.category_id =  production.categories. category_id "
                + "AND production.products.brand_id = production.brands.brand_id AND LOWER(product_name) LIKE LOWER(?)";

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
                        rs.getDouble("list_price"),
                        rs.getString("category_name"),
                        rs.getString("brand_name"));
                productsList.add(product);
            }
        } catch (SQLException e) {
            // Consider logging the exception properly
            // e.printStackTrace(); // For debugging, but prefer logging in production
            System.err.println("SQL Exception in searchProductsByName: " + e.getMessage());
        }
        return productsList;
    }

    public ArrayList<Products> getProductsByCategoryId(int categoryId) {
        ArrayList<Products> productsList = new ArrayList<>();
        String query = "SELECT product_id, product_name, production.products.brand_id, production.products.category_id, "
                + "model_year, list_price, category_name, brand_name "
                + "FROM production.products, production.categories, production.brands  "
                + "WHERE production.products.category_id =  production.categories. category_id "
                + "AND production.products.brand_id = production.brands.brand_id AND production.categories.category_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Products product = new Products(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getInt("model_year"),
                        rs.getDouble("list_price"),
                        rs.getString("category_name"),
                        rs.getString("brand_name"));
                productsList.add(product);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in getProductsByCategoryId: " + e.getMessage());
            // Consider logging the exception properly
        }
        return productsList;
    }

    public ArrayList<Products> getProductsByBrandId(int brandId) {
        ArrayList<Products> productsList = new ArrayList<>();
        String query = "SELECT product_id, product_name, production.products.brand_id, production.products.category_id, "
                + "model_year, list_price, category_name, brand_name "
                + "FROM production.products "
                + "JOIN production.categories ON production.products.category_id = production.categories.category_id "
                + "JOIN production.brands ON production.products.brand_id = production.brands.brand_id "
                + "WHERE production.products.brand_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, brandId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Products product = new Products(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getInt("model_year"),
                        rs.getDouble("list_price"),
                        rs.getString("category_name"),
                        rs.getString("brand_name"));
                productsList.add(product);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in getProductsByBrandId: " + e.getMessage());
            // Consider logging the exception properly
        }
        return productsList;
    }

    // Get basic product info for selection dialogs
    public ArrayList<Products> getAllProductsBasicInfo() {
        ArrayList<Products> products = new ArrayList<>();
        // Only select necessary fields to minimize data transfer and improve
        // performance
        String query = "SELECT product_id, product_name, list_price FROM production.products ORDER BY product_name ASC";
        try (Connection conn = DatabaseUtil.getConnection(); // Ensure connection is handled correctly
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Products product = new Products(); // Assuming Products has a default constructor and setters
                product.setProductID(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setListPrice(rs.getDouble("list_price"));
                // Do not set brand_id, category_id, model_year here as they are not in the
                // SELECT
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching basic product info: " + e.getMessage());
            // Optionally, rethrow as a custom exception or return null/empty list based on
            // error handling strategy
        }
        return products;
    }
}
