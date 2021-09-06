package controllers;

import controllers.mapper.Mapper;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.response.GameResponse;
import exception.ServerException;
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

    public static void actionCreateRoom(
            final CreateRoomRequest createRoomRequest, final ClientConnection connection)
            throws IOException, ServerException {
        final Room room = RoomService.createRoom(createRoomRequest, connection);
        log.debug("action createRoom {} {}", connection.getSocket().getPort(), createRoomRequest);
        sendResponse(connection, Mapper.toDto(room));
    }

    public static void actionJoinRoom(final JoinRoomRequest joinRoomRequest, final ClientConnection connection) throws IOException, ServerException {
        final Game game = RoomService.joinRoom(joinRoomRequest, connection);
        log.debug("action joinRoom {} {}", connection.getSocket().getPort(), joinRoomRequest);
        sendInfoAboutGame(game, game.getBlackUser());
        sendInfoAboutGame(game, game.getWhiteUser());
        log.debug("Game created by Room, {}", game);
    }

    public static void actionGetRooms(final GetRoomsRequest getRoomsRequest, final ClientConnection connection) throws ServerException, IOException {
        final List<Room> rooms = RoomService.getRooms(getRoomsRequest, connection);
        log.debug("action getRooms {} {}", connection.getSocket().getPort(), getRoomsRequest);
        sendResponse(connection, Mapper.toDto(rooms));
    }


    private static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, ServerException {
        TaskResponse.createAndSend(connection, response);
    }


}
