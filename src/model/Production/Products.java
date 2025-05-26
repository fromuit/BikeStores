/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Production;

/**
 *
 * @author duyng
 */
public class Products {
    private int productID;
    private String productName;
    private int brandID;
    private int categoryID; 
    private int modelYear;
    private double listPrice;

    public Products() {}
    public Products(int productID, String productName, int brandID, int categoryID, int modelYear, double listPrice) {
        this.productID = productID;
        this.productName = productName;
        this.brandID = brandID;
        this.categoryID = categoryID;
        this.modelYear = modelYear;
        this.listPrice = listPrice;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getBrandID() {
        return brandID;
    }

    public void setBrandID(int brandID) {
        this.brandID = brandID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getModelYear() {
        return modelYear;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    @Override
    public String toString() {
        return "Products{" + "productID=" + productID + ", productName=" + productName + ", brandID=" + brandID + ", categoryID=" + categoryID + ", modelYear=" + modelYear + ", listPrice=" + listPrice + '}';
    }
   
    
    
}
