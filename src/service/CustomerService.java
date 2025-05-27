/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.CustomersDAO;
import java.util.ArrayList;
import model.Sales.Customers;
import utils.SessionManager;
import utils.ValidationException;
/**
 *
 * @author duyng
 */
public class CustomerService {
    private final CustomersDAO customerDAO;
    private final SessionManager sessionManager;
    
    public CustomerService() {
        this.customerDAO = new CustomersDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public ArrayList<Customers> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public Customers getCustomerById(int id) {
        return customerDAO.getCustomerById(id);
    }

    public ArrayList<Customers> searchCustomers(String searchTerm) {
        return customerDAO.searchCustomers(searchTerm);
    }    
    
    public boolean addCustomer(Customers customer) throws ValidationException {
        validateCustomer(customer);
        validateBusinessRules(customer);
        
        // Check permissions
        if (!sessionManager.hasPermission("MANAGE_CUSTOMERS")) {
            throw new SecurityException("You don't have permission to add customers");
        }
        
        // Check for duplicate email
        if (customerDAO.existsByEmail(customer.getEmail())) {
            throw new ValidationException("Email already exists in the system");
        }
        
        // Check for duplicate phone
        if (customerDAO.existsByPhone(customer.getPhone())) {
            throw new ValidationException("Phone number already exists in the system");
        }
        
        return customerDAO.addCustomer(customer);
    }
    
    public boolean updateCustomer(Customers customer) throws ValidationException {
        validateCustomer(customer);
        validateBusinessRules(customer);
        
        // Check permissions
        if (!sessionManager.hasPermission("MANAGE_CUSTOMERS")) {
            throw new SecurityException("You don't have permission to update customers");
        }
        
        // Get existing customer to check permissions
        Customers existingCustomer = customerDAO.getCustomerById(customer.getPersonID());
        if (existingCustomer == null) {
            throw new ValidationException("Customer not found");
        }
        
        // Check for duplicate email (excluding current customer)
        if (customerDAO.existsByEmailExcluding(customer.getEmail(), customer.getPersonID())) {
            throw new ValidationException("Email already exists in the system");
        }
        
        // Check for duplicate phone (excluding current customer)
        if (customerDAO.existsByPhoneExcluding(customer.getPhone(), customer.getPersonID())) {
            throw new ValidationException("Phone number already exists in the system");
        }
        
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int customerId) throws ValidationException {
        Customers customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new ValidationException("Customer not found");
        }
        
        // Check permissions
        if (!sessionManager.hasPermission("MANAGE_CUSTOMERS")) {
            throw new SecurityException("You don't have permission to delete customers");
        }
        
        // Check if customer has active orders
        if (customerDAO.hasActiveOrders(customerId)) {
            throw new ValidationException("Cannot delete customer with active orders");
        }
        
        return customerDAO.deleteCustomer(customerId);
    }

    public ArrayList<String> getDistinctStates() {
        return customerDAO.getDistinctStates();
    }
    
    public ArrayList<String> getDistinctCities() {
        return customerDAO.getDistinctCities();
    }
    
    public ArrayList<String> getCitiesForState(String state) {
        return customerDAO.getCitiesForState(state);
    }

    public ArrayList<Customers> getCustomersByState(String state) {
        return customerDAO.getCustomersByState(state);
    }
    
    public ArrayList<Customers> getCustomersByCity(String city) {
        return customerDAO.getCustomersByCity(city);
    }    

    private void validateCustomer(Customers customer) throws ValidationException {
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name is required");
        }
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (!isValidEmail(customer.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        if (customer.getPhone() != null && !customer.getPhone().trim().isEmpty() && !isValidPhone(customer.getPhone())) {
            throw new ValidationException("Invalid phone format");
        }
        if (customer.getZipCode() != null && !customer.getZipCode().trim().isEmpty() && !isValidZipCode(customer.getZipCode())) {
            throw new ValidationException("Invalid zip code format");
        }
    }
    
    private void validateBusinessRules(Customers customer) throws ValidationException {
        // Validate state code (if provided)
        if (customer.getState() != null && !customer.getState().trim().isEmpty()) {
            if (!isValidStateCode(customer.getState())) {
                throw new ValidationException("Invalid state code. Please use 2-letter state abbreviation (e.g., CA, NY, TX)");
            }
        }
        
        // Validate required address fields if any address field is provided
        boolean hasAddressInfo = (customer.getStreet() != null && !customer.getStreet().trim().isEmpty()) ||
                                (customer.getCity() != null && !customer.getCity().trim().isEmpty()) ||
                                (customer.getState() != null && !customer.getState().trim().isEmpty()) ||
                                (customer.getZipCode() != null && !customer.getZipCode().trim().isEmpty());
        
        if (hasAddressInfo) {
            if (customer.getStreet() == null || customer.getStreet().trim().isEmpty()) {
                throw new ValidationException("Street address is required when providing address information");
            }
            if (customer.getCity() == null || customer.getCity().trim().isEmpty()) {
                throw new ValidationException("City is required when providing address information");
            }
            if (customer.getState() == null || customer.getState().trim().isEmpty()) {
                throw new ValidationException("State is required when providing address information");
            }
            if (customer.getZipCode() == null || customer.getZipCode().trim().isEmpty()) {
                throw new ValidationException("Zip code is required when providing address information");
            }
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean isValidPhone(String phone) {
        // Allow various phone formats: (123) 456-7890, 123-456-7890, 123.456.7890, 1234567890
        return phone.matches("^[\\d\\s\\-\\(\\)\\+\\.]+$") && phone.replaceAll("[^\\d]", "").length() >= 10;
    }
    
    private boolean isValidZipCode(String zipCode) {
        // US zip code format: 12345 or 12345-6789
        return zipCode.matches("^\\d{5}(-\\d{4})?$");
    }
    
    private boolean isValidStateCode(String state) {
        // List of valid US state codes
        String[] validStates = {
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
        };
        
        for (String validState : validStates) {
            if (validState.equalsIgnoreCase(state.trim())) {
                return true;
            }
        }
        return false;
    }    

}
