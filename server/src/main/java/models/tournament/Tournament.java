package models.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.player.User;

import java.util.List;

@Data
@AllArgsConstructor
public class Tournament {
    private final int id;
    private List<Match> matches;
    private User winner;
}
