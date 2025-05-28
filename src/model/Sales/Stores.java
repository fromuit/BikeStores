/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Sales;

import java.util.ArrayList;
import model.Production.Stocks;

/**
 *
 * @author duyng
 */
public class Stores {
    private int storeID;
    private String storeName;
    private String phone;
    private String email;
    private String street;
    private String city;
    private String state;
    private String zipCode;

    private ArrayList<Staffs> StoreStaffs = new ArrayList<>();

    private ArrayList<Stocks> StoreStocks = new ArrayList<>();

    private ArrayList<Orders> StoreOrders = new ArrayList<>();

    public Stores() {
    }

    public Stores(int storeID, String storeName, String phone, String email, String street, String city, String state,
            String zipCode) {
        this.storeID = storeID;
        this.storeName = storeName;
        this.phone = phone;
        this.email = email;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public ArrayList<Staffs> getStoreStaffs() {
        return StoreStaffs;
    }

    public void setStoreStaffs(ArrayList<Staffs> StoreStaffs) {
        this.StoreStaffs = StoreStaffs;
    }

    public ArrayList<Stocks> getStoreStocks() {
        return StoreStocks;
    }

    public void setStoreStocks(ArrayList<Stocks> StoreStocks) {
        this.StoreStocks = StoreStocks;
    }

    public ArrayList<Orders> getStoreOrders() {
        return StoreOrders;
    }

    public void setStoreOrders(ArrayList<Orders> StoreOrders) {
        this.StoreOrders = StoreOrders;
    }

    @Override
    public String toString() {
        return "Stores{" +
                "storeID=" + storeID +
                ", storeName='" + storeName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
