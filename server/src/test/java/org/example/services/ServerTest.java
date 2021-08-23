package org.example.services;

import org.example.SpringServer;
import org.example.commands.CommandResponse;
import org.example.dto.request.GameRequest;
import org.example.dto.request.player.CreateUserRequest;
import org.example.dto.request.player.GetReplayGameRequest;
import org.example.dto.request.player.MovePlayerRequest;
import org.example.dto.request.player.WantPlayRequest;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.JoinRoomRequest;
import org.example.dto.response.GameResponse;
import org.example.dto.response.game.CreateGameResponse;
import org.example.dto.response.game.GameBoardResponse;
import org.example.dto.response.game.MoveResponse;
import org.example.dto.response.game.ReplayResponse;
import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.CacheDataBaseDao;
import org.example.models.DataBaseDao;
import org.example.models.GameProperties;
import org.example.models.Player;
import org.example.models.base.*;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.game.Game;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.example.utils.JsonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {SpringServer.class})
class ServerTest {
    @Autowired
    private Server server;

    @Autowired
    protected DataBaseDao dataBaseDao;
    @Autowired
    protected CacheDataBaseDao cacheDataBaseDao;

    private static final int PORT = GameProperties.PORT;
    private static final String IP = "127.0.0.1";

    @BeforeEach
    private void clearDateBase() {
        dataBaseDao.clearAll();
        cacheDataBaseDao.clearAll();
    }

    private static UserConnection createConnection(final String ip, final int port, final String name) throws IOException, ServerException, InterruptedException {
        final Socket client = new Socket(ip, port);
        final UserConnection connection = new UserConnection(client);
        final CreateUserRequest request = new CreateUserRequest(name);
        sendRequest(connection, request);
        getResponseByFilter(connection, CommandResponse.CREATE_PLAYER);
        return connection;
    }

    private static void sendRequest(final UserConnection connection, final GameRequest request) throws IOException, ServerException, InterruptedException {
        if (connection.isConnected()) {
            connection.send(JsonService.toMsgParser(request));
            Thread.sleep(10);
        }
    }


    private static void wantPlay(final UserConnection connection) throws IOException, ServerException, InterruptedException {
        Thread.sleep(10);
        sendRequest(connection, new WantPlayRequest());
    }

    private static void fastSendRequest(final UserConnection connection, final GameRequest request) throws IOException, ServerException {
        if (connection.isConnected()) {
            connection.send(JsonService.toMsgParser(request));
        }
    }

    private static GameResponse getResponseByFilter(final UserConnection connection, final CommandResponse filter) throws IOException, ServerException {
        while (true) {
            final GameResponse response = JsonService.getResponseFromMsg(connection.readMsg());
            final CommandResponse command = JsonService.getCommandByResponse(response);
            if (command == CommandResponse.ERROR) {
                fail();
            }
            if (command == filter) {
                return response;
            }
        }
    }


