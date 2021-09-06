package gui;

import exception.ServerException;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Board;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;

import static models.GameProperties.BOARD_SIZE;

public class WindowGUI implements GameGUI {
    private final GameWindow gameWindow;

    public WindowGUI() {
        gameWindow = new GameWindow();
    }

    @Override
    public void updateGUI(final GameBoard board, final GameState gameState, final String opponent) {
        gameWindow.updateGUI(board, gameState, opponent);
    }
}

