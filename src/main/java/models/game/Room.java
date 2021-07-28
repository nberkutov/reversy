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

    public Room(int id) {
        this.id = id;
        this.state = RoomState.OPEN;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
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
