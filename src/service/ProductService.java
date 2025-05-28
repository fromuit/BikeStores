/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.ProductsDAO;
import dao.interfaces.IProductsDAO;
import java.util.ArrayList;
import model.Administration.User;
import model.Production.Products;
import utils.SessionManager;
import utils.ValidationException;

/**
 *
 * @author duyng
 */
public class ProductService {
    private static final int MIN_YEAR = 1800;
    private static final int MAX_FUTURE_YEARS = 1;
    
    private final IProductsDAO productDAO;
    private final SessionManager sessionManager;

    public ProductService() {
        this.productDAO = new ProductsDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public ProductService(IProductsDAO productDAO) {
        this.productDAO = productDAO;
        this.sessionManager = SessionManager.getInstance();
    }

    public ArrayList<Products> getAllProducts() {
        try {
            return productDAO.selectAll();
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Products getProductById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID sản phẩm phải lớn hơn 0");
        }
        return productDAO.selectById(id);
    }

    public ArrayList<Products> getProductsByCategory(int categoryId) {
        if (categoryId <= 0) {
            System.out.println("CategoryID không hợp lệ: " + categoryId + ". Trả về tất cả sản phẩm.");
            return getAllProducts();
        }
        return productDAO.getProductsByCategoryId(categoryId);
    }

    public ArrayList<Products> getProductsByBrand(int brandId) {
        if (brandId <= 0) {
            System.out.println("BrandID không hợp lệ: " + brandId + ". Trả về tất cả sản phẩm.");
            return getAllProducts();
        }
        return productDAO.getProductsByBrandId(brandId);
    }

    public ArrayList<Products> getAllProductsBasicInfo() {
        try {
            return productDAO.getAllProductsBasicInfo();
        } catch (Exception e) {
            // Handle or log the exception appropriately
            System.err.println("Error in ProductService.getAllProductsBasicInfo: " + e.getMessage());
            return new ArrayList<>(); // Return empty list on error
        }
    }

    public ArrayList<Products> searchProducts(String searchTerm) throws Exception {
        return productDAO.search(searchTerm);
    }

    public boolean addProduct(Products product) throws ValidationException, SecurityException {
        validateUserPermission(User.UserRole.EMPLOYEE, false);
        validateProduct(product);
        validateBusinessRules(product);
        
        return productDAO.insert(product);
    }

    public boolean updateProduct(Products product) throws ValidationException, SecurityException {
        validateUserPermission(User.UserRole.EMPLOYEE, false);
        validateProduct(product);
        validateProductId(product.getProductID());
        
        return productDAO.update(product);
    }

    public boolean deleteProduct(int id) throws ValidationException, SecurityException {
        validateUserPermission(User.UserRole.CHIEF_MANAGER, true);
        validateProductId(id);
        
        return productDAO.delete(id);
    }

    // Helper methods for better organization
    private void validateUserPermission(User.UserRole restrictedRole, boolean onlyChiefManager) 
            throws SecurityException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Chưa đăng nhập. Truy cập bị từ chối.");
        }
        
        if (onlyChiefManager && currentUser.getRole() != User.UserRole.CHIEF_MANAGER) {
            throw new SecurityException("Chỉ Trưởng phòng mới có quyền thực hiện thao tác này.");
        } else if (!onlyChiefManager && currentUser.getRole() == restrictedRole) {
            throw new SecurityException("Nhân viên không có quyền thực hiện thao tác này.");
        }
    }

    private void validateProduct(Products product) throws ValidationException {
        if (product == null) {
            throw new ValidationException("Đối tượng sản phẩm không được null.");
        }
        
        validateProductName(product.getProductName());
        validateIds(product.getBrandID(), product.getCategoryID());
        validateModelYear(product.getModelYear());
        validatePrice(product.getListPrice());
    }

    private void validateProductName(String productName) throws ValidationException {
        if (productName == null || productName.trim().isEmpty()) {
            throw new ValidationException("Tên sản phẩm là bắt buộc.");
        }
        if (productName.trim().length() > 255) {
            throw new ValidationException("Tên sản phẩm không được vượt quá 255 ký tự.");
        }
    }

    private void validateIds(int brandId, int categoryId) throws ValidationException {
        if (brandId <= 0) {
            throw new ValidationException("ID thương hiệu không hợp lệ.");
        }
        if (categoryId <= 0) {
            throw new ValidationException("ID danh mục không hợp lệ.");
        }
    }

    private void validateModelYear(int modelYear) throws ValidationException {
        int currentYear = java.time.Year.now().getValue();
        if (modelYear <= MIN_YEAR || modelYear > currentYear + MAX_FUTURE_YEARS) {
            throw new ValidationException("Năm sản xuất không hợp lệ.");
        }
    }

    private void validatePrice(double listPrice) throws ValidationException {
        if (listPrice < 0) {
            throw new ValidationException("Giá niêm yết không được âm.");
        }
    }

    private void validateProductId(int productId) throws ValidationException {
        if (productId <= 0) {
            throw new ValidationException("ID sản phẩm không hợp lệ.");
        }
    }

    private void validateBusinessRules(Products product) throws ValidationException {
            ///
    }
}