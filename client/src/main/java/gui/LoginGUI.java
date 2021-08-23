package gui;

import client.ClientController;
import gui.listener.GameWindowListener;
import org.example.dto.request.player.AuthUserRequest;
import org.example.dto.request.player.CreateUserRequest;
import org.example.models.ClientConnection;

import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {
    private final Container container = getContentPane();
    private final JLabel userLabel = new JLabel("Username");
    private final JTextField userTextField = new JTextField();
    private final JButton loginButton = new JButton("Login");
    private final JButton resetButton = new JButton("Registration");

    private ClientConnection connection;


    public LoginGUI() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        init();
    }

    public static void main(final String[] a) {
        final LoginGUI frame = new LoginGUI();
        frame.setVisible(true);
    }

    private void init() {
        setTitle("Login");
        setVisible(false);
        setBounds(10, 10, 370, 200);
        addWindowListener(new GameWindowListener());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
    }

    public void initConnection(final ClientConnection connection) {
        this.connection = connection;
    }

    private void setLayoutManager() {
        container.setLayout(null);
    }

    private void setLocationAndSize() {
        userLabel.setBounds(50, 25, 100, 30);
        userTextField.setBounds(150, 25, 150, 30);
        loginButton.setBounds(50, 80, 100, 30);
        resetButton.setBounds(180, 80, 120, 30);
    }

    private void addComponentsToContainer() {
        container.add(userLabel);
        container.add(userTextField);
        container.add(loginButton);
        container.add(resetButton);
    }

    private void addActionEvent() {
        loginButton.addActionListener(e -> {
            final String userText = userTextField.getText();
            ClientController.safeSendRequest(connection, new AuthUserRequest(userText));
        });
        resetButton.addActionListener(e -> {
            final String userText = userTextField.getText();
            ClientController.safeSendRequest(connection, new CreateUserRequest(userText));
        });
    }
}
