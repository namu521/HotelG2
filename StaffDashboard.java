package group2hotel;

import javax.swing.*;

public class StaffDashboard {
    private JFrame frame;

    public StaffDashboard() {
        frame = new JFrame("Staff Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel label = new JLabel("Welcome to Staff Dashboard");
        label.setBounds(50, 50, 300, 30);
        frame.add(label);

        frame.setVisible(true);
    }
}