package group2hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancelReservationPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> reservationDropdown;
    private List<Integer> reservationIds; // To map dropdown selections to reservation IDs
    private String username; // Store the admin's username

    public CancelReservationPage(String username) {
        this.username = username; // Save the username
        setTitle("Cancel Reservation Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel mainPanel = new JPanel();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel selectReservationLabel = new JLabel("Select a reservation to cancel:");
        selectReservationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(selectReservationLabel);

        // Initialize dropdown menu
        reservationDropdown = new JComboBox<>();
        mainPanel.add(reservationDropdown);

        JButton cancelButton = new JButton("Cancel Reservation");
        mainPanel.add(cancelButton);

        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);

        // Load reservations into the dropdown
        loadReservations();

        // Action listener for the Cancel button
        cancelButton.addActionListener(e -> cancelReservation());
    }

    private void loadReservations() {
        reservationIds = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch only confirmed reservations
            String query = "SELECT ReservationID, RoomID, CheckInDate, CheckOutDate FROM reservations WHERE Status = 'Confirmed'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            reservationDropdown.removeAllItems(); // Clear existing items
            while (rs.next()) {
                int reservationId = rs.getInt("ReservationID");
                String roomId = rs.getString("RoomID");
                String checkInDate = rs.getString("CheckInDate");
                String checkOutDate = rs.getString("CheckOutDate");

                // Add reservation details to dropdown
                reservationDropdown.addItem("Room " + roomId + " (" + checkInDate + " - " + checkOutDate + ")");
                reservationIds.add(reservationId); // Map dropdown index to reservation ID
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelReservation() {
        int selectedIndex = reservationDropdown.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = reservationIds.get(selectedIndex); // Get the reservation ID
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM reservations WHERE ReservationID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, reservationId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Reservation canceled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReservations(); // Reload reservations to reflect the change
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel the reservation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error canceling reservation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        dispose(); // Close the CancelReservationPage
        new AdminDashboard(username).setVisible(true); // Reopen the AdminDashboard
    }
}