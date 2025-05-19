package group2hotel;

import javax.swing.*;
import java.sql.*;

public class UpdateRoomPage {
    private JFrame frame;
    private JComboBox<String> roomDropdown;
    private JComboBox<String> availabilityDropdown;

    public UpdateRoomPage() {
        frame = new JFrame("Update Room Availability");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel label = new JLabel("Select a Room to Update:");
        label.setBounds(50, 50, 400, 30);
        frame.add(label);

        roomDropdown = new JComboBox<>();
        roomDropdown.setBounds(50, 100, 400, 30);
        frame.add(roomDropdown);

        JLabel availabilityLabel = new JLabel("Set Availability:");
        availabilityLabel.setBounds(50, 150, 400, 30);
        frame.add(availabilityLabel);

        availabilityDropdown = new JComboBox<>(new String[]{"Available", "Unavailable"});
        availabilityDropdown.setBounds(50, 200, 400, 30);
        frame.add(availabilityDropdown);

        JButton updateButton = new JButton("Update Room");
        updateButton.setBounds(50, 250, 200, 30);
        frame.add(updateButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(50, 300, 100, 30);
        frame.add(backButton);

        updateButton.addActionListener(e -> updateRoomAvailability());
        backButton.addActionListener(e -> goBack());

        loadRooms();

        frame.setVisible(true);
    }

    private void loadRooms() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT RoomID, RoomType FROM rooms";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int roomID = rs.getInt("RoomID");
                String roomType = rs.getString("RoomType");
                roomDropdown.addItem("ID: " + roomID + " - " + roomType);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading rooms: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoomAvailability() {
        String selectedRoom = (String) roomDropdown.getSelectedItem();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(frame, "No room selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roomID = Integer.parseInt(selectedRoom.split(":")[1].split("-")[0].trim());
        String availability = (String) availabilityDropdown.getSelectedItem();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE rooms SET Availability = ? WHERE RoomID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, availability.equals("Available") ? "available" : "unavailable");
            stmt.setInt(2, roomID);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Room availability updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating room availability: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        frame.dispose();
        new AdminDashboard();
    }
}