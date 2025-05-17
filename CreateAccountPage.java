package group2hotel;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class CreateAccountPage {
    private JFrame frame;
    private JTextField firstNameField, lastNameField, usernameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox;

    public CreateAccountPage() {
        frame = new JFrame("Create Account");
        frame.setSize(500, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setBounds(50, 50, 100, 30);
        frame.add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setBounds(150, 50, 200, 30);
        frame.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setBounds(50, 100, 100, 30);
        frame.add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setBounds(150, 100, 200, 30);
        frame.add(lastNameField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 150, 100, 30);
        frame.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 150, 200, 30);
        frame.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 200, 100, 30);
        frame.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 200, 200, 30);
        frame.add(passwordField);

        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setBounds(50, 250, 100, 30);
        frame.add(userTypeLabel);

        userTypeBox = new JComboBox<>(new String[]{"admin", "staff", "guest"});
        userTypeBox.setBounds(150, 250, 200, 30);
        frame.add(userTypeBox);

        // Guest-Specific Fields
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 300, 100, 30);
        frame.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 300, 200, 30);
        frame.add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 350, 100, 30);
        frame.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 350, 200, 30);
        frame.add(phoneField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(50, 400, 100, 30);
        frame.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(150, 400, 200, 30);
        frame.add(addressField);

        JButton createButton = new JButton("Create Account");
        createButton.setBounds(150, 450, 150, 30);
        frame.add(createButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(50, 450, 80, 30);
        frame.add(backButton);

        // Hide guest-specific fields for admin and staff
        userTypeBox.addActionListener(e -> toggleGuestFields());
        createButton.addActionListener(e -> createAccount());
        backButton.addActionListener(e -> goBack());

        frame.setVisible(true);
        toggleGuestFields(); // Ensure fields are hidden initially for non-guest types
    }

    private void toggleGuestFields() {
        boolean isGuest = "guest".equals(userTypeBox.getSelectedItem());
        emailField.setVisible(isGuest);
        phoneField.setVisible(isGuest);
        addressField.setVisible(isGuest);
    }

    private void createAccount() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeBox.getSelectedItem();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("guest".equals(userType) && (email.isEmpty() || phone.isEmpty() || address.isEmpty())) {
            JOptionPane.showMessageDialog(frame, "Email, Phone, and Address are required for guests!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (FirstName, LastName, Username, Password, UserType, Email, Phone, Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, username);
            stmt.setString(4, password);
            stmt.setString(5, userType);

            // Set guest-specific fields or null for admin/staff
            stmt.setString(6, "guest".equals(userType) ? email : null);
            stmt.setString(7, "guest".equals(userType) ? phone : null);
            stmt.setString(8, "guest".equals(userType) ? address : null);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Account created successfully!");

            frame.dispose();
            if ("guest".equals(userType)) {
                new GuestDashboard(username);
            } else {
                new AdminDashboard();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(frame, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
            }
        }
    }

    private void goBack() {
        frame.dispose();
        new AdminDashboard();
    }
}