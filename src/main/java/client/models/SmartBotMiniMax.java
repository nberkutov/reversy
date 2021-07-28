package client.models;

import client.models.forbot.Tree;
import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SmartBotMiniMax extends Player {
    private int depth;

    public SmartBotMiniMax(String nickname) {
        super(nickname);
        depth = 3;
    }

    public SmartBotMiniMax(String nickname, int depth) {
        super(nickname);
        this.depth = depth;
    }

    private static Integer simpleFuncEvaluation(GameBoard board, PlayerColor color) {
        return getCountCellByColor(board, color);
    }

    private static GameBoard simulationMove(GameBoard board, Point point, PlayerColor color) throws GameException {
        GameBoard newBoard = board.clone();
        BoardService.makeMove(newBoard, point, Cell.valueOf(color));
        return newBoard;
    }

    private static PlayerColor getOpponentColor(PlayerColor color) {
        if (color == PlayerColor.NONE) {
            throw new NotImplementedException();
        }
        if (color == PlayerColor.BLACK) {
            return PlayerColor.WHITE;
        }
        return PlayerColor.BLACK;
    }

    private static boolean isAvailableMovesPlayer(GameBoard board, PlayerColor color) throws GameException {
        return BoardService.getAvailableMoves(board, color).isEmpty();
    }

    private static Integer getCountCellByColor(GameBoard board, PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            return board.getCountBlackCells();
        }
        return board.getCountWhiteCells();
    }

    @Override
    public Point move(GameBoard board) throws GameException {
        Tree branch = new Tree();
        minimax(branch, board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        return branch.getMove();
    }

    private Integer minimax(final Tree branch, final GameBoard board, int depth, int alpha, int beta, boolean playerMove) throws GameException {
        if (depth == 0 || isAvailableMovesPlayer(board, color)) {
            return simpleFuncEvaluation(board, color);
        }
        if (playerMove) {
            int maxEval = Integer.MIN_VALUE;
            for (Point point : BoardService.getAvailableMoves(board, color)) {
                GameBoard newBoard = simulationMove(board, point, color);
                Integer eval = minimax(branch, newBoard, depth - 1, alpha, beta, false);

                if (maxEval <= eval) {
                    maxEval = eval;
                    branch.addNode(depth - 1, board, newBoard, point);
                }

                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }

        int minEval = Integer.MAX_VALUE;
        for (Point point : BoardService.getAvailableMoves(board, getOpponentColor(color))) {
            GameBoard newBoard = simulationMove(board, point, getOpponentColor(color));

            Integer eval = minimax(branch, newBoard, depth - 1, alpha, beta, true);

            minEval = Math.min(minEval, eval);

            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }

}
