package controllers.mapper;

import dto.response.game.GameBoardResponse;
import dto.response.game.GameResultResponse;
import dto.response.game.ReplayResponse;
import dto.response.player.CreateGameResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.MessageResponse;
import dto.response.player.PlayerResponse;
import dto.response.room.ListRoomResponse;
import dto.response.room.RoomResponse;
import exception.ServerException;
import logic.BoardFactory;
import models.game.Game;
import models.game.GameResult;
import models.game.Room;
import models.player.User;

import java.util.List;
import java.util.stream.Collectors;

public class Mapper {
    private Mapper() {
    }

    public static GameBoardResponse toDtoGame(final Game game, final User to) {
        if (to.equals(game.getWhiteUser())) {
            return new GameBoardResponse(game.getId(),
                    game.getState(),
                    game.getBoard(),
                    toDtoPlayer(game.getBlackUser()));
        }

        return new GameBoardResponse(game.getId(),
                game.getState(),
                game.getBoard(),
                toDtoPlayer(game.getWhiteUser()));
    }

    public static CreateGameResponse toDtoCreateGame(final Game game, final User user) {
        return new CreateGameResponse(game.getId(), user.getColor());
    }

    public static PlayerResponse toDtoPlayer(final User user) {
        if (user == null) {
            return new PlayerResponse(null);
        }
        return new PlayerResponse(user.getNickname());
    }

    public static CreatePlayerResponse toDtoCreatePlayer(final User user) {
        return new CreatePlayerResponse(user.getId(), user.getNickname());
    }

    public static MessageResponse toDtoMessage(final String message) {
        return new MessageResponse(message);
    }

    public static GameResultResponse toDtoGameResult(final GameResult result) {
        final PlayerResponse winner = toDtoPlayer(result.getWinner());
        return new GameResultResponse(result.getResultState(), winner);
    }

    public static ReplayResponse toDtoReplay(final Game game) throws ServerException {
        final PlayerResponse white = toDtoPlayer(game.getWhiteUser());
        final PlayerResponse black = toDtoPlayer(game.getBlackUser());
        final GameResultResponse result = toDtoGameResult(game.getResult());
        return new ReplayResponse(BoardFactory.generateStartedBoard(), game.getMoves(), result, white, black);
    }

    public static ListRoomResponse toDtoListRoom(final List<Room> list) {
        return new ListRoomResponse(list.stream()
                .map(Mapper::toDtoRoom)
                .collect(Collectors.toList()));
    }

    public static RoomResponse toDtoRoom(final Room room) {
        final PlayerResponse blackPlayer = toDtoPlayer(room.getBlackUser());
        final PlayerResponse whitePlayer = toDtoPlayer(room.getWhiteUser());
        return new RoomResponse(room.getId(), blackPlayer, whitePlayer);
    }
}
