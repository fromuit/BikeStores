package controller;

import java.util.ArrayList;
import model.Sales.Stores;
import service.StoreService;
import utils.ValidationException; 
import view.StoreManagementView;

public class StoreController {
    private final StoreService storeService;
    private StoreManagementView view; 

    public StoreController() {
        this.storeService = new StoreService();
    }

    // Constructor to be used when view is passed directly
    public StoreController(StoreManagementView view) {
        this.storeService = new StoreService();
        this.view = view;
    }

    public void setView(StoreManagementView view) {
        this.view = view;
    }

    public void loadStores() {
        try {
            ArrayList<Stores> stores = storeService.getAllStores();
            if (view != null)
                view.displayStores(stores);
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error loading stores: " + e.getMessage());
            System.err.println("Error in StoreController.loadStores: " + e.getMessage());
        }
    }

    public void addStore(Stores store) {
        try {
            if (storeService.addStore(store)) {
                if (view != null)
                    view.showMessage("Store added successfully!");
                loadStores();
            } else {
                if (view != null)
                    view.showError("Failed to add store. Check input data and logs.");
            }
        } catch (ValidationException e) {
            if (view != null)
                view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error adding store: " + e.getMessage());
            System.err.println("Error in StoreController.addStore: " + e.getMessage());
        }
    }

    public void updateStore(Stores store) {
        try {
            if (storeService.updateStore(store)) {
                if (view != null)
                    view.showMessage("Store updated successfully!");
                loadStores(); // Refresh the table
            } else {
                if (view != null)
                    view.showError("Failed to update store. Check input data and logs.");
            }
        } catch (ValidationException e) {
            if (view != null)
                view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error updating store: " + e.getMessage());
            System.err.println("Error in StoreController.updateStore: " + e.getMessage());
        }
    }

    public void deleteStore(int storeId) {
        try {
            if (storeService.deleteStore(storeId)) {
                if (view != null)
                    view.showMessage("Store deleted successfully!");
                loadStores(); 
            } else {
                if (view != null)
                    view.showError("Failed to delete store. It might be in use or not exist.");
            }
        } catch (ValidationException e) { 
            if (view != null)
                view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error deleting store: " + e.getMessage());
            System.err.println("Error in StoreController.deleteStore: " + e.getMessage());
        }
    }

    public void searchStores(String searchTerm) {
        try {
            ArrayList<Stores> stores = storeService.searchStores(searchTerm);
            if (view != null)
                view.displayStores(stores);
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error searching stores: " + e.getMessage());
            System.err.println("Error in StoreController.searchStores: " + e.getMessage());
        }
    }

    public Stores getStoreById(int storeId) {
        try {
            return storeService.getStoreById(storeId);
        } catch (SecurityException e) {
            view.showError("Access denied: " + e.getMessage());
            return null;
        } catch (Exception e) {
            view.showError("Error loading store: " + e.getMessage());
            return null;
        }
    }
}