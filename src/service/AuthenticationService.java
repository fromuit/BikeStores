/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.UserDAO;
import model.Administration.User;
import utils.PasswordUtil;
import utils.ValidationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author duyng
 */
public class AuthenticationService {
    private final UserDAO userDAO;
    private final Map<String, Integer> loginAttempts;
    private final Map<String, LocalDateTime> lockoutTime;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.loginAttempts = new HashMap<>();
        this.lockoutTime = new HashMap<>();
    }
    
    public User authenticate(String username, String password) throws ValidationException {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        
        username = username.trim().toLowerCase();
        
        // Check if account is locked
        if (isAccountLocked(username)) {
            throw new ValidationException("Account is temporarily locked due to multiple failed login attempts. Please try again later.");
        }
        
        try {
            // Get user from database
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                recordFailedAttempt(username);
                throw new ValidationException("Invalid username or password");
            }
            
            // Verify password
            if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                recordFailedAttempt(username);
                throw new ValidationException("Invalid username or password");
            }
            
            // Check if user is active
            if (!user.isActive()) {
                throw new ValidationException("Account is disabled. Please contact administrator.");
            }
            
            // Successful login - clear failed attempts
            clearFailedAttempts(username);
            
            // Update last login time
            userDAO.updateLastLogin(user.getUserID());
            
            return user;
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Authentication failed. Please try again.");
        }
    }
    
    private boolean isAccountLocked(String username) {
        if (!lockoutTime.containsKey(username)) {
            return false;
        }
        
        LocalDateTime lockTime = lockoutTime.get(username);
        LocalDateTime unlockTime = lockTime.plusMinutes(LOCKOUT_DURATION_MINUTES);
        
        if (LocalDateTime.now().isAfter(unlockTime)) {
            // Lockout period has expired
            lockoutTime.remove(username);
            loginAttempts.remove(username);
            return false;
        }
        
        return true;
    }
    
    private void recordFailedAttempt(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);
        
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            lockoutTime.put(username, LocalDateTime.now());
        }
    }
    
    private void clearFailedAttempts(String username) {
        loginAttempts.remove(username);
        lockoutTime.remove(username);
    }
    
    public boolean changePassword(int userId, String currentPassword, String newPassword) throws ValidationException {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found");
        }
        
        if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }
        
        validatePassword(newPassword);
        
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashedPassword);
    }
    
    private void validatePassword(String password) throws ValidationException {
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        
        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password must contain at least one number");
        }
    }
}
