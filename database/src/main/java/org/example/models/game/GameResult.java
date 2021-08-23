package org.example.models.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.base.GameResultState;
import org.example.models.player.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Slf4j
@Getter
@Entity(name = "game_result")
@NoArgsConstructor
public class GameResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @Enumerated(EnumType.STRING)
    private GameResultState resultState;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_winner_id")
    private User winner;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_loser_id")
    private User loser;

    public GameResult(final GameResultState resultState, final User winner, final User loser) {
        this.resultState = resultState;
        this.winner = winner;
        this.loser = loser;
    }

    private GameResult(final GameResultState resultState) {
        this(resultState, null, null);
    }

    public static GameResult winner(final User winner, final User loser) {
        return new GameResult(GameResultState.ORDINARY_VICTORY, winner, loser);
    }

    public static GameResult techWinner(final Game game, final User loser) {
        if (game.getBlackUser().equals(loser)) {
            return new GameResult(GameResultState.TECHNICAL_VICTORY, game.getWhiteUser(), game.getBlackUser());
        }
        return new GameResult(GameResultState.TECHNICAL_VICTORY, game.getBlackUser(), game.getWhiteUser());
    }

    public static GameResult playing() {
        return new GameResult(GameResultState.PLAYING);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof GameResult)) return false;
        final GameResult that = (GameResult) o;
        return getId() == that.getId() && getResultState() == that.getResultState() && Objects.equals(getWinner(), that.getWinner()) && Objects.equals(getLoser(), that.getLoser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getResultState(), getWinner(), getLoser());
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "id=" + id +
                ", resultState=" + resultState +
                ", winner=" + winner +
                ", loser=" + loser +
                '}';
    }
}
