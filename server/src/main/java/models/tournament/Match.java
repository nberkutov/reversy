package models.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.game.Game;
import models.player.User;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class Match {
    private final int id;
    private int round;
    private User first;
    private User second;
    private List<Match> parents;
    private int countGames;
    private int result;
    private User winner;
    private Set<Game> games;
}
