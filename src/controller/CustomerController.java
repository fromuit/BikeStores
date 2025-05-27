/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.ArrayList;
import model.Sales.Customers;
import service.CustomerService;
import utils.SessionManager;
import utils.ValidationException;
import view.CustomerManagementView;
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
        } catch (SecurityException e) {
            view.showError("Access denied: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error loading customers: " + e.getMessage());
            System.err.println("Error in loadCustomers: " + e.getMessage());
        }
    }

    public void searchCustomers(String searchTerm) {
        try {
            ArrayList<Customers> customers = customerService.searchCustomers(searchTerm);
            view.displayCustomers(customers);
        } catch (Exception e) {
            view.showError("Error searching customers: " + e.getMessage());
        }
    }   
    
    
    public void loadCustomersByState(String state) {
        try {
            ArrayList<Customers> customers = customerService.getCustomersByState(state);
            view.displayCustomers(customers);
        } catch (Exception e) {
            view.showError("Error loading customers by state: " + e.getMessage());
        }
    }
    
    public void loadCustomersByCity(String city) {
        try {
            ArrayList<Customers> customers = customerService.getCustomersByCity(city);
            view.displayCustomers(customers);
        } catch (Exception e) {
            view.showError("Error loading customers by city: " + e.getMessage());
        }
    }    
    
    public void addCustomer(Customers customer) {
        try {
            if (customerService.addCustomer(customer)) {
                view.showMessage("Customer added successfully!");
                loadCustomers();
                view.refreshFilters();
            } else {
                view.showError("Failed to add customer");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error adding customer: " + e.getMessage());
            System.err.println("Error in addCustomer: " + e.getMessage());
        }
    }
    
    public void updateCustomer(Customers customer) {
        try {
            if (customerService.updateCustomer(customer)) {
                view.showMessage("Customer updated successfully!");
                loadCustomers();
                view.refreshFilters();
            } else {
                view.showError("Failed to update customer");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error updating customer: " + e.getMessage());
            System.err.println("Error in updateCustomer: " + e.getMessage());
        }
    }
    
    public void deleteCustomer(int customerId) {
        try {
            if (customerService.deleteCustomer(customerId)) {
                view.showMessage("Customer deleted successfully!");
                loadCustomers();
                view.refreshFilters();
            } else {
                view.showError("Failed to delete customer");
            }
        } catch (ValidationException e) {
            view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            view.showError("Permission Error: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Error deleting customer: " + e.getMessage());
            System.err.println("Error in deleteCustomer: " + e.getMessage());
        }
    }


    public ArrayList<String> getDistinctStates() {
        try {
            return customerService.getDistinctStates();
        } catch (Exception e) {
            view.showError("Error loading states: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public ArrayList<String> getDistinctCities() {
        try {
            return customerService.getDistinctCities();
        } catch (Exception e) {
            view.showError("Error loading cities: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public ArrayList<String> getCitiesForState(String state) {
        try {
            return customerService.getCitiesForState(state);
        } catch (Exception e) {
            view.showError("Error loading cities for state: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}