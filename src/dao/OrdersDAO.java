/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import model.Sales.Orders;
import utils.DatabaseUtil;

/**
 *
 * @author duyng
 */
public class OrdersDAO {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Create - Add new order
    public int addOrder(Orders order) {
        String query = "INSERT INTO sales.orders (customer_id, order_status, order_date, required_date, shipped_date, store_id, staff_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, order.getCustID());
            pstmt.setInt(2, order.getOrderStatus());
            pstmt.setTimestamp(3, order.getOrderDate());
            pstmt.setTimestamp(4, order.getRequiredDate());
            pstmt.setTimestamp(5, order.getShippedDate());
            pstmt.setInt(6, order.getStoreID());
            pstmt.setInt(7, order.getStaffID());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
            return -1;
        }
    }

    // Read - Get all orders
    public ArrayList<Orders> getAllOrders() {
        ArrayList<Orders> orders = new ArrayList<>();
        String query = "SELECT * FROM sales.orders ORDER BY order_date DESC";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        return orders;
    }

    // Read - Get order by ID
    public Orders getOrderById(int orderId) {
        String query = "SELECT * FROM sales.orders WHERE order_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
        }
        return null;
    }

    // Update - Update existing order
    public boolean updateOrder(Orders order) {
        String query = "UPDATE sales.orders SET customer_id=?, order_status=?, order_date=?, required_date=?, shipped_date=?, store_id=?, staff_id=? WHERE order_id=?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, order.getCustID());
            pstmt.setInt(2, order.getOrderStatus());
            pstmt.setTimestamp(3, order.getOrderDate());
            pstmt.setTimestamp(4, order.getRequiredDate());
            pstmt.setTimestamp(5, order.getShippedDate());
            pstmt.setInt(6, order.getStoreID());
            pstmt.setInt(7, order.getStaffID());
            pstmt.setInt(8, order.getOrderID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            return false;
        }
    }

    // Delete - Delete order
    public boolean deleteOrder(int orderId) {
        String query = "DELETE FROM sales.orders WHERE order_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            return false;
        }
    }

    // Search orders
    public ArrayList<Orders> searchOrders(String searchTerm) {
        ArrayList<Orders> orders = new ArrayList<>();
        String query = "SELECT o.* FROM sales.orders o " +
                "LEFT JOIN sales.customers c ON o.customer_id = c.customer_id " +
                "LEFT JOIN sales.stores s ON o.store_id = s.store_id " +
                "LEFT JOIN sales.staffs st ON o.staff_id = st.staff_id " +
                "WHERE CAST(o.order_id AS VARCHAR) LIKE ? OR " +
                "LOWER(c.first_name) LIKE LOWER(?) OR " +
                "LOWER(c.last_name) LIKE LOWER(?) OR " +
                "LOWER(s.store_name) LIKE LOWER(?) OR " +
                "LOWER(st.first_name) LIKE LOWER(?) OR " +
                "LOWER(st.last_name) LIKE LOWER(?) " +
                "ORDER BY o.order_date DESC";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 6; i++) {
                pstmt.setString(i, searchPattern);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching orders: " + e.getMessage());
        }
        return orders;
    }

    // Get orders by status
    public ArrayList<Orders> getOrdersByStatus(int status) {
        ArrayList<Orders> orders = new ArrayList<>();
        String query = "SELECT * FROM sales.orders WHERE order_status = ? ORDER BY order_date DESC";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by status: " + e.getMessage());
        }
        return orders;
    }

    // Get orders by customer
    public ArrayList<Orders> getOrdersByCustomer(int customerId) {
        ArrayList<Orders> orders = new ArrayList<>();
        String query = "SELECT * FROM sales.orders WHERE customer_id = ? ORDER BY order_date DESC";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by customer: " + e.getMessage());
        }
        return orders;
    }

    // Get orders by store
    public ArrayList<Orders> getOrdersByStore(int storeId) {
        ArrayList<Orders> orders = new ArrayList<>();
        String query = "SELECT * FROM sales.orders WHERE store_id = ? ORDER BY order_date DESC";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by store: " + e.getMessage());
        }
        return orders;
    }

    // Get orders by staff
    public ArrayList<Orders> getOrdersByStaff(int staffId) {
        ArrayList<Orders> orders = new ArrayList<>();
        String query = "SELECT * FROM sales.orders WHERE staff_id = ? ORDER BY order_date DESC";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by staff: " + e.getMessage());
        }
        return orders;
    }

    // Check if order has items
    public boolean hasOrderItems(int orderId) {
        String query = "SELECT COUNT(*) FROM sales.order_items WHERE order_id = ?";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking order items: " + e.getMessage());
        }
        return false;
    }

    // Get order status name
    public String getOrderStatusName(int status) {
        String statusName;
        switch (status) {
            case 1:
                statusName = "Pending";
                break;
            case 2:
                statusName = "Processing";
                break;
            case 3:
                statusName = "Rejected";
                break;
            case 4:
                statusName = "Completed";
                break;
            default:
                statusName = "Unknown";
                break;
        }
        return statusName;
    }

    // Get orders within date range
    public ArrayList<Orders> getOrdersByDateRange(Timestamp startDate, Timestamp endDate) {
        ArrayList<Orders> orders = new ArrayList<>();
        String query = "SELECT * FROM sales.orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC";
        try (PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by date range: " + e.getMessage());
        }
        return orders;
    }

    private Orders mapResultSetToOrder(ResultSet rs) throws SQLException {
        Orders order = new Orders(
                rs.getInt("order_id"),
                rs.getInt("order_status"),
                rs.getTimestamp("order_date"),
                rs.getTimestamp("required_date"),
                rs.getInt("store_id"),
                rs.getInt("staff_id"));
        order.setCustID(rs.getInt("customer_id"));
        order.setShippedDate(rs.getTimestamp("shipped_date"));
        return order;
    }
}
