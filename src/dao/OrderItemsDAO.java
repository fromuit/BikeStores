/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import model.Sales.OrderItems;
import utils.DatabaseUtil;

/**
 *
 * @author duyng
 */
public class OrderItemsDAO {

    // Get all order items for a specific order with product details
    public ArrayList<OrderItems> getOrderItemsByOrderId(int orderId) {
        ArrayList<OrderItems> orderItems = new ArrayList<>();
        String query = "SELECT oi.*, p.product_name, p.model_year, b.brand_name, c.category_name " +
                "FROM sales.order_items oi " +
                "JOIN production.products p ON oi.product_id = p.product_id " +
                "JOIN production.brands b ON p.brand_id = b.brand_id " +
                "JOIN production.categories c ON p.category_id = c.category_id " +
                "WHERE oi.order_id = ? " +
                "ORDER BY oi.item_id";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItems orderItem = mapResultSetToOrderItem(rs);
                // Store additional product info in a way we can access it
                orderItem.setProductName(rs.getString("product_name"));
                orderItem.setBrandName(rs.getString("brand_name"));
                orderItem.setCategoryName(rs.getString("category_name"));
                orderItem.setModelYear(rs.getInt("model_year"));
                orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items: " + e.getMessage());
        }
        return orderItems;
    }

    // Calculate order total
    public double calculateOrderTotal(int orderId) {
        String query = "SELECT SUM(quantity * list_price * (1 - discount)) as total FROM sales.order_items WHERE order_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating order total: " + e.getMessage());
        }
        return 0.0;
    }

    // Get order item count
    public int getOrderItemCount(int orderId) {
        String query = "SELECT COUNT(*) FROM sales.order_items WHERE order_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order item count: " + e.getMessage());
        }
        return 0;
    }

    public int getNextItemId(int orderId) {
        String query = "SELECT MAX(item_id) FROM sales.order_items WHERE order_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1; // First item
        } catch (SQLException e) {
            System.err.println("Error getting next item ID: " + e.getMessage());
            return 1;
        }
    }

    private OrderItems mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItems orderItem = new OrderItems();
        orderItem.setOrderID(rs.getInt("order_id"));
        orderItem.setItemID(rs.getInt("item_id"));
        orderItem.setProductID(rs.getInt("product_id"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setListPrice(rs.getDouble("list_price"));
        orderItem.setDiscount(rs.getDouble("discount"));
        return orderItem;
    }

    public boolean addOrderItem(OrderItems item) {
        String query = "INSERT INTO sales.order_items (order_id, item_id, product_id, quantity, list_price, discount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, item.getOrderID());
            pstmt.setInt(2, item.getItemID());
            pstmt.setInt(3, item.getProductID());
            pstmt.setInt(4, item.getQuantity());
            pstmt.setDouble(5, item.getListPrice());
            pstmt.setDouble(6, item.getDiscount());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding order item: " + e.getMessage());
            return false;
        }
    }
}