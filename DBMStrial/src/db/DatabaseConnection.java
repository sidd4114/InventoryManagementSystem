package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_management";  // Replace 'yourDatabaseName' with your actual DB name
    private static final String USER = "root";  
    private static final String PASSWORD = "siddhen@2030";  

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

