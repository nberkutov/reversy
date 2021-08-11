package client;

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
import gui.GameGUI;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Player;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import utils.JsonService;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class Client extends Thread {
    private final Player player;
    private final ClientConnection connection;
    private final GameGUI gui;

    public Client(final String ip, final int port, final Player player, GameGUI gui) throws ServerException {
        try {
            this.player = player;
            final Socket socket = new Socket(ip, port);
            this.connection = new ClientConnection(socket);
            this.gui = gui;
        } catch (IOException e) {
            throw new ServerException(GameErrorCode.SERVER_NOT_STARTED);
        }
    }

    private static boolean nowMoveByMe(Player player, GameState state) {
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
            } catch (InterruptedException | IOException | ServerException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (connection.isConnected()) {
                try {
                    final GameResponse response = ClientController.getRequest(connection);
                    actionByResponseFromServer(response);
                } catch (ServerException e) {
                    log.error("GameError {} {}", connection.getSocket(), e.getErrorCode());
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error {} {}", connection.getSocket(), e.getMessage());
            connection.close();
        }
    }

    private void actionError(final ErrorResponse response) {
        log.error("actionError {}", response);
    }

    private void actionCreatePlayer(final CreatePlayerResponse response) throws IOException, ServerException {
        log.debug("actionCreatePlayer {}", response);
        ClientController.sendRequest(connection, new WantPlayRequest());
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
            player.setColor(PlayerColor.NONE);
            ClientController.sendRequest(connection, new WantPlayRequest());
        }
    }

    private void actionStartGame(final SearchGameResponse response) {
        log.debug("actionStartGame {}", response);
        player.setColor(response.getColor());
        System.out.println(player.getNickname() + " " + response.getColor());
    }
}
