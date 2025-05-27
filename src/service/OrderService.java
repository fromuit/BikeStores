/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.CustomersDAO;
import dao.OrdersDAO;
import dao.StaffsDAO;
import dao.StoresDAO;
import dao.OrderItemsDAO;
import java.sql.Timestamp;
import java.util.ArrayList;
import model.Administration.User;
import model.Sales.Orders;
import model.Sales.OrderItems;
import utils.SessionManager;
import utils.ValidationException;

/**
 *
 * @author duyng
 */
public class OrderService {
    private final OrdersDAO orderDAO;
    private final CustomersDAO customerDAO;
    private final StoresDAO storeDAO;
    private final StaffsDAO staffDAO;
    private final SessionManager sessionManager;
    private final OrderItemsDAO orderItemsDAO;

    public OrderService() {
        this.orderDAO = new OrdersDAO();
        this.customerDAO = new CustomersDAO();
        this.storeDAO = new StoresDAO();
        this.staffDAO = new StaffsDAO();
        this.sessionManager = SessionManager.getInstance();
        this.orderItemsDAO = new OrderItemsDAO();
    }

    public ArrayList<Orders> getAllOrders() {
        ArrayList<Orders> allOrders = orderDAO.getAllOrders();
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        // Filter orders based on user permissions
        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }

