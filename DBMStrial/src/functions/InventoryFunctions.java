package functions;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryFunctions {

    // Add a new item to the inventory
    public void addItem(int itemId, String itemName, int quantity, double price, String description) {
        String sql = "INSERT INTO inventory_stuff (itemId, itemName, quantity, price, itemDescription) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.setString(2, itemName);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, price);
            pstmt.setString(5, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if an item exists in inventory
    public boolean itemExists(int itemId, String itemName) {
        String sql = "SELECT * FROM inventory_stuff WHERE itemId = ? AND itemName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.setString(2, itemName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Increment quantity of an item
    public void incrementQuantity(int itemId, int quantity) {
        String sql = "UPDATE inventory_stuff SET quantity = quantity + ? WHERE itemId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove quantity of an item and delete the item if the quantity becomes zero
    public boolean removeItemQuantity(int itemId, int quantityToRemove) {
        String sqlCheck = "SELECT quantity FROM inventory_stuff WHERE itemId = ?";
        String sqlUpdate = "UPDATE inventory_stuff SET quantity = quantity - ? WHERE itemId = ?";
        String sqlDelete = "DELETE FROM inventory_stuff WHERE itemId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck);
             PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate);
             PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete)) {

            // Check the current quantity
            checkStmt.setInt(1, itemId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                if (quantityToRemove > currentQuantity) {
                    return false; // Not enough quantity to remove
                }

                // Update the quantity
                updateStmt.setInt(1, quantityToRemove);
                updateStmt.setInt(2, itemId);
                updateStmt.executeUpdate();

                // If quantity becomes 0, delete the item
                if (currentQuantity - quantityToRemove == 0) {
                    deleteStmt.setInt(1, itemId);
                    deleteStmt.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing item in the inventory
    public void updateItem(int itemId, String itemName, Double newPrice, String newDescription) {
        String sql = "UPDATE inventory_stuff SET price = COALESCE(?, price), itemDescription = COALESCE(?, itemDescription) WHERE itemId = ? AND itemName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, newPrice); // Allow null values for price
            pstmt.setString(2, newDescription); // Allow null values for description
            pstmt.setInt(3, itemId);
            pstmt.setString(4, itemName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all items from the inventory
    public ResultSet getAllItems() {
        String query = "SELECT * FROM inventory_stuff";
        ResultSet rs = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    // Search for an item by itemId or itemName
    public ResultSet searchItem(int itemId, String itemName) {
        String sql = "SELECT * FROM inventory_stuff WHERE itemId = ? OR itemName = ?";
        ResultSet rs = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            pstmt.setString(2, itemName);
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
