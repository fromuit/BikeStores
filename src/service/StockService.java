package service;

import dao.ProductsDAO;
import dao.StocksDAO;
import dao.StoresDAO;
import dao.interfaces.IProductsDAO;
import dao.interfaces.IStocksDAO;
import dao.interfaces.IStoresDAO;
import java.util.ArrayList;
import model.Administration.User;
import model.Production.Products;
import model.Production.Stocks;
import model.Sales.Stores;
import utils.SessionManager;
import utils.ValidationException;

public class StockService {
    private final IStocksDAO stocksDAO;
    private final IProductsDAO productsDAO; 
    private final IStoresDAO storesDAO; 
    private final SessionManager sessionManager;

    public StockService() {
        this.stocksDAO = new StocksDAO();
        this.productsDAO = new ProductsDAO();
        this.storesDAO = new StoresDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public StockService(IStocksDAO stocksDAO, IProductsDAO productsDAO, IStoresDAO storesDAO) {
        this.stocksDAO = stocksDAO;
        this.productsDAO = productsDAO;
        this.storesDAO = storesDAO;
        this.sessionManager = SessionManager.getInstance();
    }

    public Stocks getStockByStoreAndProduct(int storeId, int productId) throws SecurityException, ValidationException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view stock.");
        }
        
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

        if (!canUpdateStoreStock(storeId)) {
            throw new SecurityException("You do not have permission to update stock for store ID: " + storeId);
        }

        validateStockUpdate(storeId, productId, quantity);
        return stocksDAO.updateStockQuantity(storeId, productId, quantity);
    }

    public ArrayList<Stores> getAccessibleStoresForStockView() throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required.");
        }

        ArrayList<Stores> allStores = storesDAO.selectAll();
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER, STORE_MANAGER -> allStores;
            case EMPLOYEE -> {
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                ArrayList<Stores> filteredStores = new ArrayList<>();
                for (Stores store : allStores) {
                    if (accessibleStores.contains(store.getStoreID())) {
                        filteredStores.add(store);
                    }
                }
                yield filteredStores;
            }
            default -> new ArrayList<>();
        };
    }

    private boolean canViewStoreStock(int storeId) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;

        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER -> true;
            case EMPLOYEE -> {
                ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
                yield accessibleStores.contains(storeId);
            }
            default -> false;
        };
    }

    private boolean canUpdateStoreStock(int storeId) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;

        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true;
            case STORE_MANAGER, EMPLOYEE -> {
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

        Products product = productsDAO.selectById(productId);
        if (product == null) {
            throw new ValidationException("Product with ID " + productId + " not found.");
        }

        Stores store = storesDAO.selectById(storeId);
        if (store == null) {
            throw new ValidationException("Store with ID " + storeId + " not found.");
        }
    }
}