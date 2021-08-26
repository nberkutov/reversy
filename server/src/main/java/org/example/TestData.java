package org.example;

import org.example.exception.ServerException;
import org.example.models.DataBaseDao;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(rollbackFor = ServerException.class)
public class TestData implements CommandLineRunner {

    @Autowired
    private DataBaseDao dbd;


    @Override
    public void run(final String... args) throws Exception {
        final List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final User user = new User("Player_" + i);
            dbd.saveUser(user);

            users.add(user);
        }
        Collections.shuffle(users);
        for (int i = 0; i < 5; i++) {
            final User tmp = users.get(0);
            users.remove(tmp);
            final Room room = new Room();
            tmp.setNowRoom(room);
            room.setWhiteUser(tmp);
            dbd.saveRoom(room);
        }

        for (int i = 0; i < 5; i++) {
            final User tmp = users.get(0);
            users.remove(tmp);
            final Room room = new Room();
            tmp.setNowRoom(room);
            room.setBlackUser(tmp);
            dbd.saveRoom(room);
        }
    }
}
