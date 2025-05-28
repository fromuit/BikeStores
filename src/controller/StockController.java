package controller;

import java.util.ArrayList;
import model.Production.Stocks;
import model.Sales.Stores;
import service.StockService;
import utils.ValidationException;
import view.StockManagementView;

public class StockController {
    private final StockService stockService;
    private StockManagementView view;

    public StockController() {
        this.stockService = new StockService();
    }

    public StockController(StockManagementView view) {
        this.stockService = new StockService();
        this.view = view;
    }

    public void setView(StockManagementView view) {
        this.view = view;
    }

    public void loadStocksByStore(int storeId) {
        try {
            if (view != null) {
                ArrayList<Stocks> stocks = stockService.getStocksByStore(storeId);
                view.displayStocks(stocks);
            }
        } catch (ValidationException e) {
            if (view != null)
                view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error loading stock for store " + storeId + ": " + e.getMessage());
            System.err.println("Error in StockController.loadStocksByStore: " + e.getMessage());
        }
    }

    public void loadStocksByProduct(int productId) {
        try {
            if (view != null) {
                ArrayList<Stocks> stocks = stockService.getStocksByProduct(productId);
                view.displayStocks(stocks); // Assuming the same display method can be used or adapted
            }
        } catch (ValidationException e) {
            if (view != null)
                view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error loading stock for product " + productId + ": " + e.getMessage());
            System.err.println("Error in StockController.loadStocksByProduct: " + e.getMessage());
        }
    }

    public void updateStockQuantity(int storeId, int productId, int quantity) {
        try {
            if (stockService.updateStockQuantity(storeId, productId, quantity)) {
                if (view != null) {
                    view.showMessage("Stock quantity updated successfully!");
                    // Depending on the view's current filter, reload appropriately
                    // For example, if view is showing stocks for a specific store:
                    // loadStocksByStore(storeId);
                    // Or, if it's a general view, it might need a more generic refresh or re-search
                    view.refreshCurrentView(); // A new method in the view to handle this
                }
            } else {
                // This might not be reached if service throws exception for all failures
                if (view != null)
                    view.showError("Failed to update stock quantity.");
            }
        } catch (ValidationException e) {
            if (view != null)
                view.showError("Validation Error: " + e.getMessage());
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            if (view != null)
                view.showError("Error updating stock quantity: " + e.getMessage());
            System.err.println("Error in StockController.updateStockQuantity: " + e.getMessage());
        }
    }

    public ArrayList<Stores> getAccessibleStoresForStockView() {
        try {
            return stockService.getAccessibleStoresForStockView();
        } catch (SecurityException e) {
            if (view != null)
                view.showError("Permission Denied: " + e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            if (view != null)
                view.showError("Error loading accessible stores: " + e.getMessage());
            System.err.println("Error in StockController.getAccessibleStoresForStockView: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}