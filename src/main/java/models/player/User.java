package models.player;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.game.Game;
import models.statistics.Statistics;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@AllArgsConstructor
@Data
public class User implements Serializable {
    protected final int id;
    protected final String nickname;
    protected final Statistics statistics;

    protected PlayerState state;
    protected PlayerColor color;
    protected Game nowPlaying;

    private final Lock lock = new ReentrantLock();

    public User(final int id, final String nickname) {
        this.id = id;
        this.nickname = nickname;
        statistics = new Statistics();
        state = PlayerState.NONE;
        color = PlayerColor.NONE;
    }

    public Point move(final GameBoard board) throws GameException {
        return null;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", statistics=" + statistics +
                ", state=" + state +
                ", color=" + color +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId() &&
                Objects.equals(getNickname(), user.getNickname()) &&
                Objects.equals(getStatistics(), user.getStatistics()) &&
                getState() == user.getState() &&
                getColor() == user.getColor() &&
                Objects.equals(getNowPlaying(), user.getNowPlaying());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNickname(), getStatistics(), getState(), getColor(), getNowPlaying());
    }
}
