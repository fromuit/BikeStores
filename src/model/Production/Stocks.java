/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Production;

import model.Sales.Stores;

/**
 *
 * @author duyng
 */
public class Stocks {
    // Using object composition for richer data display
    private Stores store; // Represents the store
    private Products product; // Represents the product
    private int quantity;

    // IDs for database interaction and simpler scenarios
    private int storeID; // Foreign Key to sales.stores
    private int productID; // Foreign Key to production.products

    // Default constructor
    public Stocks() {
    }

    // Constructor with IDs and quantity (common for DAO operations)
    public Stocks(int storeID, int productID, int quantity) {
        this.storeID = storeID;
        this.productID = productID;
        this.quantity = quantity;
    }

    // Constructor with full objects and quantity (useful for service/view layers)
    public Stocks(Stores store, Products product, int quantity) {
        this.store = store;
        this.product = product;
        this.quantity = quantity;
        if (store != null) {
            this.storeID = store.getStoreID();
        }
        if (product != null) {
            this.productID = product.getProductID();
        }
    }

    // Getters and Setters
    public Stores getStore() {
        return store;
    }

    public void setStore(Stores store) {
        this.store = store;
        if (store != null) {
            this.storeID = store.getStoreID();
        }
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
        if (product != null) {
            this.productID = product.getProductID();
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
        // Optional: if you want to keep store object in sync, you might need a DAO
        // lookup here
        // or ensure it's set separately if this ID is changed directly.
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
        // Similar to storeID, consider object synchronization if needed.
    }

    @Override
    public String toString() {
        return "Stocks{" +
                "storeID=" + storeID +
                (store != null ? ", storeName='" + store.getStoreName() + "\'" : "") +
                ", productID=" + productID +
                (product != null ? ", productName='" + product.getProductName() + "\'" : "") +
                ", quantity=" + quantity +
                '}';
    }
}
