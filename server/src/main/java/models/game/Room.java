package models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.RoomState;
import models.player.User;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
public class Room implements Serializable {
    private final int id;
    private final Lock lock = new ReentrantLock();
    private User whiteUser;
    private User blackUser;
    private RoomState state;
    private int gamesNumber;

    public Room(final int id, final int gamesNumber) {
        this.id = id;
        this.state = RoomState.OPEN;
        this.gamesNumber = gamesNumber;
    }

    public void decrementGamesNumber() {
        if (gamesNumber > 0) {
            gamesNumber--;
        }
    }

    public Room(final int id) {
        this(id, 1);
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        final Room room = (Room) o;
        return getId() == room.getId() &&
                Objects.equals(getWhiteUser(), room.getWhiteUser()) &&
                Objects.equals(getBlackUser(), room.getBlackUser()) &&
                getState() == room.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWhiteUser(), getBlackUser(), getState());
    }
}
