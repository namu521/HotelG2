package group2hotel;

import javax.swing.*;

public class ViewPaymentsPage {
    private JFrame frame;

    public ViewPaymentsPage() {
        frame = new JFrame("View Payments");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel label = new JLabel("View Payments - (Functionality Not Yet Implemented)");
        label.setBounds(50, 50, 400, 30);
        frame.add(label);

        JButton backButton = new JButton("Back");
        backButton.setBounds(50, 100, 100, 30);
        frame.add(backButton);

        backButton.addActionListener(e -> goBack());

        frame.setVisible(true);
    }

    private void goBack() {
        frame.dispose();
        new AdminDashboard();
    }
}