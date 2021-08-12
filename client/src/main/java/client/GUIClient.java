package client;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.game.GameBoardResponse;
import dto.response.player.CreateGameResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.MessageResponse;
import dto.response.player.PlayerResponse;
import dto.response.room.ListRoomResponse;
import dto.response.room.RoomResponse;
import exception.ServerException;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Player;
import models.board.Point;
import models.players.SmartBot;
import models.strategies.RandomStrategy;
import utils.JsonService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class GUIClient extends Thread {
    private final ClientConnection connection;
    private final Player player;
    private WindowGUI gui;

    public GUIClient(final ClientConnection connection, final Player player) {
        this.connection = connection;
        this.player = player;
    }

    public static void main(final String[] args) {
        String nickname = "";
        while (nickname.trim().length() == 0) {
            nickname = JOptionPane.showInputDialog("Enter nickname");
        }
        String address = "";
        int port = -1;
        while (address.trim().length() == 0 || port == -1) {
            final String fullAddress = JOptionPane.showInputDialog("IP:PORT", "127.0.0.1:8081");
            final String[] parts = fullAddress.split(":");
            try {
                address = parts[0];
                port = Integer.parseInt(parts[1]);
                try (final Socket socket = new Socket(address, port)) {
                    final Thread thread =
                            new Thread(new GUIClient(new ClientConnection(socket), new SmartBot(nickname, new RandomStrategy())));
                    thread.start();
                    thread.join();
                }
            } catch (final Exception e) {
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
            } catch (final InterruptedException | ServerException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (connection.isConnected()) {
                try {
                    final GameResponse response = ClientController.getRequest(connection);
                    parseCommand(response);
                } catch (final ServerException e) {
                    log.error("GameError {} {}", connection.getSocket(), e.getErrorCode());
                }
            }
        } catch (final IOException | InterruptedException e) {
            log.error("Error {} {}", connection.getSocket(), e.getMessage());
            connection.close();
        }
    }

    private void parseCommand(final GameResponse gameResponse)
            throws ServerException, IOException, InterruptedException {
        System.out.println(gameResponse);
        switch (JsonService.getCommandByResponse(gameResponse)) {
            case ERROR:
                final ErrorResponse error = (ErrorResponse) gameResponse;
                onErrorResponse(error);
                break;
            case GAME_PLAYING:
                final GameBoardResponse response = (GameBoardResponse) gameResponse;
                onGameBoardResponse(response);
                break;
            case CREATE_PLAYER:
                final CreatePlayerResponse createPlayer = (CreatePlayerResponse) gameResponse;
                onCreatePlayerResponse(createPlayer);
                break;
            case GAME_START:
                final CreateGameResponse createGame = (CreateGameResponse) gameResponse;
                onStartGameResponse(createGame);
                break;
            case MESSAGE:
                final MessageResponse message = (MessageResponse) gameResponse;
                //actionMessage(message);
                break;
            case ROOMS:
                final ListRoomResponse getRoomsResponse = (ListRoomResponse) gameResponse;
                onGetRoomsResponse(getRoomsResponse);
                break;
            case ROOM:
                final RoomResponse roomResponse = (RoomResponse) gameResponse;
                onGetRoom(roomResponse);
            default:
                log.error("Unknown response {}", gameResponse);
        }
    }

    private void onStartGameResponse(final CreateGameResponse response) {
        log.debug("actionStartGame {}", response);
        player.setColor(response.getColor());
        gui = new WindowGUI(player.getColor());
        gui.setCallback((x, y) -> {
            try {
                ClientController.sendRequest(connection, new MovePlayerRequest(response.getGameId(), new Point(x, y)));
            } catch (final ServerException e) {
                e.printStackTrace();
            }
        });
    }

    private void onGameBoardResponse(final GameBoardResponse response) {
        try {
            gui.updateGUI(response.getBoard(), response.getState(), response.getOpponent().getNickname());
        } catch (final ServerException e) {
            e.printStackTrace();
        }
    }

    private void onErrorResponse(final ErrorResponse response) {
        JOptionPane.showMessageDialog(new JFrame(), response.getMessage());
    }

    private void onCreatePlayerResponse(final CreatePlayerResponse response) throws IOException, ServerException {
        ClientController.sendRequest(connection, new GetRoomsRequest());
    }

    private void onGetRoom(final RoomResponse response) {

    }

    private void onGetRoomsResponse(final ListRoomResponse response) {
        final String roomsHeaderText = "Количество доступных комнат: " + response.getList().size();
        final JFrame menuWindow = new JFrame();
        menuWindow.setSize(400, 300);
        menuWindow.setLocationRelativeTo(null);
        menuWindow.setLayout(new BorderLayout());
        final JPanel roomHandlingPanel = new JPanel();
        roomHandlingPanel.setLayout(new BoxLayout(roomHandlingPanel, BoxLayout.Y_AXIS));
        final JButton createRoomButton = new JButton("Создать комнату");
        createRoomButton.addActionListener(actionEvent -> {
            try {
                ClientController.sendRequest(connection, new CreateRoomRequest());
            } catch (final ServerException e) {
                e.printStackTrace();
            }
        });
        final JButton searchGameButton = new JButton("Искать игру");
        searchGameButton.addActionListener(actionEvent -> {
            try {
                ClientController.sendRequest(connection, new WantPlayRequest());
            } catch (final ServerException e) {
                e.printStackTrace();
            }
        });
        roomHandlingPanel.add(createRoomButton);
        roomHandlingPanel.add(searchGameButton);
        final JPanel roomsList = new JPanel();
        roomsList.setLayout(new BoxLayout(roomsList, BoxLayout.Y_AXIS));
        final JLabel roomsHeaderLabel = new JLabel();
        roomsHeaderLabel.setText(roomsHeaderText);
        roomsList.add(roomsHeaderLabel);
        for (final RoomResponse room : response.getList()) {
            roomsList.add(getRoomItem(room));
        }
        menuWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuWindow.add(roomsList, BorderLayout.WEST);
        menuWindow.add(roomHandlingPanel, BorderLayout.EAST);
        menuWindow.setVisible(true);
    }

    private JPanel getRoomItem(final RoomResponse room) {
        final JPanel panel = new JPanel();
        panel.setAutoscrolls(true);
        final JLabel roomTitleLabel = new JLabel();
        roomTitleLabel.setText("Комната " + room.getId());
        panel.add(roomTitleLabel);
        final JLabel roomBlackPlayerLabel = new JLabel();
        final JLabel roomWhitePlayerLabel = new JLabel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        final PlayerResponse blackPlayer = room.getBlackPlayer();
        final PlayerResponse whitePlayer = room.getWhitePlayer();
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
            final JButton joinBlackButton = new JButton("Играть черным");
            joinBlackButton.addActionListener((e) -> {
                try {
                    ClientController.sendRequest(connection, new JoinRoomRequest(room.getId()));
                } catch (final ServerException ioException) {
                    ioException.printStackTrace();
                }
            });
            panel.add(joinBlackButton);
        }
        if (whitePlayer == null) {
            final JButton joinWhiteButton = new JButton("Играть черным");
            joinWhiteButton.addActionListener((e) -> {
                try {
                    ClientController.sendRequest(connection, new JoinRoomRequest(room.getId()));
                } catch (final ServerException ioException) {
                    ioException.printStackTrace();
                }
            });
            panel.add(joinWhiteButton);
        }
        return panel;
    }
}
