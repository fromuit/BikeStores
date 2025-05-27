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
import model.Production.Products;
import service.ProductService;

import dao.OrderItemsDAO;
import model.Sales.OrderItems;

/**
 *
 * @author duyng
 */
public class OrderController {
    private final OrderService orderService;
    private final ProductService productService;
    private final OrderManagementView view;

    private final OrderItemsDAO orderItemDAO;

    public OrderController(OrderManagementView view) {
        this.view = view;
        this.orderService = new OrderService();
        this.productService = new ProductService();
        this.orderItemDAO = new OrderItemsDAO();
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
            int orderId = orderService.addOrder(order);
            if (orderId > 0) {
                view.showAddItemToOrderDialog(orderId);
                loadOrders();
            } else {
                view.showError("Failed to add order. Please check logs.");
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

    public void showOrderDetails(int orderId) {
        try {
            ArrayList<OrderItems> orderItems = orderItemDAO.getOrderItemsByOrderId(orderId);
            double orderTotal = orderItemDAO.calculateOrderTotal(orderId);
            int itemCount = orderItemDAO.getOrderItemCount(orderId);
            view.showOrderDetailsDialog(orderId, orderItems, orderTotal, itemCount);
        } catch (Exception e) {
            view.showError("Error loading order details: " + e.getMessage());
            System.err.println("Error in showOrderDetails: " + e.getMessage());
        }
    }

    public ArrayList<Products> getAllProductsForSelection() {
        try {
            return productService.getAllProductsBasicInfo();
        } catch (Exception e) {
            view.showError("Error loading products for selection: " + e.getMessage());
            System.err.println("Error in getAllProductsForSelection: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean addItemToOrder(OrderItems item) {
        try {
            int nextItemId = orderService.getNextItemIdForOrder(item.getOrderID());
            item.setItemID(nextItemId);
            if (orderService.addOrderItemToOrder(item)) {
                return true;
            }
            return false;
        } catch (ValidationException e) {
            view.showError("Validation Error adding item: " + e.getMessage());
            return false;
        } catch (Exception e) {
            view.showError("Error adding item to order: " + e.getMessage());
            System.err.println("Error in addItemToOrder: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<OrderItems> getOrderItemsForDialog(int orderId) {
        try {
            return orderService.getOrderItems(orderId);
        } catch (Exception e) {
            view.showError("Error loading items for order: " + e.getMessage());
            System.err.println("Error in getOrderItemsForDialog: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}