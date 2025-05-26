/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.Production.Brands;
import service.BrandService;
import view.BrandManagementView;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class BrandController {
    private final BrandService brandService;
    private final BrandManagementView view;
    
    public BrandController(BrandManagementView view) {
        this.view = view;
        this.brandService = new BrandService();
    }
    
    public void loadBrands() {
        try {
            ArrayList<Brands> brands = brandService.getAllBrands();
            view.displayBrands(brands);
        } catch (Exception e) {
            view.showError("Error loading brands: " + e.getMessage());
        }
    }
    
    public void addBrand(Brands brand) {
        try {
            if (brandService.addBrand(brand)) {
                view.showMessage("Brand added successfully!");
                loadBrands();
            } else {
                view.showError("Failed to add brand");
            }
        } catch (Exception e) {
            view.showError("Error adding brand: " + e.getMessage());
        }
    }
    
    public void updateBrand(Brands brand) {
        try {
            if (brandService.updateBrand(brand)) {
                view.showMessage("Brand updated successfully!");
                loadBrands();
            } else {
                view.showError("Failed to update brand");
            }
        } catch (Exception e) {
            view.showError("Error updating brand: " + e.getMessage());
        }
    }
    
    public void deleteBrand(int brandId) {
        try {
            if (brandService.deleteBrand(brandId)) {
                view.showMessage("Brand deleted successfully!");
                loadBrands();
            } else {
                view.showError("Failed to delete brand");
            }
        } catch (Exception e) {
            view.showError("Error deleting brand: " + e.getMessage());
        }
    }
}