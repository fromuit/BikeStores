package dao.interfaces;

import java.util.ArrayList;

/**
 * Generic Base DAO Interface for common CRUD operations
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseDAO<T, ID> {
    /**
     * Insert new entity
     * @param entity Entity to insert
     * @return true if successful, false otherwise
     */
    boolean insert(T entity);
    
    /**
     * Update existing entity
     * @param entity Entity to update
     * @return true if successful, false otherwise
     */
    boolean update(T entity);
    
    /**
     * Delete entity by ID
     * @param id Primary key
     * @return true if successful, false otherwise
     */
    boolean delete(ID id);
    
    /**
     * Get all entities
     * @return List of all entities
     */
    ArrayList<T> selectAll();
    
    /**
     * Get entity by ID
     * @param id Primary key
     * @return Entity if found, null otherwise
     */
    T selectById(ID id);
    
    /**
     * Search entities by term
     * @param searchTerm Search term
     * @return List of matching entities
     */
    ArrayList<T> search(String searchTerm);
} 