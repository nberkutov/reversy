package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.exception.ServerException;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Player {
    protected PlayerColor color;
    private String nickname;

    public Player(final String nickname) {
        this.nickname = nickname;
    }

    public abstract Point move(final GameBoard board) throws ServerException;

}
