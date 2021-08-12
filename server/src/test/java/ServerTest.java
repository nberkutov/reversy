import dto.request.GameRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.GetReplayGameRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.request.room.CreateRoomRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.GameResponse;
import dto.response.game.GameBoardResponse;
import dto.response.game.ReplayResponse;
import dto.response.player.SearchGameResponse;
import exception.ServerException;
import models.ClientConnection;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.RoomState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.game.Game;
import models.game.Room;
import models.player.RandomBotPlayer;
import models.player.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.DataBaseService;
import services.Server;
import utils.JsonService;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    private static final Server server = new Server();
    private static int PORT;
    private static final String IP = "127.0.0.1";

    @BeforeAll
    private static void createDateBase() {
        server.start();
        PORT = server.getPort();
    }

    @BeforeEach
    private void clearDateBase() {
        DataBaseService.clearAll();
    }

    private static ClientConnection createConnection(String ip, int port, String name) throws IOException, ServerException, InterruptedException {
        final Socket client = new Socket(ip, port);
        final ClientConnection connection = new ClientConnection(client);
        final CreatePlayerRequest request = new CreatePlayerRequest(name);
        sendRequest(connection, request);
        return connection;
    }

    private static void sendRequest(final ClientConnection connection, final GameRequest request) throws IOException, ServerException, InterruptedException {
        if (connection.isConnected()) {
            connection.send(JsonService.toMsgParser(request));
            Thread.sleep(10);
        }
    }


    private static void wantPlay(final ClientConnection connection) throws IOException, ServerException, InterruptedException {
        sendRequest(connection, new WantPlayRequest());
    }

    private static void fastSendRequest(final ClientConnection connection, final GameRequest request) throws IOException, ServerException {
        if (connection.isConnected()) {
            connection.send(JsonService.toMsgParser(request));
        }
    }

    @Test
    void createGameOnServer() throws IOException, ServerException, InterruptedException {
        final ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");

        final User bot1 = DataBaseService.getAllPlayers().get(0);
        assertEquals(DataBaseService.getAllPlayers().size(), 1);

        final ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        assertEquals(DataBaseService.getAllPlayers().size(), 2);
        final User bot2 = DataBaseService.getAllPlayers().get(1);

        wantPlay(connectionBot1);

        assertEquals(bot1.getState(), PlayerState.SEARCH_GAME);

        assertTrue(DataBaseService.getAllGames().isEmpty());

        wantPlay(connectionBot2);

        assertEquals(DataBaseService.getAllGames().size(), 1);
    }

    @Test
    void playGameOnServer() throws IOException, ServerException, InterruptedException {
        final ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        final ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        final AtomicBoolean gameIsNotFinish = new AtomicBoolean(true);
        final LinkedBlockingDeque<GameResponse> responsesBot1 = new LinkedBlockingDeque<>();
        final LinkedBlockingDeque<GameResponse> responsesBot2 = new LinkedBlockingDeque<>();

        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot1.putLast(JsonService.getResponseFromMsg(connectionBot1.readMsg()));
                }
            } catch (InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        new Thread(() -> {
            try {
                final User player = new RandomBotPlayer(0, "Bot0");
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
            } catch (InterruptedException | ServerException e) {
                fail();
            }
        }).start();
        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot2.putLast(JsonService.getResponseFromMsg(connectionBot2.readMsg()));
                }
            } catch (InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        Thread threadBot2 = new Thread(() -> {
            try {
                final User player = new RandomBotPlayer(0, "Bot1");
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
            } catch (InterruptedException | ServerException e) {
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
    void createRoomOnServer() throws IOException, ServerException, InterruptedException {
        final ClientConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        final ClientConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        assertTrue(DataBaseService.getAllGames().isEmpty());
        sendRequest(connectionBot1, new CreateRoomRequest(PlayerColor.WHITE));
        assertEquals(DataBaseService.getAllRooms().size(), 1);
        assertEquals(DataBaseService.getAllRooms().get(0).getState(), RoomState.OPEN);
        final Room room = DataBaseService.getAllRooms().get(0);
        assertTrue(DataBaseService.getAllGames().isEmpty());

        sendRequest(connectionBot2, new JoinRoomRequest(room.getId()));
        assertEquals(DataBaseService.getAllRooms().get(0).getState(), RoomState.CLOSE);

        assertEquals(DataBaseService.getAllGames().size(), 1);
    }

    @Test
    void play10players1000GamesOnServer() throws IOException, ServerException, InterruptedException {
        final int needPlayGames = 1000;

        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(createClientForPlay(PORT, IP, i, needPlayGames));
        }
        for (final Thread th : threads) {
            th.join();
        }
        for (final Game game : DataBaseService.getAllGames()) {
            assertEquals(game.getState(), GameState.END);
        }
    }

    private Thread createClientForPlay(final int PORT, String IP, int i, int needPlayGames) throws ServerException, InterruptedException, IOException {
        final ClientConnection connection = createConnection(IP, PORT, "Bot" + i);
        final AtomicBoolean play = new AtomicBoolean(true);
        final LinkedBlockingDeque<GameResponse> responsesBot = new LinkedBlockingDeque<>();

        //get
        new Thread(() -> {
            try {
                while (play.get()) {
                    responsesBot.putLast(JsonService.getResponseFromMsg(connection.readMsg()));
                }
            } catch (InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        Thread thread = new Thread(() -> {
            try {
                final User player = new RandomBotPlayer(0, "Bot0");
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
            } catch (InterruptedException | ServerException | IOException e) {
                fail();
            }
        });
        thread.start();

        wantPlay(connection);
        return thread;
    }

    private void actionPlaying(final ClientConnection connection, User user, GameBoardResponse response) {
        if (user == null || connection == null || response == null) {
            fail();
        }
        final PlayerColor color = user.getColor();
        try {
            if (nowMoveByMe(color, response.getState())) {
                final GameBoard board = response.getBoard();
                final Point move = user.move(board);
                fastSendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            }
        } catch (IOException | ServerException e) {
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
    void getReplayGameOnServer() throws IOException, ServerException, InterruptedException {
        final int needPlayGames = 1;

        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            threads.add(createClientForPlay(PORT, IP, i, needPlayGames));
        }
        for (final Thread th : threads) {
            th.join();
        }
        for (final Game game : DataBaseService.getAllGames()) {
            assertEquals(game.getState(), GameState.END);
        }

        final LinkedBlockingDeque<GameResponse> responsesBot3 = new LinkedBlockingDeque<>();
        final ClientConnection connectionBot3 = createConnection(IP, PORT, "BotReplay");
        final Game game = DataBaseService.getAllGames().get(0);

        final AtomicBoolean needGetInfo = new AtomicBoolean(true);

        new Thread(() -> {
            try {
                while (needGetInfo.get()) {
                    responsesBot3.putLast(JsonService.getResponseFromMsg(connectionBot3.readMsg()));
                }
            } catch (InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();

        final Thread threadBot3 = new Thread(() -> {
            try {
                while (needGetInfo.get()) {
                    GameResponse response = responsesBot3.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_REPLAY:
                            ReplayResponse replay = (ReplayResponse) response;
                            assertFalse(replay.getMoves().isEmpty());
                            needGetInfo.set(false);
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException | ServerException e) {
                fail();
            }
        });
        threadBot3.start();

        sendRequest(connectionBot3, new GetReplayGameRequest(game.getId()));

        threadBot3.join();
    }
}