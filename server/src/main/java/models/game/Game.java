package models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.base.GameState;
import models.base.Move;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;
import models.player.User;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Slf4j
@AllArgsConstructor
public class Game implements Serializable {
    private final User blackUser;
    private final User whiteUser;
    private final GameBoard board;
    private final Lock lock = new ReentrantLock();
    private int id;
    private GameState state;
    private GameResult result;
    private LinkedList<Move> moves;

    public Game(final int id, final User first, final User second) {
        this(new Board(), first, second);
        this.id = id;
    }

    public Game(final GameBoard board, final User first, final User second) {
        state = GameState.BLACK_MOVE;
        result = GameResult.playing(board);
        this.blackUser = first;
        this.whiteUser = second;
        blackUser.setColor(PlayerColor.BLACK);
        whiteUser.setColor(PlayerColor.WHITE);
        this.board = board;
        moves = new LinkedList<>();
    }

    public void addMove(PlayerColor color, Point point) {
        moves.addLast(Move.create(color, point));
    }


    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", " + blackUser +
                ", vs " + whiteUser +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return getId() == game.getId() &&
                Objects.equals(getBlackUser(), game.getBlackUser()) &&
                Objects.equals(getWhiteUser(), game.getWhiteUser()) &&
                Objects.equals(getBoard(), game.getBoard()) &&
                getState() == game.getState() &&
                Objects.equals(getResult(), game.getResult()) &&
                Objects.equals(getMoves(), game.getMoves());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBlackUser(), getWhiteUser(), getBoard(), getState(), getResult(), getMoves());
    }
}