        return accessibleOrders;
    }

    public Orders getOrderById(int id) {
        Orders order = orderDAO.getOrderById(id);
        if (order != null && sessionManager.canViewOrder(order)) {
            return order;
        }
        return null; // Return null if user doesn't have permission to view this order
    }

    public ArrayList<Orders> searchOrders(String searchTerm) {
        ArrayList<Orders> allOrders = orderDAO.searchOrders(searchTerm);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        // Filter search results based on user permissions
        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }

        return accessibleOrders;
    }

    public ArrayList<Orders> getOrdersByStatus(int status) {
        ArrayList<Orders> allOrders = orderDAO.getOrdersByStatus(status);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        // Filter orders based on user permissions
        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }

        return accessibleOrders;
    }

    public ArrayList<Orders> getOrdersByCustomer(int customerId) {
        ArrayList<Orders> allOrders = orderDAO.getOrdersByCustomer(customerId);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        // Filter orders based on user permissions
        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }

        return accessibleOrders;
    }

    public ArrayList<Orders> getOrdersByStore(int storeId) {
        // Check if user can access this store
        if (!sessionManager.canAccessStore(storeId)) {
            return new ArrayList<>(); // Return empty list if no access
        }

        ArrayList<Orders> allOrders = orderDAO.getOrdersByStore(storeId);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        // Filter orders based on user permissions
        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }

        return accessibleOrders;
    }

    public ArrayList<Orders> getOrdersByStaff(int staffId) {
        ArrayList<Orders> allOrders = orderDAO.getOrdersByStaff(staffId);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        // Filter orders based on user permissions
        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }

        return accessibleOrders;
    }

    public ArrayList<Orders> getOrdersByDateRange(Timestamp startDate, Timestamp endDate) {
        ArrayList<Orders> allOrders = orderDAO.getOrdersByDateRange(startDate, endDate);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        // Filter orders based on user permissions
        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }

        return accessibleOrders;
    }

    public int addOrder(Orders order) throws ValidationException {
        validateOrder(order);
        validateBusinessRules(order);

        if (!sessionManager.canAddOrder(order)) {
            throw new SecurityException("You don't have permission to add orders to this store");
        }

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            if (order.getStaffID() != currentUser.getStaffID()) {
                throw new SecurityException("Employees can only create orders assigned to themselves");
            }
        }

        return orderDAO.addOrder(order);
    }

    public boolean updateOrder(Orders order) throws ValidationException {
        validateOrder(order);
        validateBusinessRules(order);

        // Get existing order to check permissions
        Orders existingOrder = orderDAO.getOrderById(order.getOrderID());
        if (existingOrder == null) {
            throw new ValidationException("Order not found");
        }

        // Check if user can update this specific order
        if (!sessionManager.canUpdateOrder(existingOrder)) {
            throw new SecurityException("You don't have permission to update this order");
        }

        // Business rule: Cannot modify completed orders
        if (existingOrder.getOrderStatus() == 4) {
            throw new ValidationException("Cannot modify completed orders");
        }

        // For employees, ensure they can only update orders assigned to themselves
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            if (order.getStaffID() != currentUser.getStaffID()) {
                throw new SecurityException("Employees can only update orders assigned to themselves");
            }
        }

        return orderDAO.updateOrder(order);
    }

    public boolean deleteOrder(int orderId) throws ValidationException {
        Orders order = orderDAO.getOrderById(orderId);
        if (order == null) {
            throw new ValidationException("Order not found");
        }

        // Check if user can delete this specific order
        if (!sessionManager.canDeleteOrder(order)) {
            throw new SecurityException("You don't have permission to delete this order");
        }

        // Check if order has items
        if (orderDAO.hasOrderItems(orderId)) {
            throw new ValidationException(
                    "Cannot delete order with existing order items. Please remove all items first.");
        }

        // Business rule: Cannot delete completed orders
        if (order.getOrderStatus() == 4) {
            throw new ValidationException("Cannot delete completed orders");
        }

        return orderDAO.deleteOrder(orderId);
    }

    public String getOrderStatusName(int status) {
        return orderDAO.getOrderStatusName(status);
    }

    private void validateOrder(Orders order) throws ValidationException {
        if (order.getCustID() <= 0) {
            throw new ValidationException("Customer ID is required");
        }
        if (order.getOrderDate() == null) {
            throw new ValidationException("Order date is required");
        }
        if (order.getRequiredDate() == null) {
            throw new ValidationException("Required date is required");
        }
        if (order.getStoreID() <= 0) {
            throw new ValidationException("Store ID is required");
        }
        if (order.getStaffID() <= 0) {
            throw new ValidationException("Staff ID is required");
        }
        if (order.getOrderStatus() < 1 || order.getOrderStatus() > 4) {
            throw new ValidationException(
                    "Invalid order status. Must be 1-4 (Pending, Processing, Rejected, Completed)");
        }
    }

    private void validateBusinessRules(Orders order) throws ValidationException {
        // Validate customer exists
        if (customerDAO.getCustomerById(order.getCustID()) == null) {
            throw new ValidationException("Invalid customer ID. Customer does not exist.");
        }

        // Validate store exists (assuming StoresDAO has getStoreById method)
        // if (storeDAO.getStoreById(order.getStoreID()) == null) {
        // throw new ValidationException("Invalid store ID. Store does not exist.");
        // }

        // Validate staff exists
        if (staffDAO.getStaffById(order.getStaffID()) == null) {
            throw new ValidationException("Invalid staff ID. Staff does not exist.");
        }

        // Validate dates
        if (order.getRequiredDate().before(order.getOrderDate())) {
            throw new ValidationException("Required date cannot be before order date");
        }

        if (order.getShippedDate() != null) {
            if (order.getShippedDate().before(order.getOrderDate())) {
                throw new ValidationException("Shipped date cannot be before order date");
            }

            // If shipped, order should be completed
            if (order.getOrderStatus() != 4) {
                throw new ValidationException("Order must be marked as completed when shipped date is provided");
            }
        }

        // If order is completed, it should have a shipped date
        if (order.getOrderStatus() == 4 && order.getShippedDate() == null) {
            throw new ValidationException("Completed orders must have a shipped date");
        }
    }

    public boolean addOrderItemToOrder(OrderItems item) throws ValidationException {
        // Basic validation, more can be added (e.g., check product existence, stock)
        if (item.getProductID() <= 0 || item.getQuantity() <= 0 || item.getListPrice() < 0) {
            throw new ValidationException("Invalid order item data.");
        }
        return orderItemsDAO.addOrderItem(item);
    }

    public ArrayList<OrderItems> getOrderItems(int orderId) {
        return orderItemsDAO.getOrderItemsByOrderId(orderId);
    }

    public int getNextItemIdForOrder(int orderId) {
        return orderItemsDAO.getNextItemId(orderId);
    }
}
