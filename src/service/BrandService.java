/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.BrandsDAO;
import model.Production.Brands;
import java.util.ArrayList;
/**
 *
 * @author duyng
 */
public class BrandService {
    private final BrandsDAO brandDAO;
    
    public BrandService() {
        this.brandDAO = new BrandsDAO();
    }
    
    public ArrayList<Brands> getAllBrands() {
        return brandDAO.getAllBrands();
    }
    
    public Brands getBrandById(int id) {
        return brandDAO.getBrandById(id);
    }
    
    public boolean addBrand(Brands brand) {
        if (brand.getBrandName() == null || brand.getBrandName().trim().isEmpty()) {
            return false;
        }
        return brandDAO.addBrand(brand);
    }
    
    public boolean updateBrand(Brands brand) {
        return brandDAO.updateBrand(brand);
    }
    
    public boolean deleteBrand(int id) {
        return brandDAO.deleteBrand(id);
    }
}