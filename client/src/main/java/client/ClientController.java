package client;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.GameRequest;
import org.example.dto.response.GameResponse;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.ClientConnection;
import org.example.utils.JsonService;

import java.io.IOException;

@Slf4j
public class ClientController {

    private ClientController() {
    }

    public static void sendRequest(final ClientConnection server, final GameRequest request) throws ServerException {
        server.send(JsonService.toMsgParser(request));
        log.debug("sendRequest {} {}", server.getSocket().getLocalPort(), request);
    }

    public static void safeSendRequest(final ClientConnection server, final GameRequest request) {
        if (server == null) {
            return;
        }
        try {
            sendRequest(server, request);
        } catch (final ServerException e) {
            log.warn("cant send request {}", e.getMessage());
        }
    }

    public static GameResponse getRequest(final ClientConnection server) throws ServerException, IOException {
        if (!server.isConnected()) {
            throw new ServerException(GameErrorCode.CONNECTION_LOST);
        }

        final String msg = server.readMsg();
        log.debug("client.bots.Client getRequest {} {}", server.getSocket().getLocalPort(), msg);
        return JsonService.getResponseFromMsg(msg);
    }
}
