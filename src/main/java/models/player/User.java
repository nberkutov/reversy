package models.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.statistics.Statistics;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    protected int id;
    protected String nickname;
    protected Statistics statistics;

    public User(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.statistics = new Statistics();
    }

    public User(final String nickname) {
        this.nickname = nickname;
    }
}
