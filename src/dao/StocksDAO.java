/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.interfaces.IStocksDAO;
import java.sql.*;
import java.util.ArrayList; 
import model.Production.Products; 
import model.Production.Stocks;
import model.Sales.Stores;
import utils.DatabaseUtil;

/**
 * Stock Data Access Object Implementation
 */
public class StocksDAO implements IStocksDAO {

    @Override
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
        return null;
    }

    @Override
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

    @Override
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

    @Override
    public boolean updateStockQuantity(int storeId, int productId, int quantity) {
        Stocks existingStock = getStockByStoreAndProduct(storeId, productId);

        String query;
        if (existingStock != null) {
            query = "UPDATE production.stocks SET quantity = ? WHERE store_id = ? AND product_id = ?";
        } else {
            if (quantity <= 0) {
                return true;
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
            if (e.getSQLState().startsWith("23")) {
                System.err.println("Potential duplicate stock record or other integrity constraint violation. SQLState: " + e.getSQLState());
            }
            return false;
        }
    }

    private Stocks mapResultSetToStockWithDetails(ResultSet rs) throws SQLException {
        Stores store = new Stores();
        store.setStoreID(rs.getInt("store_id"));
        store.setStoreName(rs.getString("store_name"));

        Products product = new Products();
        product.setProductID(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setListPrice(rs.getDouble("list_price"));

        Stocks stockItem = new Stocks();
        stockItem.setStore(store);
        stockItem.setProduct(product);
        stockItem.setStoreID(rs.getInt("store_id"));
        stockItem.setProductID(rs.getInt("product_id"));
        stockItem.setQuantity(rs.getInt("quantity"));
        return stockItem;
    }
}
