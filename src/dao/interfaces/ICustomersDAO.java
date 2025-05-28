package dao.interfaces;

import java.util.ArrayList;
import model.Sales.Customers;

/**
 * Interface for Customer Data Access Operations
 */
public interface ICustomersDAO extends BaseDAO<Customers, Integer> {
    
    // Business-specific methods
    ArrayList<Customers> getCustomersByState(String state);
    ArrayList<Customers> getCustomersByCity(String city);
    ArrayList<String> getCitiesForState(String state);
    ArrayList<String> getDistinctStates();
    ArrayList<String> getDistinctCities();
    
    // Validation methods
    boolean existsByEmail(String email);
    boolean existsByEmailExcluding(String email, int excludeCustomerId);
    boolean existsByPhone(String phone);
    boolean existsByPhoneExcluding(String phone, int excludeCustomerId);
    boolean hasActiveOrders(int customerId);
} 