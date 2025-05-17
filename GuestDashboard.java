package group2hotel;

import javax.swing.*;
import java.sql.*;

public class GuestDashboard {
    private JFrame frame;

    public GuestDashboard(String username) {
        frame = new JFrame("Guest Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Fetch the guest's first name from the database
        String firstName = getGuestFirstName(username);

        JLabel welcomeLabel;
        if (firstName != null) {
            welcomeLabel = new JLabel("Welcome, " + firstName + "!");
        } else {
            welcomeLabel = new JLabel("Welcome to Guest Dashboard!");
        }
        welcomeLabel.setBounds(50, 20, 400, 30);
        frame.add(welcomeLabel);

        // Placeholder buttons for Guest Dashboard functionalities
        JButton checkRoomAvailabilityButton = new JButton("Check Room Availability");
        checkRoomAvailabilityButton.setBounds(50, 70, 200, 30);
        frame.add(checkRoomAvailabilityButton);

        JButton createReservationButton = new JButton("Create Reservation");
        createReservationButton.setBounds(50, 110, 200, 30);
        frame.add(createReservationButton);

        JButton viewBookingHistoryButton = new JButton("View Booking History");
        viewBookingHistoryButton.setBounds(50, 150, 200, 30);
        frame.add(viewBookingHistoryButton);

        JButton makePaymentButton = new JButton("Make Payment");
        makePaymentButton.setBounds(50, 190, 200, 30);
        frame.add(makePaymentButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 230, 200, 30);
        frame.add(logoutButton);

        // Button actions (placeholders for now)
        checkRoomAvailabilityButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Check Room Availability not implemented yet."));
        createReservationButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Create Reservation not implemented yet."));
        viewBookingHistoryButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View Booking History not implemented yet."));
        makePaymentButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Make Payment not implemented yet."));
        logoutButton.addActionListener(e -> logout());

        frame.setVisible(true);
    }

    private String getGuestFirstName(String username) {
        String firstName = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT FirstName FROM users WHERE Username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                firstName = rs.getString("FirstName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return firstName;
    }

    private void logout() {
        frame.dispose();
        new LoginPage();
    }
}