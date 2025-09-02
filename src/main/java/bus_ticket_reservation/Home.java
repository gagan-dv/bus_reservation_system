package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;

public class Home extends JFrame {
    private int userId;
    private String username;

    public Home(int userId, String username) {
        super("Bus Ticket Reservation System");
        this.userId = userId;
        this.username = username;
        initialize();
    }

    private void initialize() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeLabel = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(0, 102, 204));
        welcomeLabel.setFont(new Font("Poppins", Font.BOLD, 26));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;

        JButton searchBus = styledButton("Search Buses", new Color(0, 102, 204));
        gbc.gridy = 1;
        panel.add(searchBus, gbc);

        JButton bookTicket = styledButton("Book Ticket", new Color(0, 102, 204));
        gbc.gridy = 2;
        panel.add(bookTicket, gbc);

        JButton myBookings = styledButton("My Bookings", new Color(0, 102, 204));
        gbc.gridy = 3;
        panel.add(myBookings, gbc);

        JButton cancelTicket = styledButton("Cancel Ticket", new Color(220, 53, 69));
        gbc.gridy = 4;
        panel.add(cancelTicket, gbc);

        JButton transferTicket = styledButton("Transfer Ticket", new Color(0, 102, 204));
        gbc.gridy = 5;
        panel.add(transferTicket, gbc);

        JButton validateTicket = styledButton("Validate Ticket", new Color(0, 102, 204));
        gbc.gridy = 6;
        panel.add(validateTicket, gbc);

        JButton logout = styledButton("Logout", new Color(128, 128, 128));
        gbc.gridy = 7;
        panel.add(logout, gbc);

        // Actions (navigation stubs)
        searchBus.addActionListener(e -> new SearchBus(userId).setVisible(true));
        bookTicket.addActionListener(e -> new BookTicket(userId).setVisible(true));
        myBookings.addActionListener(e -> new MyBookings(userId).setVisible(true));
        cancelTicket.addActionListener(e -> new CancelTicket(userId).setVisible(true));
        transferTicket.addActionListener(e -> new TransferTicket(userId).setVisible(true));
        validateTicket.addActionListener(e -> new ValidateTicket(userId).setVisible(true));
        logout.addActionListener(e -> {
            this.dispose();
            new Login().setVisible(true);
        });

        add(panel);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Poppins", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(250, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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

    public static void main(String[] args) {
        new Home(1, "TestUser");
    }
}
