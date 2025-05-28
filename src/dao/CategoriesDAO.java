/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.interfaces.ICategoriesDAO;
import java.sql.*;
import java.util.ArrayList;
import model.Production.Categories;
import utils.DatabaseUtil;

/**
 * Categories Data Access Object Implementation
 */
public class CategoriesDAO implements ICategoriesDAO {
    
    @Override
    public boolean insert(Categories category) {
        return addCategory(category);
    }
    
    @Override
    public boolean update(Categories category) {
        return updateCategory(category);
    }
    
    @Override
    public boolean delete(Integer categoryId) {
        return deleteCategory(categoryId);
    }
    
    @Override
    public ArrayList<Categories> selectAll() {
        return getAllCategories();
    }
    
    @Override
    public Categories selectById(Integer categoryId) {
        return getCategoryById(categoryId);
    }
    
    @Override
    public ArrayList<Categories> search(String searchTerm) {
        ArrayList<Categories> categories = new ArrayList<>();
        String query = "SELECT * FROM production.categories WHERE LOWER(category_name) LIKE LOWER(?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching categories: " + e.getMessage());
        }
        return categories;
    }
    
    public boolean addCategory(Categories category) {
        String query = "INSERT INTO production.categories (category_name) VALUES (?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            return false;
        }
    }
    
    public ArrayList<Categories> getAllCategories() {
        ArrayList<Categories> categories = new ArrayList<>();
        String query = "SELECT * FROM production.categories";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all categories: " + e.getMessage());
        }
        return categories;
    }
    
    public Categories getCategoryById(int categoryId) {
        String query = "SELECT * FROM production.categories WHERE category_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting category by ID: " + e.getMessage());
        }
        return null;
    }
    
    public boolean updateCategory(Categories category) {
        String query = "UPDATE production.categories SET category_name=? WHERE category_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getCategoryID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteCategory(int categoryId) {
        String query = "DELETE FROM production.categories WHERE category_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, categoryId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
    
    private Categories mapResultSetToCategory(ResultSet rs) throws SQLException {
        return new Categories(
            rs.getInt("category_id"),
            rs.getString("category_name")
        );
    }
}
