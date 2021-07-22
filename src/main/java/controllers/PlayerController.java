package controllers;

import dto.request.player.AuthPlayerRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.LogoutPlayerRequest;
import dto.response.GameResponse;
import dto.response.TaskResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.MessageResponse;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.player.Player;
import services.PlayerService;

import java.io.IOException;

@Slf4j
public class PlayerController {

    public static void actionCreatePlayer(final CreatePlayerRequest createPlayer, final ClientConnection connection) throws IOException, GameException {
        Player player = PlayerService.createPlayer(createPlayer, connection);
        log.debug("action createPlayer {} {}", connection.getSocket().getPort(), createPlayer);
        sendResponse(connection, CreatePlayerResponse.toDto(player));
    }

    public static void actionAuthPlayer(final AuthPlayerRequest authPlayer, final ClientConnection connection) throws IOException, GameException {
        Player player = PlayerService.authPlayer(authPlayer, connection);
        log.debug("action authPlayer {} {}", connection.getSocket().getPort(), authPlayer);
        sendResponse(connection, CreatePlayerResponse.toDto(player));
    }

    public static void actionLogoutPlayer(final LogoutPlayerRequest logoutPlayer, final ClientConnection connection) throws IOException, GameException {
        PlayerService.logoutPlayer(logoutPlayer, connection);
        log.debug("action logoutPlayer {} {}", connection.getSocket().getPort(), logoutPlayer);
        sendResponse(connection, new MessageResponse("Logout player successfully"));
    }

    private static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, GameException {
        TaskResponse.createAndSend(connection, response);
    }

}
