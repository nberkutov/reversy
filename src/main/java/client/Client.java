package client;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameErrorCode;
import exception.GameException;
import gui.WindowGUI;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.GameBoard;
import models.base.GameState;
import models.base.PlayerColor;
import models.board.Point;
import services.BoardService;
import services.JsonService;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Random;

@Slf4j
@Data
public class Client implements Runnable {
    private int gameId;
    private PlayerColor color;
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

    private static boolean nowMoveByMe(PlayerColor color, GameState state) {
        if (color == PlayerColor.WHITE && state == GameState.WHITE_MOVE) {
            return true;
        }
        return color == PlayerColor.BLACK && state == GameState.BLACK_MOVE;
    }

    private static void sendRequest(final ClientConnection server, final GameRequest request) throws IOException, GameException {
        if (server.isConnected()) {
            log.debug("sendRequest {} {}", server.getSocket().getLocalPort(), request);
            server.send(JsonService.toMsgParser(request));
        }
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

    private static GameResponse getRequest(final ClientConnection server) throws GameException, IOException {
        if (!server.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }

        String msg = server.readMsg();
        log.debug("Client getRequest {} {}", server.getSocket().getLocalPort(), msg);
        return JsonService.getResponseFromMsg(msg);
    }

    @Override
    public void run() {
        log.info("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                sendRequest(connection, new CreatePlayerRequest("Bot"));
            } catch (InterruptedException | IOException | GameException e) {
                e.printStackTrace();
            }
        }).start();

        while (connection.isConnected()) {
            try {
                GameResponse response = getRequest(connection);
                actionByResponseFromServer(response);
            } catch (GameException | IOException | InterruptedException e) {
                log.error("Error {}", connection.getSocket(), e);
            }
        }

    }

    private void actionCreatePlayer(final CreatePlayerResponse response) throws IOException, GameException {
        //log.info("actionCreatePlayer {}", response);
        sendRequest(connection, new WantPlayRequest());
    }

    private void actionError(final ErrorResponse response) {
        log.error("actionError {}", response);
    }

    private void actionPlaying(final GameBoardResponse response) throws GameException, IOException, InterruptedException {
        log.debug("actionPlaying {} {}", connection.getSocket().getLocalPort(), response);
        GameBoard board = response.getBoard();
        gui.updateGUI(board, response.getState());
        if (response.getState() != GameState.END) {
            if (nowMoveByMe(color, response.getState())) {
                Thread.sleep(1000);
                List<Point> points = BoardService.getAvailableMoves(board, color);
                Point move = points.get(new Random().nextInt(points.size()));
                sendRequest(connection, MovePlayerRequest.toDto(gameId, move));
            }
        } else {
            gameId = -1;
            color = PlayerColor.NONE;
            //sendRequest(connection, new WantPlayRequest());
        }
    }

    private void actionStartGame(final SearchGameResponse response) {
        log.info("actionStartGame {}", response);
        gameId = response.getGameId();
        color = response.getColor();
    }
}
