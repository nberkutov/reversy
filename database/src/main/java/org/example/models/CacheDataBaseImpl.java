package org.example.models;

import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class CacheDataBaseImpl implements CacheDataBaseDao {
    private final Map<String, UserConnection> login_connects = new ConcurrentHashMap<>();
    private final Map<Long, UserConnection> connects = new ConcurrentHashMap<>();

    @Autowired
    private DataBaseDao dbd;

    @Override
    public void putConnection(final long id, final String nickname, final UserConnection connection) {
        connects.put(id, connection);
        putConnection(nickname, connection);
    }

    @Override
    public void putConnection(final String nickname, final UserConnection connection) {
        login_connects.put(nickname, connection);
    }

    @Override
    public void removeConnection(final long id, final String nickname) {
        connects.remove(id);
        removeConnection(nickname);
    }

    @Override
    public void removeConnection(final String nickname) {
        login_connects.remove(nickname);
    }

    @Override
    public UserConnection getConnectionByNickname(final String nickname) {
        return login_connects.get(nickname);
    }

    @Override
    public UserConnection getConnectionById(final long id) {
        return connects.get(id);
    }

    @Override
    public void removeAllConnects() {
        for (final Map.Entry<Long, UserConnection> entry : connects.entrySet()) {
            final long id = entry.getKey();
            final UserConnection connection = entry.getValue();
            final User user = dbd.getUserById(id);
            if (user != null) {
                login_connects.remove(user.getNickname());
            }
            connects.remove(id);
        }
    }

    @Override
    public List<UserConnection> getAllConnections() {
        return new ArrayList<>(connects.values());
    }

    @Override
    public List<UserConnection> getAuthConnections() {
        return connects.values()
                .stream()
                .filter(x -> x.getUserId() != -1)
                .collect(Collectors.toList());
    }

    @Override
    public void clearAll() {
        connects.clear();
        login_connects.clear();
    }
}
