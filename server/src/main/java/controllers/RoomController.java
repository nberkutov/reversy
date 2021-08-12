package controllers;

import controllers.mapper.Mapper;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import models.game.Room;
import services.RoomService;
import services.SenderService;

import java.util.List;

@Slf4j
public class RoomController {

    private RoomController() {
    }

    public static void actionCreateRoom(final CreateRoomRequest createRoom, final ClientConnection connection) throws ServerException {
        final Room room = RoomService.createRoom(createRoom, connection);
        SenderService.sendResponse(connection, Mapper.toDto(room));
        SenderService.broadcastResponseForAuthConnections(Mapper.toDto(RoomService.getAvailableRooms()));
        log.debug("action createRoom {} {}", connection.getSocket().getPort(), createRoom);
    }

    public static void actionJoinRoom(final JoinRoomRequest createRoom, final ClientConnection connection) throws ServerException {
        final Game game = RoomService.joinRoomAndCreateGame(createRoom, connection);
        SenderService.broadcastResponseForAuthConnections(Mapper.toDto(RoomService.getAvailableRooms()));
        log.debug("action joinRoom {} {}", connection.getSocket().getPort(), createRoom);
        log.debug("Game created by Room, {}", game);
    }

    public static void actionGetRooms(final GetRoomsRequest getRoomsRequest, final ClientConnection connection) throws ServerException {
        final List<Room> rooms = RoomService.getRooms(getRoomsRequest, connection);
        SenderService.sendResponse(connection, Mapper.toDto(rooms));
        log.debug("action getRooms {} {}", connection.getSocket().getPort(), getRoomsRequest);
    }

}
