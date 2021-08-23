package org.example.models.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.models.player.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@Entity(name = "statistic")
public class Statistics implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //    @ElementCollection
//    @CollectionTable(name = "win_against_table")
//    @MapKeyJoinColumn(name = "win_against_id")
//    @Column(name = "count_win")
    @Transient
    private Map<User, Integer> winsAgainst;
    @Column
    private int totalGames;
    @Column
    private int win;
    @Column
    private int lose;
    @Column
    private int playBlackColor;
    @Column
    private int playWhiteColor;

    public Statistics() {
        totalGames = 0;
        win = 0;
        lose = 0;
        playBlackColor = 0;
        playWhiteColor = 0;
        winsAgainst = new HashMap<>();
    }

    public void incrementPlayerAgainst(final User user) {
        Integer wins = winsAgainst.get(user);
        if (wins == null) {
            wins = 0;
        }
        winsAgainst.put(user, ++wins);
    }

    public void incrementCountGames() {
        totalGames++;
    }

    public void incrementWin() {
        win++;
    }

    public void incrementLose() {
        lose++;
    }

    public void incrementPlayBlack() {
        playBlackColor++;
    }

    public void incrementPlayWhite() {
        playWhiteColor++;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "id=" + id +
                ", totalGames=" + totalGames +
                ", win=" + win +
                ", lose=" + lose +
                ", playBlackColor=" + playBlackColor +
                ", playWhiteColor=" + playWhiteColor +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Statistics that = (Statistics) o;
        return id == that.id && totalGames == that.totalGames && win == that.win && lose == that.lose && playBlackColor == that.playBlackColor && playWhiteColor == that.playWhiteColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, totalGames, win, lose, playBlackColor, playWhiteColor);
    }
}
