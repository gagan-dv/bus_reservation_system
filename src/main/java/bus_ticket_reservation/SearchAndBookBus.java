package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchAndBookBus extends JFrame {
    private int userId;
    private JTextField sourceField, destinationField, dateField, seatField;
    private JTable busTable;
    private DefaultTableModel model;

    public SearchAndBookBus(int userId) {
        this.userId = userId;

        setTitle("Bus Ticket Reservation");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel searchPanel = new JPanel();

        searchPanel.add(new JLabel("Source:"));
        sourceField = new JTextField(10);
        searchPanel.add(sourceField);

        searchPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField(10);
        searchPanel.add(destinationField);

        searchPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(10);
        searchPanel.add(dateField);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchBuses());
        searchPanel.add(searchBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBuses());
        searchPanel.add(refreshBtn);

        add(searchPanel, BorderLayout.NORTH);
    
        model = new DefaultTableModel(
                new String[]{"Bus ID", "Bus Name", "Source", "Destination", "Date", "Price", "Seats Available"},
                0
        );
        busTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(busTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bookingPanel = new JPanel();

        bookingPanel.add(new JLabel("Seats to Book:"));
        seatField = new JTextField(5);
        bookingPanel.add(seatField);

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.addActionListener(e -> bookTicket());
        bookingPanel.add(bookBtn);

        add(bookingPanel, BorderLayout.SOUTH);

        loadBuses();

        setVisible(true);
    }

    // 🔹 Load all buses
    private void loadBuses() {
        model.setRowCount(0);
        try (Connection con = Connect_Db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM buses")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bus_id"),
                        rs.getString("bus_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("journey_date"),
                        rs.getDouble("price"),
                        rs.getInt("seats_available")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading buses: " + e.getMessage());
        }
    }

    // 🔹 Search buses
    private void searchBuses() {
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();
        String date = dateField.getText().trim();

        if (source.isEmpty() || destination.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        model.setRowCount(0);
        try (Connection con = Connect_Db.getConnection()) {
            String query = "SELECT * FROM buses WHERE source=? AND destination=? AND journey_date=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, source);
            pst.setString(2, destination);
            pst.setString(3, date);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bus_id"),
                        rs.getString("bus_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("journey_date"),
                        rs.getDouble("price"),
                        rs.getInt("seats_available")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No buses found!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching buses: " + e.getMessage());
        }
    }

    // 🔹 Book ticket
    private void bookTicket() {
        int row = busTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bus!");
            return;
        }

        int busId = (int) busTable.getValueAt(row, 0);
        int availableSeats = (int) busTable.getValueAt(row, 6);

        int seatsToBook;
        try {
            seatsToBook = Integer.parseInt(seatField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number.");
            return;
        }

        if (seatsToBook <= 0 || seatsToBook > availableSeats) {
            JOptionPane.showMessageDialog(this, "Invalid seat number.");
            return;
        }

        try (Connection con = Connect_Db.getConnection()) {
            // Insert booking
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO Bookings (user_id, bus_id, seats_booked) VALUES (?, ?, ?)"
            );
            pst.setInt(1, userId);
            pst.setInt(2, busId);
            pst.setInt(3, seatsToBook);
            pst.executeUpdate();

            // Update seats
            PreparedStatement pst2 = con.prepareStatement(
                    "UPDATE buses SET seats_available = seats_available - ? WHERE bus_id=?"
            );
            pst2.setInt(1, seatsToBook);
            pst2.setInt(2, busId);
            pst2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Ticket booked successfully!");
            loadBuses();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new SearchAndBookBus(1); // test with user_id=1
    }
}
