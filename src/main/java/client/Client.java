package client;

import com.google.gson.Gson;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.response.ErrorResponse;
import dto.response.GameBoardResponse;
import dto.response.GameResponse;
import dto.response.player.CreatePlayerResponse;
import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;

import java.io.IOException;
import java.net.Socket;

@Slf4j
@Data

public class Client implements Runnable {
    private static Gson gson = new Gson();
    private long idPlayer;
    private final ClientConnection connection;

    public Client(final String ip, final int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Client(Socket socket) throws IOException {
        this(new ClientConnection(socket));
    }

    public Client(ClientConnection connection) {
        this.connection = connection;
    }


    @Override
    public void run() {
        try {
//            getRequest(connection, GameResponse.class);

            sendJson(connection, new CreatePlayerRequest("Player"));

            CreatePlayerResponse response = getRequest(connection, CreatePlayerResponse.class);
            idPlayer = response.getId();
            while (connection.isConnected()) {
                Object o = getRequest(connection);
                actionByResponseFromServer(o.getClass(), o);
            }
        } catch (IOException | GameException e) {
            log.error("Error", e);
        }
    }

    private <T> void actionByResponseFromServer(Class<?> obj, Object t) {
        log.debug("Response {}", t);
        if (ErrorResponse.class.equals(obj)) {
            ErrorResponse response = (ErrorResponse) t;
        } else if (GameBoardResponse.class.equals(obj)) {
            GameBoardResponse response = (GameBoardResponse) t;
        } else {
            GameResponse gameResponse = (GameResponse) t;
        }
    }

    private static void sendJson(ClientConnection server, GameRequest request) throws IOException {

        if (server.isConnected()) {
            server.send(gson.toJson(request));
        }
    }

    private static GameResponse getRequest(ClientConnection server) throws GameException, IOException {
        return getRequest(server, GameResponse.class);
    }

    private static <T> T getRequest(ClientConnection server, Class<T> obj) throws GameException, IOException {

        if (!server.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }
        log.debug("Try getRequest");
        return gson.fromJson(server.getIn().readLine(), obj);
    }
}
