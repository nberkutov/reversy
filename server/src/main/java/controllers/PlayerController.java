package controllers;

import controllers.mapper.Mapper;
import dto.request.player.AuthPlayerRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.LogoutPlayerRequest;
import dto.response.GameResponse;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.player.User;
import services.PlayerService;
import services.RoomService;

import java.io.IOException;

@Slf4j
public class PlayerController {

    public static void actionCreatePlayer(final CreatePlayerRequest createPlayer, final ClientConnection connection) throws IOException, ServerException {
        User user = PlayerService.createPlayer(createPlayer, connection);
        log.debug("action createPlayer {} {}", connection.getSocket().getPort(), createPlayer);
        sendResponse(connection, Mapper.toDtoCreate(user));
        sendResponse(connection, Mapper.toDto(RoomService.getRooms(false, 10)));
    }

    public static void actionAuthPlayer(final AuthPlayerRequest authPlayer, final ClientConnection connection) throws IOException, ServerException {
        User user = PlayerService.authPlayer(authPlayer, connection);
        log.debug("action authPlayer {} {}", connection.getSocket().getPort(), authPlayer);
        sendResponse(connection, Mapper.toDtoCreate(user));
    }

    public static void actionLogoutPlayer(final LogoutPlayerRequest logoutPlayer, final ClientConnection connection) throws IOException, ServerException {
        PlayerService.logoutPlayer(logoutPlayer, connection);
        log.debug("action logoutPlayer {} {}", connection.getSocket().getPort(), logoutPlayer);
        sendResponse(connection, Mapper.toDto("Logout player successfully"));
    }

    private static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, ServerException {
        TaskResponse.createAndSend(connection, response);
    }

}
