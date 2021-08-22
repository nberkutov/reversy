package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.game.GameBoardResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.MessageResponse;
import dto.response.player.SearchGameResponse;
import exception.GameErrorCode;
import exception.ServerException;
import gui.EmptyGUI;
import gui.GameGUI;
import gui.TextGUI;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Player;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.players.SmartBot;
import models.strategies.RandomStrategy;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import strategy.ArrayBoard;
import utils.JsonService;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class Client extends Thread {
    private final Player player;
    private final ClientConnection connection;
    private final int numberOfGames;
    private final GameGUI gui;

    private int gamesCounter;

    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Config file missed.");
        }
        final File configFile = new File(args[0]);
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            final ClientProperties properties = mapper.readValue(configFile, ClientProperties.class);
            final String host = properties.getHost().orElse("127.0.0.1");
            final int port = properties.getPort().orElse(8080);
            final Player player = getPlayer(properties.getBotType().orElse("random"), properties.getNickname());
            final PlayerColor color = PlayerColor.valueOf(properties.getPlayerColor().orElse("NONE"));
            player.setColor(color);
            final GameGUI gameGUI = getGUI(properties.getGuiType().orElse("empty"));
            final int numberOfGames = properties.getNumberOfGames().orElse(1);
            initLogger(properties);
            final Client client = new Client(host, port, player, gameGUI, numberOfGames);
            client.start();
        } catch (final IOException | ServerException e) {
            e.printStackTrace();
        }
    }

    public Client(final String ip, final int port, final Player player, final GameGUI gui, final int numberOfGames)
            throws ServerException {
        try {
            this.player = player;
            final Socket socket = new Socket(ip, port);
            this.connection = new ClientConnection(socket);
            this.gui = gui;
            this.numberOfGames = numberOfGames;
            gamesCounter = 0;
        } catch (final IOException e) {
            throw new ServerException(GameErrorCode.SERVER_NOT_STARTED);
        }
    }

    private static void initLogger(final ClientProperties properties) throws IOException {
        final FileAppender clientLogsFileAppender = (RollingFileAppender) Logger.getLogger("client")
                .getAllAppenders().nextElement();
        final String logDir = properties.getLogPath().orElse("tmp");

        final String servicesLogFileName = logDir + File.separator + properties.getLogFile().orElse("client.log");
        final File file = new File(servicesLogFileName);
        final File dir = new File(logDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Не удалось создать директорию " + logDir);
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new RuntimeException("Не удалось создать файл");
        }
        clientLogsFileAppender.setFile(servicesLogFileName);
        clientLogsFileAppender.activateOptions();
    }

    private static Player getPlayer(final String playerType, final String nickname) {
        switch (playerType) {
            default:
                return new SmartBot(nickname, new RandomStrategy());
        }
    }

    private static GameGUI getGUI(final String guiType) {
        switch (guiType) {
            case "window":
                return new WindowGUI();
            case "console":
                return new TextGUI();
            default:
                return new EmptyGUI();
        }
    }

    private static boolean nowMoveByMe(final Player player, final GameState state) {
        if (player.getColor() == PlayerColor.WHITE && state == GameState.WHITE_MOVE) {
            return true;
        }
        return player.getColor() == PlayerColor.BLACK && state == GameState.BLACK_MOVE;
    }

    private void actionMessage(final MessageResponse response) {
        //log.info("actionMessage {}", response);
    }

    private void actionByResponseFromServer(final GameResponse gameResponse)
            throws ServerException, IOException, InterruptedException {
        switch (JsonService.getCommandByResponse(gameResponse)) {
            case ERROR:
                actionError((ErrorResponse) gameResponse);
                break;
            case GAME_PLAYING:
                actionPlaying((GameBoardResponse) gameResponse);
                break;
            case CREATE_PLAYER:
                actionCreatePlayer((CreatePlayerResponse) gameResponse);
                break;
            case GAME_START:
                actionStartGame((SearchGameResponse) gameResponse);
                break;
            case MESSAGE:
                actionMessage((MessageResponse) gameResponse);
                break;
            default:
                break;// log.error("Unknown response {}", gameResponse);
        }
    }

    @Override
    public void run() {
        //log.info("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(10);
                ClientController.sendRequest(connection, new CreatePlayerRequest(player.getNickname()));
            } catch (final IOException | ServerException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (connection.isConnected()) {
                try {
                    final GameResponse response = ClientController.getRequest(connection);
                    actionByResponseFromServer(response);
                } catch (final ServerException e) {
                    log.error("GameError {} {}", connection.getSocket(), e.getErrorCode());
                }
            }
        } catch (final IOException | InterruptedException e) {
            log.error("Error {} {}", connection.getSocket(), e.getMessage());
            connection.close();
        }
    }

    private void actionError(final ErrorResponse response) {
        log.error("actionError {}", response);
    }

    private void actionCreatePlayer(final CreatePlayerResponse response) throws IOException, ServerException {
        log.debug("actionCreatePlayer {}", response);
        ClientController.sendRequest(connection, new WantPlayRequest(player.getColor()));
    }

    private void actionPlaying(final GameBoardResponse response) throws ServerException, IOException {
        final ArrayBoard board = new ArrayBoard(response.getBoard());
        log.info("{} {} {}", response.getGameId(), response.getState(), board);
        gui.updateGUI(board, response.getState(), response.getOpponent().getNickname());
        if (response.getState() != GameState.END) {
            if (nowMoveByMe(player, response.getState())) {
                final Point move = player.move(board);
                ClientController.sendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            } else {
                player.triggerMoveOpponent(board);
            }
        } else {
            player.triggerGameEnd(response.getState(), board);
            player.setColor(revertColor(player.getColor()));
            if (gamesCounter++ < numberOfGames - 1) {
                ClientController.sendRequest(connection, new WantPlayRequest(player.getColor()));
            }
        }
    }

    private void actionStartGame(final SearchGameResponse response) {
        player.setColor(response.getColor());
        log.info("color={}", response.getColor());
        System.out.println("Game " + gamesCounter + " color=" + response.getColor());
        //System.out.println(player.getNickname() + " " + response.getColor());
    }

    private PlayerColor revertColor(final PlayerColor playerColor) {
        switch (playerColor) {
            case WHITE:
                return PlayerColor.BLACK;
            case BLACK:
                return PlayerColor.WHITE;
            default:
                return PlayerColor.NONE;
        }
    }
}
