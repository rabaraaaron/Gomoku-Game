package game;

import user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Board Model
 *
 * This class models a board as a series of game states, and allows for various operations to be performed on said game states.
 *
 */
class Board {
    private User[][] gameBoard;
    private LinkedList<Move> moves = new LinkedList<Move>();
    private boolean mutable;


    /**
     * Constructor for the board class
     * @param size length of the board
     * @param p1 playerOne as defined in game
     * @param p2 playerTwo as defined in game
     */
    Board(int size, User p1, User p2) {
        gameBoard = new User[size][size];
        mutable = true;
    }

    /**
     * updates the board with a player's move
     * @param x the x position of the move
     * @param y the y position of the move
     * @param currPlayer the player that made the move
     * @return true if the move is successfully placed
     */
    boolean putMove(int x, int y, User currPlayer) {
        if(!(gameBoard[x][y] == null)) {
            return false;
        }
        else {
            gameBoard[x][y] = currPlayer;
            Move tempMove = new Move(x, y, currPlayer);
            moves.add(tempMove);
            return true;
        }
    }
    
    /**
     * returns the value at a specific point on the board
     * @param x the x coord on the board
     * @param y the y coord on the board
     * @return the value at index[x][y]
     */
    User getPosition(int x, int y) {
        return gameBoard[x][y];
    }

    /**
     * returns the latest move made
     * @return the last move in the moves list
     */
    Move getLastMove() {
        return moves.getLast();
    }

    /**
     * getter for the mutable variable
     * @return mutable
     */
    boolean getMutable() {
        return mutable;
    }

    /**
     * Gets an ArrayList containing all move data. Each Move is also stored in ArrayList form.
     *
     * @return A 2d ArrayList containing Move data. The list is sorted in order that the moves were made.
     */
    ArrayList getMovesList(){
        ArrayList list = new ArrayList();
        for(int i = 0; i < moves.size(); i++){
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("time", moves.get(i).getTimestamp().toString());
            temp.put("x", Integer.valueOf(moves.get(i).getX()));
            temp.put("y", Integer.valueOf(moves.get(i).getY()));
            temp.put("user", moves.get(i).getPlayer().getUserName());
            list.add(temp);
        }
        return list;
    }


}
