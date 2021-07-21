package models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.RoomState;
import models.player.Player;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
public class Room {
    private final int id;
    private transient final Lock lock = new ReentrantLock();
    private Player whitePlayer;
    private Player blackPlayer;
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
