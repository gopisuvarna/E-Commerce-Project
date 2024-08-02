import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class registerform extends JFrame implements ActionListener {
    private JTextField nameField, emailField, phNoField, dobField, addressField, pinField;
    private JPasswordField passField;
    private Image backgroundImage;

    public registerform() {
        setTitle("Registration Form");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            // Load background image using relative path (update path as per your image location)
            backgroundImage = ImageIO.read(new File("src/cart.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BackgroundPanel bgPanel = new BackgroundPanel();
        bgPanel.setLayout(new GridBagLayout());
        bgPanel.setOpaque(false);
        add(bgPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabelAndField(bgPanel, gbc, "Username:", nameField = new JTextField(15));
        addLabelAndField(bgPanel, gbc, "Password:", passField = new JPasswordField(15));
        addLabelAndField(bgPanel, gbc, "E-mail:", emailField = new JTextField(15));
        addLabelAndField(bgPanel, gbc, "Ph no:", phNoField = new JTextField(15));
        addLabelAndField(bgPanel, gbc, "DOB (yyyy-mm-dd):", dobField = new JTextField(15));
        addLabelAndField(bgPanel, gbc, "Address:", addressField = new JTextField(15));
        addLabelAndField(bgPanel, gbc, "Pin-code:", pinField = new JTextField(15));

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(92, 159, 96));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(this);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        bgPanel.add(submitButton, gbc);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        gbc.gridx = 1;
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setForeground(Color.BLACK); // Set the font color for the text field
        textField.setBackground(new Color(255, 255, 255, 180)); // Set a semi-transparent white background
        textField.setOpaque(true);
        textField.setPreferredSize(new Dimension(200, 25)); // Set preferred size for larger text fields
        panel.add(textField, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Submit")) {
            registerUser();
        }
    }

    private void registerUser() {
        String name = nameField.getText();
        String password = new String(passField.getPassword());
        String email = emailField.getText();
        String phNo = phNoField.getText();
        String dob = dobField.getText();
        String address = addressField.getText();
        String pin = pinField.getText();

        if (!isValidInput(name, password, email, phNo, dob, address, pin)) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your entries.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ecommerce", "root", "suvarna")) {

            // Insert into registration table
            String registrationQuery = "INSERT INTO registration(name, pass, email, phno, dob, address, pin) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement registrationPs = connection.prepareStatement(registrationQuery);
            registrationPs.setString(1, name);
            registrationPs.setString(2, password);
            registrationPs.setString(3, email);
            registrationPs.setInt(4, Integer.parseInt(phNo));
            registrationPs.setDate(5, Date.valueOf(dob));
            registrationPs.setString(6, address);
            registrationPs.setInt(7, Integer.parseInt(pin));

            int registrationRowsAffected = registrationPs.executeUpdate();
            if (registrationRowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                new Login();  // Open login window after successful registration
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
            registrationPs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private boolean isValidInput(String name, String password, String email, String phNo, String dob, String address, String pin) {
        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || phNo.isEmpty() || dob.isEmpty() || address.isEmpty() || pin.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(phNo);
            Integer.parseInt(pin);
            Date.valueOf(dob);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(registerform::new);
    }
}


