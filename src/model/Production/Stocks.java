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
    private Stores store; 
    private Products product; 
    private int quantity;

    private int storeID; 
    private int productID; 

    public Stocks() {
    }

    public Stocks(int storeID, int productID, int quantity) {
        this.storeID = storeID;
        this.productID = productID;
        this.quantity = quantity;
    }

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
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
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
