package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, backButton, forgotButton;

    public Login() {
        super("Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // 🔹 Title
        JLabel titleLabel = new JLabel("Bus Ticket Reservation Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBounds(50, 20, 400, 40);
        add(titleLabel);

        // 🔹 Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(80, 90, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(200, 90, 200, 30);
        add(emailField);

        // 🔹 Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(80, 140, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(200, 140, 200, 30);
        add(passwordField);

        // 🔹 Buttons
        loginButton = new JButton("Login");
        loginButton.setBounds(80, 200, 120, 40);
        styleButton(loginButton, new Color(0, 102, 204));
        loginButton.addActionListener(this);
        add(loginButton);

        backButton = new JButton("Back");
        backButton.setBounds(260, 200, 120, 40);
        styleButton(backButton, Color.GRAY);
        backButton.addActionListener(this);
        add(backButton);

        forgotButton = new JButton("Forgot Password?");
        forgotButton.setBounds(150, 260, 200, 30);
        forgotButton.setBackground(Color.WHITE);
        forgotButton.setForeground(new Color(0, 102, 204));
        forgotButton.setBorderPainted(false);
        forgotButton.setFont(new Font("Poppins", Font.PLAIN, 12));
        forgotButton.addActionListener(this);
        add(forgotButton);

        setVisible(true);
    }

    // 🔹 Reusable button style
    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            loginUser();
        } else if (e.getSource() == backButton) {
            setVisible(false);
            new Register().setVisible(true);
        } else if (e.getSource() == forgotButton) {
            setVisible(false);
            new ForgotPassword().setVisible(true);
        }
    }

    // 🔹 Simple login logic
    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        String query = "SELECT * FROM Users WHERE email=? AND password=?";

        try (Connection conn = Connect_Db.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String userName = rs.getString("name");

                JOptionPane.showMessageDialog(this, "Welcome " + userName + "!");
                new Home(userId, userName).setVisible(true);
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login. Try again.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
