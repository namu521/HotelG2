package group2hotel;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class GuestDashboard {
    private JFrame frame;
    private String username;
    private String firstName;
    private DefaultTableModel reservationTableModel;
    private DefaultTableModel roomTableModel;

    public GuestDashboard(String username) {
        this.username = username;
        this.firstName = fetchGuestFirstName(username); // Fetch guest's first name
        frame = new JFrame("Guest Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + (firstName != null ? firstName : username) + "!");
        welcomeLabel.setBounds(50, 20, 300, 30);
        frame.add(welcomeLabel);

        // Reservations Table
        JLabel reservationsLabel = new JLabel("Your Reservations:");
        reservationsLabel.setBounds(50, 60, 300, 30);
        frame.add(reservationsLabel);

        reservationTableModel = new DefaultTableModel();
        reservationTableModel.addColumn("Reservation ID");
        reservationTableModel.addColumn("Room ID");
        reservationTableModel.addColumn("Check-In Date");
        reservationTableModel.addColumn("Check-Out Date");
        reservationTableModel.addColumn("Status");

        JTable reservationsTable = new JTable(reservationTableModel);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        reservationsScrollPane.setBounds(50, 100, 300, 200);
        frame.add(reservationsScrollPane);

        loadAllReservations();

        // Available Rooms Table
        JLabel roomsLabel = new JLabel("Available Rooms:");
        roomsLabel.setBounds(400, 60, 300, 30);
        frame.add(roomsLabel);

        roomTableModel = new DefaultTableModel();
        roomTableModel.addColumn("Room ID");
        roomTableModel.addColumn("Room Type");
        roomTableModel.addColumn("Capacity");
        roomTableModel.addColumn("Price");

        JTable roomsTable = new JTable(roomTableModel);
        JScrollPane roomsScrollPane = new JScrollPane(roomsTable);
        roomsScrollPane.setBounds(400, 100, 350, 200);
        frame.add(roomsScrollPane);

        loadAvailableRooms();

        // Action Buttons
        JButton reserveAndPayButton = new JButton("Reserve & Pay");
        reserveAndPayButton.setBounds(400, 320, 200, 30);
        frame.add(reserveAndPayButton);

        JButton cancelReservationButton = new JButton("Cancel Reservation");
        cancelReservationButton.setBounds(50, 320, 200, 30);
        frame.add(cancelReservationButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 500, 200, 30);
        frame.add(logoutButton);

        // Button Actions
        reserveAndPayButton.addActionListener(e -> reserveAndPay(roomsTable));
        cancelReservationButton.addActionListener(e -> cancelReservation(reservationsTable));
        logoutButton.addActionListener(e -> logout());

        frame.setVisible(true);
    }

    private String fetchGuestFirstName(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT FirstName FROM users WHERE Username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("FirstName");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching guest's first name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void loadAllReservations() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT r.ReservationID, r.RoomID, r.CheckInDate, r.CheckOutDate, r.Status " +
                           "FROM reservations r " +
                           "JOIN users u ON r.UserID = u.UserID " +
                           "WHERE u.Username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            reservationTableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int reservationID = rs.getInt("ReservationID");
                int roomID = rs.getInt("RoomID");
                String checkInDate = rs.getString("CheckInDate");
                String checkOutDate = rs.getString("CheckOutDate");
                String status = rs.getString("Status");

                reservationTableModel.addRow(new Object[]{reservationID, roomID, checkInDate, checkOutDate, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading reservations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAvailableRooms() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT r.RoomID, r.RoomType, r.Capacity, r.Price " +
                           "FROM rooms r " +
                           "WHERE r.RoomID NOT IN (SELECT RoomID FROM reservations WHERE Status IN ('pending', 'confirmed'))";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            roomTableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int roomID = rs.getInt("RoomID");
                String roomType = rs.getString("RoomType");
                int capacity = rs.getInt("Capacity");
                double price = rs.getDouble("Price");

                roomTableModel.addRow(new Object[]{roomID, roomType, capacity, price});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading available rooms: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reserveAndPay(JTable roomsTable) {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to reserve.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roomID = (int) roomTableModel.getValueAt(selectedRow, 0);
        double pricePerNight = (double) roomTableModel.getValueAt(selectedRow, 3);

        // Open a date selection dialog
        JDateChooser checkInDateChooser = new JDateChooser();
        JDateChooser checkOutDateChooser = new JDateChooser();

        Object[] message = {
            "Select Check-In Date:", checkInDateChooser,
            "Select Check-Out Date:", checkOutDateChooser
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Select Dates", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(frame, "Reservation canceled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date checkInDate = checkInDateChooser.getDate();
        Date checkOutDate = checkOutDateChooser.getDate();

        if (checkInDate == null || checkOutDate == null) {
            JOptionPane.showMessageDialog(frame, "Please select valid dates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate checkIn = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOut = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (!checkOut.isAfter(checkIn)) {
            JOptionPane.showMessageDialog(frame, "Check-Out Date must be after Check-In Date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = pricePerNight * numberOfNights;

        // Display total price confirmation
        int confirmation = JOptionPane.showConfirmDialog(frame, "The total price for your reservation is PHP " + totalAmount + ".\nDo you want to proceed?", "Confirm Total Price", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(frame, "Reservation canceled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] paymentMethods = {"credit_card", "debit_card", "cash", "online"};
        String paymentMethod = (String) JOptionPane.showInputDialog(frame, "Select Payment Method:", "Payment", JOptionPane.QUESTION_MESSAGE, null, paymentMethods, paymentMethods[0]);

        if (paymentMethod == null) {
            JOptionPane.showMessageDialog(frame, "Payment canceled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String reservationQuery = "INSERT INTO reservations (UserID, RoomID, CheckInDate, CheckOutDate, NumberOfGuests, Status) " +
                                      "VALUES ((SELECT UserID FROM users WHERE Username = ?), ?, ?, ?, 1, 'confirmed')";
            PreparedStatement reservationStmt = conn.prepareStatement(reservationQuery, Statement.RETURN_GENERATED_KEYS);
            reservationStmt.setString(1, username);
            reservationStmt.setInt(2, roomID);
            reservationStmt.setString(3, checkIn.toString());
            reservationStmt.setString(4, checkOut.toString());
            reservationStmt.executeUpdate();

            ResultSet generatedKeys = reservationStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to create reservation, no ID obtained.");
            }
            int reservationID = generatedKeys.getInt(1);

            String paymentQuery = "INSERT INTO payments (ReservationID, Amount, PaymentMethod) VALUES (?, ?, ?)";
            PreparedStatement paymentStmt = conn.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, reservationID);
            paymentStmt.setDouble(2, totalAmount);
            paymentStmt.setString(3, paymentMethod);
            paymentStmt.executeUpdate();

            conn.commit();

            JOptionPane.showMessageDialog(frame, "Reservation and payment successful!");

            // Refresh tables
            loadAllReservations();
            loadAvailableRooms();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error processing reservation or payment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelReservation(JTable reservationsTable) {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a reservation to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int reservationID = (int) reservationTableModel.getValueAt(selectedRow, 0);

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
            loadAllReservations();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error canceling reservation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        frame.dispose();
        new LoginPage();
    }
}