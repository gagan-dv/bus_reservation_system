package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Register extends JFrame implements ActionListener {

    private JPanel contentPane;
    private JTextField emailField, nameField, phoneField;
    private JPasswordField passwordField;
    private JButton createButton, backButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Register().setVisible(true));
    }

    public Register() {
        setBounds(600, 200, 500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Title
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBounds(120, 20, 300, 30);
        contentPane.add(titleLabel);

        // Name
        JLabel lblName = new JLabel("Name:");
        lblName.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblName.setBounds(50, 80, 100, 25);
        contentPane.add(lblName);

        nameField = new JTextField();
        nameField.setBounds(180, 80, 220, 25);
        contentPane.add(nameField);

        // Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblEmail.setBounds(50, 120, 100, 25);
        contentPane.add(lblEmail);

        emailField = new JTextField();
        emailField.setBounds(180, 120, 220, 25);
        contentPane.add(emailField);

        // Phone
        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblPhone.setBounds(50, 160, 100, 25);
        contentPane.add(lblPhone);

        phoneField = new JTextField();
        phoneField.setBounds(180, 160, 220, 25);
        contentPane.add(phoneField);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblPassword.setBounds(50, 200, 100, 25);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(180, 200, 220, 25);
        contentPane.add(passwordField);

        // Buttons
        createButton = new JButton("Register");
        createButton.setBounds(100, 300, 120, 35);
        createButton.setBackground(new Color(0, 102, 204));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Poppins", Font.BOLD, 16));
        createButton.addActionListener(this);
        contentPane.add(createButton);

        backButton = new JButton("Exit");
        backButton.setBounds(260, 300, 120, 35);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Poppins", Font.BOLD, 16));
        backButton.addActionListener(this);
        contentPane.add(backButton);

        JLabel loginLabel = new JLabel();
        loginLabel.setText("<html><center>Already have an account?<br>Login</center></html>");
        loginLabel.setForeground(new Color(0, 102, 204));
        loginLabel.setBounds(150, 360, 200, 30);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new Login().setVisible(true);
                setVisible(false);
            }
        });
        add(loginLabel);

        // Panel border
        JPanel panel = new JPanel();
        panel.setBounds(30, 60, 420, 260);
        panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 102, 204), 2), "Sign Up",
                TitledBorder.LEADING, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 16), new Color(0, 102, 204)));
        panel.setBackground(Color.WHITE);
        contentPane.add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            createAccount();
        } else if (e.getSource() == backButton) {
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
            new Login().setVisible(true);

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Email already exists. Choose another one.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
