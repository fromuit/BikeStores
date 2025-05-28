/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author duyng
 */
public class DatabaseUtil {
        private static Connection connection;
        
        public static Connection getConnection() throws SQLException {
            try {
                if (connection == null || connection.isClosed()) {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    
                    String dbURL = "jdbc:sqlserver://localhost:1433;"
                            + "databaseName=BikeStores;"
                            + "encrypt=true;trustServerCertificate=true;"
                            + "user=sa;password=sa";
                    
                    connection = DriverManager.getConnection(dbURL);
                }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy JDBC Driver!", e);
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }
}
