package bus_ticket_reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility for Bus Ticket Reservation System.
 */
public class Connect_Db {

    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/bus_ticket_r_sys";
    private static final String USER = "cbs";                 // use your MySQL username
    private static final String PASSWORD = "M0nkrus@2025";    // use your MySQL password

    // Load MySQL driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver Loaded Successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found. Add MySQL Connector/J to classpath.");
            e.printStackTrace();
        }
    }

    // To create a new DB connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Test DB connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect: " + e.getMessage());
        }
    }
}
