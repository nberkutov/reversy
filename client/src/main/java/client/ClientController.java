package client;

import dto.request.GameRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import utils.JsonService;

import java.io.IOException;

@Slf4j
public class ClientController {

    private ClientController() {
    }

    public static void sendRequest(final ClientConnection server, final GameRequest request) throws ServerException {
        server.send(JsonService.toMsgParser(request));
        log.debug("sendRequest {} {}", server.getSocket().getLocalPort(), request);
    }

    public static GameResponse getRequest(final ClientConnection server) throws ServerException, IOException {
        if (!server.isConnected()) {
            throw new ServerException(GameErrorCode.CONNECTION_LOST);
        }

        final String msg = server.readMsg();
        log.debug("client.Client getRequest {} {}", server.getSocket().getLocalPort(), msg);
        return JsonService.getResponseFromMsg(msg);
    }
}
