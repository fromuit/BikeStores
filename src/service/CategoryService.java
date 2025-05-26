/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.CategoriesDAO;
import model.Production.Categories;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class CategoryService {
    private final CategoriesDAO categoryDAO;
    
    public CategoryService() {
        this.categoryDAO = new CategoriesDAO();
    }
    
    public ArrayList<Categories> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
    
    public Categories getCategoryById(int id) {
        return categoryDAO.getCategoryById(id);
    }
    
    public boolean addCategory(Categories category) {
        // Add business logic validation here if needed
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            return false;
        }
        return categoryDAO.addCategory(category);
    }
    
    public boolean updateCategory(Categories category) {
        return categoryDAO.updateCategory(category);
    }
    
    public boolean deleteCategory(int id) {
        return categoryDAO.deleteCategory(id);
    }
}
