package service;

import dao.StocksDAO;
import dao.ProductsDAO; // Needed to validate product existence
import dao.StoresDAO; // Needed to validate store existence
import dao.StaffsDAO; // Needed to determine store access
import model.Production.Stocks;
import model.Production.Products;
import model.Sales.Stores;
import model.Sales.Staffs;
import model.Administration.User;
import utils.SessionManager;
import utils.ValidationException;
import java.util.ArrayList;

public class StockService {
    private final StocksDAO stocksDAO;
    private final ProductsDAO productsDAO; // For validating product
    private final StoresDAO storesDAO; // For validating store
    private final SessionManager sessionManager;

    public StockService() {
        this.stocksDAO = new StocksDAO();
        this.productsDAO = new ProductsDAO();
        this.storesDAO = new StoresDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public Stocks getStockByStoreAndProduct(int storeId, int productId) throws SecurityException, ValidationException {
        // Add permission checks if necessary
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view stock.");
        }
        // Potentially check if user has access to this store's stock information
        // if (!sessionManager.canViewStoreStock(storeId)) {
        // throw new SecurityException("You do not have permission to view stock for
        // this store.");
        // }

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
        // if (!sessionManager.canViewStoreStock(storeId)) {
        // throw new SecurityException("Permission denied to view stock for store ID: "
        // + storeId);
        // }
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

        User.UserRole role = currentUser.getRole();

        if (role == User.UserRole.STORE_MANAGER || role == User.UserRole.EMPLOYEE) {
            Integer userStaffId = currentUser.getStaffID();
            if (userStaffId == null) {
                throw new SecurityException(
                        "User is not associated with any staff record, cannot determine store access.");
            }
            StaffsDAO staffDAO = new StaffsDAO(); // Ideally, inject this or use a service method
            Staffs staff = staffDAO.getStaffById(userStaffId);

            Integer staffStoreId = (staff != null) ? staff.getStoreID() : null;

            if (staffStoreId == null) {
                throw new SecurityException("Could not determine the store for the current user.");
            }

            if (staffStoreId.intValue() != storeId) {
                throw new SecurityException("You do not have permission to update stock for store ID: " + storeId +
                        ". You can only update stock for store ID: " + staffStoreId);
            }
        } else if (role != User.UserRole.CHIEF_MANAGER) {
            throw new SecurityException("Your role does not have permission to update stock quantities.");
        }

        validateStockUpdate(storeId, productId, quantity);
        return stocksDAO.updateStockQuantity(storeId, productId, quantity);
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