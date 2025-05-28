/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.CustomersDAO;
import dao.OrderItemsDAO;
import dao.OrdersDAO;
import dao.StaffsDAO;
import dao.StoresDAO;
import dao.interfaces.ICustomersDAO;
import dao.interfaces.IOrderItemsDAO;
import dao.interfaces.IOrdersDAO;
import dao.interfaces.IStaffsDAO;
import dao.interfaces.IStoresDAO;
import java.sql.Timestamp;
import java.util.ArrayList;
import model.Administration.User;
import model.Sales.OrderItems;
import model.Sales.Orders;
import utils.SessionManager;
import utils.ValidationException;

/**
 *
 * @author duyng
 */
public class OrderService {
    private final IOrdersDAO orderDAO;
    private final ICustomersDAO customerDAO;
    private final IStoresDAO storeDAO;
    private final IStaffsDAO staffDAO;
    private final SessionManager sessionManager;
    private final IOrderItemsDAO orderItemsDAO;

    public OrderService() {
        this.orderDAO = new OrdersDAO();
        this.customerDAO = new CustomersDAO();
        this.storeDAO = new StoresDAO();
        this.staffDAO = new StaffsDAO();
        this.sessionManager = SessionManager.getInstance();
        this.orderItemsDAO = new OrderItemsDAO();
    }

    // Alternative constructor for dependency injection
    public OrderService(IOrdersDAO orderDAO, ICustomersDAO customerDAO, IStoresDAO storeDAO, IStaffsDAO staffDAO, IOrderItemsDAO orderItemsDAO) {
        this.orderDAO = orderDAO;
        this.customerDAO = customerDAO;
        this.storeDAO = storeDAO;
        this.staffDAO = staffDAO;
        this.sessionManager = SessionManager.getInstance();
        this.orderItemsDAO = orderItemsDAO;
    }

    public ArrayList<Orders> getAllOrders() {
        ArrayList<Orders> allOrders = orderDAO.selectAll();
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }
        return accessibleOrders;
    }

    public Orders getOrderById(int id) {
        Orders order = orderDAO.selectById(id);
        if (order != null && sessionManager.canViewOrder(order)) {
            return order;
        }
        return null;
    }

    public ArrayList<Orders> searchOrders(String searchTerm) {
        ArrayList<Orders> allOrders = orderDAO.search(searchTerm);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

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

        for (Orders order : allOrders) {
            if (sessionManager.canViewOrder(order)) {
                accessibleOrders.add(order);
            }
        }
        return accessibleOrders;
    }

    public ArrayList<Orders> getOrdersByStore(int storeId) {
        if (!sessionManager.canAccessStore(storeId)) {
            return new ArrayList<>();
        }

        ArrayList<Orders> allOrders = orderDAO.getOrdersByStore(storeId);
        ArrayList<Orders> accessibleOrders = new ArrayList<>();

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

        return orderDAO.addOrderWithId(order);
    }

    public boolean updateOrder(Orders order) throws ValidationException {
        validateOrder(order);
        validateBusinessRules(order);

        Orders existingOrder = orderDAO.selectById(order.getOrderID());
        if (existingOrder == null) {
            throw new ValidationException("Order not found");
        }

        if (!sessionManager.canUpdateOrder(existingOrder)) {
            throw new SecurityException("You don't have permission to update this order");
        }

        if (existingOrder.getOrderStatus() == 4) {
            throw new ValidationException("Cannot modify completed orders");
        }

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            if (order.getStaffID() != currentUser.getStaffID()) {
                throw new SecurityException("Employees can only update orders assigned to themselves");
            }
        }

        return orderDAO.update(order);
    }

    public boolean deleteOrder(int orderId) throws ValidationException {
        Orders order = orderDAO.selectById(orderId);
        if (order == null) {
            throw new ValidationException("Order not found");
        }

        if (!sessionManager.canDeleteOrder(order)) {
            throw new SecurityException("You don't have permission to delete this order");
        }

        if (orderDAO.hasOrderItems(orderId)) {
            throw new ValidationException("Cannot delete order with existing items");
        }

        return orderDAO.delete(orderId);
    }

    public String getOrderStatusName(int status) {
        return orderDAO.getOrderStatusName(status);
    }

    private void validateOrder(Orders order) throws ValidationException {
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        if (order.getCustID() <= 0) {
            throw new ValidationException("Valid customer ID is required");
        }
        if (order.getStoreID() <= 0) {
            throw new ValidationException("Valid store ID is required");
        }
        if (order.getStaffID() <= 0) {
            throw new ValidationException("Valid staff ID is required");
        }
        if (order.getOrderDate() == null) {
            throw new ValidationException("Order date is required");
        }
        if (order.getRequiredDate() == null) {
            throw new ValidationException("Required date is required");
        }
    }

    private void validateBusinessRules(Orders order) throws ValidationException {
        if (customerDAO.selectById(order.getCustID()) == null) {
            throw new ValidationException("Customer does not exist");
        }
        if (storeDAO.selectById(order.getStoreID()) == null) {
            throw new ValidationException("Store does not exist");
        }
        if (staffDAO.selectById(order.getStaffID()) == null) {
            throw new ValidationException("Staff does not exist");
        }
        if (order.getRequiredDate().before(order.getOrderDate())) {
            throw new ValidationException("Required date cannot be before order date");
        }
        if (order.getShippedDate() != null && order.getShippedDate().before(order.getOrderDate())) {
            throw new ValidationException("Shipped date cannot be before order date");
        }
    }

    public boolean addOrderItemToOrder(OrderItems item) throws ValidationException {
        if (item.getOrderID() <= 0) {
            throw new ValidationException("Valid order ID is required");
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
