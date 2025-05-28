package dao.interfaces;

import java.util.ArrayList;
import model.Sales.Staffs;

/**
 * Interface for Staff Data Access Operations
 */
public interface IStaffsDAO extends BaseDAO<Staffs, Integer> {
    
    // Business-specific methods
    ArrayList<Staffs> getStaffsByStore(int storeId);
    
    // Validation methods
    boolean existsByEmail(String email);
    boolean existsByEmailExcluding(String email, int excludeStaffId);
    boolean storeExists(int storeId);
    boolean hasActiveOrders(int staffId);
    boolean isManagingOtherStaff(int staffId);
} 