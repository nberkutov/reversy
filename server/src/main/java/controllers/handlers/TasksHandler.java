package controllers.handlers;

import controllers.*;
import controllers.mapper.Mapper;
import dto.request.GameRequest;
import dto.request.player.*;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.PlayerService;
import utils.JsonService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

@AllArgsConstructor
@Slf4j
public class TasksHandler extends Thread {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final LinkedBlockingDeque<ClientConnection> waiting;

    public static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, ServerException {
        TaskResponse.createAndSend(connection, response);
    }

    public static void broadcastResponse(final List<ClientConnection> connections, final GameResponse response) throws IOException, ServerException {
        for (final ClientConnection connection : connections) {
            sendResponse(connection, response);
        }
    }

    @Override
    public void run() {
        log.info("TasksHandler started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
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
                            case GET_REPLAY_GAME:
                                GetReplayGameRequest getGame = (GetReplayGameRequest) request;
                                GameController.actionGetReplayGame(getGame, connection);
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
                        }
                    } catch (ServerException e) {
                        log.warn("HandlerTasks {} {}", e.getMessage(), task.getClient());
                        sendResponse(task.getClient(), ErrorResponse.toDto(e));
                    }
                } catch (IOException | ServerException e) {
                    log.error("Thread error ", e);
                }
            }
        } catch (InterruptedException e) {
            log.info("TasksHandler closed");
        }
    }

    public void actionWantPlay(final WantPlayRequest wantPlay, final ClientConnection connection) throws InterruptedException, IOException, ServerException {
        PlayerService.canPlayerSearchGame(connection);
        waiting.putLast(connection);
        sendResponse(connection, Mapper.toDto("Search game"));
        log.debug("player put in waiting {}", connection.getUser());
    }
}
