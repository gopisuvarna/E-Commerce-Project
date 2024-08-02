import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentOptionsFrame extends JFrame {
    private JTextField addressTextField;
    private JLabel addressLabel;
    private int uid; // Store uid here instead of username
    private String username;
    public PaymentOptionsFrame(String cartItemsText, double totalAmount, String address,int uid) {
        setTitle("Payment Options");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load the background image
                ImageIcon backgroundImage = new ImageIcon("src/pay.jpg");
                // Draw the image at the panel's top-left corner
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        add(mainPanel);


        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("Payment Options");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);

        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Payment Options"));
        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        JRadioButton option1RadioButton = new JRadioButton("UPI");
        JRadioButton option2RadioButton = new JRadioButton("Card");
        JRadioButton option3RadioButton = new JRadioButton("Net Banking");

        ButtonGroup paymentOptionsGroup = new ButtonGroup();
        paymentOptionsGroup.add(option1RadioButton);
        paymentOptionsGroup.add(option2RadioButton);
        paymentOptionsGroup.add(option3RadioButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        optionsPanel.add(option1RadioButton, gbc);

        gbc.gridy++;
        optionsPanel.add(option2RadioButton, gbc);

        gbc.gridy++;
        optionsPanel.add(option3RadioButton, gbc);

        addressTextField = new JTextField();
        addressTextField.setPreferredSize(new Dimension(300, 50)); // Set preferred size
        addressLabel = new JLabel("Address");
        gbc.gridy++;
        optionsPanel.add(addressLabel, gbc);

        gbc.gridy++;
        optionsPanel.add(addressTextField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton proceedButton = new JButton("Proceed");
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = "";
                if (option1RadioButton.isSelected()) {
                    selectedOption = "UPI";
                } else if (option2RadioButton.isSelected()) {
                    selectedOption = "Card";
                } else if (option3RadioButton.isSelected()) {
                    selectedOption = "Net Banking";
                } else {
                    JOptionPane.showMessageDialog(PaymentOptionsFrame.this, "Please select a payment option.");
                    return;
                }

                String address = addressTextField.getText();
                if (address.isEmpty()) {
                    JOptionPane.showMessageDialog(PaymentOptionsFrame.this, "Please enter an address.");
                    return;
                }

                double amount = totalAmount; // Assuming totalAmount is accessible here

                insertPayment(uid, selectedOption, amount);

                JOptionPane.showMessageDialog(PaymentOptionsFrame.this, "Selected Option: " + selectedOption);
                JOptionPane.showMessageDialog(PaymentOptionsFrame.this, "Payment Successful\nSave the Receipt");

                generateReceipt(cartItemsText, totalAmount, address);

                JOptionPane.showMessageDialog(PaymentOptionsFrame.this, "Receipt Downloaded\nVisit again");
                dispose();
                UserDashboard userDashboard = new UserDashboard(uid);
                userDashboard.setVisible(true);
            }
        });
        buttonPanel.add(proceedButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Retrieve uid based on username
        //this.uid = retrieveUid();
    }

    private int retrieveUid(String username) {
        int uid = -1;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/ecommerce", "root", "suvarna")) {

            String query = "SELECT rid FROM registration WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                uid = rs.getInt("rid");
            } else {
                JOptionPane.showMessageDialog(this, "Username not found in registration table.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while retrieving UID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return uid;
    }

    private void insertPayment(int uid, String paymentMethod, double amount) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/ecommerce", "root", "suvarna")) {

            String query = "INSERT INTO payments(uid, payment_method, amount) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, uid);
            ps.setString(2, paymentMethod);
            ps.setDouble(3, amount);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment details inserted successfully.");
            } else {
                System.out.println("Failed to insert payment details.");
            }
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while processing payment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReceipt(String cartItemsText, double totalAmount, String address) {
        JFileChooser fileChooser = new JFileChooser();
        int userChoice = fileChooser.showSaveDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                writer.write("Receipt\n\n");
                writer.write("Items in Cart:\n\n");
                writer.write(cartItemsText);
                writer.write("\n\n");
                writer.write("Total Amount: ₹" + totalAmount);
                writer.write("\n\n");
                writer.write("Address: " + address);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
