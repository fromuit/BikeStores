package service;

import dao.ProductsDAO;
import dao.StocksDAO;
import dao.StoresDAO;
import java.util.ArrayList; 
import model.Administration.User;
import model.Production.Products;
import model.Production.Stocks;
import model.Sales.Stores;
import utils.SessionManager;
import utils.ValidationException;

public class StockService {
    private final StocksDAO stocksDAO;
    private final ProductsDAO productsDAO; 
    private final StoresDAO storesDAO; 
    private final SessionManager sessionManager;

    public StockService() {
        this.stocksDAO = new StocksDAO();
        this.productsDAO = new ProductsDAO();
        this.storesDAO = new StoresDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public Stocks getStockByStoreAndProduct(int storeId, int productId) throws SecurityException, ValidationException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view stock.");
        }
        
        // Check if user can view this store's stock
        if (!canViewStoreStock(storeId)) {
            throw new SecurityException("You do not have permission to view stock for this store.");
        }

        if (storeId <= 0 || productId <= 0) {
            throw new ValidationException("Store ID and Product ID must be valid.");
        }
        return stocksDAO.getStockByStoreAndProduct(storeId, productId);
    }

    public ArrayList<Stocks> getStocksByStore(int storeId) throws SecurityException, ValidationException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required.");
        }
        
        // Check if user can view this store's stock
        if (!canViewStoreStock(storeId)) {
            throw new SecurityException("Permission denied to view stock for store ID: " + storeId);
        }
        
        if (storeId <= 0) {
            throw new ValidationException("Store ID must be valid.");
        }
        return stocksDAO.getStocksByStore(storeId);
    }

    public ArrayList<Stocks> getStocksByProduct(int productId) throws SecurityException, ValidationException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required.");
        }
        if (productId <= 0) {
            throw new ValidationException("Product ID must be valid.");
        }
        return stocksDAO.getStocksByProduct(productId);
    }

    public boolean updateStockQuantity(int storeId, int productId, int quantity)
            throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to update stock.");
        }

        // Check if user can update stock for this store
        if (!canUpdateStoreStock(storeId)) {
            throw new SecurityException("You do not have permission to update stock for store ID: " + storeId);
        }

        validateStockUpdate(storeId, productId, quantity);
        return stocksDAO.updateStockQuantity(storeId, productId, quantity);
    }

    /**
     * Get list of stores that current user can view stock for
     */
    public ArrayList<Stores> getAccessibleStoresForStockView() throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required.");
        }

        ArrayList<Stores> allStores = storesDAO.getAllStores();
        
        // Debug logging
        System.out.println("Current user role: " + currentUser.getRole());
        System.out.println("Current user staff ID: " + currentUser.getStaffID());
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER, STORE_MANAGER -> {
                System.out.println("Returning all stores for CHIEF_MANAGER/STORE_MANAGER: " + allStores.size());
                yield allStores; // Can view all stores
            }
            case EMPLOYEE -> {
                // Employees can view their own store
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                System.out.println("Employee accessible stores: " + accessibleStores);
                
                ArrayList<Stores> filteredStores = new ArrayList<>();
                for (Stores store : allStores) {
                    if (accessibleStores.contains(store.getStoreID())) {
                        filteredStores.add(store);
                        System.out.println("Added store: " + store.getStoreID() + " - " + store.getStoreName());
                    }
                }
                System.out.println("Filtered stores count: " + filteredStores.size());
                yield filteredStores;
            }
            default -> {
                System.out.println("Unknown role, returning empty list");
                yield new ArrayList<>();
            }
        };
    }

    /**
     * Check if current user can view stock for a specific store
     */
    private boolean canViewStoreStock(int storeId) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;

        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true; // Can view all stores
            case STORE_MANAGER -> true; // Can view all stores (but can only update their own)
            case EMPLOYEE -> {
                // Can view their own store
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                yield accessibleStores.contains(storeId);
            }
            default -> false;
        };
    }

    /**
     * Check if current user can update stock for a specific store
     */
    private boolean canUpdateStoreStock(int storeId) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;

        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true; // Can update all stores
            case STORE_MANAGER, EMPLOYEE -> {
                // Can only update their own store
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                yield accessibleStores.contains(storeId);
            }
            default -> false;
        };
    }

    private void validateStockUpdate(int storeId, int productId, int quantity) throws ValidationException {
        if (storeId <= 0) {
            throw new ValidationException("Store ID is invalid.");
        }
        if (productId <= 0) {
            throw new ValidationException("Product ID is invalid.");
        }
        if (quantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative.");
        }

        // Check if product and store exist before attempting to update stock
        Products product = productsDAO.getProductById(productId);
        if (product == null) {
            throw new ValidationException("Product with ID " + productId + " not found.");
        }

        Stores store = storesDAO.getStoreById(storeId);
        if (store == null) {
            throw new ValidationException("Store with ID " + storeId + " not found.");
        }
        // Further business rules can be added here, e.g., max stock quantity, etc.
    }
}