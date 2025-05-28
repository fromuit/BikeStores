package service;

import dao.StoresDAO;
import dao.interfaces.IStoresDAO;
import java.util.ArrayList;
import java.util.regex.Pattern;
import model.Administration.User;
import model.Sales.Stores;
import utils.SessionManager;
import utils.ValidationException;

public class StoreService {
    private final IStoresDAO storesDAO;
    private final SessionManager sessionManager;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9. ()-]{7,25}$");

    public StoreService() {
        this.storesDAO = new StoresDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public StoreService(IStoresDAO storesDAO) {
        this.storesDAO = storesDAO;
        this.sessionManager = SessionManager.getInstance();
    }

    public ArrayList<Stores> getAllStores() throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view stores.");
        }
        
        ArrayList<Stores> allStores = storesDAO.selectAll();
        return filterStoresByAccess(allStores);
    }

    private ArrayList<Stores> filterStoresByAccess(ArrayList<Stores> stores) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getRole() == User.UserRole.CHIEF_MANAGER) {
            return stores; 
        }
        
        if (currentUser.getRole() == User.UserRole.STORE_MANAGER) {
            return stores; 
        }
        
        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            ArrayList<Integer> accessibleStores = sessionManager.getAccessibleStoreIds();
            ArrayList<Stores> filteredStores = new ArrayList<>();
            
            for (Stores store : stores) {
                if (accessibleStores.contains(store.getStoreID())) {
                    filteredStores.add(store);
                }
            }
            return filteredStores;
        }
        
        return new ArrayList<>(); 
    }

    public Stores getStoreById(int storeId) throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view store details.");
        }
        
        if (!canAccessStore(storeId)) {
            throw new SecurityException("You don't have permission to view this store.");
        }
        
        return storesDAO.selectById(storeId);
    }

    private boolean canAccessStore(int storeId) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;
        
        return switch (currentUser.getRole()) {
            case CHIEF_MANAGER -> true; 
            case STORE_MANAGER -> true; 
            case EMPLOYEE -> sessionManager.getAccessibleStoreIds().contains(storeId); 
            default -> false;
        };
    }

    public boolean addStore(Stores store) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to add stores.");
        }

        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER) {
            throw new SecurityException("Only Chief Managers can add new stores.");
        }
        
        validateStore(store);
        return storesDAO.insert(store);
    }

    public boolean updateStore(Stores store) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to update stores.");
        }
        
        if (store.getStoreID() <= 0) {
            throw new ValidationException("Store ID for update is invalid.");
        }

        if (currentUser.getRole() == User.UserRole.EMPLOYEE) {
            throw new SecurityException("Employees cannot update store information.");
        }
        
        if (currentUser.getRole() == User.UserRole.STORE_MANAGER) {
            if (!sessionManager.canAccessStore(store.getStoreID())) {
                throw new SecurityException("You can only update information for your own store.");
            }
        } 
        
        validateStore(store);
        return storesDAO.update(store);
    }

    public boolean deleteStore(int storeId) throws ValidationException, SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to delete stores.");
        }

        if (currentUser.getRole() != User.UserRole.CHIEF_MANAGER) {
            throw new SecurityException("Only Chief Managers can delete stores.");
        }
        
        if (storeId <= 0) {
            throw new ValidationException("Store ID for delete is invalid.");
        }
        
        return storesDAO.delete(storeId);
    }

    public ArrayList<Stores> searchStores(String searchTerm) throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Authentication required to search stores.");
        }
        
        ArrayList<Stores> searchResults = storesDAO.search(searchTerm);
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
    }
}