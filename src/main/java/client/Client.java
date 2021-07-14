package client;

import com.google.gson.Gson;
import controllers.commands.CommandRequest;
import controllers.commands.CommandResponse;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.GameState;
import models.base.PlayerColor;
import models.board.Board;
import models.board.Point;
import services.BoardService;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Random;

@Slf4j
@Data
public class Client implements Runnable {
    private static Gson gson = new Gson();
    private int playerId;
    private int gameId;
    private PlayerColor color;
    private final ClientConnection connection;

    public Client(final String ip, final int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Client(final Socket socket) throws IOException {
        this(new ClientConnection(socket));
    }

    public Client(final ClientConnection connection) {
        this.connection = connection;
    }

    private static boolean nowMoveByMe(PlayerColor color, GameState state) {
        if (color == PlayerColor.WHITE && state == GameState.WHITE) {
            return true;
        }
        if (color == PlayerColor.BLACK && state == GameState.BLACK) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        log.debug("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                sendRequest(connection, new CreatePlayerRequest("Bot"));
            } catch (InterruptedException | IOException | GameException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            while (connection.isConnected()) {
                GameResponse response = getRequest(connection);
                actionByResponseFromServer(response);
            }
        } catch (IOException | GameException e) {
            connection.close();
            log.error("Error", e);
        }
    }

    private void actionByResponseFromServer(final GameResponse gameResponse) throws GameException, IOException {

        switch (CommandResponse.getCommandByResponse(gameResponse)) {
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
                CreateGameResponse createGame = (CreateGameResponse) gameResponse;
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

    private void actionError(final ErrorResponse response) {
        log.error("actionError {}", response.getMessage());
    }

    private void actionPlaying(final GameBoardResponse response) throws GameException, IOException {
        log.info("actionPlaying {}", response);
        if (response.getState() != GameState.END) {

            if (nowMoveByMe(color, response.getState())) {
                Board board = response.getBoard();
                List<Point> points = BoardService.getAvailableMoves(board, color);
                Point move = points.get(new Random().nextInt(points.size()));
                sendRequest(connection, MovePlayerRequest.toDto(playerId, gameId, move));
            }
        } else {
            gameId = -1;
            color = null;
            sendRequest(connection, new WantPlayRequest(playerId));
        }
    }

    private void actionCreatePlayer(final CreatePlayerResponse response) throws IOException, GameException {
        //log.info("actionCreatePlayer {}", response);
        playerId = response.getId();
        sendRequest(connection, new WantPlayRequest(playerId));
    }

    private static void sendRequest(final ClientConnection server, final GameRequest request) throws IOException, GameException {
        if (server.isConnected()) {
            server.send(CommandRequest.toJsonParser(request));
        }
    }

    private static GameResponse getRequest(final ClientConnection server) throws GameException, IOException {
        if (!server.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }
        String msg = server.getIn().readUTF();
        return CommandResponse.getResponseFromJson(msg);
    }

    private void actionStartGame(final CreateGameResponse response) {
        log.info("actionStartGame {}", response);
        gameId = response.getGameId();
        color = response.getColor();
    }
}
