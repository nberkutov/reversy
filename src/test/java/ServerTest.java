import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.request.player.WantPlayRequest;
import dto.response.GameBoardResponse;
import dto.response.GameResponse;
import dto.response.SearchGameResponse;
import exception.GameException;
import models.ClientConnection;
import models.base.PlayerState;
import models.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.DataBaseService;
import services.JsonService;
import services.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerTest {
    private static DataBaseService dataBaseService;

    @BeforeAll
    private static void clearDateBase() {
        dataBaseService = new DataBaseService();
        DataBaseService.clearAll();
    }

    private static ClientConnection createConnection(String ip, int port, String name) throws IOException, GameException, InterruptedException {
        Socket client = new Socket(ip, port);
        ClientConnection connection = new ClientConnection(client);
        CreatePlayerRequest request = new CreatePlayerRequest(name);
        sendRequest(connection, request);
        return connection;
    }

    private static void sendRequest(final ClientConnection connection, final GameRequest request) throws IOException, GameException, InterruptedException {
        if (connection.isConnected()) {
            connection.send(JsonService.toMsgParser(request));
            Thread.sleep(100);
        }
    }

    private static void wantPlay(final ClientConnection connection) throws IOException, GameException, InterruptedException {
        sendRequest(connection, new WantPlayRequest());
    }

    @Test
    void createGameOnServer() throws IOException, GameException, InterruptedException {
        final int PORT = 8085;
        final String IP = "127.0.0.1";
        Server server = new Server(PORT, dataBaseService);
        Thread thread = new Thread(server);
        thread.start();

        assertTrue(DataBaseService.getAllPlayers().isEmpty());

        ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");

        Player bot1 = DataBaseService.getAllPlayers().get(0);
        assertEquals(DataBaseService.getAllPlayers().size(), 1);

        ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        assertEquals(DataBaseService.getAllPlayers().size(), 2);
        Player bot2 = DataBaseService.getAllPlayers().get(1);

        wantPlay(connectionBot1);

        assertEquals(bot1.getState(), PlayerState.SEARCH_GAME);

        assertTrue(DataBaseService.getAllGames().isEmpty());

        wantPlay(connectionBot2);

        assertEquals(DataBaseService.getAllGames().size(), 1);
    }

    @Test
    void createPlayOnServer() throws IOException, GameException, InterruptedException {
        final int PORT = 8085;
        final String IP = "127.0.0.1";
        Server server = new Server(PORT, dataBaseService);
        Thread thread = new Thread(server);
        thread.start();

        ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        boolean gameIsNotFinish = true;
        LinkedBlockingDeque<GameResponse> responsesBot1 = new LinkedBlockingDeque<>();
        LinkedBlockingDeque<GameResponse> responsesBot2 = new LinkedBlockingDeque<>();

        new Thread(() -> {
            try {
                while (gameIsNotFinish) {
                    responsesBot1.putLast(JsonService.getResponseFromMsg(connectionBot1.readMsg()));
                }
            } catch (InterruptedException | GameException | IOException e) {
                fail();
            }
        }).start();

        new Thread(() -> {
            try {
                Player player;
                while (gameIsNotFinish) {
                    GameResponse response = responsesBot1.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case GAME_PLAYING:
                            GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            actionPlaying(gameBoardresponse);
                            break;
                        case GAME_START:
                            SearchGameResponse createGame = (SearchGameResponse) response;
                            player = new Player(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException | GameException e) {
                fail();
            }
        }).start();

        new Thread(() -> {
            try {
                while (gameIsNotFinish) {
                    responsesBot2.putLast(JsonService.getResponseFromMsg(connectionBot2.readMsg()));
                }
            } catch (InterruptedException | GameException | IOException e) {
                fail();
            }
        }).start();

        wantPlay(connectionBot1);
        wantPlay(connectionBot2);
    }

    private void actionPlaying(GameBoardResponse response) {
    }


    private void switchAction(ClientConnection connection, GameResponse response) throws GameException {

    }
}