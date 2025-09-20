package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Register extends JFrame implements ActionListener {

    private JTextField emailField, nameField, phoneField;
    private JPasswordField passwordField;
    private JButton registerButton, exitButton;



    public Register() {

        setTitle("Register - Bus Ticket Reservation");
        setSize(500, 450);
        setLocationRelativeTo(null); // center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
        setContentPane(contentPane);
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBounds(120, 20, 300, 30);
        contentPane.add(titleLabel);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(50, 80, 100, 25);
        contentPane.add(lblName);

        nameField = new JTextField();
        nameField.setBounds(180, 80, 220, 25);
        contentPane.add(nameField);
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 120, 100, 25);
        contentPane.add(lblEmail);

        emailField = new JTextField();
        emailField.setBounds(180, 120, 220, 25);
        contentPane.add(emailField);

        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setBounds(50, 160, 100, 25);
        contentPane.add(lblPhone);

        phoneField = new JTextField();
        phoneField.setBounds(180, 160, 220, 25);
        contentPane.add(phoneField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 200, 100, 25);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(180, 200, 220, 25);
        contentPane.add(passwordField);
        registerButton = new JButton("Register");
        registerButton.setBounds(100, 300, 120, 35);
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(this);
        contentPane.add(registerButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(260, 300, 120, 35);
        exitButton.setBackground(Color.GRAY);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(this);
        contentPane.add(exitButton);

        // ==== Login Label ====
        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setForeground(new Color(0, 102, 204));
        loginLabel.setBounds(150, 360, 250, 30);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new Login();
                setVisible(false);
            }
        });
        contentPane.add(loginLabel);

        JPanel panel = new JPanel();
        panel.setBounds(30, 60, 420, 260);
        panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 102, 204), 2),
                "Sign Up", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16), new Color(0, 102, 204)));
        panel.setBackground(Color.WHITE);
        contentPane.add(panel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            createAccount();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    private void createAccount() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = Connect_Db.getConnection()) {
            String query = "INSERT INTO Users (name, email, phone, password) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, password);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            this.setVisible(false);
            new Login();

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Email already exists. Choose another one.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Register(); // object is created and window shows itself
    }
}
