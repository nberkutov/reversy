package gui;


import org.example.exception.ServerException;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;

public interface GameGUI {
    void setTitle(String title);

    void updateGUI(final GameBoard board, final GameState gameState) throws ServerException;

    void setSimpleCloseByWindow(boolean bool);

    boolean isVisible();
}
