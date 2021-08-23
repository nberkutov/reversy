package client;

import gui.GUI;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.GameResponse;
import org.example.dto.response.game.CreateGameResponse;
import org.example.dto.response.game.GameBoardResponse;
import org.example.dto.response.game.ReplayResponse;
import org.example.dto.response.player.CreatePlayerResponse;
import org.example.dto.response.player.LogoutResponse;
import org.example.dto.response.player.MessageResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.CloseRoomResponse;
import org.example.dto.response.room.ListRoomResponse;
import org.example.dto.response.room.RoomResponse;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.ClientConnection;
import org.example.models.base.GameState;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.utils.JsonService;
import replay.ReplaySimulator;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class GUIClient extends Thread {
    private final ClientConnection connection;
    private final GUI gui;
    private PlayerColor gameColor;
    private long gameId;

    public GUIClient(final String ip, final int port, final GUI gui) throws ServerException {
        try {
            final Socket socket = new Socket(ip, port);
            this.connection = new ClientConnection(socket);
            this.gui = gui;
        } catch (final IOException e) {
            throw new ServerException(GameErrorCode.SERVER_NOT_STARTED);
        }
    }

    private static boolean nowMoveByMe(final PlayerColor color, final GameState state) {
        if (color == PlayerColor.WHITE && state == GameState.WHITE_MOVE) {
            return true;
        }
        return color == PlayerColor.BLACK && state == GameState.BLACK_MOVE;
    }

    private void actionMessage(final MessageResponse response) {
        gui.createMessage(response.getMessage());
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
                actionStartGame((CreateGameResponse) gameResponse);
                break;
            case MESSAGE:
                actionMessage((MessageResponse) gameResponse);
                break;
            case GAME_REPLAY:
                actionReplay((ReplayResponse) gameResponse);
                break;
            case ROOM:
                actionRoom((RoomResponse) gameResponse);
                break;
            case CLOSE_ROOM_RESPONSE:
                actionCloseRoom((CloseRoomResponse) gameResponse);
                break;
            case LOGOUT:
                actionLogout((LogoutResponse) gameResponse);
                break;
            case GET_INFO_USER_RESPONSE:
                actionGetInfoPlayer((PlayerResponse) gameResponse);
                break;
            case ROOMS:
                actionRooms((ListRoomResponse) gameResponse);
                break;
            default:
                log.error("Unknown response {}", gameResponse);
        }
    }

    private void actionCloseRoom(final CloseRoomResponse gameResponse) {
        gui.closeRoom();
    }

    private void actionGetInfoPlayer(final PlayerResponse playerResponse) {
        gui.initPlayerInfo(playerResponse);
    }

    private void actionLogout(final LogoutResponse gameResponse) {
        gui.closeMenuAndInitAuth();
    }

    private void actionRoom(final RoomResponse room) {
        gui.initRoom(room);
    }

    private void actionReplay(final ReplayResponse replay) {
        final ReplaySimulator sim = new ReplaySimulator();
        sim.init(replay);
        sim.start();
    }

    private void actionRooms(final ListRoomResponse gameResponse) {
        gui.updateMenu(gameResponse);
        gui.createInfo("Rooms updated");
    }

    @Override
    public void run() {
        gui.init(connection);

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
            gui.createError("[CRITICAL] Lost connection with server.");
            connection.close();
        }
    }

    private void actionError(final ErrorResponse response) {
        gui.createError(response.getMessage() + response.getErrorCode());
    }

    private void actionCreatePlayer(final CreatePlayerResponse response) {
        gui.closeAuthAndInitMenu(response.getId(), response.getNickname());
    }

    private void actionPlaying(final GameBoardResponse response) {
        final GameBoard board = response.getBoard();
        final GameState state = response.getState();
        gui.updateGame(board, state);

        if (state != GameState.END) {
            if (nowMoveByMe(gameColor, response.getState())) {
                gui.updateGameTitle(String.format("Game: %d; Ваш ход", gameId));
            } else {
                gui.updateGameTitle(String.format("Game: %d; Ход противника", gameId));
            }
        } else {
            gui.updateGameTitle(String.format("Game: %d; Игра закончена", gameId));
            gui.closeGame();
        }

    }

    private void actionStartGame(final CreateGameResponse response) {
        gameColor = response.getColor();
        final String nicknameOpponent = response.getOpponent().getNickname();
        gameId = response.getGameId();
        gui.initGame(gameId, nicknameOpponent);

        gui.updateGameTitle(String.format("Game: %d", gameId));
    }
}
