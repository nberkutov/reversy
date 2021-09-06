package gui;


import exception.ServerException;
import models.base.GameState;
import models.base.interfaces.GameBoard;

public interface GameGUI {
    void updateGUI(final GameBoard board, final GameState gameState, final String opponent) throws ServerException;

    static GameGUI getGUI(final GUIType guiType) {
        switch (guiType) {
            case WINDOW:
                return new WindowGUI();
            case CONSOLE:
                return new TextGUI();
            default:
                return new EmptyGUI();
        }
    }
}
