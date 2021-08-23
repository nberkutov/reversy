package org.example.controllers.mapper;

import org.example.dto.response.game.*;
import org.example.dto.response.player.CreatePlayerResponse;
import org.example.dto.response.player.MessageResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.ListRoomResponse;
import org.example.dto.response.room.RoomResponse;
import org.example.exception.ServerException;
import org.example.logic.BoardFactory;
import org.example.models.base.Move;
import org.example.models.board.Point;
import org.example.models.game.Game;
import org.example.models.game.GameResult;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.statistics.Statistics;

import java.util.List;
import java.util.stream.Collectors;

public class Mapper {
    private Mapper() {
    }

    public static GameBoardResponse toDtoGame(final Game game) {
        return new GameBoardResponse(game.getId(),
                game.getState(),
                game.getBoard());
    }

    public static CreateGameResponse toDtoCreateGame(final Game game, final User user) {
        if (user.equals(game.getWhiteUser())) {
            return new CreateGameResponse(game.getId(), user.getColor(), toDtoPlayer(game.getBlackUser()));
        }
        return new CreateGameResponse(game.getId(), user.getColor(), toDtoPlayer(game.getWhiteUser()));
    }

    public static PlayerResponse toDtoPlayer(final User user) {
        if (user == null) {
            return new PlayerResponse(null, 0, 0, 0);
        }
        final Statistics stats = user.getStatistics();
        return new PlayerResponse(user.getNickname(), stats.getTotalGames(), stats.getWin(), stats.getLose());
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
        final List<MoveResponse> moves = game.getMoves()
                .stream()
                .map(Mapper::toDtoMove)
                .collect(Collectors.toList());
        return new ReplayResponse(BoardFactory.generateStartedBoard(), moves, result, white, black);
    }

    public static MoveResponse toDtoMove(final Move move) {
        return new MoveResponse(move.getColor(), toDtoPoint(move.getPoint()));
    }

    public static PointResponse toDtoPoint(final Point point) {
        return new PointResponse(point.getX(), point.getY());
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
