package client;

import client.models.Player;
import client.models.RandomBotPlayer;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.player.*;
import dto.response.room.ListRoomResponse;
import dto.response.room.RoomResponse;
import exception.GameException;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.board.Point;
import services.JsonService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class GUIClient implements Runnable {
    private final ClientConnection connection;
    private Player player;
    private WindowGUI gui;

    public GUIClient(ClientConnection connection, Player player) {
        this.connection = connection;
        this.player = player;
    }

    public static void main(String[] args) {
        String nickname = "";
        while (nickname.trim().length() == 0) {
            nickname = JOptionPane.showInputDialog("Enter nickname");
        }
        String address = "";
        int port = -1;
        while (address.trim().length() == 0 || port == -1) {
            String fullAddress = JOptionPane.showInputDialog("IP:PORT", "127.0.0.1:8081");
            String[] parts = fullAddress.split(":");
            try {
                address = parts[0];
                port = Integer.parseInt(parts[1]);
                try (Socket socket = new Socket(address, port)) {
                    Thread thread =
                            new Thread(new GUIClient(new ClientConnection(socket), new RandomBotPlayer(nickname)));
                    thread.start();
                    thread.join();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        log.info("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                ClientController.sendRequest(connection, new CreatePlayerRequest(player.getNickname()));
            } catch (InterruptedException | IOException | GameException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (connection.isConnected()) {
                try {
                    GameResponse response = ClientController.getRequest(connection);
                    parseCommand(response);
                } catch (GameException e) {
                    log.error("GameError {} {}", connection.getSocket(), e.getErrorCode());
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error {} {}", connection.getSocket(), e.getMessage());
            connection.close();
        }
    }

    private void parseCommand(final GameResponse gameResponse)
            throws GameException, IOException, InterruptedException {
        System.out.println(gameResponse);
        switch (JsonService.getCommandByResponse(gameResponse)) {
            case ERROR:
                ErrorResponse error = (ErrorResponse) gameResponse;
                onErrorResponse(error);
                break;
            case GAME_PLAYING:
                GameBoardResponse response = (GameBoardResponse) gameResponse;
                onGameBoardResponse(response);
                break;
            case CREATE_PLAYER:
                CreatePlayerResponse createPlayer = (CreatePlayerResponse) gameResponse;
                onCreatePlayerResponse(createPlayer);
                break;
            case GAME_START:
                SearchGameResponse createGame = (SearchGameResponse) gameResponse;
                onStartGameResponse(createGame);
                break;
            case MESSAGE:
                MessageResponse message = (MessageResponse) gameResponse;
                //actionMessage(message);
                break;
            case ROOMS:
                ListRoomResponse getRoomsResponse = (ListRoomResponse) gameResponse;
                onGetRoomsResponse(getRoomsResponse);
                break;
            case ROOM:
                RoomResponse roomResponse = (RoomResponse) gameResponse;
                onGetRoom(roomResponse);
            default:
                log.error("Unknown response {}", gameResponse);
        }
    }

    private void onStartGameResponse(SearchGameResponse response) {
        log.debug("actionStartGame {}", response);
        player.setColor(response.getColor());
        gui = new WindowGUI(player.getColor());
        gui.setCallback((x, y) -> {
            try {
                ClientController.sendRequest(connection, new MovePlayerRequest(response.getGameId(), new Point(x, y)));
            } catch (IOException | GameException e) {
                e.printStackTrace();
            }
        });
    }

    private void onGameBoardResponse(GameBoardResponse response) {
        try {
            gui.updateGUI(response.getBoard(), response.getState());
        } catch (GameException e) {
            e.printStackTrace();
        }
    }

    private void onErrorResponse(ErrorResponse response) {
        JOptionPane.showMessageDialog(new JFrame(), response.getMessage());
    }

    private void onCreatePlayerResponse(CreatePlayerResponse response) throws IOException, GameException {
        ClientController.sendRequest(connection, new GetRoomsRequest());
    }

    private void onGetRoom(RoomResponse response) {

    }

    private void onGetRoomsResponse(ListRoomResponse response) {
        String roomsHeaderText = "Количество доступных комнат: " + response.getList().size();
        JFrame menuWindow = new JFrame();
        menuWindow.setSize(400, 300);
        menuWindow.setLocationRelativeTo(null);
        menuWindow.setLayout(new BorderLayout());
        JPanel roomHandlingPanel = new JPanel();
        roomHandlingPanel.setLayout(new BoxLayout(roomHandlingPanel, BoxLayout.Y_AXIS));
        JButton createRoomButton = new JButton("Создать комнату");
        createRoomButton.addActionListener(actionEvent -> {
            try {
                ClientController.sendRequest(connection, new CreateRoomRequest());
            } catch (IOException | GameException e) {
                e.printStackTrace();
            }
        });
        JButton searchGameButton = new JButton("Искать игру");
        searchGameButton.addActionListener(actionEvent -> {
            try {
                ClientController.sendRequest(connection, new WantPlayRequest());
            } catch (IOException | GameException e) {
                e.printStackTrace();
            }
        });
        roomHandlingPanel.add(createRoomButton);
        roomHandlingPanel.add(searchGameButton);
        JPanel roomsList = new JPanel();
        roomsList.setLayout(new BoxLayout(roomsList, BoxLayout.Y_AXIS));
        JLabel roomsHeaderLabel = new JLabel();
        roomsHeaderLabel.setText(roomsHeaderText);
        roomsList.add(roomsHeaderLabel);
        for (RoomResponse room : response.getList()) {
            roomsList.add(getRoomItem(room));
        }
        menuWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuWindow.add(roomsList, BorderLayout.WEST);
        menuWindow.add(roomHandlingPanel, BorderLayout.EAST);
        menuWindow.setVisible(true);
    }

    private JPanel getRoomItem(RoomResponse room) {
        JPanel panel = new JPanel();
        panel.setAutoscrolls(true);
        JLabel roomTitleLabel = new JLabel();
        roomTitleLabel.setText("Комната "+ room.getId());
        panel.add(roomTitleLabel);
        JLabel roomBlackPlayerLabel = new JLabel();
        JLabel roomWhitePlayerLabel = new JLabel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        PlayerResponse blackPlayer = room.getBlackPlayer();
        PlayerResponse whitePlayer = room.getWhitePlayer();
        if (blackPlayer == null) {
            roomBlackPlayerLabel.setText("Черные: Свободно");
        } else {
            roomBlackPlayerLabel.setText("Черные: " + blackPlayer.getNickname());
        }
        if (whitePlayer == null) {
            roomWhitePlayerLabel.setText("Белые: Свободно");
        } else {
            roomWhitePlayerLabel.setText("Белые: " + whitePlayer.getNickname());
        }
        panel.add(roomBlackPlayerLabel);
        panel.add(roomWhitePlayerLabel);
        if (blackPlayer == null) {
            JButton joinBlackButton = new JButton("Играть черным");
            joinBlackButton.addActionListener((e) -> {
                try {
                    ClientController.sendRequest(connection, new JoinRoomRequest(room.getId()));
                } catch (IOException | GameException ioException) {
                    ioException.printStackTrace();
                }
            });
            panel.add(joinBlackButton);
        }
        if (whitePlayer == null) {
            JButton joinWhiteButton = new JButton("Играть черным");
            joinWhiteButton.addActionListener((e) -> {
                try {
                    ClientController.sendRequest(connection, new JoinRoomRequest(room.getId()));
                } catch (IOException | GameException ioException) {
                    ioException.printStackTrace();
                }
            });
            panel.add(joinWhiteButton);
        }
        return panel;
    }
}