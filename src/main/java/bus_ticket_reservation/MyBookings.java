package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookings extends JFrame {
    private int userId;
    private JTable table;
    private DefaultTableModel model;
    private JButton cancelButton, backButton;

    public MyBookings(int userId) {
        this.userId = userId;

        setTitle("My Bookings");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new String[]{"Booking ID", "Bus Name", "Source", "Destination", "Journey Date", "Seats Booked", "Price"},
                0
        );
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        cancelButton = new JButton("Cancel Booking");
        backButton = new JButton("Back");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadBookings();

        // Cancel booking event
        cancelButton.addActionListener(e -> cancelBooking());

        // Back button → close this window
        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadBookings() {
        model.setRowCount(0); // clear table
        String query = "SELECT b.booking_id, bs.bus_name, bs.source, bs.destination, bs.journey_date, " +
                "b.seats_booked, (b.seats_booked * bs.price) AS total_price " +
                "FROM Bookings b " +
                "JOIN buses bs ON b.bus_id = bs.bus_id " +
                "WHERE b.user_id = ?";

        try (Connection conn = Connect_Db.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("bus_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("journey_date"),
                        rs.getInt("seats_booked"),
                        rs.getDouble("total_price")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }

    private void cancelBooking() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
            return;
        }

        int bookingId = (int) model.getValueAt(row, 0);
        int seatsBooked = (int) model.getValueAt(row, 5);

        try (Connection conn = Connect_Db.getConnection()) {
            conn.setAutoCommit(false);

            // Get bus_id first
            int busId = -1;
            PreparedStatement pst1 = conn.prepareStatement("SELECT bus_id FROM Bookings WHERE booking_id=?");
            pst1.setInt(1, bookingId);
            ResultSet rs = pst1.executeQuery();
            if (rs.next()) {
                busId = rs.getInt("bus_id");
            }

            // Delete booking
            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM Bookings WHERE booking_id=?");
            pst2.setInt(1, bookingId);
            pst2.executeUpdate();

            // Update seat availability
            PreparedStatement pst3 = conn.prepareStatement("UPDATE buses SET seats_available = seats_available + ? WHERE bus_id=?");
            pst3.setInt(1, seatsBooked);
            pst3.setInt(2, busId);
            pst3.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
            loadBookings();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cancelling booking: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new MyBookings(1); // test with user_id = 1
    }
}
