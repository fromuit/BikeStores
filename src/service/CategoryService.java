/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.CategoriesDAO;
import dao.interfaces.ICategoriesDAO;
import java.util.ArrayList;
import model.Production.Categories;
/**
 *
 * @author duyng
 */
public class CategoryService {
    private final ICategoriesDAO categoryDAO;
    
    public CategoryService() {
        this.categoryDAO = new CategoriesDAO();
    }
    
    // Alternative constructor for dependency injection
    public CategoryService(ICategoriesDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }
    
    public ArrayList<Categories> getAllCategories() {
        return categoryDAO.selectAll();
    }
    
    public Categories getCategoryById(int id) {
        return categoryDAO.selectById(id);
    }
    
    public boolean addCategory(Categories category) {
        // Add business logic validation here if needed
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            return false;
        }
        return categoryDAO.insert(category);
    }
    
    public boolean updateCategory(Categories category) {
        return categoryDAO.update(category);
    }
    
    public boolean deleteCategory(int id) {
        return categoryDAO.delete(id);
    }
}
