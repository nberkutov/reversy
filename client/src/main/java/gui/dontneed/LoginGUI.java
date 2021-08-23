package gui.dontneed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginGUI implements ActionListener {

    private static JLabel userLabel;
    private static JTextField userText;
    private static JLabel passwordLabel;
    private static JPasswordField passwordText;
    private static JButton button;
    private static JLabel success;
    private static JLabel failure;

    public static void main(String[] args) {

        // new instance of a JFrame, which creates the window of the application.
        JFrame frame = new JFrame();

        // new instance of a JPanel, which is a container inside the JFrame window.
        JPanel panel = new JPanel();

        // Adds the container with adjusted sizing to the window of the application.
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        // Neglects any formal layout of the container, thus enabling custom sizing.
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY);

        // Adds the user name label to the container, with adjusted sizing.
        userLabel = new JLabel("Username: ");
        userLabel.setBounds(10, 20, 100, 25);
        userLabel.setFont(new java.awt.Font("Arial", Font.ITALIC, 16));
        userLabel.setForeground(Color.BLACK);
        panel.add(userLabel);

        // Adds the text field next to the user name label, with adjusted sizing.
        userText = new JTextField(20);
        userText.setBounds(130, 20, 205, 25);
        panel.add(userText);

        // Adds the password label to the container, with adjusted sizing.
        passwordLabel = new JLabel("Password: ");
        passwordLabel.setBounds(10, 50, 100, 25);
        passwordLabel.setFont(new java.awt.Font("Arial", Font.ITALIC, 16));
        passwordLabel.setForeground(Color.BLACK);
        panel.add(passwordLabel);

        // Adds the password text field next to the password label, with the adjusted sizing.
        passwordText = new JPasswordField();
        passwordText.setBounds(130, 50, 205, 25);
        panel.add(passwordText);

        // Adds a Login button to the container, with an event listener and adjusted sizing.
        button = new JButton("Login");
        button.setBounds(130, 100, 205, 25);
        button.addActionListener(new LoginGUI());
        panel.add(button);

        // Adds a Success message if you entered the correct login information.
        success = new JLabel("");
        success.setBounds(10, 160, 400, 25);
        panel.add(success);

        // Adds a Failure message if you entered the incorrect login information.+
        failure = new JLabel("");
        failure.setBounds(10, 160, 700, 25);
        failure.setForeground(Color.RED);
        panel.add(failure);

        // Makes the frame container appear on the screen when set to 'true'.
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = userText.getText();
        String password = passwordText.getText();

        if (user.equals("Alonso") && password.equals("password")) {
            success.setText("Login successful");
        } else {
            failure.setText("The Username or Password does not match our records.");
        }
    }

}