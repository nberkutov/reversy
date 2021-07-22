package controllers.handlers;

import controllers.GameController;
import controllers.PlayerController;
import controllers.RoomController;
import dto.request.GameRequest;
import dto.request.TaskRequest;
import dto.request.player.*;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.request.server.CreateGameRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.TaskResponse;
import dto.response.player.MessageResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.JsonService;
import services.PlayerService;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

@AllArgsConstructor
@Slf4j
public class TasksHandler extends Thread {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final LinkedBlockingDeque<ClientConnection> waiting;

    @Override
    public void run() {
        log.info("HandlerTasks started");
        while (true) {
            try {
                TaskRequest task = requests.takeFirst();
                try {
                    GameRequest request = task.getRequest();
                    ClientConnection connection = task.getClient();
                    switch (JsonService.getCommandByRequest(request)) {
                        case CREATE_PLAYER:
                            CreatePlayerRequest createPlayer = (CreatePlayerRequest) request;
                            PlayerController.actionCreatePlayer(createPlayer, connection);
                            break;
                        case PLAYER_AUTH:
                            AuthPlayerRequest authPlayer = (AuthPlayerRequest) request;
                            PlayerController.actionAuthPlayer(authPlayer, connection);
                            break;
                        case PLAYER_LOGOUT:
                            LogoutPlayerRequest logoutPlayer = (LogoutPlayerRequest) request;
                            PlayerController.actionLogoutPlayer(logoutPlayer, task.getClient());
                            break;
                        case WANT_PLAY:
                            WantPlayRequest wantPlay = (WantPlayRequest) request;
                            actionWantPlay(wantPlay, connection);
                            break;
                        case PLAYING_MOVE:
                            MovePlayerRequest movePlayer = (MovePlayerRequest) request;
                            GameController.actionMovePlayer(movePlayer, connection);
                            break;
                        case GET_GAME_INFO:
                            GetGameInfoRequest getGame = (GetGameInfoRequest) request;
                            GameController.actionGetGameInfo(getGame, connection);
                            break;
                        case CREATE_ROOM:
                            CreateRoomRequest createRoom = (CreateRoomRequest) request;
                            RoomController.actionCreateRoom(createRoom, connection);
                            break;
                        case JOIN_ROOM:
                            JoinRoomRequest joinRoom = (JoinRoomRequest) request;
                            RoomController.actionJoinRoom(joinRoom, connection);
                            break;
                        case GET_ROOMS:
                            GetRoomsRequest getRoomsRequest = (GetRoomsRequest) request;
                            RoomController.actionGetRooms(getRoomsRequest, connection);
                            break;
                        case SEARCH_CREATE_GAME:
                            CreateGameRequest createGame = (CreateGameRequest) request;
                            GameController.actionCreateGame(createGame, connection);
                            break;
                    }
                } catch (GameException e) {
                    log.warn("HandlerTasks {} {}", task.getRequest(), task.getClient().getSocket(), e);
                    sendResponse(task.getClient(), ErrorResponse.toDto(e));
                }
            } catch (InterruptedException | IOException | GameException e) {
                log.error("Thread error ", e);
            }
        }
    }

    public void actionWantPlay(final WantPlayRequest wantPlay, final ClientConnection connection) throws InterruptedException, IOException, GameException {
        PlayerService.canPlayerSearchGame(connection);
        waiting.putLast(connection);
        sendResponse(connection, new MessageResponse("Search game"));
        log.debug("player put in waiting {}", connection.getPlayer());
    }

    private void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, GameException {
        TaskResponse.createAndSend(connection, response);
    }
}