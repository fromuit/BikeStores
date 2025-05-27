/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.CustomersDAO;
import model.Sales.Customers;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class CustomerService {
    private final CustomersDAO customerDAO;
    
    public CustomerService() {
        this.customerDAO = new CustomersDAO();
    }
    
    public ArrayList<Customers> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public Customers getCustomerById(int id) {
        return customerDAO.getCustomerById(id);
    }
    
    public boolean addCustomer(Customers customer) {
        // Add business logic validation here if needed
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            return false;
        }
        return customerDAO.addCustomer(customer);
    }
    
    public boolean updateCustomer(Customers customer) {
        // Add business logic validation here if needed
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int id) {
        // Add business logic validation here if needed
        return customerDAO.deleteCustomer(id);
    }
}
