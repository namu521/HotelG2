package group2hotel;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        frame = new JFrame("Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        frame.add(userLabel);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        frame.add(passLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 30);
        frame.add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        frame.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 150, 100, 30);
        frame.add(loginButton);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(150, 200, 150, 30);
        frame.add(createAccountButton);

        loginButton.addActionListener(e -> authenticateUser());
        createAccountButton.addActionListener(e -> openCreateAccountPage());

        frame.setVisible(true);
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT UserType FROM users WHERE Username = ? AND Password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String userType = rs.getString("UserType");
                frame.dispose();
                switch (userType) {
                    case "admin":
                        new AdminDashboard();
                        break;
                    case "guest":
                        new GuestDashboard(username);
                        break;
                    case "staff":
                        new StaffDashboard();
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openCreateAccountPage() {
        frame.dispose();
        new CreateAccountPage();
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}