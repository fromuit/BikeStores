package dao.interfaces;

import model.Administration.User;

/**
 * Interface for User Data Access Operations
 */
public interface IUserDAO extends BaseDAO<User, Integer> {
    
    // Authentication-specific methods
    User getUserByUsername(String username);
    boolean updateLastLogin(int userId);
    boolean updatePassword(int userId, String hashedPassword);
} 