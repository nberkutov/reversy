package client.models;

import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;
import services.BoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public class MinimaxBotPlayer extends Player {
    private static final class Answer {
        final Point move;
        final PlayerColor color;

        private Answer(Point move, PlayerColor color) {
            this.move = move;
            this.color = color;
        }
    }

    private static final class AnswerAndWin {
        final Answer answer;
        final int win;

        private AnswerAndWin(Answer answer, int win) {
            this.answer = answer;
            this.win = win;
        }
    }
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
        //System.out.println(color);
    }

    @Override
    public Point move(GameBoard board) throws GameException {
        final List<Point> moves = BoardService.getAvailableMoves(board, color);
        final List<AnswerAndWin> answerAndWins = new ArrayList<>();
        for (Point move : moves) {
            currentPlayer = color;
            GameBoard boardCopy = new Board(board);
            BoardService.makeMove(boardCopy, move, Cell.valueOf(color));
            int win = getWinByTree(boardCopy, 4);
            answerAndWins.add(new AnswerAndWin(new Answer(move, color), win));
        }
        return getGreedyDecision(answerAndWins, aw -> aw.win).answer.move;
    }

    private AnswerAndWin getGreedyDecision(List<AnswerAndWin> awList, ToIntFunction<AnswerAndWin> winCalculator) {
        AnswerAndWin bestAW = awList.get(0);
        int bestWin = winCalculator.applyAsInt(bestAW);
        for (int i = 1; i < awList.size(); i++) {
            final AnswerAndWin currentAW = awList.get(i);
            final int currentWin = winCalculator.applyAsInt(currentAW);
            if (currentWin > bestWin) {
                bestAW = currentAW;
                bestWin = currentWin;
            }
        }
        return bestAW;
    }

    private int getWinByTree(GameBoard board, int depth) throws GameException {
        final ToIntFunction<AnswerAndWin> winCalculator;
        final PlayerColor simColor;
        final PlayerColor nextPlayer = currentPlayer == PlayerColor.WHITE
                ? PlayerColor.BLACK
                : PlayerColor.WHITE;
        if (nextPlayer == color) {
            winCalculator = aw -> aw.win;
            simColor = color;
        } else {
            winCalculator = aw -> -aw.win;
            simColor = opponentColor;
        }
        final PlayerColor winner =  getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            int w = computeWin(winner, board);
            //System.out.println(w);
            return w;
        }
        final List<Point> moves = BoardService.getAvailableMoves(board, simColor);
        final List<AnswerAndWin> awList = new ArrayList<>();
        currentPlayer = simColor;
        for (Point move : moves) {
            GameBoard boardCopy = new Board(board);
            BoardService.makeMove(boardCopy, move, Cell.valueOf(simColor));
            final int win = getWinByTree(boardCopy, depth - 1);
            awList.add(new AnswerAndWin(new Answer(move, simColor), win));
        }
        int w = -getGreedyDecision(awList, winCalculator).win;
        return w;
    }

    private PlayerColor getEndOfGame(GameBoard board) throws GameException {
        List<Point> blackMoves = BoardService.getAvailableMoves(board, PlayerColor.BLACK);
        List<Point> whiteMoves = BoardService.getAvailableMoves(board, PlayerColor.WHITE);
        if (board.getCountEmpty() == 0 || blackMoves.isEmpty() || whiteMoves.isEmpty()) {
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
            //return Integer.compare(board.getCountBlackCells(), board.getCountWhiteCells());
        } else {
            return board.getCountWhiteCells() - board.getCountBlackCells();
            //return Integer.compare(board.getCountWhiteCells(), board.getCountBlackCells());
        }
    }
}
