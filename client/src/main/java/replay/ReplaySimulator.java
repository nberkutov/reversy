package replay;

import gui.GameGUI;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.game.GameResultResponse;
import org.example.dto.response.game.MoveResponse;
import org.example.dto.response.game.ReplayResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.base.Cell;
import org.example.models.base.GameResultState;
import org.example.models.base.GameState;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReplaySimulator extends Thread {
    private final GameGUI gui;
    private PlayerResponse black;
    private PlayerResponse white;
    private GameBoard board;
    private GameResultResponse result;
    private List<MoveResponse> moves;

    public ReplaySimulator() {
        this.gui = new WindowGUI(true);
    }

    public void init(final ReplayResponse response) {
        black = response.getBlack();
        white = response.getWhite();
        board = response.getStart();
        result = response.getResult();
        moves = new ArrayList<>(response.getMoves());
    }

    @Override
    public void run() {
        try {
            gui.setTitle(String.format("Replay: За чёрных %s VS За белых %s", black.getNickname(), white.getNickname()));

            gui.updateGUI(board, GameState.BLACK_MOVE);
            for (final MoveResponse move : moves) {
                if (!gui.isVisible()) {
                    return;
                }
                final Point point = move.getPoint().to();
                final PlayerColor colorMove = move.getColor();
                final GameBoard copy = board.clone();
                BoardLogic.makeMove(copy, point, Cell.valueOf(colorMove));
                gui.updateGUI(copy, getStateByColor(colorMove.getOpponent()));
                board = copy;
                Thread.sleep(500);
            }
            final PlayerResponse winner = result.getWinner();
            final GameResultState state = result.getState();
            JOptionPane.showMessageDialog(null, String.format("Победил %s \n%s", winner.getNickname(), state));
        } catch (final InterruptedException | ServerException e) {
            log.error("Replay error", e);
        }
    }

    private GameState getStateByColor(PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            return GameState.BLACK_MOVE;
        }
        if (color == PlayerColor.WHITE) {
            return GameState.BLACK_MOVE;
        }
        return GameState.END;
    }
}