    @Test
    @Transactional
    void testAutoLogOut() throws InterruptedException, ExecutionException {
        final ExecutorService executor = Executors.newFixedThreadPool(50);

        class ConNickname {
            final String nick;
            final UserConnection con;
            final long gameId;

            public ConNickname(final String nick, final UserConnection con, final long gameId) {
                this.nick = nick;
                this.con = con;
                this.gameId = gameId;
            }
        }

        // Список ассоциированных с Callable задач Future
        final List<Future<ConNickname>> futures = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            final Future<ConNickname> future;
            final int finalI = i;
            future = executor.submit(() -> {
                final String nickname = "bota" + finalI;
                final UserConnection cc = createConnection(IP, PORT, nickname);
                wantPlay(cc);
                final CreateGameResponse cgr = (CreateGameResponse) getResponseByFilter(cc, CommandResponse.GAME_START);
                return new ConNickname(nickname, cc, cgr.getGameId());
            });
            futures.add(future);
        }
        final List<ConNickname> list = new ArrayList<>();
        for (final Future<ConNickname> future : futures) {
            list.add(future.get());
        }
        Collections.shuffle(list);
        final ConNickname tmp = list.get(0);
        tmp.con.close();
        Thread.sleep(300);
        final User user = dataBaseDao.getUserByNickname(tmp.nick);
        final Game gameClosed = dataBaseDao.getGameById(tmp.gameId);
        assertEquals(user, gameClosed.getResult().getLoser());
        assertNull(user.getNowPlaying());
        assertEquals(50, dataBaseDao.getAllPlayers().size());
        assertEquals(49, cacheDataBaseDao.getAllConnections().size());
    }

    @Test
    void createMultiGames() throws IOException, ServerException, InterruptedException {

        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            final int finalI = i;
            final Thread tmp = new Thread(() -> {
                try {
                    final UserConnection cl = createConnection(IP, PORT, "BotA" + finalI);
                    wantPlay(cl);
                    getResponseByFilter(cl, CommandResponse.GAME_PLAYING);
                } catch (final IOException | ServerException | InterruptedException e) {
                    fail();
                }
            });
            tmp.start();
            threads.add(tmp);
        }
        for (final Thread th : threads) {
            th.join();
        }
        assertEquals(50, dataBaseDao.getAllPlayers().size());
        assertEquals(25, dataBaseDao.getAllGames().size());
    }

    @Test
    void createGameOnServer() throws IOException, ServerException, InterruptedException {
        final UserConnection connectionBot1 = createConnection(IP, PORT, "Bot1");

        final User bot1 = dataBaseDao.getAllPlayers().get(0);
        assertEquals(1, dataBaseDao.getAllPlayers().size());

        final UserConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        assertEquals(2, dataBaseDao.getAllPlayers().size());

        wantPlay(connectionBot1);
        Thread.sleep(500);

        assertEquals(PlayerState.SEARCH_GAME, dataBaseDao.getUserByNickname("bot1").getState());

        assertTrue(dataBaseDao.getAllGames().isEmpty());

        wantPlay(connectionBot2);
        final GameBoardResponse response = (GameBoardResponse) getResponseByFilter(connectionBot1, CommandResponse.GAME_PLAYING);
        final GameBoardResponse response2 = (GameBoardResponse) getResponseByFilter(connectionBot2, CommandResponse.GAME_PLAYING);
        final List<Game> games = dataBaseDao.getAllGames();
        assertEquals(1, games.size());
        assertEquals(games.get(0).getId(), response.getGameId());
        assertEquals(games.get(0).getId(), response2.getGameId());
    }

    @Test
    void testGetReplayGame() throws ServerException, IOException, InterruptedException {
        final UserConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        final UserConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        final AtomicBoolean gameIsNotFinish = new AtomicBoolean(true);
        final LinkedBlockingDeque<GameResponse> responsesBot1 = new LinkedBlockingDeque<>();
        final LinkedBlockingDeque<GameResponse> responsesBot2 = new LinkedBlockingDeque<>();
        final List<Move> moves = Collections.synchronizedList(new ArrayList<>());
        final PlayerColor[] colorMove = {PlayerColor.BLACK};

        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot1.putLast(JsonService.getResponseFromMsg(connectionBot1.readMsg()));
                }
            } catch (final InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        new Thread(() -> {
            try {
                final Player player = new Player("Bot0") {
                    @Override
                    public Point move(final GameBoard board) throws ServerException {
                        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
                        final Point p = points.get(new Random().nextInt(points.size()));
                        moves.add(new Move(colorMove[0], p));
                        colorMove[0] = colorMove[0].getOpponent();
                        return p;
                    }
                };
                while (gameIsNotFinish.get()) {
                    final GameResponse response = responsesBot1.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            final GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connectionBot1, player, gameBoardresponse);
                            } else {
                                gameIsNotFinish.set(false);
                            }
                            break;
                        case GAME_START:
                            final CreateGameResponse createGame = (CreateGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (final InterruptedException | ServerException e) {
                fail();
            }
        }).start();
        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot2.putLast(JsonService.getResponseFromMsg(connectionBot2.readMsg()));
                }
            } catch (final InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        final Thread threadBot2 = new Thread(() -> {
            try {
                final Player player = new Player("Bot1") {
                    @Override
                    public Point move(final GameBoard board) throws ServerException {
                        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
                        final Point p = points.get(new Random().nextInt(points.size()));
                        moves.add(new Move(colorMove[0], p));
                        colorMove[0] = colorMove[0].getOpponent();
                        return p;
                    }
                };
                while (gameIsNotFinish.get()) {
                    final GameResponse response = responsesBot2.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            final GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connectionBot2, player, gameBoardresponse);
                            } else {
                                gameIsNotFinish.set(false);
                            }
                            break;
                        case GAME_START:
                            final CreateGameResponse createGame = (CreateGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (final InterruptedException | ServerException e) {
                fail();
            }
        });
        threadBot2.start();

        wantPlay(connectionBot1);
        wantPlay(connectionBot2);

        threadBot2.join();
        assertEquals(GameState.END, dataBaseDao.getAllGames().get(0).getState());
        final Game game = dataBaseDao.getAllGames().get(0);
        sendRequest(connectionBot1, new GetReplayGameRequest(game.getId()));
        final ReplayResponse response = (ReplayResponse) getResponseByFilter(connectionBot1, CommandResponse.GAME_REPLAY);
        final List<MoveResponse> moveResponses = response.getMoves();
        assertEquals(moves.size(), moveResponses.size());
        for (int i = 0; i < moveResponses.size(); i++) {
            final Move local = moves.get(i);
            final MoveResponse fromServer = moveResponses.get(i);
            assertEquals(local.getPoint().getX(), fromServer.getPoint().getX());
            assertEquals(local.getPoint().getY(), fromServer.getPoint().getY());
            assertEquals(local.getColor(), fromServer.getColor());
        }
    }


    @Test
    void playGameOnServer() throws IOException, ServerException, InterruptedException {
        final UserConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        final UserConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        final AtomicBoolean gameIsNotFinish = new AtomicBoolean(true);
        final LinkedBlockingDeque<GameResponse> responsesBot1 = new LinkedBlockingDeque<>();
        final LinkedBlockingDeque<GameResponse> responsesBot2 = new LinkedBlockingDeque<>();

        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot1.putLast(JsonService.getResponseFromMsg(connectionBot1.readMsg()));
                }
            } catch (final InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        new Thread(() -> {
            try {
                final Player player = new Player("Bot0") {
                    @Override
                    public Point move(final GameBoard board) throws ServerException {
                        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
                        return points.get(new Random().nextInt(points.size()));
                    }
                };
                while (gameIsNotFinish.get()) {
                    final GameResponse response = responsesBot1.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            final GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connectionBot1, player, gameBoardresponse);
                            } else {
                                gameIsNotFinish.set(false);
                            }
                            break;
                        case GAME_START:
                            final CreateGameResponse createGame = (CreateGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (final InterruptedException | ServerException e) {
                fail();
            }
        }).start();
        //get
        new Thread(() -> {
            try {
                while (gameIsNotFinish.get()) {
                    responsesBot2.putLast(JsonService.getResponseFromMsg(connectionBot2.readMsg()));
                }
            } catch (final InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        final Thread threadBot2 = new Thread(() -> {
            try {
                final Player player = new Player("Bot1") {
                    @Override
                    public Point move(final GameBoard board) throws ServerException {
                        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
                        return points.get(new Random().nextInt(points.size()));
                    }
                };
                while (gameIsNotFinish.get()) {
                    final GameResponse response = responsesBot2.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            final GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connectionBot2, player, gameBoardresponse);
                            } else {
                                gameIsNotFinish.set(false);
                            }
                            break;
                        case GAME_START:
                            final CreateGameResponse createGame = (CreateGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (final InterruptedException | ServerException e) {
                fail();
            }
        });
        threadBot2.start();

        wantPlay(connectionBot1);
        wantPlay(connectionBot2);

        threadBot2.join();
        assertEquals(GameState.END, dataBaseDao.getAllGames().get(0).getState());
    }

    @Test
    void createRoomOnServer() throws IOException, ServerException, InterruptedException {
        final UserConnection connectionBot1 = createConnection(IP, PORT, "Bot1");
        final UserConnection connectionBot2 = createConnection(IP, PORT, "Bot2");

        assertTrue(dataBaseDao.getAllGames().isEmpty());
        sendRequest(connectionBot1, new CreateRoomRequest(PlayerColor.WHITE));
        getResponseByFilter(connectionBot1, CommandResponse.ROOM);
        assertEquals(1, dataBaseDao.getAllRooms().size());
        assertEquals(RoomState.OPEN, dataBaseDao.getAllRooms().get(0).getState());
        final Room room = dataBaseDao.getAllRooms().get(0);
        assertTrue(dataBaseDao.getAllGames().isEmpty());

        sendRequest(connectionBot2, new JoinRoomRequest(room.getId()));
        getResponseByFilter(connectionBot2, CommandResponse.GAME_START);
        assertEquals(1, dataBaseDao.getAllGames().size());
        getResponseByFilter(connectionBot2, CommandResponse.GAME_PLAYING);
        assertEquals(RoomState.CLOSE, dataBaseDao.getAllRooms().get(0).getState());
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
        for (final Game game : dataBaseDao.getAllGames()) {
            assertEquals(GameState.END, game.getState());
        }
        assertTrue(needPlayGames <= dataBaseDao.getAllGames().size());
    }

    private Thread createClientForPlay(final int PORT, final String IP, final int i, final int needPlayGames) throws ServerException, InterruptedException, IOException {
        final UserConnection connection = createConnection(IP, PORT, "Bot" + i);
        final AtomicBoolean play = new AtomicBoolean(true);
        final LinkedBlockingDeque<GameResponse> responsesBot = new LinkedBlockingDeque<>();

        //get
        new Thread(() -> {
            try {
                while (play.get()) {
                    responsesBot.putLast(JsonService.getResponseFromMsg(connection.readMsg()));
                }
            } catch (final InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();
        //handler
        final Thread thread = new Thread(() -> {
            try {
                final Player player = new Player("Bot0") {
                    @Override
                    public Point move(final GameBoard board) throws ServerException {
                        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
                        return points.get(new Random().nextInt(points.size()));
                    }
                };
                while (play.get()) {
                    final GameResponse response = responsesBot.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_PLAYING:
                            final GameBoardResponse gameBoardresponse = (GameBoardResponse) response;
                            if (gameBoardresponse.getState() != GameState.END) {
                                actionPlaying(connection, player, gameBoardresponse);
                            } else {
                                if (dataBaseDao.getAllGames().size() > needPlayGames) {
                                    play.set(false);
                                } else {
                                    player.setColor(PlayerColor.NONE);
                                    wantPlay(connection);
                                }
                            }
                            break;
                        case GAME_START:
                            final CreateGameResponse createGame = (CreateGameResponse) response;
                            player.setColor(createGame.getColor());
                            break;
                        default:
                            break;
                    }
                }
            } catch (final InterruptedException | ServerException | IOException e) {
                fail();
            }
        });
        thread.start();
        wantPlay(connection);
        return thread;
    }

    private void actionPlaying(final UserConnection connection, final Player player, final GameBoardResponse response) {
        if (player == null || connection == null || response == null) {
            fail();
        }
        final PlayerColor color = player.getColor();
        try {
            if (nowMoveByMe(color, response.getState())) {
                final GameBoard board = response.getBoard();
                final Point move = player.move(board);
                fastSendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            }
        } catch (final IOException | ServerException e) {
            fail();
        }

    }

    private boolean nowMoveByMe(final PlayerColor color, final GameState state) {
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
        for (final Game game : dataBaseDao.getAllGames()) {
            assertEquals(game.getState(), GameState.END);
        }

        final LinkedBlockingDeque<GameResponse> responsesBot3 = new LinkedBlockingDeque<>();
        final UserConnection connectionBot3 = createConnection(IP, PORT, "BotReplay");
        final Game game = dataBaseDao.getAllGames().get(0);

        final AtomicBoolean needGetInfo = new AtomicBoolean(true);

        new Thread(() -> {
            try {
                while (needGetInfo.get()) {
                    responsesBot3.putLast(JsonService.getResponseFromMsg(connectionBot3.readMsg()));
                }
            } catch (final InterruptedException | ServerException | IOException e) {
                fail();
            }
        }).start();

        final Thread threadBot3 = new Thread(() -> {
            try {
                while (needGetInfo.get()) {
                    final GameResponse response = responsesBot3.takeFirst();
                    switch (JsonService.getCommandByResponse(response)) {
                        case ERROR:
                            fail();
                            break;
                        case GAME_REPLAY:
                            final ReplayResponse replay = (ReplayResponse) response;
                            assertFalse(replay.getMoves().isEmpty());
                            needGetInfo.set(false);
                            break;
                        default:
                            break;
                    }
                }
            } catch (final InterruptedException | ServerException e) {
                fail();
            }
        });
        threadBot3.start();

        sendRequest(connectionBot3, new GetReplayGameRequest(game.getId()));

        threadBot3.join();
    }
}