package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.mapper.Mapper;
import org.example.dto.request.player.*;
import org.example.dto.response.player.LogoutResponse;
import org.example.exception.ServerException;
import org.example.models.DataBaseDao;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.example.services.PlayerService;
import org.example.services.RoomService;
import org.example.services.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
@Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
public class PlayerController {
    @Autowired
    private PlayerService ps;
    @Autowired
    private DataBaseDao dbd;
    @Autowired
    private SenderService ss;
    @Autowired
    private RoomService rs;
    @Autowired
    private LinkedBlockingDeque<UserConnection> waiting;

    public void actionCreatePlayer(final CreateUserRequest createPlayer, final UserConnection connection) throws ServerException {
        final User user = ps.createUser(createPlayer, connection);
        ss.sendResponse(connection, Mapper.toDtoCreatePlayer(user));
        ss.sendResponse(connection, Mapper.toDtoListRoom(rs.getAvailableRooms()));
        log.debug("action createPlayer {} {}", connection.getSocket().getPort(), createPlayer);
    }

    public void actionWantPlay(final WantPlayRequest wantPlay, final UserConnection connection) throws InterruptedException, IOException, ServerException {
        ps.canPlayerSearchGame(connection);
        waiting.putLast(connection);
        ss.sendResponse(connection, Mapper.toDtoMessage("Search game"));
        log.debug("player put in waiting {}", connection.getUserId());
    }

    public void actionGetInfo(final GetInfoAboutUserRequest getInfo, final UserConnection connection) throws ServerException {
        final User user = ps.getInfoAboutUser(getInfo, connection);
        ss.sendResponse(connection, Mapper.toDtoPlayer(user));
        log.debug("getInfoUser {}", connection.getUserId());
    }

    public void actionAuthPlayer(final AuthUserRequest authPlayer, final UserConnection connection) throws ServerException {
        final User user = ps.authPlayer(authPlayer, connection);
        ss.sendResponse(connection, Mapper.toDtoCreatePlayer(user));
        ss.sendResponse(connection, Mapper.toDtoListRoom(rs.getAvailableRooms()));
        log.debug("action authPlayer {} {}", connection.getSocket().getPort(), authPlayer);
    }

    public void actionLogoutPlayer(final LogoutPlayerRequest logoutPlayer, final UserConnection connection) throws ServerException {
        ps.logoutPlayer(logoutPlayer, connection);
        ss.sendResponse(connection, Mapper.toDtoMessage("Logout player successfully"));
        ss.sendResponse(connection, new LogoutResponse());
        log.debug("action logoutPlayer {} {}", connection.getSocket().getPort(), logoutPlayer);
    }

    public void actionAutoLogoutPlayer(final UserConnection connection) {
        try {
            ps.autoLogoutPlayer(connection);
        } catch (final ServerException e) {
            log.warn("action AutoLogout player {} {}", connection, e.getMessage());
        }
    }

    public boolean canPlay(final UserConnection connection) throws ServerException {
        if (!ps.canSearchGame(connection)) {
            ps.setPlayerStateNone(connection);
            return false;
        }
        return true;
    }
}
