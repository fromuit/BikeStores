/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.CustomersDAO;
import dao.OrdersDAO;
import dao.StaffsDAO;
import dao.StoresDAO;
import java.sql.Timestamp;
import java.util.ArrayList;
import model.Sales.Orders;
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
    
    public OrderService() {
        this.orderDAO = new OrdersDAO();
        this.customerDAO = new CustomersDAO();
        this.storeDAO = new StoresDAO();
        this.staffDAO = new StaffsDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public ArrayList<Orders> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    public Orders getOrderById(int id) {
        return orderDAO.getOrderById(id);
    }

    public ArrayList<Orders> searchOrders(String searchTerm) {
        return orderDAO.searchOrders(searchTerm);
    }
    
    public ArrayList<Orders> getOrdersByStatus(int status) {
        return orderDAO.getOrdersByStatus(status);
    }
    
    public ArrayList<Orders> getOrdersByCustomer(int customerId) {
        return orderDAO.getOrdersByCustomer(customerId);
    }
    
    public ArrayList<Orders> getOrdersByStore(int storeId) {
        return orderDAO.getOrdersByStore(storeId);
    }
    
    public ArrayList<Orders> getOrdersByStaff(int staffId) {
        return orderDAO.getOrdersByStaff(staffId);
    }
    
    public ArrayList<Orders> getOrdersByDateRange(Timestamp startDate, Timestamp endDate) {
        return orderDAO.getOrdersByDateRange(startDate, endDate);
    }
    
    public boolean addOrder(Orders order) throws ValidationException {
        validateOrder(order);
        validateBusinessRules(order);
        
        // Check permissions
        if (!sessionManager.hasPermission("MANAGE_ORDERS")) {
            throw new SecurityException("You don't have permission to add orders");
        }
        
        return orderDAO.addOrder(order);
    }
    
    public boolean updateOrder(Orders order) throws ValidationException {
        validateOrder(order);
        validateBusinessRules(order);
        
        // Check permissions
        if (!sessionManager.hasPermission("MANAGE_ORDERS")) {
            throw new SecurityException("You don't have permission to update orders");
        }
        
        // Get existing order to check permissions
        Orders existingOrder = orderDAO.getOrderById(order.getOrderID());
        if (existingOrder == null) {
            throw new ValidationException("Order not found");
        }
        
        // Business rule: Cannot modify completed orders
        if (existingOrder.getOrderStatus() == 4) {
            throw new ValidationException("Cannot modify completed orders");
        }
        
        return orderDAO.updateOrder(order);
    }
    
    public boolean deleteOrder(int orderId) throws ValidationException {
        Orders order = orderDAO.getOrderById(orderId);
        if (order == null) {
            throw new ValidationException("Order not found");
        }
        
        // Check permissions
        if (!sessionManager.hasPermission("MANAGE_ORDERS")) {
            throw new SecurityException("You don't have permission to delete orders");
        }
        
        // Check if order has items
        if (orderDAO.hasOrderItems(orderId)) {
            throw new ValidationException("Cannot delete order with existing order items. Please remove all items first.");
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
            throw new ValidationException("Invalid order status. Must be 1-4 (Pending, Processing, Rejected, Completed)");
        }
    }
    
    private void validateBusinessRules(Orders order) throws ValidationException {
        // Validate customer exists
        if (customerDAO.getCustomerById(order.getCustID()) == null) {
            throw new ValidationException("Invalid customer ID. Customer does not exist.");
        }
        
        // Validate store exists (assuming StoresDAO has getStoreById method)
        // if (storeDAO.getStoreById(order.getStoreID()) == null) {
        //     throw new ValidationException("Invalid store ID. Store does not exist.");
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
}
