package bus_ticket_reservation;

import javax.swing.*;
import java.awt.*;

public class Home extends JFrame {
    private int userId;
    private String username;

    public Home(int userId, String username) {
        super("Bus Ticket Reservation System"); // set window title
        this.userId = userId;
        this.username = username;

        initialize();
    }

    private void initialize() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel welcomeLabel = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(0, 102, 204));
        welcomeLabel.setFont(new Font("Poppins", Font.BOLD, 26));

        gbc.gridx = 0; // column
        gbc.gridy = 0; // row
        gbc.gridwidth = 2; // take up 2 columns
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 30, 0);
        panel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1; // buttons take one column
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 12, 12, 12); 

        JButton bookTicket = createButton("Book Ticket", new Color(0, 102, 204));
        gbc.gridy = 1;
        panel.add(bookTicket, gbc);

        JButton myBookings = createButton("My Bookings", new Color(0, 102, 204));
        gbc.gridy = 2;
        panel.add(myBookings, gbc);

        JButton transferTicket = createButton("Transfer Ticket", new Color(0, 102, 204));
        gbc.gridy = 3;
        panel.add(transferTicket, gbc);

        JButton validateTicket = createButton("Validate Ticket", new Color(0, 102, 204));
        gbc.gridy = 4;
        panel.add(validateTicket, gbc);

        JButton logout = createButton("Logout", Color.GRAY);
        gbc.gridy = 5;
        panel.add(logout, gbc);


        bookTicket.addActionListener(e -> new SearchAndBookBus(userId).setVisible(true));
        myBookings.addActionListener(e -> new MyBookings(userId).setVisible(true));
        transferTicket.addActionListener(e -> new TransferTicket(userId).setVisible(true));
        validateTicket.addActionListener(e -> new ValidateTicket(userId).setVisible(true));
        logout.addActionListener(e -> {
            dispose(); // close this window
            new Login().setVisible(true); // open login again
        });

        add(panel); // add panel to frame
        setSize(550, 500); // window size
        setLocationRelativeTo(null); // center window on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // show window
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Poppins", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(450, 45)); // fixed size
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
