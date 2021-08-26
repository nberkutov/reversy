package org.example.controllers.handlers;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.GameController;
import org.example.controllers.PlayerController;
import org.example.controllers.RoomController;
import org.example.controllers.TaskRequest;
import org.example.dto.request.GameRequest;
import org.example.dto.request.player.*;
import org.example.dto.request.room.CloseRoomRequest;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.GetRoomsRequest;
import org.example.dto.request.room.JoinRoomRequest;
import org.example.dto.response.ErrorResponse;
import org.example.exception.ServerException;
import org.example.models.player.UserConnection;
import org.example.services.PlayerService;
import org.example.services.SenderService;
import org.example.utils.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Repository
@Scope("prototype")
public class TasksHandler extends Thread {
    @Autowired
    private LinkedBlockingDeque<TaskRequest> requests;
    @Autowired
    private GameController gc;
    @Autowired
    private PlayerController pc;
    @Autowired
    private RoomController rc;
    @Autowired
    private PlayerService ps;
    @Autowired
    private SenderService ss;


    @Override
    public void run() {
        log.info("TasksHandler started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final TaskRequest task = requests.takeFirst();
                    try {
                        final GameRequest request = task.getRequest();
                        final UserConnection connection = task.getClient();
                        switch (JsonService.getCommandByRequest(request)) {
                            case CREATE_PLAYER:
                                pc.actionCreatePlayer((CreateUserRequest) request, connection);
                                break;
                            case PLAYER_AUTH:
                                pc.actionAuthPlayer((AuthUserRequest) request, connection);
                                break;
                            case PLAYER_LOGOUT:
                                pc.actionLogoutPlayer((LogoutPlayerRequest) request, task.getClient());
                                break;
                            case WANT_PLAY:
                                pc.actionWantPlay((WantPlayRequest) request, connection);
                                break;
                            case GET_INFO_USER:
                                pc.actionGetInfo((GetInfoAboutUserRequest) request, connection);
                                break;
                            case PLAYING_MOVE:
                                gc.actionMovePlayer((MovePlayerRequest) request, connection);
                                break;
                            case GET_REPLAY_GAME:
                                gc.actionGetReplayGame((GetReplayGameRequest) request, connection);
                                break;
                            case CREATE_ROOM:
                                rc.actionCreateRoom((CreateRoomRequest) request, connection);
                                break;
                            case JOIN_ROOM:
                                rc.actionJoinRoom((JoinRoomRequest) request, connection);
                                break;
                            case CLOSE_ROOM_REQUEST:
                                rc.actionCloseRoom((CloseRoomRequest) request, connection);
                                break;
                            case GET_ROOMS:
                                rc.actionGetRooms((GetRoomsRequest) request, connection);
                                break;
                        }
                    } catch (final ServerException e) {
                        log.warn("HandlerTasks {} {}", e.getMessage(), task.getClient());
                        ss.sendResponse(task.getClient(), ErrorResponse.toDto(e));
                    }
                } catch (final IOException | ServerException e) {
                    log.error("Thread error ", e);
                }
            }
        } catch (final InterruptedException e) {
            log.info("TasksHandler closed");
            Thread.currentThread().interrupt();
        }
    }
}
