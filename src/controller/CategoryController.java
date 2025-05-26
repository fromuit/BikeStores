/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.Production.Categories;
import service.CategoryService;
import view.CategoryManagementView;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryManagementView view;
    
    public CategoryController(CategoryManagementView view) {
        this.view = view;
        this.categoryService = new CategoryService();
    }
    
    public void loadCategories() {
        try {
            ArrayList<Categories> categories = categoryService.getAllCategories();
            view.displayCategories(categories);
        } catch (Exception e) {
            view.showError("Error loading categories: " + e.getMessage());
        }
    }
    
    public void addCategory(Categories category) {
        try {
            if (categoryService.addCategory(category)) {
                view.showMessage("Category added successfully!");
                loadCategories(); // Refresh the table
            } else {
                view.showError("Failed to add category");
            }
        } catch (Exception e) {
            view.showError("Error adding category: " + e.getMessage());
        }
    }
    
    public void updateCategory(Categories category) {
        try {
            if (categoryService.updateCategory(category)) {
                view.showMessage("Category updated successfully!");
                loadCategories(); // Refresh the table
            } else {
                view.showError("Failed to update category");
            }
        } catch (Exception e) {
            view.showError("Error updating category: " + e.getMessage());
        }
    }
    
    public void deleteCategory(int categoryId) {
        try {
            if (categoryService.deleteCategory(categoryId)) {
                view.showMessage("Category deleted successfully!");
                loadCategories(); // Refresh the table
            } else {
                view.showError("Failed to delete category");
            }
        } catch (Exception e) {
            view.showError("Error deleting category: " + e.getMessage());
        }
    }
}
