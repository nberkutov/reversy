package services;

import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.RoomState;
import models.game.Game;
import models.game.Room;
import models.player.Player;

import java.util.List;
import java.util.Random;

public class RoomService extends DataBaseService {

    public static Room createRoom(final CreateRoomRequest createRoom, final ClientConnection connection) throws GameException {
        checkRequestAndConnection(createRoom, connection);
        Player player = connection.getPlayer();
        playerIsNotNull(player);
        try {
            player.lock();
            playerIsNotStateNone(player);
            Room room = putRoom();
            PlayerColor color = createRoom.getColor();
            setPlayerInRoom(room, player, color);
            player.setState(PlayerState.WAITING_ROOM);
            return room;
        } finally {
            player.unlock();
        }
    }

    public static Game joinRoom(final JoinRoomRequest joinRoom, final ClientConnection connection) throws GameException {
        checkRequestAndConnection(joinRoom, connection);
        Player player = connection.getPlayer();
        playerIsNotNull(player);
        Room room = getRoomById(joinRoom.getId());
        roomIsNotNull(room);
        try {
            room.lock();
            roomIsNotClosed(room);
            playerIsNotStateNone(player);
            takeFreeColorInRoom(room, player);
            room.setState(RoomState.CLOSE);
            return GameService.createGameByRoom(room);
        } finally {
            room.unlock();
        }
    }

    public static List<Room> getRooms(final GetRoomsRequest getRooms, final ClientConnection connection) throws GameException {
        checkRequestAndConnection(getRooms, connection);
        validateRequest(getRooms);
        boolean needClose = getRooms.isNeedClose();
        int limit = getRooms.getLimit();
        return DataBaseService.getRooms(needClose, limit);
    }

    private static void validateRequest(final GetRoomsRequest getRooms) throws GameException {
        if (getRooms.getLimit() <= 0) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }


    private static void setPlayerInRoom(final Room room, final Player player, final PlayerColor needColor) {
        switch (needColor) {
            case BLACK:
                room.setBlackPlayer(player);
                break;
            case WHITE:
                room.setWhitePlayer(player);
                break;
            default:
                if (new Random().nextBoolean()) {
                    room.setBlackPlayer(player);
                } else {
                    room.setWhitePlayer(player);
                }
        }
    }

    private static void roomIsNotClosed(final Room room) throws GameException {
        if (room.getState() == RoomState.CLOSE) {
            throw new GameException(GameErrorCode.ROOM_IS_CLOSED);
        }
    }

    private static void takeFreeColorInRoom(final Room room, final Player player) {
        if (room.getBlackPlayer() == null) {
            room.setBlackPlayer(player);
        }
        if (room.getWhitePlayer() == null) {
            room.setWhitePlayer(player);
        }
    }
}
