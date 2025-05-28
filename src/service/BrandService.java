/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.BrandsDAO;
import dao.interfaces.IBrandsDAO;
import java.util.ArrayList;
import model.Production.Brands;
/**
 *
 * @author duyng
 */
public class BrandService {
    private final IBrandsDAO brandDAO;
    
    public BrandService() {
        this.brandDAO = new BrandsDAO();
    }
    
    public BrandService(IBrandsDAO brandDAO) {
        this.brandDAO = brandDAO;
    }
    
    public ArrayList<Brands> getAllBrands() {
        return brandDAO.selectAll();
    }
    
    public Brands getBrandById(int id) {
        return brandDAO.selectById(id);
    }
    
    public boolean addBrand(Brands brand) {
        if (brand.getBrandName() == null || brand.getBrandName().trim().isEmpty()) {
            return false;
        }
        return brandDAO.insert(brand);
    }
    
    public boolean updateBrand(Brands brand) {
        return brandDAO.update(brand);
    }
    
    public boolean deleteBrand(int id) {
        return brandDAO.delete(id);
    }
}