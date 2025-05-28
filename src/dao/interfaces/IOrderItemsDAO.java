package dao.interfaces;

import java.util.ArrayList;
import model.Sales.OrderItems;

/**
 * Interface for Order Items Data Access Operations
 */
public interface IOrderItemsDAO {
    
    // OrderItems-specific operations
    ArrayList<OrderItems> getOrderItemsByOrderId(int orderId);
    double calculateOrderTotal(int orderId);
    int getOrderItemCount(int orderId);
    int getNextItemId(int orderId);
    boolean addOrderItem(OrderItems orderItem);
} 