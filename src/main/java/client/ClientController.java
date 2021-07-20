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

    public static void sendRequest(final ClientConnection server, final GameRequest request) throws IOException, GameException {
        if (server.isConnected()) {
            log.debug("sendRequest {} {}", server.getSocket().getLocalPort(), request);
            server.send(JsonService.toMsgParser(request));
        }
    }

    public static GameResponse getRequest(final ClientConnection server) throws GameException, IOException {
        if (!server.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }

        String msg = server.readMsg();
        log.debug("Client getRequest {} {}", server.getSocket().getLocalPort(), msg);
        return JsonService.getResponseFromMsg(msg);
    }
}
