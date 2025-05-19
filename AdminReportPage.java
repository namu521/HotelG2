package group2hotel;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder; // Added missing import
import javax.swing.table.DefaultTableModel;

public class AdminReportPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AdminReportPage frame = new AdminReportPage();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public AdminReportPage() {
        setTitle("Admin Report Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); // Fixed compilation issue
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Table to display the report
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Reservation ID");
        tableModel.addColumn("Guest Name");
        tableModel.addColumn("Room ID");
        tableModel.addColumn("Check-In Date");
        tableModel.addColumn("Check-Out Date");
        tableModel.addColumn("Payment Amount");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);

        // Load Report Button
        JButton loadReportButton = new JButton("Load Report");
        loadReportButton.addActionListener(e -> loadReportData());
        buttonPanel.add(loadReportButton);
    }

    /**
     * Load data into the report table.
     */
    private void loadReportData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT r.ReservationID, u.FirstName, r.RoomID, r.CheckInDate, r.CheckOutDate, p.Amount " +
                           "FROM reservations r " +
                           "JOIN users u ON r.UserID = u.UserID " +
                           "JOIN payments p ON r.ReservationID = p.ReservationID";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int reservationID = rs.getInt("ReservationID");
                String guestName = rs.getString("FirstName");
                int roomID = rs.getInt("RoomID");
                String checkInDate = rs.getString("CheckInDate");
                String checkOutDate = rs.getString("CheckOutDate");
                double amount = rs.getDouble("Amount");

                tableModel.addRow(new Object[]{reservationID, guestName, roomID, checkInDate, checkOutDate, amount});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading report data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Navigate back to the Admin Dashboard.
     */
    private void goBack() {
        dispose(); // Close the current AdminReportPage
        new AdminDashboard("admin"); // Reopen the Admin Dashboard (replace "admin" with the actual username if needed)
    }
}