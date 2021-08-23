package org.example.models;

import org.example.models.player.UserConnection;

import java.util.List;

public interface CacheDataBaseDao {
    void putConnection(final long id, final String nickname, final UserConnection connection);

    void putConnection(final String nickname, final UserConnection connection);

    void removeConnection(final long id, final String nickname);

    void removeConnection(final String nickname);

    UserConnection getConnectionByNickname(final String nickname);

    UserConnection getConnectionById(final long id);

    void removeAllConnects();

    List<UserConnection> getAllConnections();

    List<UserConnection> getAuthConnections();

    void clearAll();
}
