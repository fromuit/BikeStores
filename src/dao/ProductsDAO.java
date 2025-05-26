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
        String query = "SELECT * FROM production.products"; 
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Products product = new Products(rs.getInt("product_id"),
                                                                         rs.getString("product_name"),
                                                                         rs.getInt("brand_id"),
                                                                         rs.getInt("category_id"),
                                                                         rs.getInt("model_year"),
                                                                         rs.getDouble("list_price"));
                products.add(product);
            }
        } catch (SQLException e) {}
       
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
                    rs.getDouble("list_price")
                );
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
}
