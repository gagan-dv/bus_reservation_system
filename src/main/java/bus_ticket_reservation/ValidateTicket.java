package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ValidateTicket extends JFrame {
    private JTextField bookingIdField;
    private JButton validateButton, backButton;
    private int userId;

    public ValidateTicket(int userId) {
        this.userId = userId; // store the logged-in user
        setTitle("Validate Ticket");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Validate Your Ticket", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel bookingLabel = new JLabel("Enter Booking ID:");
        bookingLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(bookingLabel, gbc);

        bookingIdField = new JTextField(15);
        bookingIdField.setFont(new Font("Poppins", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(bookingIdField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        validateButton = styledButton("Validate", new Color(0, 102, 204));
        backButton = styledButton("Back", new Color(128, 128, 128));

        buttonPanel.add(validateButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Actions
        validateButton.addActionListener(e -> validateTicket());
        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void validateTicket() {
        String bookingIdText = bookingIdField.getText().trim();
        if (bookingIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Booking ID.");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Booking ID format.");
            return;
        }

        try (Connection conn = Connect_Db.getConnection()) {
            String query = """
                SELECT b.booking_id, b.seats_booked, bu.bus_name, bu.journey_date, bu.source, bu.destination
                FROM Bookings b
                JOIN buses bu ON b.bus_id = bu.bus_id
                WHERE b.booking_id = ? AND b.user_id = ?
            """;

            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, bookingId);
            pst.setInt(2, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int seats = rs.getInt("seats_booked");
                String busName = rs.getString("bus_name");
                String date = rs.getString("journey_date");
                String source = rs.getString("source");
                String destination = rs.getString("destination");

                if (seats > 0) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Ticket is VALID!\n\n" +
                                    "Booking ID: " + bookingId + "\n" +
                                    "Bus: " + busName + "\n" +
                                    "Route: " + source + " → " + destination + "\n" +
                                    "Journey Date: " + date + "\n" +
                                    "Seats Booked: " + seats,
                            "Ticket Validated", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Ticket is INVALID (no seats booked).");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No booking found with this ID for your account.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error validating ticket: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ValidateTicket(1); // test with user_id = 1
    }
}
