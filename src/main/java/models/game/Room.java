package models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.RoomState;
import models.player.User;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
public class Room implements Serializable {
    private final int id;
    private transient final Lock lock = new ReentrantLock();
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
}
