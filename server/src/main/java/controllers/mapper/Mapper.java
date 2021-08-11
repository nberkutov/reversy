package controllers.mapper;

import dto.response.game.GameBoardResponse;
import dto.response.game.GameResultResponse;
import dto.response.game.ReplayResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.MessageResponse;
import dto.response.player.PlayerResponse;
import dto.response.player.SearchGameResponse;
import dto.response.room.ListRoomResponse;
import dto.response.room.RoomResponse;
import models.board.Board;
import models.game.Game;
import models.game.GameResult;
import models.game.Room;
import models.player.User;

import java.util.List;
import java.util.stream.Collectors;

public class Mapper {
    public static GameBoardResponse toDtoGame(final Game game, final User to) {
        if (to.equals(game.getWhiteUser())) {
            return new GameBoardResponse(game.getId(),
                    game.getState(),
                    game.getBoard(),
                    toDto(game.getBlackUser()));
        }

        return new GameBoardResponse(game.getId(),
                game.getState(),
                game.getBoard(),
                toDto(game.getWhiteUser()));
    }

    public static SearchGameResponse toDtoSearch(final Game game, final User user) {
        return new SearchGameResponse(game.getId(), user.getColor());
    }

    public static PlayerResponse toDto(User user) {
        if (user == null) {
            return null;
        }
        return new PlayerResponse(user.getNickname());
    }

    public static CreatePlayerResponse toDtoCreate(User user) {
        return new CreatePlayerResponse(user.getId(), user.getNickname());
    }

    public static MessageResponse toDto(final String message) {
        return new MessageResponse(message);
    }

    public static GameResultResponse toDto(final GameResult result) {
        PlayerResponse winner = toDto(result.getWinner());
        return new GameResultResponse(result.getResultState(), winner);
    }

    public static ReplayResponse toDto(final Game game) {
        PlayerResponse white = toDto(game.getWhiteUser());
        PlayerResponse black = toDto(game.getBlackUser());
        GameResultResponse result = toDto(game.getResult());
        return new ReplayResponse(new Board(), game.getMoves(), result, white, black);
    }

    public static ListRoomResponse toDto(List<Room> list) {
        return new ListRoomResponse(list.stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList()));
    }

    public static RoomResponse toDto(final Room room) {
        PlayerResponse blackPlayer = toDto(room.getBlackUser());
        PlayerResponse whitePlayer = toDto(room.getWhiteUser());
        return new RoomResponse(room.getId(), blackPlayer, whitePlayer);
    }
}
