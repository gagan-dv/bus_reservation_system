package bus_ticket_reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect_Db {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/bus_ticket_r_sys";
        String user = "cbs";              // your MySQL username
        String password = "M0nkrus@2025"; // your MySQL password

        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
