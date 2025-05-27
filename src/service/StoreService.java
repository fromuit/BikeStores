package service;

import dao.StoresDAO;
import model.Sales.Stores;
import model.Administration.User;
import utils.SessionManager;
import utils.ValidationException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class StoreService {
    private final StoresDAO storesDAO;
    private final SessionManager sessionManager;

    // Basic email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    // Basic phone regex pattern (allows for some variation, e.g. spaces, hyphens)
    // This is a simple example; a more robust regex might be needed for
    // international numbers.
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9. ()-]{7,25}$");

    public StoreService() {
        this.storesDAO = new StoresDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public ArrayList<Stores> getAllStores() throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        // For now, assuming all authenticated users can view all stores.
        // Add role-specific logic if needed, e.g.:
        // if (currentUser == null || currentUser.getRole() ==
        // User.UserRole.SOME_RESTRICTED_ROLE) {
        // throw new SecurityException("You do not have permission to view stores.");
        // }
        return storesDAO.getAllStores();
    }

    public Stores getStoreById(int storeId) throws SecurityException {
        // Similar permission check as getAllStores if needed
        return storesDAO.getStoreById(storeId);
    }

    public boolean addStore(Stores store) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null || currentUser.getRole() == User.UserRole.EMPLOYEE) {
            throw new SecurityException("You do not have permission to add new stores.");
        }
        validateStore(store);
        return storesDAO.addStore(store);
    }

    public boolean updateStore(Stores store) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null || currentUser.getRole() == User.UserRole.EMPLOYEE) {
            throw new SecurityException("You do not have permission to update stores.");
        }
        if (store.getStoreID() <= 0) {
            throw new ValidationException("Store ID for update is invalid.");
        }
        validateStore(store);
        return storesDAO.updateStore(store);
    }

    public boolean deleteStore(int storeId) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null || currentUser.getRole() != User.UserRole.CHIEF_MANAGER) { // Only Chief Manager can
                                                                                           // delete
            throw new SecurityException("You do not have permission to delete stores.");
        }
        if (storeId <= 0) {
            throw new ValidationException("Store ID for delete is invalid.");
        }
        // Add business logic here: e.g., check if store has associated staff/orders
        // that need to be handled
        // For example, prevent deletion if there are active orders or staff assigned to
        // this store.
        // This logic might involve calling other DAOs/Services.
        return storesDAO.deleteStore(storeId);
    }

    public ArrayList<Stores> searchStores(String searchTerm) throws SecurityException {
        // Similar permission check as getAllStores if needed
        return storesDAO.searchStores(searchTerm);
    }

    private void validateStore(Stores store) throws ValidationException {
        if (store == null) {
            throw new ValidationException("Store object cannot be null.");
        }
        if (store.getStoreName() == null || store.getStoreName().trim().isEmpty()) {
            throw new ValidationException("Store name is required.");
        }
        if (store.getStoreName().length() > 255) {
            throw new ValidationException("Store name cannot exceed 255 characters.");
        }
        if (store.getPhone() != null && !store.getPhone().trim().isEmpty()
                && !PHONE_PATTERN.matcher(store.getPhone()).matches()) {
            throw new ValidationException("Invalid phone number format.");
        }
        if (store.getPhone() != null && store.getPhone().length() > 25) {
            throw new ValidationException("Phone number cannot exceed 25 characters.");
        }
        if (store.getEmail() != null && !store.getEmail().trim().isEmpty()
                && !EMAIL_PATTERN.matcher(store.getEmail()).matches()) {
            throw new ValidationException("Invalid email format.");
        }
        if (store.getEmail() != null && store.getEmail().length() > 255) {
            throw new ValidationException("Email cannot exceed 255 characters.");
        }
        if (store.getStreet() != null && store.getStreet().length() > 255) {
            throw new ValidationException("Street address cannot exceed 255 characters.");
        }
        if (store.getCity() != null && store.getCity().length() > 255) {
            throw new ValidationException("City name cannot exceed 255 characters.");
        }
        if (store.getState() != null && store.getState().length() > 25) {
            throw new ValidationException("State name cannot exceed 25 characters.");
        }
        if (store.getZipCode() != null && store.getZipCode().length() > 10) {
            throw new ValidationException("Zip code cannot exceed 10 characters.");
        }
        // Add more specific validation as needed, e.g., zip code format for a specific
        // country.
    }
}