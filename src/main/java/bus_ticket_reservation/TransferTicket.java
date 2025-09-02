package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TransferTicket extends JFrame {
    private int userId;
    private JTextField bookingIdField, targetEmailField;
    private JButton transferButton, backButton;

    public TransferTicket(int userId) {
        this.userId = userId;

        setTitle("Transfer Ticket");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel bookingLabel = new JLabel("Enter Booking ID:");
        bookingIdField = new JTextField(15);

        JLabel targetLabel = new JLabel("Transfer To (Recipient Email):");
        targetEmailField = new JTextField(20);

        transferButton = new JButton("Transfer Ticket");
        backButton = new JButton("Back");

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(bookingLabel);
        panel.add(bookingIdField);
        panel.add(targetLabel);
        panel.add(targetEmailField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(transferButton);
        buttonPanel.add(backButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        transferButton.addActionListener(e -> transferTicket());
        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void transferTicket() {
        String bookingIdText = bookingIdField.getText().trim();
        String recipientEmail = targetEmailField.getText().trim();

        if (bookingIdText.isEmpty() || recipientEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.");
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

            // 1️⃣ Verify booking belongs to current user
            PreparedStatement pst1 = conn.prepareStatement(
                    "SELECT bus_id, seats_booked FROM Bookings WHERE booking_id=? AND user_id=?"
            );
            pst1.setInt(1, bookingId);
            pst1.setInt(2, userId);
            ResultSet rs1 = pst1.executeQuery();

            if (!rs1.next()) {
                JOptionPane.showMessageDialog(this, "Booking not found or not yours.");
                return;
            }

            int busId = rs1.getInt("bus_id");
            int seatsBooked = rs1.getInt("seats_booked");

            // 2️⃣ Find recipient user_id
            PreparedStatement pst2 = conn.prepareStatement("SELECT user_id FROM Users WHERE email=?");
            pst2.setString(1, recipientEmail);
            ResultSet rs2 = pst2.executeQuery();

            if (!rs2.next()) {
                JOptionPane.showMessageDialog(this, "Recipient email not found.");
                return;
            }
            int recipientId = rs2.getInt("user_id");

            if (recipientId == userId) {
                JOptionPane.showMessageDialog(this, "You cannot transfer a ticket to yourself.");
                return;
            }

            // 3️⃣ Update booking to new owner
            PreparedStatement pst3 = conn.prepareStatement(
                    "UPDATE Bookings SET user_id=? WHERE booking_id=?"
            );
            pst3.setInt(1, recipientId);
            pst3.setInt(2, bookingId);
            pst3.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Ticket transferred successfully to " + recipientEmail);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error transferring ticket: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new TransferTicket(1); // test with user_id = 1
    }
}
