package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BookTicket extends JFrame {
    private int userId;
    private JTable busTable;
    private JButton bookButton;
    private JTextField seatField;

    public BookTicket(int userId) {
        this.userId = userId;
        setTitle("Book Ticket");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Table to show buses
        busTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(busTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Seats to book:"));
        seatField = new JTextField(5);
        bottomPanel.add(seatField);

        bookButton = new JButton("Book Ticket");
        bottomPanel.add(bookButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load buses into table
        loadBuses();

        // Book ticket action
        bookButton.addActionListener(e -> bookTicket());

        setVisible(true);
    }

    private void loadBuses() {
        try (Connection con = Connect_Db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM buses")) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Bus ID", "Bus Name", "Source", "Destination", "Date", "Departure", "Arrival", "Price", "Seats Available"}, 0
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bus_id"),
                        rs.getString("bus_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("journey_date"),
                        rs.getTime("departure_time"),
                        rs.getTime("arrival_time"),
                        rs.getBigDecimal("price"),
                        rs.getInt("seats_available")
                });
            }
            busTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading buses: " + e.getMessage());
        }
    }

    private void bookTicket() {
        int row = busTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bus.");
            return;
        }

        int busId = (int) busTable.getValueAt(row, 0);
        int availableSeats = (int) busTable.getValueAt(row, 8);

        int seatsToBook;
        try {
            seatsToBook = Integer.parseInt(seatField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number of seats.");
            return;
        }

        if (seatsToBook <= 0 || seatsToBook > availableSeats) {
            JOptionPane.showMessageDialog(this, "Invalid seat selection.");
            return;
        }

        try (Connection con = Connect_Db.getConnection()) {
            con.setAutoCommit(false);

            // Insert booking
            String insertBooking = "INSERT INTO Bookings (user_id, bus_id, seats_booked) VALUES (?, ?, ?)";
            try (PreparedStatement pst = con.prepareStatement(insertBooking)) {
                pst.setInt(1, userId);
                pst.setInt(2, busId);
                pst.setInt(3, seatsToBook);
                pst.executeUpdate();
            }

            // Update seats
            String updateSeats = "UPDATE buses SET seats_available = seats_available - ? WHERE bus_id=?";
            try (PreparedStatement pst2 = con.prepareStatement(updateSeats)) {
                pst2.setInt(1, seatsToBook);
                pst2.setInt(2, busId);
                pst2.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "Ticket booked successfully!");
            loadBuses(); // refresh table
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new BookTicket(1); // test with user_id=1
    }
}
