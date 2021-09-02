package player;

import base.BotType;
import base.Strategy;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import profile.Profile;
import selfplay.BotPlayer;
import strategy.*;

import java.util.function.ToDoubleBiFunction;

@Data
@AllArgsConstructor
public abstract class Player {
    protected PlayerColor color;
    private String nickname;
    protected Strategy strategy;

    public Player(final String nickname, final Strategy strategy) {
        this.nickname = nickname;
        this.strategy = strategy;
        strategy.setColor(color);
    }

    /**
     * Выбирает ход на текущей игровой доске.
     * @param board игровая доска.
     * @return Ход на текущей доске.
     */
    public abstract Point move(GameBoard board) throws ServerException;

    public void setColor(final PlayerColor color) {
        this.color = color;
        strategy.setColor(color);
    }

    public static Player getBotPlayer(
            final BotType botType,
            final String nickname,
            final int depth,
            final ToDoubleBiFunction<GameBoard, PlayerColor> utility,
            final Profile profile
    ) {
        switch (botType) {
            case AB_PRUNING:
                return new BotPlayer(nickname, new ABPruningStrategy(depth, utility));
            case EXPECTIMAX:
                return new BotPlayer(nickname, new ExpectimaxStrategy(depth, utility));
            case MINIMAX:
                return new BotPlayer(nickname, new MinimaxStrategy(depth, utility));
            case MT_MINIMAX:
                return  new BotPlayer(nickname, new MTMinimaxStrategy(depth, utility));
            case PROFILE:
                return new BotPlayer(nickname, new ProfileStrategy(depth, profile, utility));
            default:
                throw new IllegalArgumentException("Illegal player type.");
        }
    }
}
