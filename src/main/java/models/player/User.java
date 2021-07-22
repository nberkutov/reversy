package models.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import models.statistics.Statistics;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class User {
    protected final int id;
    protected final String nickname;
    protected Statistics statistics;

    private transient final Lock lock = new ReentrantLock();

    public User(final int id, final String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.statistics = new Statistics();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
