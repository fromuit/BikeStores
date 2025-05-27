/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Sales;

/**
 *
 * @author duyng
 */
public class Staffs extends Person {
    private int active; 
    private int storeID; 
    private Object managerID = null;
    
    public Staffs(int personID, String firstName, String lastName, String email, String phone, int active, int store_id , Object managerID) {
        super(personID, firstName, lastName, email, phone);
        this.active = active;
        this.storeID = store_id;
        this.managerID = managerID;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int store_id) {
        this.storeID = store_id;
    }

    public Object getManagerID() {
        return managerID;
    }

    public void setManagerID(Object managerID) {
        this.managerID = managerID;
    }

}
