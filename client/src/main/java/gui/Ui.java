package gui;

import client.ClientController;
import org.example.dto.request.player.MovePlayerRequest;
import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.ListRoomResponse;
import org.example.dto.response.room.RoomResponse;
import org.example.models.ClientConnection;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;

import javax.swing.*;


public class Ui implements GUI {
    private ClientConnection connection;
    private LoginGUI auth;
    private MenuGUI menu;
    private GameWindow game;
    private RoomGUI room;
    private ConsoleGUI console;

    public Ui() {
        SwingUtilities.invokeLater(() -> {
            auth = new LoginGUI();
            menu = new MenuGUI(this);
            game = new GameWindow();
            room = new RoomGUI();
            console = new ConsoleGUI();
        });
    }

    @Override
    public void init(final ClientConnection connection) {
        SwingUtilities.invokeLater(() -> {
            this.connection = connection;
            auth.initConnection(connection);
            menu.initConnection(connection);
            room.initConnection(connection);
            auth.setVisible(true);
            console.setVisible(true);
            menu.setVisible(false);
            game.setVisible(false);
            room.setVisible(false);
        });
    }

    @Override
    public void closeAuthAndInitMenu(final long playerId, final String nickname) {
        SwingUtilities.invokeLater(() -> {
            auth.setVisible(false);
            menu.setVisible(true);
            menu.updateInfoPlayer(playerId, nickname);
        });
    }

    @Override
    public void updateMenu(final ListRoomResponse rooms) {
        SwingUtilities.invokeLater(() -> {
            menu.updateRooms(rooms);
        });
    }

    @Override
    public void closeMenuAndInitAuth() {
        SwingUtilities.invokeLater(() -> {
            auth.setVisible(true);
            menu.setVisible(false);
            game.setVisible(false);
            room.setVisible(false);
        });
    }

    @Override
    public void initGame(final long gameId, final String nickOpponent) {
        SwingUtilities.invokeLater(() -> {
            game.setCallback((x, y) -> {
                ClientController.safeSendRequest(connection, new MovePlayerRequest(gameId, new Point(x, y)));
            });
            game.updateNicknameOpponent(nickOpponent);
            auth.setVisible(false);
            menu.setVisible(false);
            game.setVisible(true);
            room.setVisible(false);
        });
    }

    @Override
    public void initRoom(final RoomResponse roomResponse) {
        SwingUtilities.invokeLater(() -> {
            room.initRoom(roomResponse);
            room.setVisible(true);
        });
    }

    @Override
    public void closeRoom() {
        SwingUtilities.invokeLater(() -> {
            room.setVisible(false);
        });
    }

    @Override
    public void initPlayerInfo(final PlayerResponse playerResponse) {
        SwingUtilities.invokeLater(() -> {
            final PlayerGUI playerGUI = new PlayerGUI();
            playerGUI.initPlayer(playerResponse);
            playerGUI.setVisible(true);
        });
    }

    @Override
    public void updateGameTitle(final String title) {
        SwingUtilities.invokeLater(() -> {
            game.setTitle(title);
        });
    }

    @Override
    public void updateGame(final GameBoard board, final GameState state) {
        SwingUtilities.invokeLater(() -> {
            game.updateGUI(board, state);
        });
    }

    @Override
    public void closeGame() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(new JFrame(), "Игра окончена");
            auth.setVisible(false);
            menu.setVisible(true);
            game.setVisible(false);
        });
    }

    @Override
    public void createError(final String error) {
        SwingUtilities.invokeLater(() -> {
            console.addString("[ERROR] " + error);
        });
    }

    @Override
    public void createInfo(final String info) {
        SwingUtilities.invokeLater(() -> {
            console.addString("[INFO] " + info);
        });
    }

    @Override
    public void createMessage(final String message) {
        SwingUtilities.invokeLater(() -> {
            console.addString("[MESSAGE] " + message);
        });
    }
}
