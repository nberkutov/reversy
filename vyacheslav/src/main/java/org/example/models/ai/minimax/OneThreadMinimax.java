package org.example.models.ai.minimax;


import lombok.Data;
import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.base.Cell;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Algorithm;

import java.util.List;

@Data
public class OneThreadMinimax {
    private MiniMaxInfo info;
    private int alpha;
    private int beta;

    public OneThreadMinimax(MiniMaxInfo info) {
        this.info = info;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws ServerException {
        return !BoardLogic.getAvailableMoves(board, color).isEmpty();
    }

    public int minimax(final GameBoard board, Point move, int depth, PlayerColor moveColor) throws ServerException {
        if (depth >= info.getMaxDepth()
                || BoardLogic.isNotPossiblePlayOnBoard(board)
                || moreTriggers(board, moveColor, move)) {
            return funcEvaluation(board, moveColor, move);
        }

        List<Point> listMoves = BoardLogic.getAvailableMoves(board, moveColor);

        if (moveColor == info.getMyColor()) {
            int maxEval = Integer.MIN_VALUE;
            for (final Point point : listMoves) {
                int eval = simulationMove(board, point, moveColor, depth + 1);

                if (maxEval <= eval) {
                    maxEval = eval;

                    info.getBranch().addNode(depth + 1, board, point, eval);
                }

                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }

        int minEval = Integer.MAX_VALUE;

        for (final Point point : listMoves) {
            int eval = -1 * simulationMove(board, point, moveColor, depth + 1);

            minEval = Math.min(minEval, eval);

            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return getAlgoByPlayerColor(color).triggerEvaluationCall(board, color, move);
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return getAlgoByPlayerColor(color).funcEvaluation(board, color, move);
    }

    private Algorithm getAlgoByPlayerColor(final PlayerColor color) {
        Algorithm algo = info.getAlgoBot();
        if (color == info.getMyColor()) {
            algo = info.getAlgoPlayer();
        }
        return algo;
    }

    private int simulationMove(final GameBoard board,
                               final Point point,
                               final PlayerColor color,
                               int depth) throws ServerException {
        GameBoard newBoard = board.clone();
        BoardLogic.makeMove(newBoard, point, Cell.valueOf(color));
        PlayerColor opponentColor = color.getOpponent();
        PlayerColor nextMove = color;
        if (canMove(newBoard, opponentColor)) {
            nextMove = opponentColor;
        }

        return minimax(newBoard, point, depth, nextMove);
    }
}
