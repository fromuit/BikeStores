/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.Production.Brands;
import utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class BrandsDAO {
    
    // Create - Add new brand
    public boolean addBrand(Brands brand) {
        String query = "INSERT INTO production.brands (brand_name) VALUES (?)";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, brand.getBrandName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Read - Get all brands
    public ArrayList<Brands> getAllBrands() {
        ArrayList<Brands> brands = new ArrayList<>();
        String query = "SELECT * FROM production.brands";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                brands.add(mapResultSetToBrand(rs));
            }
        } catch (SQLException e) {
        }
        return brands;
    }
    
    // Read - Get brand by ID
    public Brands getBrandById(int brandId) {
        String query = "SELECT * FROM production.brands WHERE brand_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, brandId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBrand(rs);
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    // Update - Update existing brand
    public boolean updateBrand(Brands brand) {
        String query = "UPDATE production.brands SET brand_name=? WHERE brand_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setString(1, brand.getBrandName());
            pstmt.setInt(2, brand.getBrandID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Delete - Delete brand
    public boolean deleteBrand(int brandId) {
        String query = "DELETE FROM production.brands WHERE brand_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, brandId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Helper method to map ResultSet to Brand object
    private Brands mapResultSetToBrand(ResultSet rs) throws SQLException {
        return new Brands(
            rs.getInt("brand_id"),
            rs.getString("brand_name")
        );
    }
}