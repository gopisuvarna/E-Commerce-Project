import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BillingDashboard extends JFrame {
    private JTextArea cartTextArea;
    private double totalAmount;
    private JLabel totalLabel;
    private int uid; // Store uid here

    public BillingDashboard(UserDashboard userDashboard) {
        this.cartTextArea = userDashboard.getCartTextArea();
        this.totalAmount = userDashboard.getTotalAmount();
        this.uid = userDashboard.getUid(); // Fetch uid from UserDashboard

        setTitle("Billing Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Custom panel to draw background image
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            private Image backgroundImage = new ImageIcon("src/bill.jpg").getImage(); // Replace with your image path

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0)); // Remove padding

        // Header panel with title
        JLabel titleLabel = new JLabel("Billing Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font for title
        titleLabel.setForeground(Color.WHITE); // Set text color to white
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Display area for cart items and total amount
        JTextPane displayArea = new JTextPane();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setForeground(Color.WHITE); // Set text color to black
        displayArea.setOpaque(false); // Make text pane transparent

        // Set the background color only for the text content
        displayArea.setBackground(new Color(0, 0, 0, 0)); // Transparent background

        // Center the text
        StyledDocument doc = displayArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // Set the text with a white background for the text content
        displayArea.setText("Items in Cart:\n\n" + cartTextArea.getText() + "\n\nTotal Amount: ₹" + String.format("%.2f", totalAmount));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(500, 200)); // Adjust dimensions as needed
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Total amount label at the bottom
        totalLabel = new JLabel("Total Amount: ₹" + String.format("%.2f", totalAmount));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font for total amount
        totalLabel.setForeground(Color.WHITE); // Set text color to white
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.add(totalLabel, BorderLayout.EAST);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);

        // Proceed to Payment button
        JButton proceedToPaymentButton = new JButton("Proceed to Payment");
        proceedToPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(BillingDashboard.this, "Select Payment Option");

                // Get cart items text and address from displayArea and addressTextField
                String cartItemsText = displayArea.getText().substring(displayArea.getText().indexOf("Items in Cart:") + "Items in Cart:".length());
                String address = JOptionPane.showInputDialog(BillingDashboard.this, "Enter your address:");

                // Open the PaymentOptionsFrame and pass the necessary parameters
                PaymentOptionsFrame paymentOptionsFrame = new PaymentOptionsFrame(cartItemsText, totalAmount, address, uid);
                paymentOptionsFrame.setVisible(true);

                // Close the BillingDashboard frame
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(proceedToPaymentButton, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);

        // Set panel background
        mainPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent to show the background image

        // Add main panel to the frame
        add(mainPanel);
    }
}
