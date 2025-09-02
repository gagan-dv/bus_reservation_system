package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CancelTicket extends JFrame {
    private int userId;
    private JTextField bookingIdField;
    private JButton cancelButton, backButton;

    public CancelTicket(int userId) {
        this.userId = userId;

        setTitle("Cancel Ticket");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Enter Booking ID to Cancel:");
        bookingIdField = new JTextField(15);

        cancelButton = new JButton("Cancel Ticket");
        backButton = new JButton("Back");

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(label);
        panel.add(bookingIdField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel);
        add(panel);

        cancelButton.addActionListener(e -> cancelTicket());
        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void cancelTicket() {
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
            conn.setAutoCommit(false);

            // Get bus_id and seats_booked for this booking
            PreparedStatement pst1 = conn.prepareStatement(
                    "SELECT bus_id, seats_booked FROM Bookings WHERE booking_id=? AND user_id=?"
            );
            pst1.setInt(1, bookingId);
            pst1.setInt(2, userId);
            ResultSet rs = pst1.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Booking not found or not yours.");
                return;
            }

            int busId = rs.getInt("bus_id");
            int seatsBooked = rs.getInt("seats_booked");

            // Delete booking
            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM Bookings WHERE booking_id=?");
            pst2.setInt(1, bookingId);
            pst2.executeUpdate();

            // Update bus seat availability
            PreparedStatement pst3 = conn.prepareStatement(
                    "UPDATE buses SET seats_available = seats_available + ? WHERE bus_id=?"
            );
            pst3.setInt(1, seatsBooked);
            pst3.setInt(2, busId);
            pst3.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cancelling ticket: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new CancelTicket(1); // test with user_id = 1
    }
}
