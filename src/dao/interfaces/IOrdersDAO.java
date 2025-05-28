package dao.interfaces;

import java.sql.Timestamp;
import java.util.ArrayList;
import model.Sales.Orders;

/**
 * Interface for Order Data Access Operations
 */
public interface IOrdersDAO extends BaseDAO<Orders, Integer> {
    
    /**
     * Add order and return generated order ID
     * @param order Order to add
     * @return Generated order ID, -1 if failed
     */
    int addOrderWithId(Orders order);
    
    // Business-specific query methods
    ArrayList<Orders> getOrdersByStatus(int status);
    ArrayList<Orders> getOrdersByCustomer(int customerId);
    ArrayList<Orders> getOrdersByStore(int storeId);
    ArrayList<Orders> getOrdersByStaff(int staffId);
    ArrayList<Orders> getOrdersByDateRange(Timestamp startDate, Timestamp endDate);
    
    // Utility methods
    boolean hasOrderItems(int orderId);
    String getOrderStatusName(int status);
} 