package client;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameException;
import gui.WindowGUI;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.GameBoard;
import models.base.GameState;
import models.base.PlayerColor;
import models.board.Point;
import models.player.Player;
import models.player.RandomBot;
import services.JsonService;

import java.io.IOException;
import java.net.Socket;

@Slf4j
@Data
public class Client implements Runnable {
    private Player player = new RandomBot();
    private final ClientConnection connection;
    private WindowGUI gui;

    public Client(final String ip, final int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Client(final Socket socket) throws IOException {
        this(new ClientConnection(socket));
    }

    public Client(final ClientConnection connection) {
        this.connection = connection;
        gui = new WindowGUI();
    }

    private void actionByResponseFromServer(final GameResponse gameResponse) throws GameException, IOException, InterruptedException {
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

    private static boolean nowMoveByMe(Player player, GameState state) {
        if (player.getColor() == PlayerColor.WHITE && state == GameState.WHITE_MOVE) {
            return true;
        }
        return player.getColor() == PlayerColor.BLACK && state == GameState.BLACK_MOVE;
    }

    @Override
    public void run() {
        log.info("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                ClientController.sendRequest(connection, new CreatePlayerRequest("Bot"));
            } catch (InterruptedException | IOException | GameException e) {
                e.printStackTrace();
            }
        }).start();

        while (connection.isConnected()) {
            try {
                GameResponse response = ClientController.getRequest(connection);
                actionByResponseFromServer(response);
            } catch (GameException | IOException | InterruptedException e) {
                log.error("Error {}", connection.getSocket(), e);
            }
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
        gui.updateGUI(board, response.getState());
        if (response.getState() != GameState.END) {
            if (nowMoveByMe(player, response.getState())) {
                Thread.sleep(1000);
                Point move = player.move(board);
                ClientController.sendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            }
        } else {
            player.setColor(PlayerColor.NONE);
            //ClientController.sendRequest(connection, new WantPlayRequest());
        }
    }

    private void actionStartGame(final SearchGameResponse response) {
        log.debug("actionStartGame {}", response);
        player.setColor(response.getColor());
    }
}
