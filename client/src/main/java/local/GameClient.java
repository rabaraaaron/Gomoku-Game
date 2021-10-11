package local;

import gui.GameView;
import java.awt.*;

public class GameClient{
    /**
     * Starting point for the client.  Displays the main GUI.
     * @param args ignored
     */
    public static void main(String[] args) {
        GameView game = new GameView();
        // Create and display the GameView.
        EventQueue.invokeLater( () -> game.setVisible(true) );
    }
}
