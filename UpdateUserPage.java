package group2hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UpdateUserPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private String username; // Store the admin's username

    public UpdateUserPage(String username) {
        this.username = username; // Save the admin's username
        setTitle("Update User Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Panel for the table of users
        JPanel tablePanel = new JPanel(new BorderLayout());
        contentPane.add(tablePanel, BorderLayout.CENTER);

        JLabel tableLabel = new JLabel("Select a user to edit:");
        tableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        // Initialize the table and model
        tableModel = new DefaultTableModel(new Object[]{"Username", "First Name", "Last Name", "Email", "Phone", "Address"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for the form to update user details
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        contentPane.add(formPanel, BorderLayout.SOUTH);

        JLabel usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setEditable(false); // Username should not be editable
        formPanel.add(usernameField);

        JLabel firstNameLabel = new JLabel("First Name:");
        formPanel.add(firstNameLabel);

        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        formPanel.add(lastNameLabel);

        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        JLabel emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel);

        emailField = new JTextField();
        formPanel.add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        formPanel.add(phoneLabel);

        phoneField = new JTextField();
        formPanel.add(phoneField);

        JLabel addressLabel = new JLabel("Address:");
        formPanel.add(addressLabel);

        addressField = new JTextField();
        formPanel.add(addressField);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateUserProfile());
        formPanel.add(updateButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBack());
        formPanel.add(backButton);

        // Load users into the table
        loadUsers();

        // Add a listener to populate fields when a user is selected
        userTable.getSelectionModel().addListSelectionListener(event -> populateFields());
    }

    private void loadUsers() {
        tableModel.setRowCount(0); // Clear existing rows
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT Username, FirstName, LastName, Email, Phone, Address FROM users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("Username");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                String address = rs.getString("Address");
                tableModel.addRow(new Object[]{username, firstName, lastName, email, phone, address});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 0);
            String firstName = (String) tableModel.getValueAt(selectedRow, 1);
            String lastName = (String) tableModel.getValueAt(selectedRow, 2);
            String email = (String) tableModel.getValueAt(selectedRow, 3);
            String phone = (String) tableModel.getValueAt(selectedRow, 4);
            String address = (String) tableModel.getValueAt(selectedRow, 5);

            usernameField.setText(username);
            firstNameField.setText(firstName);
            lastNameField.setText(lastName);
            emailField.setText(email);
            phoneField.setText(phone);
            addressField.setText(address);
        }
    }

    private void updateUserProfile() {
        String username = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET FirstName = ?, LastName = ?, Email = ?, Phone = ?, Address = ? WHERE Username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "User profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating user profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        dispose(); // Close the UpdateUserPage
        new AdminDashboard(username).setVisible(true); // Reopen the AdminDashboard
    }
}