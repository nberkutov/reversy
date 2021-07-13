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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Point;

import java.io.IOException;
import java.net.Socket;

@Slf4j
@Data

public class Client implements Runnable {
    private static Gson gson = new Gson();
    private long playerId;
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


    @Override
    public void run() {
        log.debug("Debug connect {}", connection);
//        new Thread(new Runnable() {
//            @SneakyThrows
//            @Override
//            public void run() {
//                Thread.sleep(1000);
//                sendRequest(connection, new CreatePlayerRequest("Test"));
//                Thread.sleep(1000);
//                sendRequest(connection, new WantPlayRequest(0));
//                Thread.sleep(1000);
//                sendRequest(connection, new MovePlayerRequest(0, 0, new Point(1, 1)));
//            }
//        }).start();
        try {
            while (connection.isConnected()) {
                GameResponse response = getRequest(connection);
                actionByResponseFromServer(response);
            }
        } catch (IOException | GameException e) {
            log.error("Error", e);
        }
    }

    private void actionByResponseFromServer(final GameResponse gameResponse) throws GameException {
        log.debug("GetResponse {}", gameResponse);
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

    private void actionMessage(final MessageResponse message) {

    }

    private void actionError(final ErrorResponse response) {

    }

    private void actionPlaying(final GameBoardResponse response) {

    }

    private void actionCreatePlayer(final CreatePlayerResponse response) {

    }

    private void actionStartGame(final CreateGameResponse response) {

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
}
