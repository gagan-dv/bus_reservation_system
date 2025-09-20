package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookings extends JFrame {
    private int userId;
    private JTable table;
    private DefaultTableModel model;

    public MyBookings(int userId) {
        this.userId = userId;

        setTitle("My Bookings");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

     
        model = new DefaultTableModel(
                new String[]{"Booking ID", "Bus Name", "Source", "Destination", "Date", "Seats", "Price"},
                0
        );
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

       
        JButton cancelButton = new JButton("Cancel Booking");
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> cancelBooking());
        refreshButton.addActionListener(e -> loadBookings());
        backButton.addActionListener(e -> dispose());

        loadBookings(); // load bookings when window opens
        setVisible(true);
    }

    private void loadBookings() {
        model.setRowCount(0); // clear old rows

        String query = "SELECT b.booking_id, bs.bus_name, bs.source, bs.destination, bs.journey_date, " +
                "b.seats_booked, (b.seats_booked * bs.price) AS total_price " +
                "FROM Bookings b JOIN buses bs ON b.bus_id = bs.bus_id WHERE b.user_id = ?";

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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void cancelBooking() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking first.");
            return;
        }

        int bookingId = (int) model.getValueAt(row, 0);
        int seatsBooked = (int) model.getValueAt(row, 5); // column 5 = seats

        // Ask how many seats to cancel
        String input = JOptionPane.showInputDialog(this,
                "You have " + seatsBooked + " seats.\nHow many seats do you want to cancel?");

        if (input == null) return; // user pressed cancel

        int seatsToCancel;
        try {
            seatsToCancel = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.");
            return;
        }

        if (seatsToCancel <= 0 || seatsToCancel > seatsBooked) {
            JOptionPane.showMessageDialog(this, "Enter a number between 1 and " + seatsBooked + ".");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel " + seatsToCancel + " seat(s)?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Connect_Db.getConnection()) {
                if (seatsToCancel == seatsBooked) {
                    PreparedStatement pst = conn.prepareStatement(
                            "DELETE FROM Bookings WHERE booking_id=?");
                    pst.setInt(1, bookingId);
                    pst.executeUpdate();
                } else {    
                    PreparedStatement pst = conn.prepareStatement(
                            "UPDATE Bookings SET seats_booked = seats_booked - ? WHERE booking_id=?");
                    pst.setInt(1, seatsToCancel);
                    pst.setInt(2, bookingId);
                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Cancellation successful!");
                loadBookings();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new MyBookings(1); // test with user_id = 1
    }
}
