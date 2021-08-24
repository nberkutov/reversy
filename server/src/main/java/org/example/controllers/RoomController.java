package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.mapper.Mapper;
import org.example.dto.request.room.CloseRoomRequest;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.GetRoomsRequest;
import org.example.dto.request.room.JoinRoomRequest;
import org.example.dto.response.room.CloseRoomResponse;
import org.example.exception.ServerException;
import org.example.models.game.Game;
import org.example.models.game.Room;
import org.example.models.player.UserConnection;
import org.example.services.RoomService;
import org.example.services.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public class RoomController {
    @Autowired
    private RoomService rs;
    @Autowired
    private SenderService ss;

    public void actionCreateRoom(final CreateRoomRequest createRoom, final UserConnection connection) throws ServerException {
        final Room room = rs.createRoom(createRoom, connection);
        ss.sendResponse(connection, Mapper.toDtoRoom(room));
        ss.broadcastResponseForAuthConnections(Mapper.toDtoListRoom(rs.getAvailableRooms()));
        log.debug("action createRoom {} {}", connection.getSocket(), createRoom);
    }

    public void actionJoinRoom(final JoinRoomRequest createRoom, final UserConnection connection) throws ServerException {
        final Game game = rs.joinRoomAndCreateGame(createRoom, connection);
        ss.sendResponse(game.getBlackUser(), Mapper.toDtoCreateGame(game, game.getBlackUser()));
        ss.sendResponse(game.getWhiteUser(), Mapper.toDtoCreateGame(game, game.getWhiteUser()));
        ss.sendResponse(game.getBlackUser(), Mapper.toDtoGame(game));
        ss.sendResponse(game.getWhiteUser(), Mapper.toDtoGame(game));
        ss.broadcastResponseForAuthConnections(Mapper.toDtoListRoom(rs.getAvailableRooms()));
        log.debug("action joinRoom {} {}", connection.getSocket(), createRoom);
    }

    public void actionCloseRoom(final CloseRoomRequest closeRoomRequest, final UserConnection connection) throws ServerException {
        rs.closeRoom(closeRoomRequest, connection);
        ss.broadcastResponseForAuthConnections(Mapper.toDtoListRoom(rs.getAvailableRooms()));
        ss.sendResponse(connection, new CloseRoomResponse());
        log.debug("action closeRoom {}", connection.getSocket());
    }

    public void actionGetRooms(final GetRoomsRequest getRoomsRequest, final UserConnection connection) throws ServerException {
        final List<Room> rooms = rs.getRooms(getRoomsRequest, connection);
        ss.sendResponse(connection, Mapper.toDtoListRoom(rooms));
        log.debug("action getRooms {} {}", connection.getSocket(), getRoomsRequest);
    }

}
