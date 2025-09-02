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

        // ---- Welcome Label ----
        JLabel welcomeLabel = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(0, 102, 204));
        welcomeLabel.setFont(new Font("Poppins", Font.BOLD, 26));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // span across all columns
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; // don't stretch label
        gbc.insets = new Insets(20, 0, 30, 0); // spacing above/below label
        panel.add(welcomeLabel, gbc);

        // ---- Reset for buttons ----
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridwidth = 1;
        gbc.gridx = 0;

        // ---- Buttons ----
        JButton bookTicket = styledButton("Book Ticket", new Color(0, 102, 204));
        gbc.gridy = 2;
        panel.add(bookTicket, gbc);

        JButton myBookings = styledButton("My Bookings", new Color(0, 102, 204));
        gbc.gridy = 3;
        panel.add(myBookings, gbc);

        JButton transferTicket = styledButton("Transfer Ticket", new Color(0, 102, 204));
        gbc.gridy = 5;
        panel.add(transferTicket, gbc);

        JButton validateTicket = styledButton("Validate Ticket", new Color(0, 102, 204));
        gbc.gridy = 6;
        panel.add(validateTicket, gbc);

        JButton logout = styledButton("Logout", new Color(128, 128, 128));
        gbc.gridy = 7;
        panel.add(logout, gbc);

        // ---- Button Actions ----
        bookTicket.addActionListener(e -> new SearchAndBookBus(userId).setVisible(true));
        myBookings.addActionListener(e -> new MyBookings(userId).setVisible(true));
        transferTicket.addActionListener(e -> new TransferTicket(userId).setVisible(true));
        validateTicket.addActionListener(e -> new ValidateTicket(userId).setVisible(true));
        logout.addActionListener(e -> {
            this.dispose();
            new Login().setVisible(true);
        });

        // ---- Frame Setup ----
        add(panel);
        setSize(550, 500);
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
        button.setPreferredSize(new Dimension(450, 45)); // button size
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
