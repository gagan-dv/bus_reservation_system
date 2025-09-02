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
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel bookingLabel = new JLabel("Enter Booking ID:");
        bookingIdField = new JTextField(15);

        validateButton = new JButton("Validate");
        backButton = new JButton("Back");

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(bookingLabel);
        panel.add(bookingIdField);
        panel.add(validateButton);
        panel.add(backButton);

        add(panel);

        validateButton.addActionListener(e -> validateTicket());
        backButton.addActionListener(e -> dispose());

        setVisible(true);
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
            pst.setInt(2, userId); // ensure the booking belongs to logged-in user
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
                                    "Seats Booked: " + seats);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Ticket is INVALID (no seats booked).");
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ No booking found with this ID for your account.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error validating ticket: " + e.getMessage());
        }
    }
}
