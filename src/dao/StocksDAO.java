/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Production.Stocks;
import model.Production.Products; // For joining and displaying product name
import model.Sales.Stores; // For joining and displaying store name
import utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author duyng
 */
public class StocksDAO {

    // Get stock quantity for a specific product at a specific store
    public Stocks getStockByStoreAndProduct(int storeId, int productId) {
        String query = "SELECT s.store_id, s.product_id, s.quantity, " +
                "st.store_name, p.product_name, p.list_price " +
                "FROM production.stocks s " +
                "JOIN sales.stores st ON s.store_id = st.store_id " +
                "JOIN production.products p ON s.product_id = p.product_id " +
                "WHERE s.store_id = ? AND s.product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, storeId);
            pstmt.setInt(2, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStockWithDetails(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting stock by store and product: " + e.getMessage());
        }
        return null; // Or return new Stocks(storeId, productId, 0) if you prefer to indicate 0 stock
                     // vs no record
    }

    // Get all stock items for a specific store, including product details
    public ArrayList<Stocks> getStocksByStore(int storeId) {
        ArrayList<Stocks> stocks = new ArrayList<>();
        String query = "SELECT s.store_id, s.product_id, s.quantity, " +
                "st.store_name, p.product_name, p.list_price " +
                "FROM production.stocks s " +
                "JOIN sales.stores st ON s.store_id = st.store_id " +
                "JOIN production.products p ON s.product_id = p.product_id " +
                "WHERE s.store_id = ? ORDER BY p.product_name";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stocks.add(mapResultSetToStockWithDetails(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting stocks by store: " + e.getMessage());
        }
        return stocks;
    }

    // Get all stock items for a specific product across all stores
    public ArrayList<Stocks> getStocksByProduct(int productId) {
        ArrayList<Stocks> stocks = new ArrayList<>();
        String query = "SELECT s.store_id, s.product_id, s.quantity, " +
                "st.store_name, p.product_name, p.list_price " +
                "FROM production.stocks s " +
                "JOIN sales.stores st ON s.store_id = st.store_id " +
                "JOIN production.products p ON s.product_id = p.product_id " +
                "WHERE s.product_id = ? ORDER BY st.store_name";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stocks.add(mapResultSetToStockWithDetails(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting stocks by product: " + e.getMessage());
        }
        return stocks;
    }

    // Update stock quantity for a product at a store.
    // This method can also be used to add a new stock record if quantity > 0 and
    // record doesn't exist (UPSERT logic).
    public boolean updateStockQuantity(int storeId, int productId, int quantity) {
        // Check if the stock record exists
        Stocks existingStock = getStockByStoreAndProduct(storeId, productId);

        String query;
        if (existingStock != null) {
            // Record exists, update it
            query = "UPDATE production.stocks SET quantity = ? WHERE store_id = ? AND product_id = ?";
        } else {
            // Record does not exist, insert it (only if quantity > 0, or handle as per
            // business logic)
            // If quantity is 0 for a new record, we might not want to insert it.
            if (quantity <= 0) {
                // System.out.println("Skipping insert for zero or negative quantity on new
                // stock record.");
                return true; // Or false, depending on whether this is considered an error or a no-op
            }
            query = "INSERT INTO production.stocks (store_id, product_id, quantity) VALUES (?, ?, ?)";
        }

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (existingStock != null) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, storeId);
                pstmt.setInt(3, productId);
            } else {
                pstmt.setInt(1, storeId);
                pstmt.setInt(2, productId);
                pstmt.setInt(3, quantity);
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock quantity: " + e.getMessage());
            // Specific error for primary key violation if trying to insert a duplicate
            // (though the check above should prevent this specific scenario if
            // getStockByStoreAndProduct is accurate)
            if (e.getSQLState().startsWith("23")) {
                System.err
                        .println("Potential duplicate stock record or other integrity constraint violation. SQLState: "
                                + e.getSQLState());
            }
            return false;
        }
    }

    // Helper method to map ResultSet to a Stocks object with store and product
    // details filled
    private Stocks mapResultSetToStockWithDetails(ResultSet rs) throws SQLException {
        Stores store = new Stores();
        store.setStoreID(rs.getInt("store_id"));
        store.setStoreName(rs.getString("store_name"));

        Products product = new Products();
        product.setProductID(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setListPrice(rs.getDouble("list_price")); // Assuming list_price is in products table

        Stocks stockItem = new Stocks();
        stockItem.setStore(store); // Set the full store object
        stockItem.setProduct(product); // Set the full product object
        stockItem.setStoreID(rs.getInt("store_id")); // also set IDs directly
        stockItem.setProductID(rs.getInt("product_id"));
        stockItem.setQuantity(rs.getInt("quantity"));
        return stockItem;
    }
}
