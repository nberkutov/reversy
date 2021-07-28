package client;

import client.models.Player;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.GameBoardResponse;
import dto.response.player.MessageResponse;
import dto.response.player.SearchGameResponse;
import exception.GameErrorCode;
import exception.GameException;
import gui.GameGUI;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.JsonService;

import java.io.IOException;
import java.net.Socket;

@Slf4j
@EqualsAndHashCode(callSuper = false)
public class Client extends Thread {
    private final Player player;
    private final ClientConnection connection;
    private final GameGUI gui;

    public Client(final String ip, final int port, final Player player, GameGUI gui) throws GameException {
        try {
            this.player = player;
            Socket socket = new Socket(ip, port);
            this.connection = new ClientConnection(socket);
            this.gui = gui;
        } catch (IOException e) {
            throw new GameException(GameErrorCode.SERVER_NOT_STARTED);
        }
    }

    private void actionByResponseFromServer(final GameResponse gameResponse)
            throws GameException, IOException, InterruptedException {
        switch (JsonService.getCommandByResponse(gameResponse)) {
            case ERROR:
                ErrorResponse error = (ErrorResponse) gameResponse;
                actionError(error);
                break;
            case GAME_PLAYING:
                GameBoardResponse response = (GameBoardResponse) gameResponse;
                actionPlaying(response);
                break;
            case CREATE_PLAYER:
                CreatePlayerResponse createPlayer = (CreatePlayerResponse) gameResponse;
                actionCreatePlayer(createPlayer);
                break;
            case GAME_START:
                SearchGameResponse createGame = (SearchGameResponse) gameResponse;
                actionStartGame(createGame);
                break;
            case MESSAGE:
                MessageResponse message = (MessageResponse) gameResponse;
                actionMessage(message);
                break;
            default:
                log.error("Unknown response {}", gameResponse);
        }
    }

    private void actionMessage(final MessageResponse response) {
        log.info("actionMessage {}", response);
    }

    private static boolean nowMoveByMe(Player user, GameState state) {
        if (user.getColor() == PlayerColor.WHITE && state == GameState.WHITE_MOVE) {
            return true;
        }
        return user.getColor() == PlayerColor.BLACK && state == GameState.BLACK_MOVE;
    }

    @Override
    public void run() {
        log.info("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(10);
                ClientController.sendRequest(connection, new CreatePlayerRequest(player.getNickname()));
            } catch (InterruptedException | IOException | GameException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (connection.isConnected()) {
                try {
                    GameResponse response = ClientController.getRequest(connection);
                    actionByResponseFromServer(response);
                } catch (GameException e) {
                    log.error("GameError {} {}", connection.getSocket(), e.getErrorCode());
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error {} {}", connection.getSocket(), e.getMessage());
            connection.close();
        }
    }

    private void actionError(final ErrorResponse response) {
        log.error("actionError {}", response);
    }

    private void actionCreatePlayer(final CreatePlayerResponse response) throws IOException, GameException {
        log.debug("actionCreatePlayer {}", response);
        ClientController.sendRequest(connection, new WantPlayRequest());
    }

    private void actionPlaying(final GameBoardResponse response) throws GameException, IOException, InterruptedException {
        log.debug("actionPlaying {} {}", connection.getSocket().getLocalPort(), response);
        GameBoard board = response.getBoard();
        gui.updateGUI(board, response.getState(), response.getOpponent().getNickname());
        if (response.getState() != GameState.END) {
            if (nowMoveByMe(player, response.getState())) {
                Thread.sleep(1);
                Point move = player.move(board);
                ClientController.sendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            }
        } else {
            player.setColor(PlayerColor.NONE);
            ClientController.sendRequest(connection, new WantPlayRequest());
        }
    }

    private void actionStartGame(final SearchGameResponse response) {
        log.debug("actionStartGame {}", response);
        player.setColor(response.getColor());
    }
}
