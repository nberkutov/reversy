package client.models.ai.expectimax;

import client.models.ai.minimax.tree.Tree;
import client.models.strategies.Strategy;
import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;

public class Expectimax {
    private final int depth;
    private final PlayerColor myColor;
    private final Strategy strategyPlayer;
    private final Strategy strategyBot;

    public Expectimax(int depth, PlayerColor myColor, Strategy strategyPlayer, Strategy strategyBot) {
        this.depth = depth;
        this.myColor = myColor;
        this.strategyPlayer = strategyPlayer;
        this.strategyBot = strategyBot;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws GameException {
        return !BoardService.getAvailableMoves(board, color).isEmpty();
    }

    public float expectimax(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws GameException {
        if (depth >= this.depth
                || BoardService.isNotPossiblePlayOnBoard(board)
                || moreTriggers(board, moveColor, move, depth)) {
            return funcEvaluation(board, moveColor, move, depth);
        }

        List<Point> listMoves = BoardService.getAvailableMoves(board, moveColor);


        if (moveColor == myColor) {
            float maxEval = -1000f;
            for (Point point : listMoves) {
                float eval = simulationMove(branch, board, point, myColor, depth + 1);

                if (maxEval <= eval) {
                    maxEval = eval;
                    branch.addNode(depth + 1, board, point, eval);
                }

            }
            return maxEval;
        }

        float sumEval = 0f;
        PlayerColor opponentColor = myColor.getOpponent();
        for (Point point : listMoves) {
            sumEval += simulationMove(branch, board, point, opponentColor, depth + 1);
        }

        return sumEval / listMoves.size();
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

    private float simulationMove(final Tree branch,
                                 final GameBoard board,
                                 final Point point,
                                 final PlayerColor color,
                                 int depth) throws GameException {
        GameBoard newBoard = board.clone();
        BoardService.makeMove(newBoard, point, Cell.valueOf(color));
        PlayerColor opponentColor = color.getOpponent();
        PlayerColor moveColor = color;
        if (canMove(newBoard, opponentColor)) {
            moveColor = opponentColor;
        }

        return expectimax(branch, newBoard, point, depth, moveColor);
    }


}
