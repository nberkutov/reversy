import dto.request.GameRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.GetGameInfoRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.request.room.CreateRoomRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.GameResponse;
import dto.response.player.GameBoardResponse;
import dto.response.player.SearchGameResponse;
import exception.GameException;
import models.ClientConnection;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.RoomState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.game.Game;
import models.game.Room;
import models.player.Player;
import models.player.RandomBotPlayer;
import org.junit.jupiter.api.Test;
import services.DataBaseService;
import services.JsonService;
import services.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerTest {
    private static DataBaseService dataBaseService = new DataBaseService();

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
            Thread.sleep(10);
        }
    }


    private static void wantPlay(final ClientConnection connection) throws IOException, GameException, InterruptedException {
        sendRequest(connection, new WantPlayRequest());
    }

    @Test
    void createGameOnServer() throws IOException, GameException, InterruptedException {
        DataBaseService.clearAll();
        final int PORT = 8085;
        final String IP = "127.0.0.1";
        Server server = new Server(PORT, dataBaseService);
        Thread thread = new Thread(server);
        thread.start();

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
    void playGameOnServer() throws IOException, GameException, InterruptedException {
        DataBaseService.clearAll();
        final int PORT = 8085;
        final String IP = "127.0.0.1";
        Server server = new Server(PORT, dataBaseService);
        Thread thread = new Thread(server);
        thread.start();

        ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        AtomicBoolean gameIsNotFinish = new AtomicBoolean(true);
        LinkedBlockingDeque<GameResponse> responsesBot1 = new LinkedBlockingDeque<>();
        LinkedBlockingDeque<GameResponse> responsesBot2 = new LinkedBlockingDeque<>();

        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot1.putLast(JsonService.getResponseFromMsg(connectionBot1.readMsg()));
                }
            } catch (InterruptedException | GameException | IOException e) {
                fail();
            }
        }).start();
        //handler
        new Thread(() -> {
            try {
                Player player = new RandomBotPlayer(0, "Bot0");
                while (gameIsNotFinish.get()) {
                    GameResponse response = responsesBot1.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connectionBot1, player, gameBoardresponse);
                            } else {
                                gameIsNotFinish.set(false);
                            }
                            break;
                        case GAME_START:
                            SearchGameResponse createGame = (SearchGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException | GameException e) {
                fail();
            }
        }).start();
        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot2.putLast(JsonService.getResponseFromMsg(connectionBot2.readMsg()));
                }
            } catch (InterruptedException | GameException | IOException e) {
                fail();
            }
        }).start();
        //handler
        Thread threadBot2 = new Thread(() -> {
            try {
                Player player = new RandomBotPlayer(0, "Bot1");
                while (gameIsNotFinish.get()) {
                    GameResponse response = responsesBot2.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connectionBot2, player, gameBoardresponse);
                            } else {
                                gameIsNotFinish.set(false);
                            }
                            break;
                        case GAME_START:
                            SearchGameResponse createGame = (SearchGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException | GameException e) {
                fail();
            }
        });
        threadBot2.start();

        wantPlay(connectionBot1);
        wantPlay(connectionBot2);

        threadBot2.join();
        assertEquals(DataBaseService.getAllGames().get(0).getState(), GameState.END);
    }

    @Test
    void createRoomOnServer() throws IOException, GameException, InterruptedException {
        DataBaseService.clearAll();
        final int PORT = 8085;
        final String IP = "127.0.0.1";
        Server server = new Server(PORT, dataBaseService);
        Thread thread = new Thread(server);
        thread.start();

        ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        assertTrue(DataBaseService.getAllGames().isEmpty());
        sendRequest(connectionBot1, new CreateRoomRequest(PlayerColor.WHITE));
        assertEquals(DataBaseService.getAllRooms().size(), 1);
        assertEquals(DataBaseService.getAllRooms().get(0).getState(), RoomState.OPEN);
        Room room = DataBaseService.getAllRooms().get(0);
        assertTrue(DataBaseService.getAllGames().isEmpty());

        sendRequest(connectionBot2, new JoinRoomRequest(room.getId()));
        assertEquals(DataBaseService.getAllRooms().get(0).getState(), RoomState.CLOSE);

        assertEquals(DataBaseService.getAllGames().size(), 1);
    }

    private static void fastSendRequest(final ClientConnection connection, final GameRequest request) throws IOException, GameException, InterruptedException {
        if (connection.isConnected()) {
            connection.send(JsonService.toMsgParser(request));
        }
    }

    @Test
    void play10players1000GamesOnServer() throws IOException, GameException, InterruptedException {
        DataBaseService.clearAll();
        final int PORT = 8085;
        final String IP = "127.0.0.1";
        final int needPlayGames = 1000;
        Server server = new Server(PORT, dataBaseService);
        Thread thread = new Thread(server);
        thread.start();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(createClientForPlay(PORT, IP, i, needPlayGames));
        }
        for (Thread th : threads) {
            th.join();
        }
        for (Game game : DataBaseService.getAllGames()) {
            assertEquals(game.getState(), GameState.END);
        }

    }

    private Thread createClientForPlay(final int PORT, String IP, int i, int needPlayGames) throws GameException, InterruptedException, IOException {
        ClientConnection connection = createConnection(IP, PORT, "Bot" + i);
        AtomicBoolean play = new AtomicBoolean(true);
        LinkedBlockingDeque<GameResponse> responsesBot = new LinkedBlockingDeque<>();

        //get
        new Thread(() -> {
            try {
                while (play.get()) {
                    responsesBot.putLast(JsonService.getResponseFromMsg(connection.readMsg()));
                }
            } catch (InterruptedException | GameException | IOException e) {
                fail();
            }
        }).start();
        //handler
        Thread thread = new Thread(() -> {
            try {
                Player player = new RandomBotPlayer(0, "Bot0");
                while (play.get()) {
                    GameResponse response = responsesBot.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connection, player, gameBoardresponse);
                            } else {
                                if (DataBaseService.getAllGames().size() > needPlayGames) {
                                    play.set(false);
                                } else {
                                    player.setColor(PlayerColor.NONE);
                                    wantPlay(connection);
                                }
                            }
                            break;
                        case GAME_START:
                            SearchGameResponse createGame = (SearchGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException | GameException | IOException e) {
                fail();
            }
        });
        thread.start();

        wantPlay(connection);
        return thread;
    }

    private void actionPlaying(final ClientConnection connection, Player player, GameBoardResponse response) {
        if (player == null || connection == null || response == null) {
            fail();
        }
        PlayerColor color = player.getColor();
        try {
            if (nowMoveByMe(color, response.getState())) {
                GameBoard board = response.getBoard();
                Point move = player.move(board);
                fastSendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            }
        } catch (IOException | GameException | InterruptedException e) {
            fail();
        }

    }

    private boolean nowMoveByMe(PlayerColor color, GameState state) {
        if (color == PlayerColor.WHITE && state == GameState.WHITE_MOVE) {
            return true;
        }
        return color == PlayerColor.BLACK && state == GameState.BLACK_MOVE;
    }

    @Test
    void getInfoGameOnServer() throws IOException, GameException, InterruptedException {
        final int PORT = 8085;
        final String IP = "127.0.0.1";
        Server server = new Server(PORT, dataBaseService);
        Thread thread = new Thread(server);
        thread.start();

        ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        wantPlay(connectionBot1);
        wantPlay(connectionBot2);

        LinkedBlockingDeque<GameResponse> responsesBot3 = new LinkedBlockingDeque<>();
        ClientConnection connectionBot3 = createConnection(IP, PORT, "Bot3");
        Game game = DataBaseService.getAllGames().get(0);

        AtomicBoolean needGetInfo = new AtomicBoolean(true);

        new Thread(() -> {
            try {
                while (needGetInfo.get()) {
                    responsesBot3.putLast(JsonService.getResponseFromMsg(connectionBot2.readMsg()));
                }
            } catch (InterruptedException | GameException | IOException e) {
                fail();
            }
        }).start();

        Thread threadBot3 = new Thread(() -> {
            try {
                while (needGetInfo.get()) {
                    GameResponse response = responsesBot3.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            GameBoard board = gameBoardresponse.getBoard();
                            assertEquals(board, game.getBoard());
                            needGetInfo.set(false);
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException | GameException e) {
                fail();
            }
        });
        threadBot3.start();

        sendRequest(connectionBot3, new GetGameInfoRequest(game.getId()));

        threadBot3.join();
    }
}