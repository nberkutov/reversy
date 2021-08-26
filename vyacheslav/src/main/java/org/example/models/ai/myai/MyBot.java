package org.example.models.ai.myai;


import lombok.AllArgsConstructor;
import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.ai.minimax.tree.Tree;
import org.example.models.base.Cell;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Algorithm;

import java.util.List;

@AllArgsConstructor
public class MyBot {
    private final int depth;
    private final PlayerColor myColor;
    private final Algorithm algo;

    public int calculateMove(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws ServerException {
        if (depth >= this.depth
                || BoardLogic.isNotPossiblePlayOnBoard(board)
                || moreTriggers(board, moveColor, move)) {
            return funcEvaluation(board, moveColor, move);
        }

        List<Point> listMoves = BoardLogic.getAvailableMoves(board, moveColor);

        int maxEval = Integer.MIN_VALUE;
        for (final Point point : listMoves) {
            int eval = simulationMove(branch, board, point, myColor, depth + 1);

            if (maxEval <= eval) {
                maxEval = eval;

                branch.addNode(depth + 1, board, point, eval);
            }

        }
        return maxEval;
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return algo.triggerEvaluationCall(board, color, move);
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return algo.funcEvaluation(board, color, move);
    }

    private int simulationMove(final Tree branch,
                               final GameBoard board,
                               final Point point,
                               final PlayerColor color,
                               int depth) throws ServerException {
        GameBoard newBoard = board.clone();
        BoardLogic.makeMove(newBoard, point, Cell.valueOf(color));

        return calculateMove(branch, newBoard, point, depth, color);
    }
}
