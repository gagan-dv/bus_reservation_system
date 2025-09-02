package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchAndBookBus extends JFrame {
    private int userId;
    private JTextField sourceField, destinationField, dateField, seatField;
    private JTable busTable;

    public SearchAndBookBus(int userId) {
        super("🚍 Bus Ticket Reservation");
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // 🔹 Title
        JLabel title = new JLabel("Search & Book Buses", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 60, 114));
        add(title, BorderLayout.NORTH);

        // 🔹 Search Panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(new Color(245, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSource = new JLabel("Source:");
        lblSource.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sourceField = new JTextField(12);

        JLabel lblDestination = new JLabel("Destination:");
        lblDestination.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        destinationField = new JTextField(12);

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        dateField = new JTextField(12);

        JButton searchBtn = createButton("Search", new Color(0, 123, 255));
        searchBtn.addActionListener(e -> searchBuses());

        JButton refreshBtn = createButton("Refresh All", new Color(40, 167, 69));
        refreshBtn.addActionListener(e -> loadBuses());

        gbc.gridx = 0; gbc.gridy = 0; searchPanel.add(lblSource, gbc);
        gbc.gridx = 1; gbc.gridy = 0; searchPanel.add(sourceField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; searchPanel.add(lblDestination, gbc);
        gbc.gridx = 3; gbc.gridy = 0; searchPanel.add(destinationField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; searchPanel.add(lblDate, gbc);
        gbc.gridx = 1; gbc.gridy = 1; searchPanel.add(dateField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; searchPanel.add(searchBtn, gbc);
        gbc.gridx = 3; gbc.gridy = 1; searchPanel.add(refreshBtn, gbc);

        add(searchPanel, BorderLayout.NORTH);

        // 🔹 Bus Table
        busTable = new JTable();
        busTable.setRowHeight(28);
        busTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        busTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Bus ID", "Bus Name", "Source", "Destination", "Date", "Departure", "Arrival", "Price", "Seats Available"}
        ));

        JScrollPane scrollPane = new JScrollPane(busTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Buses"));
        add(scrollPane, BorderLayout.CENTER);

        // 🔹 Booking Panel
        JPanel bookingPanel = new JPanel();
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        bookingPanel.add(new JLabel("Seats to Book:"));
        seatField = new JTextField(5);
        bookingPanel.add(seatField);

        JButton bookBtn = createButton("Book Ticket", new Color(255, 87, 34));
        bookBtn.addActionListener(e -> bookTicket());
        bookingPanel.add(bookBtn);

        add(bookingPanel, BorderLayout.SOUTH);

        // Load all buses at start
        loadBuses();

        setVisible(true);
    }

    // 🔹 Create Styled Button
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }

    // 🔹 Load all buses
    private void loadBuses() {
        try (Connection con = Connect_Db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM buses")) {

            DefaultTableModel model = (DefaultTableModel) busTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bus_id"),
                        rs.getString("bus_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("journey_date"),
                        rs.getTime("departure_time"),
                        rs.getTime("arrival_time"),
                        rs.getDouble("price"),
                        rs.getInt("seats_available")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading buses: " + e.getMessage());
        }
    }

    // 🔹 Search Buses
    private void searchBuses() {
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();
        String date = dateField.getText().trim();

        if (source.isEmpty() || destination.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try (Connection con = Connect_Db.getConnection()) {
            String query = "SELECT * FROM buses WHERE source=? AND destination=? AND journey_date=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, source);
            pst.setString(2, destination);
            pst.setString(3, date);

            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) busTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bus_id"),
                        rs.getString("bus_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("journey_date"),
                        rs.getTime("departure_time"),
                        rs.getTime("arrival_time"),
                        rs.getDouble("price"),
                        rs.getInt("seats_available")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No buses found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching buses: " + e.getMessage());
        }
    }

    // 🔹 Book Ticket
    private void bookTicket() {
        int row = busTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Please select a bus!");
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
            JOptionPane.showMessageDialog(this, "⚠ Invalid seat selection.");
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
            JOptionPane.showMessageDialog(this, "🎉 Ticket booked successfully!");
            loadBuses(); // refresh table
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new SearchAndBookBus(1); // test with user_id=1
    }
}
