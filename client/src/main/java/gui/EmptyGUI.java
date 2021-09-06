package gui;

import exception.ServerException;
import lombok.NoArgsConstructor;
import models.base.GameState;
import models.base.interfaces.GameBoard;

@NoArgsConstructor
public class EmptyGUI implements GameGUI {
    @Override
    public void updateGUI(final GameBoard board, final GameState gameState, final String opponent) throws ServerException {

    }
}
