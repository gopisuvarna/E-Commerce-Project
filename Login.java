import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame implements ActionListener {
    private JTextField t_us;
    private JPasswordField t_pas;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton submit;
    private JButton signup_but;
    private JLabel message;
    private Image backgroundImage;

    public Login() {
        setTitle("Smart Cart Login");

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("src/smart.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the panel with background image
        BackgroundPanel bgPanel = new BackgroundPanel();
        bgPanel.setLayout(new GridBagLayout());
        add(bgPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create headline label
        JLabel headlineLabel = new JLabel("LOGIN");
        headlineLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headlineLabel.setForeground(Color.WHITE);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        bgPanel.add(headlineLabel, gbc);

        // Create username label and field
        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        bgPanel.add(usernameLabel, gbc);

        t_us = new JTextField(15);
        t_us.setFont(new Font("Arial", Font.PLAIN, 14));
        t_us.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        gbc.gridy = 1;
        bgPanel.add(t_us, gbc);

        // Create password label and field
        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        bgPanel.add(passwordLabel, gbc);

        t_pas = new JPasswordField(15);
        t_pas.setFont(new Font("Arial", Font.PLAIN, 14));
        t_pas.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        gbc.gridy = 2;
        bgPanel.add(t_pas, gbc);

        // Create submit button
        submit = new JButton("Sign in");
        submit.setFont(new Font("Arial", Font.BOLD, 14));
        submit.setBackground(Color.BLUE);
        submit.setForeground(Color.WHITE);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        bgPanel.add(submit, gbc);
        submit.addActionListener(this);

        // Create signup button and message
        message = new JLabel("Don't have an account?");
        message.setFont(new Font("Arial", Font.PLAIN, 16));
        message.setForeground(Color.WHITE);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 4;
        bgPanel.add(message, gbc);

        signup_but = new JButton("Sign Up");
        signup_but.setFont(new Font("Arial", Font.BOLD, 14));
        signup_but.setBackground(Color.GREEN);
        signup_but.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 4;
        bgPanel.add(signup_but, gbc);
        signup_but.addActionListener(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String user = t_us.getText();
            String pass = new String(t_pas.getPassword());

            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommerce", "root", "suvarna");
                String query = "SELECT rid FROM registration WHERE name = ? AND pass = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, user);
                ps.setString(2, pass);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("rid"); // Retrieve userId
                    JOptionPane.showMessageDialog(this, "Login Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                    openUserDashboard(userId); // Open UserDashboard with userId
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == signup_but) {
            new registerform();
            this.dispose();
        }
    }

    private void openUserDashboard(int userId) {
        SwingUtilities.invokeLater(() -> {
            UserDashboard userDashboard = new UserDashboard(userId);
            userDashboard.setVisible(true);
        });
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
        SwingUtilities.invokeLater(Login::new);
    }
}
