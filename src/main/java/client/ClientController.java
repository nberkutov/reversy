package client;

import dto.request.GameRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.JsonService;

import java.io.IOException;

@Slf4j
public class ClientController {

    public static void sendRequest(final ClientConnection connection, final GameRequest request) throws IOException, GameException {
        if (connection.isConnected()) {
            log.debug("sendRequest {} {}", connection.getSocket().getLocalPort(), request);
            connection.send(JsonService.toMsgParser(request));
        }
    }

    public static GameResponse getRequest(final ClientConnection connection) throws GameException, IOException {
        if (!connection.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }

        String msg = connection.readMsg();
        log.debug("Client getRequest {} {}", connection.getSocket().getLocalPort(), msg);
        return JsonService.getResponseFromMsg(msg);
    }
}
