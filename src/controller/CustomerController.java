/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.Sales.Customers;
import service.CustomerService;
import view.CustomerManagementView;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerManagementView view;
    
    public CustomerController(CustomerManagementView view) {
        this.view = view;
        this.customerService = new CustomerService();
    }
    
    public void loadCustomers() {
        try {
            ArrayList<Customers> customers = customerService.getAllCustomers();
            view.displayCustomers(customers);
        } catch (Exception e) {
            view.showError("Error loading customers: " + e.getMessage());
        }
    }
    
    public void addCustomer(Customers customer) {
        try {
            if (customerService.addCustomer(customer)) {
                view.showMessage("Customer added successfully!");
                loadCustomers(); // Refresh the table
            } else {
                view.showError("Failed to add customer");
            }
        } catch (Exception e) {
            view.showError("Error adding customer: " + e.getMessage());
        }
    }
    
    public void updateCustomer(Customers customer) {
        try {
            if (customerService.updateCustomer(customer)) {
                view.showMessage("Customer updated successfully!");
                loadCustomers(); // Refresh the table
            } else {
                view.showError("Failed to update customer");
            }
        } catch (Exception e) {
            view.showError("Error updating customer: " + e.getMessage());
        }
    }
    
    public void deleteCustomer(int customerId) {
        try {
            if (customerService.deleteCustomer(customerId)) {
                view.showMessage("Customer deleted successfully!");
                loadCustomers(); // Refresh the table
            } else {
                view.showError("Failed to delete customer");
            }
        } catch (Exception e) {
            view.showError("Error deleting customer: " + e.getMessage());
        }
    }
}