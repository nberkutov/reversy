package client.models.ai.minimax;

import client.models.ai.minimax.tree.Tree;
import client.models.strategies.Strategy;
import exception.GameException;
import lombok.Data;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;

@Data
public class OneThreadMinimax {
    private final int depth;
    private final PlayerColor myColor;
    private final Strategy strategyPlayer;
    private final Strategy strategyBot;
    private int alpha;
    private int beta;

    public OneThreadMinimax(int depth, PlayerColor myColor, Strategy strategyPlayer, Strategy strategyBot) {
        this.depth = depth;
        this.myColor = myColor;
        this.strategyPlayer = strategyPlayer;
        this.strategyBot = strategyBot;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws GameException {
        return !BoardService.getAvailableMoves(board, color).isEmpty();
    }

    public int minimax(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws GameException {
        if (depth >= this.depth
                || BoardService.isNotPossiblePlayOnBoard(board)
                || moreTriggers(board, moveColor, move, depth)) {
            return funcEvaluation(board, moveColor, move, depth);
        }

        List<Point> listMoves = BoardService.getAvailableMoves(board, moveColor);

        if (moveColor == myColor) {
            int maxEval = Integer.MIN_VALUE;
            for (Point point : listMoves) {
                int eval = simulationMove(branch, board, point, myColor, depth + 1);

                if (maxEval <= eval) {
                    maxEval = eval;

                    branch.addNode(depth + 1, board, point, eval);
                }

                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }

        int minEval = Integer.MAX_VALUE;
        PlayerColor opponentColor = myColor.getOpponent();
        for (Point point : listMoves) {
            int eval = -1 * simulationMove(branch, board, point, opponentColor, depth + 1);

            minEval = Math.min(minEval, eval);

            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move, final int depth) throws GameException {
        if (color == this.myColor) {
            return strategyPlayer.triggerEvaluationCall(board, color, move, depth, this.depth);
        }
        return strategyBot.triggerEvaluationCall(board, color, move, depth, this.depth);
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move, int depth) throws GameException {
        if (color == this.myColor) {
            return strategyPlayer.funcEvaluation(board, color, move, depth, this.depth);
        }
        return strategyBot.funcEvaluation(board, color, move, depth, this.depth);
    }

    private int simulationMove(final Tree branch,
                               final GameBoard board,
                               final Point point,
                               final PlayerColor color,
                               int depth) throws GameException {
        GameBoard newBoard = board.clone();
        BoardService.makeMove(newBoard, point, Cell.valueOf(color));
        PlayerColor opponentColor = color.getOpponent();
        PlayerColor nextMove = color;
        if (canMove(newBoard, opponentColor)) {
            nextMove = opponentColor;
        }

        return minimax(branch, newBoard, point, depth, nextMove);
    }
}
