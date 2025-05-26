package model.Sales;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.ArrayList;

/**
 *
 * @author duyng
 */
public class Customers extends Person {
    private String street; 
    private String city;
    private String state;
    private String zipCode;
    
    private ArrayList<Orders> CustOrders = new ArrayList<>();

    public Customers(int personID, String firstName, String lastName, String email) {
        super(personID, firstName, lastName, email);
    }

    public Customers(int personID, String firstName, String lastName, String email, String phone, String street, String city, String state, String zipCode) {
        super(personID, firstName, lastName, email, phone);
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public ArrayList<Orders> getCustOrders() {
        return CustOrders;
    }

    public void setCustOrders(ArrayList<Orders> CustOrders) {
        this.CustOrders = CustOrders;
    }


    
}
