package dao.interfaces;

import java.util.ArrayList;
import model.Production.Stocks;

/**
 * Interface for Stock Data Access Operations
 */
public interface IStocksDAO {
    
    // Stock-specific operations (different from standard CRUD)
    Stocks getStockByStoreAndProduct(int storeId, int productId);
    ArrayList<Stocks> getStocksByStore(int storeId);
    ArrayList<Stocks> getStocksByProduct(int productId);
    boolean updateStockQuantity(int storeId, int productId, int quantity);
} 