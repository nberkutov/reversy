package gui.dontneed;

import gui.GameGUI;
import lombok.NoArgsConstructor;
import org.example.exception.ServerException;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;

@NoArgsConstructor
public class EmptyGUI implements GameGUI {
    @Override
    public void setTitle(String title) {

    }

    @Override
    public void updateGUI(final GameBoard board, final GameState gameState) throws ServerException {

    }

    @Override
    public void setSimpleCloseByWindow(boolean bool) {

    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
