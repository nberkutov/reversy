package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.mapper.Mapper;
import org.example.dto.request.player.GetReplayGameRequest;
import org.example.dto.request.player.MovePlayerRequest;
import org.example.exception.ServerException;
import org.example.models.game.Game;
import org.example.models.player.UserConnection;
import org.example.services.GameService;
import org.example.services.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public class GameController {
    @Autowired
    private GameService gs;
    @Autowired
    private SenderService ss;

    public void actionMovePlayer(final MovePlayerRequest movePlayer, final UserConnection connection) throws ServerException {
        final Game game = gs.makePlayerMove(movePlayer, connection);
        ss.sendResponse(game.getBlackUser(), Mapper.toDtoGame(game));
        ss.sendResponse(game.getWhiteUser(), Mapper.toDtoGame(game));
        log.debug("action movePlayer {}", movePlayer);
    }

    public void actionGetReplayGame(final GetReplayGameRequest getGame, final UserConnection connection) throws ServerException {
        final Game game = gs.getReplayGame(getGame, connection);
        ss.sendResponse(connection, Mapper.toDtoReplay(game));
        log.debug("action GetReplayGame, {}", game);
    }

    public void actionCreateGame(final UserConnection firstConnection, final UserConnection secondConnection) throws ServerException {
        final Game game = gs.createGameBySearch(firstConnection, secondConnection);
        ss.sendResponse(game.getBlackUser(), Mapper.toDtoCreateGame(game, game.getBlackUser()));
        ss.sendResponse(game.getWhiteUser(), Mapper.toDtoCreateGame(game, game.getWhiteUser()));
        ss.sendResponse(game.getBlackUser(), Mapper.toDtoGame(game));
        ss.sendResponse(game.getWhiteUser(), Mapper.toDtoGame(game));
        log.debug("Game created by search, {}", game);
    }

}
