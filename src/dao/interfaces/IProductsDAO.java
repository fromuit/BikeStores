package dao.interfaces;

import java.util.ArrayList;
import model.Production.Products;

public interface IProductsDAO extends BaseDAO<Products, Integer> {
    ArrayList<Products> getProductsByCategoryId(int categoryId);
    ArrayList<Products> getProductsByBrandId(int brandId);
    ArrayList<Products> getAllProductsBasicInfo();
} 