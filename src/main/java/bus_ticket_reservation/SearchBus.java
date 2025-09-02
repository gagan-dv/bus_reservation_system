package bus_ticket_reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SearchBus extends JFrame {
    private int userId;
    private JTextField sourceField, destinationField, dateField;
    private JTable resultsTable;

    public SearchBus(int userId) {
        super("Search Buses");
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 🔹 Top Panel (Search Form)
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblSource = new JLabel("Source:");
        sourceField = new JTextField(10);
        JLabel lblDestination = new JLabel("Destination:");
        destinationField = new JTextField(10);
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        dateField = new JTextField(10);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0, 102, 204));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> searchBuses());

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            this.dispose();
            new Home(userId, "User").setVisible(true); // Username can be passed properly
        });

        gbc.gridx = 0; gbc.gridy = 0; topPanel.add(lblSource, gbc);
        gbc.gridx = 1; topPanel.add(sourceField, gbc);
        gbc.gridx = 2; topPanel.add(lblDestination, gbc);
        gbc.gridx = 3; topPanel.add(destinationField, gbc);
        gbc.gridx = 4; topPanel.add(lblDate, gbc);
        gbc.gridx = 5; topPanel.add(dateField, gbc);
        gbc.gridx = 6; topPanel.add(searchBtn, gbc);
        gbc.gridx = 7; topPanel.add(backBtn, gbc);

        add(topPanel, BorderLayout.NORTH);

        // 🔹 Table for Results
        resultsTable = new JTable();
        resultsTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Bus ID", "Bus Name", "Source", "Destination", "Departure", "Arrival", "Price", "Seats Available"}
        ));
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        setVisible(true);
    }

    // 🔹 Search Bus Logic
    private void searchBuses() {
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();
        String date = dateField.getText().trim();

        if (source.isEmpty() || destination.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try (Connection conn = Connect_Db.getConnection()) {
            String query = "SELECT bus_id, bus_name, source, destination, departure_time, arrival_time, price, seats_available " +
                    "FROM buses WHERE source=? AND destination=? AND journey_date=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, source);
            pst.setString(2, destination);
            pst.setString(3, date);

            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
            model.setRowCount(0); // Clear old results

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bus_id"),
                        rs.getString("bus_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("departure_time"),
                        rs.getString("arrival_time"),
                        rs.getDouble("price"),
                        rs.getInt("seats_available")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No buses found for the given route/date.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new SearchBus(1);
    }
}
