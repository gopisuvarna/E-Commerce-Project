import javax.imageio.ImageIO;
import javax.sql.RowSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDashboard extends JFrame {

    private JTextArea cartTextArea;
    private double totalAmount;
    private JLabel totalLabel;
    private List<Product> productList;
    private int userId;

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/ecommerce";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "suvarna";

    // SQL queries for product management
    private static final String INSERT_PRODUCT_SQL = "INSERT INTO products (pname, image, price, category, rating) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_PRODUCT_SQL = "UPDATE products SET pname = ?, image = ?, price = ?, category = ?, rating = ? WHERE pid = ?";
    private static final String DELETE_PRODUCT_SQL = "DELETE FROM products WHERE pid = ?";

    public UserDashboard(int userId) {
        this.userId=userId;
        setTitle("SMART MART");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        productList = new ArrayList<>();
        cartTextArea = new JTextArea(10, 30);
        // Load products from the database
        loadProductsFromDatabase();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        add(mainPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(32, 178, 170));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("SMART MART products");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Create and add the categories combo box
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBackground(new Color(244, 194, 194));
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("All");
        categoryComboBox.addItem("Electronics");
        categoryComboBox.addItem("Clothing");
        categoryComboBox.addItem("Books");
        categoryComboBox.addItem("Home Decor");
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryComboBox);
        contentPanel.add(categoryPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel productsPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        productsPanel.setBorder(BorderFactory.createTitledBorder("Products"));
        productsPanel.setBackground(new Color(245, 245, 176));
        scrollPane.setViewportView(productsPanel);

        updateProductPanel(productsPanel, "All");


        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton resetButton = new JButton("Reset");
        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(resetPanel, BorderLayout.WEST);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetCart();
            }
        });
        resetPanel.add(resetButton);

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (totalAmount == 0.0) {
                    JOptionPane.showMessageDialog(UserDashboard.this, "Please select products before checkout.");
                } else {
                    JOptionPane.showMessageDialog(UserDashboard.this, "Checkout complete!");
                    BillingDashboard billingDashboard = new BillingDashboard(UserDashboard.this);
                    billingDashboard.setVisible(true);

                }
            }
        });

        JButton insertButton = new JButton("Insert Product");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call a method to handle inserting a new product
                insertProductDialog();
            }
        });

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call a method to handle deleting a product
                deleteProductDialog();
            }
        });

        JButton updateButton = new JButton("Update Product");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call a method to handle updating a product
                updateProductDialog();
            }
        });
        bottomPanel.setBackground(new Color(32, 178, 170));
        bottomPanel.add(insertButton);
        bottomPanel.add(updateButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(checkoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                updateProductPanel(productsPanel, selectedCategory);
            }
        });
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
        cartPanel.setBackground(new Color(181, 235, 255));
        mainPanel.add(cartPanel, BorderLayout.EAST);

        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(new Font("Arial", Font.BOLD, 18));
        cartPanel.add(cartLabel, BorderLayout.NORTH);

        cartTextArea = new JTextArea(10, 10);
        cartTextArea.setEditable(false);
        cartTextArea.setFont(new Font("Arial", Font.PLAIN, 15)); // Set the font size
        JScrollPane cartScrollPane = new JScrollPane(cartTextArea);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("Total Amount: ₹0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        cartPanel.add(totalLabel, BorderLayout.SOUTH);
    }

    private void loadProductsFromDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM products";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String productName = resultSet.getString("pname");
                    String imagePath = resultSet.getString("image");
                    String price = resultSet.getString("price");
                    double amount = resultSet.getDouble("price");
                    String category = resultSet.getString("category");
                    int rating = resultSet.getInt("rating"); // Retrieve rating from the result set

                    productList.add(new Product(productName, imagePath, price, amount, category, rating)); // Pass rating to Product constructor
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProductPanel(JPanel productsPanel, String selectedCategory) {

        productsPanel.removeAll();
        JPanel productContainer = new JPanel(new GridLayout(0, 4, 10, 10));

        // Iterate over the products and add only those matching the selected category
        for (Product product : productList) {
            if (selectedCategory.equals("All") || product.getCategory().equals(selectedCategory)) {
                addProductPanel(productContainer, product);
            }
        }

        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        productsPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh the panel
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    public void resetCart() {
        cartTextArea.setText("");
        totalAmount = 0.0;
        totalLabel.setText("Total Amount: ₹0.00");
    }

    private void addProductPanel(JPanel productsPanel, Product product) {
        try {
            // Load image from URL
            ImageIcon productImage = new ImageIcon(new URL(product.getImagePath()));
            Image scaledProductImage = productImage.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);

            JPanel productPanel = new JPanel(new GridBagLayout());
            productPanel.setPreferredSize(new Dimension(315, 250)); // Adjusted size to accommodate image
            productPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel imageLabel = new JLabel(new ImageIcon(scaledProductImage));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 0, 10, 0);
            productPanel.add(imageLabel, gbc);

            JLabel nameLabel = new JLabel(product.getProductName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Increase font size
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 0, 5, 0);
            productPanel.add(nameLabel, gbc);

            JLabel priceLabel = new JLabel(product.getPrice());
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Increase font size
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 0, 5, 0);
            productPanel.add(priceLabel, gbc);

            JLabel ratingLabel = new JLabel("Rating: " + product.getRating()); // Add JLabel for displaying rating
            ratingLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Increase font size
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 0, 5, 0);
            productPanel.add(ratingLabel, gbc);

            JButton addToCartButton = new JButton("Add to Cart");
            addToCartButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Increase font size
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            productPanel.add(addToCartButton, gbc);

            addToCartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String cartItem = product.getProductName() + " - " + product.getPrice();
                    cartTextArea.append(cartItem + "\n");
                    totalAmount += product.getAmount();
                    totalLabel.setText(String.format("Total Amount: ₹%.2f", totalAmount));
                }
            });

            productsPanel.add(productPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to handle inserting a new product
    private void insertProductDialog() {
        // Create dialog components
        JTextField nameField = new JTextField(10);
        JTextField imageField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JTextField categoryField = new JTextField(10);
        JTextField ratingField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Image URL:"));
        panel.add(imageField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Rating:"));
        panel.add(ratingField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Insert Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT_SQL)) {
                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setString(2, imageField.getText());
                preparedStatement.setDouble(3, Double.parseDouble(priceField.getText()));
                preparedStatement.setString(4, categoryField.getText());
                preparedStatement.setInt(5, Integer.parseInt(ratingField.getText()));
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Product inserted successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to handle updating an existing product
    private void updateProductDialog() {
        // Create dialog components
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(10);
        JTextField imageField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JTextField categoryField = new JTextField(10);
        JTextField ratingField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Product ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Image URL:"));
        panel.add(imageField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Rating:"));
        panel.add(ratingField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCT_SQL)) {
                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setString(2, imageField.getText());
                preparedStatement.setDouble(3, Double.parseDouble(priceField.getText()));
                preparedStatement.setString(4, categoryField.getText());
                preparedStatement.setInt(5, Integer.parseInt(ratingField.getText()));
                preparedStatement.setInt(6, Integer.parseInt(idField.getText()));
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Product updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update product. Product ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to handle deleting an existing product
    private void deleteProductDialog() {
        JTextField idField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Product ID:"));
        panel.add(idField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Delete Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCT_SQL)) {
                preparedStatement.setInt(1, Integer.parseInt(idField.getText()));
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Product deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete product. Product ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public JTextArea getCartTextArea() {
        return cartTextArea;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<Product> getProductList(){
        return productList;
    }
    public int getUid() {
        return userId;
    }

    // Inner Product class definition
    public class Product {
        private String productName;
        private String imagePath;
        private String price;
        private double amount;
        private String category;
        private int rating;

        public Product(String productName, String imagePath, String price, double amount, String category, int rating) {
            this.productName = productName;
            this.imagePath = imagePath;
            this.price = price;
            this.amount = amount;
            this.category = category;
            this.rating = rating;
        }

        public String getProductName() {
            return productName;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getPrice() {
            return price;
        }

        public double getAmount() {
            return amount;
        }

        public String getCategory() {
            return category;
        }

        public int getRating() {
            return rating;
        }
    }

    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UserDashboard(userId);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Simulate a login scenario (replace with actual login/authentication logic)
                String username = JOptionPane.showInputDialog("Enter username:");
                String password = JOptionPane.showInputDialog("Enter password:");

                // Example of checking username and password (replace with your authentication logic)
                int userId = authenticateUser(username, password);

                if (userId != -1) {
                    UserDashboard userDashboard = new UserDashboard(userId);
                    userDashboard.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Exiting...");
                    System.exit(0); // Exit application if authentication fails
                }
            }
        });
    }

    private static int authenticateUser(String username, String password) {
        int userId = -1; // Initialize userId to -1 (or any default value indicating failure)

        // Example logic to authenticate user (replace with your actual authentication mechanism)
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT rid FROM registration WHERE name = ? AND pass = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userId = resultSet.getInt("id"); // Retrieve user ID if authentication successful
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database connection or query execution errors
        }

        return userId;
    }
}


