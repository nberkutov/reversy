package client.models;

import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;
import services.BoardService;
import services.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public class MinimaxBotPlayer extends Player {
    private PlayerColor opponentColor;
    private PlayerColor currentPlayer;

    public MinimaxBotPlayer(String nickname) {
        super(nickname);
    }

    @Override
    public void setColor(PlayerColor color) {
        super.setColor(color);
        if (color == PlayerColor.BLACK) {
            opponentColor = PlayerColor.WHITE;
        } else {
            opponentColor = PlayerColor.BLACK;
        }
    }

    @Override
    public Point move(GameBoard board) throws GameException {
        List<Point> moves = BoardService.getAvailableMoves(board, color);
        for (Point move : moves) {
            GameBoard boardCopy = new Board(board);
            currentPlayer = color;
            BoardService.makeMove(board, move, Cell.valueOf(color));
            int win = getWinByTree(boardCopy, 3);

        }
    }

    private Integer estimate(GameBoard board, PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLACK) {
            return board.getCountBlackCells();
        }
        return board.getCountWhiteCells();
    }

    private int getWinByTree(GameBoard board, int depth) throws GameException {
        final ToIntBiFunction<GameBoard, PlayerColor> winCalculator;
        final PlayerColor simColor;
        final PlayerColor nextPlayer = currentPlayer == PlayerColor.WHITE
                ? PlayerColor.BLACK
                : PlayerColor.WHITE;
        if (nextPlayer == color) {
            winCalculator = this::estimate;
            simColor = color;
        } else {
            winCalculator = (board1, playerColor) -> -estimate(board1, playerColor);
            simColor = opponentColor;
        }
        final PlayerColor winner =  getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return computeWin(winner, board);
        }
        final List<Point> moves = BoardService.getAvailableMoves(board, simColor);
        final List<Point> answer = new ArrayList<>();
        final List<Integer> answerPoints = new ArrayList<>();
        for (Point move : moves) {
            GameBoard boardCopy = new Board(board);
            final int win = getWinByTree(boardCopy, depth - 1);
            answer.add(move);
            answerPoints.add(win);
        }
        Point maxMove = answer.get(0);
        int maxWin = answerPoints.get(0);
        for (int i = 0; i < answer.size(); i++) {
            if (answerPoints.get(i) > maxWin) {
                maxWin = answerPoints.get(i);
                maxMove = answer.get(0);
            }
        }
        return maxWin;
    }

    private PlayerColor getEndOfGame(GameBoard board) throws GameException {
        List<Point> blackMoves = BoardService.getAvailableMoves(board, PlayerColor.BLACK);
        List<Point> whiteMoves = BoardService.getAvailableMoves(board, PlayerColor.WHITE);
        if (board.getCountEmpty() == 0 || blackMoves.size() == 0 && whiteMoves.size() == 0) {
            if (board.getCountBlackCells() > board.getCountWhiteCells()) {
                return PlayerColor.BLACK;
            }
            return PlayerColor.WHITE;
        }
        return PlayerColor.NONE;
    }

    private int computeWin(PlayerColor winnerColor, GameBoard board) {
        if (winnerColor == PlayerColor.BLACK) {
            return board.getCountBlackCells() - board.getCountWhiteCells();
        }
        return board.getCountWhiteCells() - board.getCountBlackCells();
    }
}
