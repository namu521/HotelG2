package group2hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private String username;
    private String firstName;
    private JTable table;
    private DefaultTableModel tableModel;

    public AdminDashboard(String username) {
        this.username = username;
        this.firstName = fetchAdminFirstName(username); // Fetch admin's first name

        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome to the Admin Dashboard, " + (firstName != null ? firstName : username) + "!");
        welcomeLabel.setBounds(50, 20, 600, 30);
        add(welcomeLabel);

        // Table for Reservations and Booked Rooms
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Reservation ID");
        tableModel.addColumn("Room ID");
        tableModel.addColumn("Guest Name");
        tableModel.addColumn("Check-In Date");
        tableModel.addColumn("Check-Out Date");
        tableModel.addColumn("Status");
        tableModel.addColumn("Total Price"); // Added Total Price Column

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 800, 300);
        add(scrollPane);

        // Load data into table
        loadReservationData();

        // Action Buttons
        JButton createUserButton = new JButton("Create User");
        createUserButton.setBounds(50, 450, 150, 30);
        add(createUserButton);

        JButton updateUserButton = new JButton("Update User Profile");
        updateUserButton.setBounds(220, 450, 150, 30);
        add(updateUserButton);

        JButton recordPaymentButton = new JButton("Record Payment");
        recordPaymentButton.setBounds(390, 450, 150, 30);
        add(recordPaymentButton);

        JButton viewPaymentsButton = new JButton("View Payments");
        viewPaymentsButton.setBounds(50, 500, 150, 30);
        add(viewPaymentsButton);

        JButton cancelReservationButton = new JButton("Cancel Reservation");
        cancelReservationButton.setBounds(220, 500, 150, 30);
        add(cancelReservationButton);

        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setBounds(390, 500, 150, 30);
        add(generateReportButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(560, 500, 150, 30);
        add(logoutButton);

        // Button Actions
        createUserButton.addActionListener(e -> {
            dispose(); // Close the Admin Dashboard
            new CreateAccountPage(username).setVisible(true); // Open CreateAccountPage and pass admin username
        });

        updateUserButton.addActionListener(e -> {
            dispose(); // Close the Admin Dashboard
            new UpdateUserPage(username).setVisible(true); // Open Update User Profile Page
        });

        recordPaymentButton.addActionListener(e -> {
            dispose(); // Close the Admin Dashboard
            new RecordPaymentPage(username).setVisible(true); // Open RecordPaymentPage and pass admin username
        });

        viewPaymentsButton.addActionListener(e -> {
            dispose(); // Close the Admin Dashboard
            new ViewPaymentsPage(username).setVisible(true); // Open ViewPaymentsPage and pass admin username
        });

        cancelReservationButton.addActionListener(e -> {
            dispose(); // Close the Admin Dashboard
            new CancelReservationPage(username).setVisible(true); // Open Cancel Reservation Page
        });

        generateReportButton.addActionListener(e -> openAdminReportPage());

        logoutButton.addActionListener(e -> logout());

        setVisible(true);
    }

    private void openAdminReportPage() {
        dispose(); // Dispose the Admin Dashboard frame
        AdminReportPage reportPage = new AdminReportPage(); // Instantiate AdminReportPage
        reportPage.setVisible(true); // Explicitly set the report page to visible
    }

    private String fetchAdminFirstName(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT FirstName FROM users WHERE Username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("FirstName");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching admin's first name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void loadReservationData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT r.ReservationID, r.RoomID, u.FirstName, r.CheckInDate, r.CheckOutDate, r.Status, r.TotalPrice " +
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
                double totalPrice = rs.getDouble("TotalPrice"); // Fetch Total Price

                tableModel.addRow(new Object[]{reservationID, roomID, guestName, checkInDate, checkOutDate, status, totalPrice});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading reservation data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        dispose();
        new LoginPage();
    }
}