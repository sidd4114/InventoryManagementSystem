package gui;

import functions.InventoryFunctions;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InventoryGUI {

    private JFrame frame;
    private JTextField itemIdField;
    private JTextField itemNameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField descriptionField;
    private InventoryFunctions functions;
    private DefaultTableModel tableModel;

    // Colors that we are using for theme
    private final Color burntOrange = new Color(0xCC5500);
    private final Color darkerBurntOrange = new Color(0xA63E00);
    private final Color white = Color.WHITE;
    private final Color softBeige = new Color(0xFAF3E0); // Soft beige color

    public InventoryGUI() {
        functions = new InventoryFunctions();
        showSplashScreen(); // Show splash screen before initializing main GUI
    }

    private void showSplashScreen() {
        // Create a JWindow for the splash screen
        JWindow splash = new JWindow();

        // Load the image from the file path and scaling it
        ImageIcon splashImage = new ImageIcon("C:/Users/siddh/DBMStrial/images/coverpage.jpg");
        Image scaledImage = splashImage.getImage().getScaledInstance(1366, 768, Image.SCALE_SMOOTH);
        JLabel splashLabel = new JLabel(new ImageIcon(scaledImage));

        // Set the size of the splash screen
        splash.setSize(1366, 768);
        splash.setLocationRelativeTo(null);
        splash.getContentPane().add(splashLabel);
        splash.setVisible(true);

        // Use SwingWorker to handle the delay and initialization of the main GUI
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // Simulate a delay of 3 seconds
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                splash.setVisible(false); // Hide the splash screen
                initializeMainGUI(); // Load the main GUI
            }
        };

        worker.execute(); // Start the background task
    }

    private void initializeMainGUI() {
        frame = new JFrame("Inventory Management");

        // Set frame size and center on screen
        frame.setSize(1366, 768);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Set background color for the frame
        frame.getContentPane().setBackground(softBeige); 

        // Panel for Input Fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Adding New Item"));
        inputPanel.setBackground(softBeige); // Set background color for input panel

        inputPanel.add(new JLabel("Item ID:"));
        itemIdField = new JTextField();
        inputPanel.add(itemIdField);

        inputPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        inputPanel.add(itemNameField);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Panel for Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(softBeige); // Set background color for button panel

        // Create buttons with styling
        JButton addButton = createStyledButton("Add Item");
        addButton.addActionListener(e -> addItem());
        buttonPanel.add(addButton);

        JButton removeButton = createStyledButton("Remove Item");
        removeButton.addActionListener(e -> removeItemPopup());
        buttonPanel.add(removeButton);

        JButton updateButton = createStyledButton("Update Item");
        updateButton.addActionListener(e -> updateItemPopup());
        buttonPanel.add(updateButton);

        JButton viewButton = createStyledButton("View Items");
        viewButton.addActionListener(e -> viewItems());
        buttonPanel.add(viewButton);
        JButton searchButton = createStyledButton("Search Item");
        searchButton.addActionListener(e -> searchItemPopup());
        buttonPanel.add(searchButton);
        

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Table for displaying items
        String[] columnNames = {"Sr. No.", "Item ID", "Item Name", "Quantity", "Price", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable itemTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(itemTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Inventory Items"));
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Set table properties
        itemTable.setFillsViewportHeight(true);
        itemTable.setBackground(Color.WHITE); // Keeping table background white for contrast
        itemTable.setForeground(Color.BLACK);

        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(burntOrange);
        button.setForeground(white);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkerBurntOrange);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(burntOrange);
            }
        });

        return button;
    }

    private void addItem() {
        try {
            int itemId = Integer.parseInt(itemIdField.getText());
            String itemName = itemNameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            String description = descriptionField.getText();
    
            // Check if the item already exists
            if (functions.itemExists(itemId, itemName)) {
                functions.incrementQuantity(itemId, quantity);
                showCustomMessageDialog("Item already exists. Quantity incremented.");
            } else {
                functions.addItem(itemId, itemName, quantity, price, description);
                showCustomMessageDialog("Item Added Successfully!");
            }
    
            clearFields();
        } catch (NumberFormatException e) {
            // Show custom dialog with error message
            showCustomMessageDialog("Please enter valid numbers for Item ID, Quantity, and Price.");
        }
    }
    

    private void removeItemPopup() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding for better spacing
    
        // Set teal background for the entire panel
        panel.setBackground(new Color(0, 128, 128)); // Teal color #008080
    
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel itemIdLabel = new JLabel("Item ID:");
        itemIdLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger and bold font
        itemIdLabel.setForeground(Color.WHITE); // Set text color to white for better contrast
        panel.add(itemIdLabel, gbc);
    
        JTextField itemIdField = new JTextField(12); // Increased size for text field
        itemIdField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for text field
        itemIdField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Slightly thicker border
        itemIdField.setBackground(new Color(245, 245, 245)); // Light background for text field
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(itemIdField, gbc);
    
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel quantityLabel = new JLabel("Quantity to Remove:");
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger and bold font
        quantityLabel.setForeground(Color.WHITE); // Set text color to white for better contrast
        panel.add(quantityLabel, gbc);
    
        JTextField quantityField = new JTextField(12); // Increased size for text field
        quantityField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for text field
        quantityField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Slightly thicker border
        quantityField.setBackground(new Color(245, 245, 245)); // Light background for text field
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(quantityField, gbc);
    
        // Create and set the dialog
        JDialog dialog = new JDialog(frame, "Remove Item", true);
        dialog.getContentPane().add(panel);
        dialog.setSize(700, 550); // Set the width to 700 and height to 550
        dialog.setLocationRelativeTo(frame); // Center on parent frame
        dialog.getContentPane().setBackground(new Color(0, 128, 128)); // Teal color #008080 for the dialog background
    
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16)); // Larger button text
        confirmButton.setBackground(new Color(34, 139, 34)); // Set Confirm button color (green)
        confirmButton.setForeground(Color.WHITE); // Set text color to white for better visibility
        confirmButton.addActionListener(e -> {
            try {
                int itemId = Integer.parseInt(itemIdField.getText());
                int quantityToRemove = Integer.parseInt(quantityField.getText());
    
                boolean success = functions.removeItemQuantity(itemId, quantityToRemove);
                if (success) {
                    showCustomMessageDialog("Item quantity removed successfully!");
                } else {
                    showCustomMessageDialog("Not enough quantity to remove.");
                }
                dialog.dispose(); // Close the dialog
            } catch (NumberFormatException ex) {
                showCustomMessageDialog("Please enter valid numbers for Item ID and Quantity.");
            }
        });
    
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16)); // Larger button text
        cancelButton.setBackground(new Color(178, 34, 34)); // Set Cancel button color (red)
        cancelButton.setForeground(Color.WHITE); // Set text color to white for better visibility
        cancelButton.addActionListener(e -> dialog.dispose());
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 128, 128)); // Ensure button panel background matches teal
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
    
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showCustomMessageDialog(String message) {
        // Create a custom dialog
        JDialog messageDialog = new JDialog(frame, "Alert", true);
        messageDialog.setSize(400, 200); // Set size of the dialog
        messageDialog.setLocationRelativeTo(frame); // Center on parent frame
        messageDialog.getContentPane().setBackground(new Color(0, 128, 128)); // Teal color #008080
    
        // Create a panel for the message
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(new Color(0, 128, 128)); // Same teal background
        messagePanel.setLayout(new BorderLayout());
    
        // Use HTML in JLabel to allow text wrapping
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Font size and style
        messageLabel.setForeground(Color.WHITE); // Set text color to white for better contrast
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
    
        // OK button
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 16)); // Larger button text
        okButton.setBackground(new Color(34, 139, 34)); // Set button color (green)
        okButton.setForeground(Color.WHITE); // Set text color to white for better visibility
        okButton.addActionListener(e -> messageDialog.dispose()); // Close the dialog on button click
    
        // Add components to the message panel
        messagePanel.add(messageLabel, BorderLayout.CENTER); // Add message label
        messagePanel.add(okButton, BorderLayout.SOUTH); // Add OK button
    
        // Add the message panel to the dialog
        messageDialog.getContentPane().add(messagePanel);
        messageDialog.setVisible(true); // Show the custom message dialog
    }
    
    private void updateItemPopup() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding for better spacing
    
        // Set teal background for the entire panel
        panel.setBackground(new Color(0, 128, 128)); // Teal color #008080
    
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel itemIdLabel = new JLabel("Item ID:");
        itemIdLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger and bold font
        itemIdLabel.setForeground(Color.WHITE); // Set text color to white for better contrast
        panel.add(itemIdLabel, gbc);
    
        JTextField itemIdField = new JTextField(12); // Increased size for text field
        itemIdField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for text field
        itemIdField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Slightly thicker border
        itemIdField.setBackground(new Color(245, 245, 245)); // Light background for text field
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(itemIdField, gbc);
    
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger and bold font
        itemNameLabel.setForeground(Color.WHITE); // Set text color to white for better contrast
        panel.add(itemNameLabel, gbc);
    
        JTextField itemNameField = new JTextField(12); // Increased size for text field
        itemNameField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for text field
        itemNameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Slightly thicker border
        itemNameField.setBackground(new Color(245, 245, 245)); // Light background for text field
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(itemNameField, gbc);
    
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel priceLabel = new JLabel("New Price:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger and bold font
        priceLabel.setForeground(Color.WHITE); // Set text color to white for better contrast
        panel.add(priceLabel, gbc);
    
        JTextField priceField = new JTextField(12); // Increased size for text field
        priceField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for text field
        priceField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Slightly thicker border
        priceField.setBackground(new Color(245, 245, 245)); // Light background for text field
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(priceField, gbc);
    
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel descriptionLabel = new JLabel("New Description(Optional):");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger and bold font
        descriptionLabel.setForeground(Color.WHITE); // Set text color to white for better contrast
        panel.add(descriptionLabel, gbc);
    
        JTextField descriptionField = new JTextField(12); // Increased size for text field
        descriptionField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for text field
        descriptionField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Slightly thicker border
        descriptionField.setBackground(new Color(245, 245, 245)); // Light background for text field
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(descriptionField, gbc);
    
        // Create and set the dialog
        JDialog dialog = new JDialog(frame, "Update Item", true);
        dialog.getContentPane().add(panel);
        dialog.setSize(700, 550); // Set the width to 700 and height to 550
        dialog.setLocationRelativeTo(frame); // Center on parent frame
        dialog.getContentPane().setBackground(new Color(0, 128, 128)); // Teal color #008080 for the dialog background
    
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16)); // Larger button text
        confirmButton.setBackground(new Color(34, 139, 34)); // Set Confirm button color (green)
        confirmButton.setForeground(Color.WHITE); // Set text color to white for better visibility
        confirmButton.addActionListener(e -> {
            try {
                int itemId = Integer.parseInt(itemIdField.getText());
                String itemName = itemNameField.getText();
                double newPrice = Double.parseDouble(priceField.getText());
                String newDescription = descriptionField.getText();
    
                // Check if description is empty; if so, pass null to retain the old description
                if (newDescription.isEmpty()) {
                    newDescription = null;
                }
    
                functions.updateItem(itemId, itemName, newPrice, newDescription);
                showCustomMessageDialog("Item updated successfully!");
                dialog.dispose(); // Close the dialog
            } catch (NumberFormatException ex) {
                showCustomMessageDialog("Please enter valid numbers for Item ID, Name, and Price.");
            }
        });
    
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16)); // Larger button text
        cancelButton.setBackground(new Color(178, 34, 34)); // Set Cancel button color (red)
        cancelButton.setForeground(Color.WHITE); // Set text color to white for better visibility
        cancelButton.addActionListener(e -> dialog.dispose());
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 128, 128)); // Ensure button panel background matches teal
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
    
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    

    private void viewItems() {
        try {
            ResultSet rs = functions.getAllItems();
            tableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                int itemId = rs.getInt("itemId");
                String itemName = rs.getString("itemName");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String description = rs.getString("itemdescription");
                tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, itemId, itemName, quantity, price, description});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error retrieving data: " + e.getMessage());
        }
    }
    private void searchItemPopup() {
        // Create a panel for the search popup
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15); // Uniform padding for all fields
    
        // Set panel background color similar to update UI (e.g., teal)
        panel.setBackground(new Color(0, 128, 128)); // Teal color #008080
    
        // Label for "Item ID"
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel itemIdLabel = new JLabel("Item ID:");
        itemIdLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Consistent bold font
        itemIdLabel.setForeground(Color.WHITE); // White font for contrast
        panel.add(itemIdLabel, gbc);
    
        // Text field for entering "Item ID"
        JTextField itemIdField = new JTextField(12);
        itemIdField.setFont(new Font("Arial", Font.PLAIN, 18)); // Same font size for text field
        itemIdField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Thicker border for input field
        itemIdField.setBackground(new Color(245, 245, 245)); // Light background
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(itemIdField, gbc);
    
        // Label for "Item Name"
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Consistent bold font
        itemNameLabel.setForeground(Color.WHITE); // White font
        panel.add(itemNameLabel, gbc);
    
        // Text field for entering "Item Name"
        JTextField itemNameField = new JTextField(12);
        itemNameField.setFont(new Font("Arial", Font.PLAIN, 18)); // Same font size for text field
        itemNameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Thicker border
        itemNameField.setBackground(new Color(245, 245, 245)); // Light background
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(itemNameField, gbc);
    
        // Create Confirm and Cancel buttons
        JButton confirmButton = new JButton("Search");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmButton.setBackground(new Color(34, 139, 34)); // Green background
        confirmButton.setForeground(Color.WHITE); // White text
    
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setBackground(new Color(178, 34, 34)); // Red background
        cancelButton.setForeground(Color.WHITE); // White text
    
        // Create the dialog for the search item popup
        final JDialog dialog = new JDialog(frame, "Search Item", true); // Declare dialog as final
        dialog.getContentPane().add(panel, BorderLayout.CENTER);
    
        // Add action listener for the confirm button
        confirmButton.addActionListener(e -> {
            String itemIdText = itemIdField.getText();
            String itemName = itemNameField.getText();
            searchItem(itemIdText, itemName);  // Call the search function
        });
    
        // Add action listener for the cancel button to close the dialog
        cancelButton.addActionListener(e -> dialog.dispose());
    
        // Create a panel for buttons and add the buttons to it
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 128, 128)); // Consistent teal background
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
    
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH); // Add button panel at the bottom
        dialog.setSize(500, 300); // Adjusted size
        dialog.setLocationRelativeTo(frame); // Center it relative to parent frame
        dialog.getContentPane().setBackground(new Color(0, 128, 128)); // Set dialog background
        dialog.setVisible(true); // Show the dialog
    }
    
    private void searchItem(String itemIdText, String itemName) {
        try {
            int itemId = itemIdText.isEmpty() ? -1 : Integer.parseInt(itemIdText);
            ResultSet rs = functions.searchItem(itemId, itemName);
    
            // Create a new JDialog to display search results
            JDialog resultDialog = new JDialog(frame, "Search Results", true);
            resultDialog.setSize(600, 400); // Adjust size as needed
            resultDialog.setLocationRelativeTo(frame);
            resultDialog.setLayout(new BorderLayout());
    
            // Set the background color to match the search item popup
            resultDialog.getContentPane().setBackground(new Color(0, 128, 128)); // Teal color
    
            // Create a table to display results
            String[] columnNames = {"Item ID", "Item Name", "Quantity", "Price", "Description"};
            DefaultTableModel resultTableModel = new DefaultTableModel(columnNames, 0);
            JTable resultTable = new JTable(resultTableModel);
            resultTable.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font for the table
            resultTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering
            JScrollPane resultScrollPane = new JScrollPane(resultTable);
            resultScrollPane.setBackground(new Color(0, 128, 128)); // Match background
            resultDialog.add(resultScrollPane, BorderLayout.CENTER);
    
            // Populate the table with the search results
            while (rs.next()) {
                int id = rs.getInt("itemId");
                String name = rs.getString("itemName");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String description = rs.getString("itemdescription");
                resultTableModel.addRow(new Object[]{id, name, quantity, price, description});
            }
    
            // Create a "Close" button to close the dialog
            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("Arial", Font.BOLD, 16));
            closeButton.setBackground(new Color(178, 34, 34)); // Red background
            closeButton.setForeground(Color.WHITE); // White text
    
            closeButton.addActionListener(e -> resultDialog.dispose()); // Action to close the dialog
    
            // Create a panel for the close button and add it
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(0, 128, 128)); // Match background
            buttonPanel.add(closeButton);
            resultDialog.add(buttonPanel, BorderLayout.SOUTH); // Add button panel at the bottom
    
            resultDialog.setVisible(true); // Show the dialog
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error retrieving item: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid Item ID.");
        }
    }
    
    

    private void clearFields() {
        itemIdField.setText("");
        itemNameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        descriptionField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryGUI::new);
    }
}
