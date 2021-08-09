package services;

import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import exception.GameErrorCode;
import exception.ServerException;
import models.ClientConnection;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.RoomState;
import models.game.Game;
import models.game.Room;
import models.player.User;

import java.util.List;
import java.util.Random;

public class RoomService extends DataBaseService {

    public static Room createRoom(final CreateRoomRequest createRoom, final ClientConnection connection) throws ServerException {
        checkRequestAndConnection(createRoom, connection);
        User user = connection.getUser();
        playerIsNotNull(user);
        try {
            user.lock();
            playerIsNotStateNone(user);
            Room room = putRoom();
            PlayerColor color = createRoom.getColor();
            setPlayerInRoom(room, user, color);
            user.setState(PlayerState.WAITING_ROOM);
            return room;
        } finally {
            user.unlock();
        }
    }

    public static Game joinRoom(final JoinRoomRequest joinRoom, final ClientConnection connection) throws ServerException {
        checkRequestAndConnection(joinRoom, connection);
        User user = connection.getUser();
        playerIsNotNull(user);
        Room room = getRoomById(joinRoom.getId());
        roomIsNotNull(room);
        try {
            room.lock();
            roomIsNotClosed(room);
            playerIsNotStateNone(user);
            takeFreeColorInRoom(room, user);
            room.setState(RoomState.CLOSE);
            return GameService.createGameByRoom(room);
        } finally {
            room.unlock();
        }
    }

    public static List<Room> getRooms(final GetRoomsRequest getRooms, final ClientConnection connection) throws ServerException {
        checkRequestAndConnection(getRooms, connection);
        validateRequest(getRooms);
        boolean needClose = getRooms.isNeedClose();
        int limit = getRooms.getLimit();
        return DataBaseService.getRooms(needClose, limit);
    }

    private static void validateRequest(final GetRoomsRequest getRooms) throws ServerException {
        if (getRooms.getLimit() <= 0) {
            throw new ServerException(GameErrorCode.INVALID_REQUEST);
        }
    }


    private static void setPlayerInRoom(final Room room, final User user, final PlayerColor needColor) {
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