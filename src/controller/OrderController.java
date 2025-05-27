/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import model.Sales.Orders;
import service.OrderService;
import utils.ValidationException;
import view.OrderManagementView;

/**
 *
 * @author duyng
 */
public class OrderController {
    private final OrderService orderService;
    private final OrderManagementView view;
    
    public OrderController(OrderManagementView view) {
        this.view = view;
        this.orderService = new OrderService();
    }
    
    public void loadOrders() {
        try {
            ArrayList<Orders> orders = orderService.getAllOrders();
            view.displayOrders(orders);
        } catch (SecurityException e) {
            view.showError("Access denied: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error loading orders: " + e.getMessage());
            System.err.println("Error in loadOrders: " + e.getMessage());
        }
    }

    public void searchOrders(String searchTerm) {
        try {
            ArrayList<Orders> orders = orderService.searchOrders(searchTerm);
            view.displayOrders(orders);
        } catch (Exception e) {
            view.showError("Error searching orders: " + e.getMessage());
        }
    }
    
    public void loadOrdersByStatus(int status) {
        try {
            ArrayList<Orders> orders = orderService.getOrdersByStatus(status);
            view.displayOrders(orders);
        } catch (Exception e) {
            view.showError("Error loading orders by status: " + e.getMessage());
        }
    }
    
    public void loadOrdersByCustomer(int customerId) {
        try {
            ArrayList<Orders> orders = orderService.getOrdersByCustomer(customerId);
            view.displayOrders(orders);
        } catch (Exception e) {
            view.showError("Error loading orders by customer: " + e.getMessage());
        }
    }
    
    public void loadOrdersByStore(int storeId) {
        try {
            ArrayList<Orders> orders = orderService.getOrdersByStore(storeId);
            view.displayOrders(orders);
        } catch (Exception e) {
            view.showError("Error loading orders by store: " + e.getMessage());
        }
    }
    
    public void loadOrdersByStaff(int staffId) {
        try {
            ArrayList<Orders> orders = orderService.getOrdersByStaff(staffId);
            view.displayOrders(orders);
        } catch (Exception e) {
            view.showError("Error loading orders by staff: " + e.getMessage());
        }
    }
    
    public void loadOrdersByDateRange(Timestamp startDate, Timestamp endDate) {
        try {
            ArrayList<Orders> orders = orderService.getOrdersByDateRange(startDate, endDate);
            view.displayOrders(orders);
        } catch (Exception e) {
            view.showError("Error loading orders by date range: " + e.getMessage());
        }
    }
    
    public void addOrder(Orders order) {
        try {
            if (orderService.addOrder(order)) {
                view.showMessage("Order added successfully!");
                loadOrders();
            } else {
                view.showError("Failed to add order");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error adding order: " + e.getMessage());
            System.err.println("Error in addOrder: " + e.getMessage());
        }
    }
    
    public void updateOrder(Orders order) {
        try {
            if (orderService.updateOrder(order)) {
                view.showMessage("Order updated successfully!");
                loadOrders();
            } else {
                view.showError("Failed to update order");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error updating order: " + e.getMessage());
            System.err.println("Error in updateOrder: " + e.getMessage());
        }
    }
    
    public void deleteOrder(int orderId) {
        try {
            if (orderService.deleteOrder(orderId)) {
                view.showMessage("Order deleted successfully!");
                loadOrders();
            } else {
                view.showError("Failed to delete order");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error deleting order: " + e.getMessage());
            System.err.println("Error in deleteOrder: " + e.getMessage());
        }
    }
    
    public String getOrderStatusName(int status) {
        return orderService.getOrderStatusName(status);
    }
}