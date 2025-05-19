package group2hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class StaffDashboard {
    private JFrame frame;
    private String username;
    private String firstName;
    private JTable table;
    private DefaultTableModel tableModel;

    public StaffDashboard(String username) {
        this.username = username;
        this.firstName = fetchStaffFirstName(username); // Fetch staff's first name

        frame = new JFrame("Staff Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome to the Staff Dashboard, " + (firstName != null ? firstName : username) + "!");
        welcomeLabel.setBounds(50, 20, 400, 30);
        frame.add(welcomeLabel);

        // Table for Reservations and Booked Rooms
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Reservation ID");
        tableModel.addColumn("Room ID");
        tableModel.addColumn("Guest Name");
        tableModel.addColumn("Check-In Date");
        tableModel.addColumn("Check-Out Date");
        tableModel.addColumn("Status");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 70, 700, 300);
        frame.add(scrollPane);

        // Load data into table
        loadReservationData();

        // Action Buttons
        JButton cancelReservationButton = new JButton("Cancel Reservation");
        cancelReservationButton.setBounds(50, 400, 200, 30);
        frame.add(cancelReservationButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(300, 400, 200, 30);
        frame.add(logoutButton);

        // Button Actions
        cancelReservationButton.addActionListener(e -> cancelReservation());
        logoutButton.addActionListener(e -> logout());

        frame.setVisible(true);
    }

    private String fetchStaffFirstName(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT FirstName FROM users WHERE Username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("FirstName");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching staff's first name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void loadReservationData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT r.ReservationID, r.RoomID, u.FirstName, r.CheckInDate, r.CheckOutDate, r.Status " +
                           "FROM reservations r JOIN users u ON r.UserID = u.UserID";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int reservationID = rs.getInt("ReservationID");
                int roomID = rs.getInt("RoomID");
                String guestName = rs.getString("FirstName");
                String checkInDate = rs.getString("CheckInDate");
                String checkOutDate = rs.getString("CheckOutDate");
                String status = rs.getString("Status");

                tableModel.addRow(new Object[]{reservationID, roomID, guestName, checkInDate, checkOutDate, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading reservation data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a reservation to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int reservationID = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel this reservation?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE reservations SET Status = 'canceled' WHERE ReservationID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, reservationID);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Reservation canceled successfully!");

            // Refresh reservations table
            loadReservationData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error canceling reservation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        frame.dispose();
        new LoginPage();
    }
}