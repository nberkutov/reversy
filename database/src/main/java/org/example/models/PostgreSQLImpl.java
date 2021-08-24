package org.example.models;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.base.RoomState;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.ArrayBoard;
import org.example.models.game.Game;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.repository.BoardRepository;
import org.example.repository.GameRepository;
import org.example.repository.RoomRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
//@Transactional(rollbackFor = ServerException.class, propagation = Propagation.SUPPORTS)
public class PostgreSQLImpl implements DataBaseDao {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Override
    public Game getGameById(final long gameId) {
        final Optional<Game> optionalGame = gameRepository.findById(gameId);
        optionalGame.ifPresent(game -> game.getBoard().updateCellsByText());
        return optionalGame.orElse(null);
    }

    @Override
    public Game saveGame(final Game game) throws ServerException {
        try {
            saveGameBoard(game.getBoard());
            gameRepository.save(game);
            return game;
        } catch (final RuntimeException e) {
            log.warn("saveGame", e);
            throw new ServerException(GameErrorCode.DATABASE_ERROR);
        }
    }

    private GameBoard saveGameBoard(final GameBoard board) throws ServerException {
        board.updateTextCells();
        return boardRepository.save((ArrayBoard) board);
    }

    @Override
    public Room getRoomById(final long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    @Override
    public Room saveRoom(final Room room) throws ServerException {
        try {
            return roomRepository.save(room);
        } catch (final RuntimeException e) {
            log.warn("saveRoom", e);
            throw new ServerException(GameErrorCode.DATABASE_ERROR);
        }
    }

    @Override
    public void removeRoom(final Room room) throws ServerException {
        try {
            roomRepository.delete(room);
        } catch (final RuntimeException e) {
            log.warn("removeRoom", e);
            throw new ServerException(GameErrorCode.DATABASE_ERROR);
        }
    }

    @Override
    public User getUserById(final long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByNickname(final String nickname) {
        return userRepository.findByNickname(nickname).orElse(null);
    }

    @Override
    public User saveUser(final User user) throws ServerException {
        try {
            return userRepository.save(user);
        } catch (final DataIntegrityViolationException e) {
            throw new ServerException(GameErrorCode.NICKNAME_ALREADY_USED);
        } catch (final RuntimeException e) {
            log.warn("saveUser", e);
            throw new ServerException(GameErrorCode.DATABASE_ERROR);
        }
    }

    @Override
    public List<User> getAllPlayers() {
        return userRepository.findAll();
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public List<Room> getRooms(final boolean needClose, final int offset, final int limit) throws ServerException {
        try {
            final PageRequest pageable = PageRequest.of(offset, limit);
            if (needClose) {
                return roomRepository.findAll(pageable).toList();
            }
            return roomRepository.findAllByState(RoomState.OPEN, pageable).toList();
        } catch (final RuntimeException e) {
            log.warn("getRooms", e);
            throw new ServerException(GameErrorCode.DATABASE_ERROR);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public void clearAll() {
        roomRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }
}
