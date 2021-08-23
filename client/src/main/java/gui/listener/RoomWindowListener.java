package gui.listener;

import gui.RoomGUI;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class RoomWindowListener implements WindowListener {
    @Override
    public void windowOpened(final WindowEvent e) {

    }

    @Override
    public void windowClosing(final WindowEvent e) {
        final RoomGUI gui = (RoomGUI) e.getWindow();
        gui.closeRoom();
    }

    @Override
    public void windowClosed(final WindowEvent e) {

    }

    @Override
    public void windowIconified(final WindowEvent e) {

    }

    @Override
    public void windowDeiconified(final WindowEvent e) {

    }

    @Override
    public void windowActivated(final WindowEvent e) {

    }

    @Override
    public void windowDeactivated(final WindowEvent e) {

    }
}
