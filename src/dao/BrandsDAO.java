/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.interfaces.IBrandsDAO;
import java.sql.*;
import java.util.ArrayList;
import model.Production.Brands;
import utils.DatabaseUtil;

/**
 * Brands Data Access Object Implementation
 */
public class BrandsDAO implements IBrandsDAO {
    
    @Override
    public boolean insert(Brands brand) {
        return addBrand(brand);
    }
    
    @Override
    public boolean update(Brands brand) {
        return updateBrand(brand);
    }
    
    @Override
    public boolean delete(Integer brandId) {
        return deleteBrand(brandId);
    }
    
    @Override
    public ArrayList<Brands> selectAll() {
        return getAllBrands();
    }
    
    @Override
    public Brands selectById(Integer brandId) {
        return getBrandById(brandId);
    }
    
    @Override
    public ArrayList<Brands> search(String searchTerm) {
        ArrayList<Brands> brands = new ArrayList<>();
        String query = "SELECT * FROM production.brands WHERE LOWER(brand_name) LIKE LOWER(?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                brands.add(mapResultSetToBrand(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching brands: " + e.getMessage());
        }
        return brands;
    }
    
    public boolean addBrand(Brands brand) {
        String query = "INSERT INTO production.brands (brand_name) VALUES (?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, brand.getBrandName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding brand: " + e.getMessage());
            return false;
        }
    }
    
    public ArrayList<Brands> getAllBrands() {
        ArrayList<Brands> brands = new ArrayList<>();
        String query = "SELECT * FROM production.brands";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                brands.add(mapResultSetToBrand(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all brands: " + e.getMessage());
        }
        return brands;
    }
    
    public Brands getBrandById(int brandId) {
        String query = "SELECT * FROM production.brands WHERE brand_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, brandId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBrand(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting brand by ID: " + e.getMessage());
        }
        return null;
    }
    
    public boolean updateBrand(Brands brand) {
        String query = "UPDATE production.brands SET brand_name=? WHERE brand_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, brand.getBrandName());
            pstmt.setInt(2, brand.getBrandID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating brand: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBrand(int brandId) {
        String query = "DELETE FROM production.brands WHERE brand_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, brandId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting brand: " + e.getMessage());
            return false;
        }
    }
    
    private Brands mapResultSetToBrand(ResultSet rs) throws SQLException {
        return new Brands(
            rs.getInt("brand_id"),
            rs.getString("brand_name")
        );
    }
}