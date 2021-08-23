package gui;

import client.ClientController;
import gui.listener.RoomWindowListener;
import org.example.dto.request.room.CloseRoomRequest;
import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.RoomResponse;
import org.example.models.ClientConnection;

import javax.swing.*;
import java.awt.*;

public class RoomGUI extends JFrame {
    private final JPanel mainPanel;
    private final JPanel roomPanel;
    private final JPanel panelButton;
    private final JButton closeRoomButton;
    private final JLabel idRoom;
    private final JLabel whitePlayer;
    private final JLabel blackPlayer;

    private ClientConnection connection;

    public RoomGUI() {
        mainPanel = new JPanel();
        roomPanel = new JPanel();
        panelButton = new JPanel();
        idRoom = new JLabel();
        whitePlayer = new JLabel();
        blackPlayer = new JLabel();
        closeRoomButton = new JButton("Закрыть комнату");
        setLayoutManager();
        addComponentsToJFrame();
        addActionEvent();
        init();
    }

    public static void main(final String[] a) {
        final RoomGUI frame = new RoomGUI();
        frame.initRoom(new RoomResponse(2, new PlayerResponse("A"), new PlayerResponse(null)));
        frame.setVisible(true);
    }

    public void initConnection(final ClientConnection connection) {
        this.connection = connection;
    }

    private void defaultTexts() {
        whitePlayer.setText("Белый игрок: Ждём");
        blackPlayer.setText("Чёрный игрок: Ждём");
    }

    private void init() {
        setTitle("Комната");
        setPreferredSize(new Dimension(350, 200));
        addWindowListener(new RoomWindowListener());
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void setLayoutManager() {
        mainPanel.setLayout(new BorderLayout());
        roomPanel.setLayout(new GridLayout(3, 2, 1, 1));
        panelButton.setLayout(new FlowLayout());
    }

    private void addComponentsToJFrame() {
        mainPanel.add(roomPanel, BorderLayout.CENTER);
        roomPanel.add(idRoom);
        roomPanel.add(whitePlayer);
        roomPanel.add(blackPlayer);
        panelButton.add(closeRoomButton);
        mainPanel.add(panelButton, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public void initRoom(final RoomResponse response) {
        defaultTexts();
        idRoom.setText(String.format("Комната: %d", response.getId()));
        final PlayerResponse white = response.getWhitePlayer();
        final PlayerResponse black = response.getBlackPlayer();

        if (white != null && white.getNickname() != null) {
            whitePlayer.setText(String.format("Белый игрок: %s", white.getNickname()));
        }
        if (black != null && black.getNickname() != null) {
            blackPlayer.setText(String.format("Чёрный игрок: %s", black.getNickname()));
        }

    }

    public void closeRoom() {
        final Object[] options = {"Да", "Нет!"};
        final int n = JOptionPane
                .showOptionDialog(null, "Закрыть комнату?",
                        "Подтверждение", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            ClientController.safeSendRequest(connection, new CloseRoomRequest());
        }
    }

    private void addActionEvent() {
        closeRoomButton.addActionListener(e -> {
            closeRoom();
        });
    }
}
