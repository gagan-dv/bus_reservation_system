package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {
    private JLabel l1, l2, titleLabel;
    private JTextField t1;
    private JPasswordField t2;
    private JButton b1, b2, forgotButton;

    public Login() {
        super("Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        titleLabel = new JLabel("Bus Ticket Reservation Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBounds(50, 20, 400, 40);
        add(titleLabel);

        l1 = new JLabel("Email:");
        l1.setBounds(80, 90, 100, 30);
        add(l1);

        t1 = new JTextField();
        t1.setBounds(200, 90, 200, 30);
        add(t1);

        l2 = new JLabel("Password:");
        l2.setBounds(80, 140, 100, 30);
        add(l2);

        t2 = new JPasswordField();
        t2.setBounds(200, 140, 200, 30);
        add(t2);

        b1 = new JButton("Login");
        b1.setBounds(80, 200, 120, 40);
        b1.setBackground(new Color(0, 102, 204));
        b1.setForeground(Color.WHITE);
        b1.setFont(new Font("Poppins", Font.BOLD, 14));
        b1.addActionListener(this);
        add(b1);

        b2 = new JButton("Back");
        b2.setBounds(260, 200, 120, 40);
        b2.setBackground(Color.GRAY);
        b2.setForeground(Color.WHITE);
        b2.setFont(new Font("Poppins", Font.BOLD, 14));
        b2.addActionListener(this);
        add(b2);

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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) { // Login
            String email = t1.getText().trim();
            String password = new String(t2.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
                return;
            }

            String q = "SELECT * FROM Users WHERE email=? AND password=?";
            try (Connection con = Connect_Db.getConnection();
                 PreparedStatement pst = con.prepareStatement(q)) {

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
                    JOptionPane.showMessageDialog(this, "Invalid login");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            }

        } else if (ae.getSource() == b2) { // Back
            setVisible(false);
            new Register().setVisible(true);
        }
        // Forgot password flow (placeholder)
        else if (ae.getSource() == forgotButton) {
            JOptionPane.showMessageDialog(this, "Forgot password functionality not implemented yet.");
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
