package controllers.handlers;

import controllers.GameController;
import controllers.PlayerController;
import controllers.RoomController;
import controllers.TaskRequest;
import controllers.mapper.Mapper;
import dto.request.GameRequest;
import dto.request.player.*;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.ErrorResponse;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.PlayerService;
import services.SenderService;
import utils.JsonService;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

@AllArgsConstructor
@Slf4j
public class TasksHandler extends Thread {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final LinkedBlockingDeque<ClientConnection> waiting;

    @Override
    public void run() {
        log.info("TasksHandler started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final TaskRequest task = requests.takeFirst();
                    try {
                        final GameRequest request = task.getRequest();
                        final ClientConnection connection = task.getClient();
                        switch (JsonService.getCommandByRequest(request)) {
                            case CREATE_PLAYER:
                                PlayerController.actionCreatePlayer((CreatePlayerRequest) request, connection);
                                break;
                            case PLAYER_AUTH:
                                PlayerController.actionAuthPlayer((AuthPlayerRequest) request, connection);
                                break;
                            case PLAYER_LOGOUT:
                                PlayerController.actionLogoutPlayer((LogoutPlayerRequest) request, task.getClient());
                                break;
                            case WANT_PLAY:
                                actionWantPlay((WantPlayRequest) request, connection);
                                break;
                            case PLAYING_MOVE:
                                GameController.actionMovePlayer((MovePlayerRequest) request, connection);
                                break;
                            case GET_REPLAY_GAME:
                                GameController.actionGetReplayGame((GetReplayGameRequest) request, connection);
                                break;
                            case CREATE_ROOM:
                                RoomController.actionCreateRoom((CreateRoomRequest) request, connection);
                                break;
                            case JOIN_ROOM:
                                RoomController.actionJoinRoom((JoinRoomRequest) request, connection);
                                break;
                            case GET_ROOMS:
                                RoomController.actionGetRooms((GetRoomsRequest) request, connection);
                                break;
                        }
                    } catch (final ServerException e) {
                        log.warn("HandlerTasks {} {}", e.getMessage(), task.getClient());
                        SenderService.sendResponse(task.getClient(), ErrorResponse.toDto(e));
                    }
                } catch (final IOException | ServerException e) {
                    log.error("Thread error ", e);
                }
            }
        } catch (final InterruptedException e) {
            log.info("TasksHandler closed");
        }
    }

    public void actionWantPlay(final WantPlayRequest wantPlay, final ClientConnection connection) throws InterruptedException, IOException, ServerException {
        PlayerService.canPlayerSearchGame(connection);
        waiting.putLast(connection);
        SenderService.sendResponse(connection, Mapper.toDtoMessage("Search game"));
        log.debug("player put in waiting {}", connection.getUser());
    }
}
