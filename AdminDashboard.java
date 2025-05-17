package group2hotel;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard {
    private JFrame frame;

    public AdminDashboard() {
        frame = new JFrame("Admin Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome to Admin Dashboard");
        welcomeLabel.setBounds(50, 20, 300, 30);
        frame.add(welcomeLabel);

        // User Management Section
        JButton createUserButton = new JButton("Create User");
        createUserButton.setBounds(50, 70, 200, 30);
        frame.add(createUserButton);

        JButton updateUserButton = new JButton("Update User Profile");
        updateUserButton.setBounds(50, 110, 200, 30);
        frame.add(updateUserButton);

        // Room Management Section
        JButton updateRoomButton = new JButton("Update Room Availability");
        updateRoomButton.setBounds(50, 150, 200, 30);
        frame.add(updateRoomButton);

        // Reservation Management Section
        JButton modifyReservationButton = new JButton("Modify Reservation");
        modifyReservationButton.setBounds(50, 190, 200, 30);
        frame.add(modifyReservationButton);

        JButton cancelReservationButton = new JButton("Cancel Reservation");
        cancelReservationButton.setBounds(50, 230, 200, 30);
        frame.add(cancelReservationButton);

        // Payment Management Section
        JButton recordPaymentButton = new JButton("Record Payment");
        recordPaymentButton.setBounds(50, 270, 200, 30);
        frame.add(recordPaymentButton);

        JButton viewPaymentButton = new JButton("View Payments");
        viewPaymentButton.setBounds(50, 310, 200, 30);
        frame.add(viewPaymentButton);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 350, 200, 30);
        frame.add(logoutButton);

        // Button Actions
        createUserButton.addActionListener(e -> openCreateAccountPage());
        updateUserButton.addActionListener(e -> new UpdateUserPage());
        updateRoomButton.addActionListener(e -> new UpdateRoomPage());
        modifyReservationButton.addActionListener(e -> new ModifyReservationPage());
        cancelReservationButton.addActionListener(e -> new CancelReservationPage());
        recordPaymentButton.addActionListener(e -> new RecordPaymentPage());
        viewPaymentButton.addActionListener(e -> new ViewPaymentsPage());
        logoutButton.addActionListener(e -> logout());

        frame.setVisible(true);
    }

    private void openCreateAccountPage() {
        frame.dispose();
        new CreateAccountPage();
    }

    private void logout() {
        frame.dispose();
        new LoginPage();
    }
}