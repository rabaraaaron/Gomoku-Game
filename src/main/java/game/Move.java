package game;

import user.User;
import java.sql.Timestamp;

/**
 * Move Model
 *
 * This class models a single change in a board state -- where the change took place, the inciting player, and the time the move was made.
 */

class Move {
    private int x, y;
    private User player; // consider changing this to int 'userID'
    private Timestamp timestamp;

    /**
     * Constructs a new move object representing a move at point (x,y) made by the specified user.
     *
     * @param xCoord the x coordinate of the move
     * @param yCoord the y coordinate of the move
     * @param p the user that made the move
     */
    protected Move(int xCoord, int yCoord, User p){
        x = xCoord;
        y = yCoord;
        player = p;
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Gets the X coordinate of this move.
     *
     * @return the x coordinate of this move
     */
    int getX(){
        return x;
    }

    /**
     * Gets the y coordinate of this move.
     *
     * @return the y coordinate of this move
     */
    int getY(){
        return y;
    }

    /**
     * Gets the user that made this move.
     *
     * @return the user that made this move
     */
    User getPlayer(){
        return player;
    }

    /**
     * Gets the time that this move was made
     *
     * @return the time that this move was made
     */
    Timestamp getTimestamp(){
        return timestamp;
    }
}
