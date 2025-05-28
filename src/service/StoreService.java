package service;

import dao.StoresDAO;
import java.util.ArrayList;
import java.util.regex.Pattern;
import model.Administration.User;
import model.Sales.Stores;
import utils.SessionManager;
import utils.ValidationException;

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
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view stores.");
        }
        
        ArrayList<Stores> allStores = storesDAO.getAllStores();
        return filterStoresByAccess(allStores);
    }

    private ArrayList<Stores> filterStoresByAccess(ArrayList<Stores> stores) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getRole() == User.UserRole.CHIEF_MANAGER) {
            return stores; // Chief managers see all stores
        }
        
        if (currentUser.getRole() == User.UserRole.STORE_MANAGER) {
            return stores; // Store managers can see all stores but can only edit their own
        }
        
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            // Employees can only see their own store
            ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
            ArrayList<Stores> filteredStores = new ArrayList<>();
            
            for (Stores store : stores) {
                if (accessibleStores.contains(store.getStoreID())) {
                    filteredStores.add(store);
                }
            }
            return filteredStores;
        }
        
        return new ArrayList<>(); // Default: no access
    }

    public Stores getStoreById(int storeId) throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view store details.");
        }
        
        // Check if user can access this specific store
        if (!canAccessStore(storeId)) {
            throw new SecurityException("You don't have permission to view this store.");
        }
        
        return storesDAO.getStoreById(storeId);
    }

    private boolean canAccessStore(int storeId) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true; // Can access all stores
            case STORE_MANAGER -> true; // Can view all stores
            case EMPLOYEE -> sessionManager.getAccessibleStoreIds().contains(storeId); // Only their store
            default -> false;
        };
    }

    public boolean addStore(Stores store) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to add stores.");
        }
        
        // Only CHIEF_MANAGER can add new stores
        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER) {
            throw new SecurityException("Only Chief Managers can add new stores.");
        }
        
        validateStore(store);
        return storesDAO.addStore(store);
    }

    public boolean updateStore(Stores store) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to update stores.");
        }
        
        if (store.getStoreID() <= 0) {
            throw new ValidationException("Store ID for update is invalid.");
        }
        
        // Check permissions based on role
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            throw new SecurityException("Employees cannot update store information.");
        }
        
        if (currentUser.getRole() == User.UserRole.STORE_MANAGER) {
            // Store managers can only update their own store
            if (!sessionManager.canAccessStore(store.getStoreID())) {
                throw new SecurityException("You can only update information for your own store.");
            }
        }
        
        // CHIEF_MANAGER can update any store (no additional check needed)
        
        validateStore(store);
        return storesDAO.updateStore(store);
    }

    public boolean deleteStore(int storeId) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to delete stores.");
        }
        
        // Only CHIEF_MANAGER can delete stores
        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER) {
            throw new SecurityException("Only Chief Managers can delete stores.");
        }
        
        if (storeId <= 0) {
            throw new ValidationException("Store ID for delete is invalid.");
        }
        
        // Add business logic here: e.g., check if store has associated staff/orders
        // For example, prevent deletion if there are active orders or staff assigned to this store.
        return storesDAO.deleteStore(storeId);
    }

    public ArrayList<Stores> searchStores(String searchTerm) throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to search stores.");
        }
        
        ArrayList<Stores> searchResults = storesDAO.searchStores(searchTerm);
        return filterStoresByAccess(searchResults);
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