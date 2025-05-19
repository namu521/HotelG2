package group2hotel;

import javax.swing.*;

public class RecordPaymentPage extends JFrame {
    private String adminUsername;

    public RecordPaymentPage(String adminUsername) {
        this.adminUsername = adminUsername;

        setTitle("Record Payment");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel label = new JLabel("Record Payment Page for Admin: " + adminUsername);
        label.setBounds(50, 50, 400, 30);
        add(label);

        JButton backButton = new JButton("Back");
        backButton.setBounds(50, 300, 100, 30);
        backButton.addActionListener(e -> {
            dispose(); // Close this page
            new AdminDashboard(adminUsername).setVisible(true); // Go back to Admin Dashboard
        });
        add(backButton);

        setVisible(true);
    }
}