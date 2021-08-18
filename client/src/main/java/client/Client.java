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
import utils.JsonService;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class Client extends Thread {
    private final Player player;
    private final ClientConnection connection;
    private final GameGUI gui;

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
            final Client client = new Client(host, port, player, gameGUI);
            client.start();
        } catch (final IOException | ServerException e) {
            e.printStackTrace();
        }
    }

    public Client(final String ip, final int port, final Player player, final GameGUI gui) throws ServerException {
        try {
            this.player = player;
            final Socket socket = new Socket(ip, port);
            this.connection = new ClientConnection(socket);
            this.gui = gui;
        } catch (final IOException e) {
            throw new ServerException(GameErrorCode.SERVER_NOT_STARTED);
        }
    }

    private static Player getPlayer(final String playerType, final String nickname) {
        switch (playerType) {
            /*
                case "random":
                return new SmartBot(nickname, new RandomStrategy());
            */
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
        log.info("actionMessage {}", response);
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
                log.error("Unknown response {}", gameResponse);
        }
    }

    @Override
    public void run() {
        log.info("Debug connect {}", connection);
        new Thread(() -> {
            try {
                Thread.sleep(10);
                ClientController.sendRequest(connection, new CreatePlayerRequest(player.getNickname()));
            } catch (final InterruptedException | IOException | ServerException e) {
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

    private void actionPlaying(final GameBoardResponse response) throws ServerException, IOException, InterruptedException {
        log.debug("actionPlaying {} {}", connection.getSocket().getLocalPort(), response);
        final GameBoard board = response.getBoard();
        gui.updateGUI(board, response.getState(), response.getOpponent().getNickname());
        if (response.getState() != GameState.END) {
            if (nowMoveByMe(player, response.getState())) {
                Thread.sleep(100);
                final Point move = player.move(board);
                ClientController.sendRequest(connection, MovePlayerRequest.toDto(response.getGameId(), move));
            } else {
                player.triggerMoveOpponent(board);
            }
        } else {
            player.triggerGameEnd(response.getState(), board);
            //player.setColor(PlayerColor.NONE);
            ClientController.sendRequest(connection, new WantPlayRequest(player.getColor()));
        }
    }

    private void actionStartGame(final SearchGameResponse response) {
        log.debug("actionStartGame {}", response);
        player.setColor(response.getColor());
        System.out.println(player.getNickname() + " " + response.getColor());
    }
}
