package org.example.models.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.models.base.PlayerColor;
import org.example.models.base.PlayerState;
import org.example.models.game.Game;
import org.example.models.game.GameResult;
import org.example.models.game.Room;
import org.example.models.statistics.Statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    @Column(unique = true, nullable = false)
    protected String nickname;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_stats_id", referencedColumnName = "id")
    protected Statistics statistics;
    @Column
    @Enumerated(EnumType.STRING)
    protected PlayerState state;
    @Column
    @Enumerated(EnumType.STRING)
    protected PlayerColor color;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "now_game_id", referencedColumnName = "id")
    protected Game nowPlaying;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "now_room_id", referencedColumnName = "id")
    protected Room nowRoom;

    @OneToMany(mappedBy = "winner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<GameResult> gamesWin;
    @OneToMany(mappedBy = "loser", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<GameResult> gamesLose;


    public User(final String nickname) {
        this.nickname = nickname;
        statistics = new Statistics();
        state = PlayerState.NONE;
        color = PlayerColor.NONE;
        gamesLose = new ArrayList<>();
        gamesWin = new ArrayList<>();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User user = (User) o;
        return id == user.id && Objects.equals(nickname, user.nickname) && state == user.state && color == user.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, state, color);
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
}
