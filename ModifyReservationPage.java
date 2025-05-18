package group2hotel;

import javax.swing.*;

public class ModifyReservationPage {
    private JFrame frame;

    public ModifyReservationPage() {
        frame = new JFrame("Modify Reservation");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel label = new JLabel("Modify Reservation - (Functionality Not Yet Implemented)");
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