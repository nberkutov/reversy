package org.example.models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.base.GameState;
import org.example.models.base.Move;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.ArrayBoard;
import org.example.models.board.Point;
import org.example.models.player.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "games")
public class Game implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "black_user", referencedColumnName = "id")
    private User blackUser;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "white_user", referencedColumnName = "id")
    private User whiteUser;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ArrayBoard.class, orphanRemoval = true)
    @JoinColumn(name = "game_board", referencedColumnName = "id")
    private GameBoard board;

    @Column
    @Enumerated(EnumType.STRING)
    private GameState state;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "game_result", referencedColumnName = "id")
    private GameResult result;
    //    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "game_moves",
            joinColumns = @JoinColumn(name = "id_game"),
            inverseJoinColumns = @JoinColumn(name = "id_move"))
    @OrderBy("id ASC")
    private List<Move> moves;

    public Game(final GameBoard board, final User first, final User second) {
        state = GameState.BLACK_MOVE;
        result = GameResult.playing();
        this.blackUser = first;
        this.whiteUser = second;
        blackUser.setColor(PlayerColor.BLACK);
        whiteUser.setColor(PlayerColor.WHITE);
        this.board = board;
        moves = new ArrayList<>();
    }

    public void addMove(final PlayerColor color, final Point point) {
        moves.add(Move.create(color, point));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Game game = (Game) o;
        return id == game.id && Objects.equals(blackUser, game.blackUser) && Objects.equals(whiteUser, game.whiteUser) && Objects.equals(board, game.board) && state == game.state && Objects.equals(result, game.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, blackUser, whiteUser, board, state, result);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", blackUser=" + blackUser +
                ", whiteUser=" + whiteUser +
                ", board=" + board +
                ", state=" + state +
                '}';
    }
}
