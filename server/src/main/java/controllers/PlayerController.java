package controllers;

import controllers.mapper.Mapper;
import dto.request.player.AuthPlayerRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.LogoutPlayerRequest;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.player.User;
import services.PlayerService;
import services.RoomService;
import services.SenderService;

@Slf4j
public class PlayerController {
    private PlayerController() {
    }

    public static void actionCreatePlayer(final CreatePlayerRequest createPlayer, final ClientConnection connection) throws ServerException {
        final User user = PlayerService.createPlayer(createPlayer, connection);
        SenderService.sendResponse(connection, Mapper.toDtoCreatePlayer(user));
        SenderService.sendResponse(connection, Mapper.toDto(RoomService.getAvailableRooms()));
        log.debug("action createPlayer {} {}", connection.getSocket().getPort(), createPlayer);
    }

    public static void actionAuthPlayer(final AuthPlayerRequest authPlayer, final ClientConnection connection) throws ServerException {
        final User user = PlayerService.authPlayer(authPlayer, connection);
        SenderService.sendResponse(connection, Mapper.toDtoCreatePlayer(user));
        SenderService.sendResponse(connection, Mapper.toDto(RoomService.getAvailableRooms()));
        log.debug("action authPlayer {} {}", connection.getSocket().getPort(), authPlayer);
    }

    public static void actionLogoutPlayer(final LogoutPlayerRequest logoutPlayer, final ClientConnection connection) throws ServerException {
        PlayerService.logoutPlayer(logoutPlayer, connection);
        SenderService.sendResponse(connection, Mapper.toDtoMessage("Logout player successfully"));
        log.debug("action logoutPlayer {} {}", connection.getSocket().getPort(), logoutPlayer);
    }

}
