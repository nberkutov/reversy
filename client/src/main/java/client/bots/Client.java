package client.bots;

import client.ClientController;
import gui.GameGUI;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.player.CreateUserRequest;
import org.example.dto.request.player.MovePlayerRequest;
import org.example.dto.request.player.WantPlayRequest;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.GameResponse;
import org.example.dto.response.game.CreateGameResponse;
import org.example.dto.response.game.GameBoardResponse;
import org.example.dto.response.player.CreatePlayerResponse;
import org.example.dto.response.player.MessageResponse;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.ClientConnection;
import org.example.models.Player;
import org.example.models.SmartPlayer;
import org.example.models.base.GameState;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.utils.JsonService;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class Client extends Thread {
    private final SmartPlayer player;
    private final ClientConnection connection;
    private final GameGUI gui;

    public Client(final String ip, final int port, final SmartPlayer player, final GameGUI gui) throws ServerException {
        try {
            this.player = player;
            final Socket socket = new Socket(ip, port);
            this.connection = new ClientConnection(socket);
            this.gui = gui;
        } catch (final IOException e) {
            throw new ServerException(GameErrorCode.SERVER_NOT_STARTED);
        }
    }

    private static boolean nowMoveByMe(final Player player, final GameState state) {
        if (player.getColor() == PlayerColor.WHITE && state == GameState.WHITE_MOVE) {
            return true;
        }
        return player.getColor() == PlayerColor.BLACK && state == GameState.BLACK_MOVE;
    }

    private void actionMessage(final MessageResponse response) {
        log.info("actionMessage {}", response);
    }

    private void actionByResponseFromServer(final GameResponse gameResponse)
            throws ServerException, IOException, InterruptedException {
        switch (JsonService.getCommandByResponse(gameResponse)) {
            case ERROR:
                actionError((ErrorResponse) gameResponse);
                break;
            case GAME_PLAYING:
                actionPlaying((GameBoardResponse) gameResponse);
                break;
            case CREATE_PLAYER:
                actionCreatePlayer((CreatePlayerResponse) gameResponse);
                break;
            case GAME_START:
                actionStartGame((CreateGameResponse) gameResponse);
                break;
            case MESSAGE:
                actionMessage((MessageResponse) gameResponse);
                break;
            case ROOMS:
                System.out.println("Rooms");
                break;
            default:
                log.error("Unknown response {}", gameResponse);
        }
    }

    @Override
    public void run() {
        log.info("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(10);
                ClientController.sendRequest(connection, new CreateUserRequest(player.getNickname()));
            } catch (final InterruptedException | ServerException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (connection.isConnected()) {
                try {
                    final GameResponse response = ClientController.getRequest(connection);
                    actionByResponseFromServer(response);
                } catch (final ServerException e) {
                    log.error("GameError {} {}", connection.getSocket(), e.getErrorCode());
                }
            }
        } catch (final IOException | InterruptedException e) {
            log.error("Error {} {}", connection.getSocket(), e.getMessage());
            connection.close();
        }
    }

    private void actionError(final ErrorResponse response) {
        log.error("actionError {}", response);
    }

    private void actionCreatePlayer(final CreatePlayerResponse response) throws ServerException {
        log.debug("actionCreatePlayer {}", response);
        ClientController.sendRequest(connection, new WantPlayRequest());
    }

    private void actionPlaying(final GameBoardResponse response) throws ServerException, InterruptedException {
        log.debug("actionPlaying {} {}", connection.getSocket().getLocalPort(), response);
        final GameBoard board = response.getBoard();
        gui.updateGUI(board, response.getState());
        if (response.getState() != GameState.END) {
            if (nowMoveByMe(player, response.getState())) {
                Thread.sleep(100);
                final Point move = player.move(board);
                ClientController.sendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            } else {
                player.triggerMoveOpponent(board);
            }
        } else {
            player.triggerGameEnd(response.getState(), board);
            player.setColor(PlayerColor.NONE);
            ClientController.sendRequest(connection, new WantPlayRequest());
        }
    }

    private void actionStartGame(final CreateGameResponse response) {
        log.debug("actionStartGame {}", response);
        player.setColor(response.getColor());
        System.out.println(player.getNickname() + " " + response.getColor());
    }
}
