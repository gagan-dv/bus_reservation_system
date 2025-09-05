package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TransferTicket extends JFrame {
    private int userId;
    private JTextField bookingIdField, targetEmailField;

    public TransferTicket(int userId) {
        this.userId = userId;

        setTitle("Transfer Ticket");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Labels + fields
        JLabel bookingLabel = new JLabel("Enter Booking ID:");
        bookingIdField = new JTextField(15);

        JLabel targetLabel = new JLabel("Transfer To (Recipient Email):");
        targetEmailField = new JTextField(20);

        // Buttons
        JButton transferButton = new JButton("Transfer Ticket");
        JButton backButton = new JButton("Back");

        // Layout
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

        // Actions
        transferButton.addActionListener(e -> transferTicket());
        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void transferTicket() {
        String bookingText = bookingIdField.getText().trim();
        String email = targetEmailField.getText().trim();

        if (bookingText.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Booking ID must be a number.");
            return;
        }

        try (Connection conn = Connect_Db.getConnection()) {
            // check if booking belongs to user
            PreparedStatement pst1 = conn.prepareStatement(
                    "SELECT booking_id FROM Bookings WHERE booking_id=? AND user_id=?"
            );
            pst1.setInt(1, bookingId);
            pst1.setInt(2, userId);
            ResultSet rs1 = pst1.executeQuery();
            if (!rs1.next()) {
                JOptionPane.showMessageDialog(this, "Booking not found or not yours.");
                return;
            }

            // get recipient user_id
            PreparedStatement pst2 = conn.prepareStatement("SELECT user_id FROM Users WHERE email=?");
            pst2.setString(1, email);
            ResultSet rs2 = pst2.executeQuery();
            if (!rs2.next()) {
                JOptionPane.showMessageDialog(this, "Recipient not found.");
                return;
            }
            int recipientId = rs2.getInt("user_id");

            if (recipientId == userId) {
                JOptionPane.showMessageDialog(this, "You cannot transfer to yourself.");
                return;
            }

            // transfer booking
            PreparedStatement pst3 = conn.prepareStatement(
                    "UPDATE Bookings SET user_id=? WHERE booking_id=?"
            );
            pst3.setInt(1, recipientId);
            pst3.setInt(2, bookingId);
            pst3.executeUpdate();

            JOptionPane.showMessageDialog(this, "Ticket transferred successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new TransferTicket(1);
    }
}
