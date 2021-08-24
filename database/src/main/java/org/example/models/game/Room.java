package org.example.models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.models.base.RoomState;
import org.example.models.player.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@Entity(name = "rooms")
public class Room implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "white_user", referencedColumnName = "id")
    private User whiteUser;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "black_user", referencedColumnName = "id")
    private User blackUser;
    @Column
    @Enumerated(EnumType.STRING)
    private RoomState state;

    public Room() {
        this.state = RoomState.OPEN;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Room room = (Room) o;
        return id == room.id && Objects.equals(whiteUser, room.whiteUser) && Objects.equals(blackUser, room.blackUser) && state == room.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, whiteUser, blackUser, state);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", whiteUser=" + whiteUser +
                ", blackUser=" + blackUser +
                ", state=" + state +
                '}';
    }
}
