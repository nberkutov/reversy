package controllers;

import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.GameResponse;
import dto.response.TaskResponse;
import dto.response.room.ListRoomResponse;
import dto.response.room.RoomResponse;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import models.game.Room;
import services.RoomService;

import java.io.IOException;
import java.util.List;

import static controllers.GameController.sendInfoAboutGame;

@Slf4j
public class RoomController {
    public static void actionCreateRoom(final CreateRoomRequest createRoom, final ClientConnection connection) throws IOException, GameException {
        Room room = RoomService.createRoom(createRoom, connection);
        log.debug("action createRoom {} {}", connection.getSocket().getPort(), createRoom);
        sendResponse(connection, RoomResponse.toDto(room));
    }

    public static void actionJoinRoom(final JoinRoomRequest createRoom, final ClientConnection connection) throws IOException, GameException {
        Game game = RoomService.joinRoom(createRoom, connection);
        log.debug("action joinRoom {} {}", connection.getSocket().getPort(), createRoom);
        sendInfoAboutGame(game, game.getBlackUser());
        sendInfoAboutGame(game, game.getWhiteUser());
        log.debug("Game created by Room, {}", game);
    }

    public static void actionGetRooms(final GetRoomsRequest getRoomsRequest, final ClientConnection connection) throws GameException, IOException {
        List<Room> rooms = RoomService.getRooms(getRoomsRequest, connection);
        log.debug("action getRooms {} {}", connection.getSocket().getPort(), getRoomsRequest);
        sendResponse(connection, ListRoomResponse.toDto(rooms));
    }


    private static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, GameException {
        TaskResponse.createAndSend(connection, response);
    }


}
