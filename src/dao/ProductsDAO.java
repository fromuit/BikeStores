/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.interfaces.IProductsDAO;
import java.sql.*;
import java.util.ArrayList;
import model.Production.Products;
import utils.DatabaseUtil;

/**
 *
 * @author duyng
 */
public class ProductsDAO implements IProductsDAO {
    
    // ========== BaseDAO Implementation ==========
    
    @Override
    public boolean insert(Products product) {
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

    @Override
    public boolean update(Products product) {
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

    @Override
    public boolean delete(Integer productId) {
        String query = "DELETE FROM production.products WHERE product_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public ArrayList<Products> selectAll() {
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
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public Products selectById(Integer productId) {
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
                        rs.getString("category_name"),
                        rs.getString("brand_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Products> search(String searchTerm) {
        ArrayList<Products> productsList = new ArrayList<>();
        String query = "SELECT product_id, product_name, production.products.brand_id, production.products.category_id, "
                + "model_year, list_price, category_name, brand_name "
                + "FROM production.products, production.categories, production.brands  "
                + "WHERE production.products.category_id =  production.categories. category_id "
                + "AND production.products.brand_id = production.brands.brand_id AND LOWER(product_name) LIKE LOWER(?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + searchTerm + "%");
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
            System.err.println("SQL Exception in search: " + e.getMessage());
        }
        return productsList;
    }

    // ========== IProductsDAO Specific Methods ==========

    @Override
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
        }
        return productsList;
    }

    @Override
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
        }
        return productsList;
    }

    @Override
    public ArrayList<Products> getAllProductsBasicInfo() {
        ArrayList<Products> products = new ArrayList<>();
        String query = "SELECT product_id, product_name, list_price FROM production.products ORDER BY product_name ASC";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Products product = new Products();
                product.setProductID(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setListPrice(rs.getDouble("list_price"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching basic product info: " + e.getMessage());
        }
        return products;
    }

    // ========== Legacy Methods (để backward compatibility) ==========
    
    /**
     * @deprecated Use selectAll() instead
     */
    @Deprecated
    public ArrayList<Products> getAllProducts() {
        return selectAll();
    }

    /**
     * @deprecated Use insert() instead
     */
    @Deprecated
    public boolean addProduct(Products product) {
        return insert(product);
    }

    /**
     * @deprecated Use update() instead
     */
    @Deprecated
    public boolean updateProduct(Products product) {
        return update(product);
    }

    /**
     * @deprecated Use delete() instead
     */
    @Deprecated
    public boolean deleteProduct(int productId) {
        return delete(productId);
    }

    /**
     * @deprecated Use selectById() instead
     */
    @Deprecated
    public Products getProductById(int productId) {
        return selectById(productId);
    }

    /**
     * @deprecated Use search() instead
     */
    @Deprecated
    public ArrayList<Products> searchProductsByName(String searchName) {
        return search(searchName);
    }
}
