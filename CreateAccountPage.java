package group2hotel;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class CreateAccountPage extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox;
    private String adminUsername; // Admin's username

    public CreateAccountPage(String adminUsername) {
        this.adminUsername = adminUsername; // Save admin's username
        setTitle("Create Account");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setBounds(50, 50, 100, 30);
        add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setBounds(150, 50, 200, 30);
        add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setBounds(50, 100, 100, 30);
        add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setBounds(150, 100, 200, 30);
        add(lastNameField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 150, 100, 30);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 150, 200, 30);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 200, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 200, 200, 30);
        add(passwordField);

        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setBounds(50, 250, 100, 30);
        add(userTypeLabel);

        userTypeBox = new JComboBox<>(new String[]{"admin", "staff", "guest"});
        userTypeBox.setBounds(150, 250, 200, 30);
        add(userTypeBox);

        // Guest-Specific Fields
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 300, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 300, 200, 30);
        add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 350, 100, 30);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 350, 200, 30);
        add(phoneField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(50, 400, 100, 30);
        add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(150, 400, 200, 30);
        add(addressField);

        JButton createButton = new JButton("Create Account");
        createButton.setBounds(150, 450, 150, 30);
        add(createButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(50, 450, 80, 30);
        add(backButton);

        // Hide guest-specific fields for admin and staff
        userTypeBox.addActionListener(e -> toggleGuestFields());
        createButton.addActionListener(e -> createAccount());
        backButton.addActionListener(e -> goBack());

        setVisible(true);
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
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("guest".equals(userType) && (email.isEmpty() || phone.isEmpty() || address.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Email, Phone, and Address are required for guests!", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Account created successfully!");

            dispose();
            new AdminDashboard(adminUsername).setVisible(true); // Go back to AdminDashboard
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
            }
        }
    }

    private void goBack() {
        dispose();
        new AdminDashboard(adminUsername).setVisible(true); // Pass admin username back to AdminDashboard
    }
}