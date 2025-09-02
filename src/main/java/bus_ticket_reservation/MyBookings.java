package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookings extends JFrame {
    private final int userId;
    private JTable table;
    private DefaultTableModel model;

    public MyBookings(int userId) {
        this.userId = userId;

        setTitle("My Bookings");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadBookings();

        setVisible(true);
    }

    private void initUI() {
        // Table model
        model = new DefaultTableModel(
                new String[]{"Booking ID", "Bus Name", "Source", "Destination", "Journey Date", "Seats", "Price"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table non-editable
            }
        };

        // Table
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Poppins", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JButton cancelButton = styledButton("Cancel Booking", new Color(220, 53, 69));
        JButton refreshButton = styledButton("Refresh", new Color(0, 102, 204));
        JButton backButton = styledButton("Back", new Color(128, 128, 128));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        // Add components
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        cancelButton.addActionListener(e -> cancelBooking());
        refreshButton.addActionListener(e -> loadBookings());
        backButton.addActionListener(e -> dispose());
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 40));

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
            JOptionPane.showMessageDialog(this, "⚠️ Please select a booking to cancel.");
            return;
        }

        int bookingId = (int) model.getValueAt(row, 0);
        int seatsBooked = (int) model.getValueAt(row, 5);

        String input = JOptionPane.showInputDialog(
                this,
                "This booking has " + seatsBooked + " seats.\nHow many seats do you want to cancel?",
                "Cancel Seats",
                JOptionPane.PLAIN_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) return;

        int seatsToCancel;
        try {
            seatsToCancel = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid number.");
            return;
        }

        if (seatsToCancel <= 0 || seatsToCancel > seatsBooked) {
            JOptionPane.showMessageDialog(this, "❌ Enter a valid number (1 - " + seatsBooked + ").");
            return;
        }

        try (Connection conn = Connect_Db.getConnection()) {
            conn.setAutoCommit(false);

            // Get bus_id
            int busId = -1;
            PreparedStatement pst1 = conn.prepareStatement("SELECT bus_id FROM Bookings WHERE booking_id=?");
            pst1.setInt(1, bookingId);
            ResultSet rs = pst1.executeQuery();
            if (rs.next()) {
                busId = rs.getInt("bus_id");
            }

            if (seatsToCancel == seatsBooked) {
                // Cancel full booking
                PreparedStatement pst2 = conn.prepareStatement("DELETE FROM Bookings WHERE booking_id=?");
                pst2.setInt(1, bookingId);
                pst2.executeUpdate();
            } else {
                // Update booking with fewer seats
                PreparedStatement pst2 = conn.prepareStatement(
                        "UPDATE Bookings SET seats_booked = seats_booked - ? WHERE booking_id=?"
                );
                pst2.setInt(1, seatsToCancel);
                pst2.setInt(2, bookingId);
                pst2.executeUpdate();
            }

            // Update bus seats
            PreparedStatement pst3 = conn.prepareStatement(
                    "UPDATE buses SET seats_available = seats_available + ? WHERE bus_id=?"
            );
            pst3.setInt(1, seatsToCancel);
            pst3.setInt(2, busId);
            pst3.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "✅ " + seatsToCancel + " seat(s) cancelled successfully!");
            loadBookings();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cancelling booking: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new MyBookings(1); // test with user_id = 1
    }
}
