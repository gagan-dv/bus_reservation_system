package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.security.SecureRandom;
import java.util.Base64;

public class ForgotPassword extends JFrame {
    private JTextField emailField, tokenField;
    private JPasswordField newPasswordField;
    private JButton requestButton, resetButton, backButton;

    // ==== Main Method ====
    public static void main(String[] args) {
        new ForgotPassword();
    }

    // ==== Constructor ====
    public ForgotPassword() {
        super("Forgot Password");

        // ==== Window Setup ====
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 248, 255));
        setLayout(null);

        // ==== Title Panel ====
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 500, 50);
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Forgot Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel);

        // ==== Email ====
        JLabel emailLabel = new JLabel("Registered Email:");
        emailLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        emailLabel.setBounds(80, 100, 150, 30);
        add(emailLabel);

        emailField = new JTextField();
        styleTextField(emailField);
        emailField.setBounds(240, 100, 180, 30);
        add(emailField);

        requestButton = new JButton("Send Reset Token");
        styleButton(requestButton, new Color(0, 102, 204));
        requestButton.setBounds(150, 150, 200, 40);
        add(requestButton);

        // ==== Token ====
        JLabel tokenLabel = new JLabel("Enter Token:");
        tokenLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        tokenLabel.setBounds(80, 210, 150, 30);
        add(tokenLabel);

        tokenField = new JTextField();
        styleTextField(tokenField);
        tokenField.setBounds(240, 210, 180, 30);
        add(tokenField);

        // ==== New Password ====
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        newPassLabel.setBounds(80, 260, 150, 30);
        add(newPassLabel);

        newPasswordField = new JPasswordField();
        styleTextField(newPasswordField);
        newPasswordField.setBounds(240, 260, 180, 30);
        add(newPasswordField);

        resetButton = new JButton("Reset Password");
        styleButton(resetButton, new Color(0, 153, 76));
        resetButton.setBounds(150, 320, 200, 40);
        add(resetButton);

        // ==== Back Button ====
        backButton = new JButton("Back to Login");
        styleButton(backButton, new Color(153, 153, 153));
        backButton.setBounds(150, 380, 200, 40);
        add(backButton);

        // ==== Button Actions ====
        requestButton.addActionListener(e -> handleRequestReset());
        resetButton.addActionListener(e -> handlePasswordReset());
        backButton.addActionListener(e -> {
            dispose();
            new Login();
        });

        // ==== Show Window ====
        setVisible(true);
    }

    // ==== Reusable style for text fields ====
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Poppins", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setBackground(Color.WHITE);
    }

    // ==== Reusable style for buttons ====
    private void styleButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ==== Generate secure random token ====
    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // ==== Step 1: Request reset ====
    private void handleRequestReset() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter your registered email.");
            return;
        }

        String token = generateToken();
        String sql = "UPDATE Users SET reset_token=?, reset_token_expiry=NOW() + INTERVAL 15 MINUTE WHERE email=?";

        try (Connection con = Connect_Db.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, token);
            pst.setString(2, email);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                String subject = "Bus Reservation Password Reset";
                String body = "Your reset token is: " + token + "\n\nValid for 15 minutes.";
                EmailUtils.sendEmail(email, subject, body);
                JOptionPane.showMessageDialog(this, "A reset token has been sent to your email.");
            } else {
                JOptionPane.showMessageDialog(this, "No account found with this email.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ==== Step 2: Reset password ====
    private void handlePasswordReset() {
        String email = emailField.getText().trim();
        String token = tokenField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());

        if (email.isEmpty() || token.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        String check = "SELECT * FROM Users WHERE email=? AND reset_token=? AND reset_token_expiry > NOW()";
        try (Connection con = Connect_Db.getConnection();
             PreparedStatement pst = con.prepareStatement(check)) {

            pst.setString(1, email);
            pst.setString(2, token);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String update = "UPDATE Users SET password=?, reset_token=NULL, reset_token_expiry=NULL WHERE email=?";
                try (PreparedStatement pst2 = con.prepareStatement(update)) {
                    pst2.setString(1, newPassword);
                    pst2.setString(2, email);
                    pst2.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Password reset successful! You can now log in.");
                dispose();
                new Login();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid or expired token.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
