package org.example.models.ai.expectimax;

import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.ai.minimax.tree.Tree;
import org.example.models.base.Cell;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Algorithm;

import java.util.List;

public class Expectimax {
    private final int depth;
    private final PlayerColor myColor;
    private final Algorithm algoPlayer;
    private final Algorithm algoBot;

    public Expectimax(int depth, PlayerColor myColor, Algorithm algoPlayer, Algorithm algoBot) {
        this.depth = depth;
        this.myColor = myColor;
        this.algoPlayer = algoPlayer;
        this.algoBot = algoBot;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws ServerException {
        return !BoardLogic.getAvailableMoves(board, color).isEmpty();
    }

    public float expectimax(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws ServerException {
        if (depth >= this.depth
                || BoardLogic.isNotPossiblePlayOnBoard(board)
                || moreTriggers(board, moveColor, move)) {
            return funcEvaluation(board, moveColor, move);
        }

        final List<Point> listMoves = BoardLogic.getAvailableMoves(board, moveColor);


        if (moveColor == myColor) {
            float maxEval = -1000f;
            for (final Point point : listMoves) {
                float eval = simulationMove(branch, board, point, moveColor, depth + 1);

                if (maxEval <= eval) {
                    maxEval = eval;
                    branch.addNode(depth + 1, board, point, eval);
                }

            }
            return maxEval;
        }

        float sumEval = 0f;

        for (final Point point : listMoves) {
            sumEval += simulationMove(branch, board, point, moveColor, depth + 1);
        }

        return sumEval / listMoves.size();
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return getAlgoByPlayerColor(color).triggerEvaluationCall(board, color, move);
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return getAlgoByPlayerColor(color).funcEvaluation(board, color, move);
    }

    private Algorithm getAlgoByPlayerColor(final PlayerColor color) {
        Algorithm algo = algoBot;
        if (color == myColor) {
            algo = algoPlayer;
        }
        return algo;
    }

    private float simulationMove(final Tree branch,
                                 final GameBoard board,
                                 final Point point,
                                 final PlayerColor color,
                                 int depth) throws ServerException {
        final GameBoard newBoard = board.clone();
        BoardLogic.makeMove(newBoard, point, Cell.valueOf(color));
        final PlayerColor opponentColor = color.getOpponent();
        PlayerColor moveColor = color;
        if (canMove(newBoard, opponentColor)) {
            moveColor = opponentColor;
        }

        return expectimax(branch, newBoard, point, depth, moveColor);
    }


}
