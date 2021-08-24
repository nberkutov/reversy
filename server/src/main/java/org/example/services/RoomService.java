package org.example.services;

import org.example.dto.request.room.CloseRoomRequest;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.GetRoomsRequest;
import org.example.dto.request.room.JoinRoomRequest;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.base.PlayerColor;
import org.example.models.base.PlayerState;
import org.example.models.base.RoomState;
import org.example.models.game.Game;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public class RoomService extends DataBaseService {

    public Room createRoom(final CreateRoomRequest createRoom, final UserConnection connection) throws ServerException {
        checkRequestAndConnection(createRoom, connection);
        final User user = dbd.getUserById(connection.getUserId());
        userIsNotNull(user);
        userIsNotStateNone(user);
        Room room = new Room();
        final PlayerColor color = createRoom.getColor();
        setPlayerInRoomByColor(room, user, color);
        user.setState(PlayerState.WAITING_ROOM);
        user.setNowRoom(room);
        room = dbd.saveRoom(room);
        return room;

    }

    public Game joinRoomAndCreateGame(final JoinRoomRequest joinRoom, final UserConnection connection) throws ServerException {
        checkRequestAndConnection(joinRoom, connection);
        final User user = dbd.getUserById(connection.getUserId());
        userIsNotNull(user);
        final Room room = dbd.getRoomById(joinRoom.getId());
        roomIsNotNull(room);
        roomIsNotClosed(room);
        userIsNotStateNone(user);
        takeFreeColorInRoom(room, user);
        room.setState(RoomState.CLOSE);

        dbd.saveRoom(room);
        return gs.createGameByRoom(room);
    }

    public void closeRoom(final CloseRoomRequest closeRoomRequest, final UserConnection connection) throws ServerException {
        checkRequestAndConnection(closeRoomRequest, connection);
        final User user = dbd.getUserById(connection.getUserId());
        userIsNotNull(user);
        final Room room = user.getNowRoom();
        roomIsNotNull(room);
        roomIsNotClosed(room);
        ps.setPlayerStateNone(user);
        room.setBlackUser(null);
        room.setWhiteUser(null);
        dbd.removeRoom(room);
    }

    public List<Room> getRooms(final GetRoomsRequest getRooms, final UserConnection connection) throws ServerException {
        checkRequestAndConnection(getRooms, connection);
        validateRequest(getRooms);
        final boolean needClose = getRooms.isNeedClose();
        final int limit = getRooms.getLimit();
        return dbd.getRooms(needClose, 0, limit);
    }

    public List<Room> getAvailableRooms() throws ServerException {
        return dbd.getRooms(false, 0, 100);
    }

    private static void validateRequest(final GetRoomsRequest getRooms) throws ServerException {
        if (getRooms.getLimit() < 0) {
            throw new ServerException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static void setPlayerInRoomByColor(final Room room, final User user, final PlayerColor needColor) {
        switch (needColor) {
            case BLACK:
                room.setBlackUser(user);
                break;
            case WHITE:
                room.setWhiteUser(user);
                break;
            default:
                if (new Random().nextBoolean()) {
                    room.setBlackUser(user);
                } else {
                    room.setWhiteUser(user);
                }
        }
    }

    private static void roomIsNotClosed(final Room room) throws ServerException {
        if (room.getState() == RoomState.CLOSE) {
            throw new ServerException(GameErrorCode.ROOM_IS_CLOSED);
        }
    }

    private static void takeFreeColorInRoom(final Room room, final User user) {
        if (room.getBlackUser() == null) {
            room.setBlackUser(user);
        }
        if (room.getWhiteUser() == null) {
            room.setWhiteUser(user);
        }
    }


}
