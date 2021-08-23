package org.example.dto.response.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.response.GameResponse;


@EqualsAndHashCode
@Data
@AllArgsConstructor
public class PlayerResponse implements GameResponse {
    private final String nickname;
    private final int totalGames;
    private final int winGames;
    private final int loseGames;

    public PlayerResponse(final String nickname) {
        this.nickname = nickname;
        this.totalGames = 0;
        this.winGames = 0;
        this.loseGames = 0;
    }
}
