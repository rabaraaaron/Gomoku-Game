package game;

import java.util.Random;
import user.User;

/**
 * Rules Class
 *
 * Connect6Rules is a class that stores and manipulates the game rules for a game of connect 6 between 2 players using standard Go equipment.
 * It is capable of determining a turn order and a winner for the game, as defined in the Connect6 Rules. https://en.wikipedia.org/wiki/Connect6
 */

class Connect6Rules implements Rules {
    public static final int CONNECT_TARGET = 6; // The number of pieces that must be in a row for a victory
    public static final int BOARD_SIZE = 19; // The size of a single side of the square board.
    public static final int NUMBER_OF_PLAYERS = 2; // The number of players who can play the game
    public static final int TURNS_PER_PLAYER = 2; // The number of turns each player can take on their turn.
    private User[] turnOrder; // Used to determine which player goes next.
    private int currentTurn;

    /**
     * Creates a new Connect6Rules, setting a turn order for the given players, and the size of the board to the given value.
     *
     * @param playerA the first user
     * @param playerB the second user
     */
    Connect6Rules(User playerA, User playerB){
        setTurnOrder(playerA, playerB);
    }

    /**
     * Determines who goes first and then sets the turn order accordingly
     *
     * @param playerA Player a
     * @param playerB Player b
     */
    public void setTurnOrder(User playerA, User playerB){
        User first = coinToss(playerA, playerB);
        if(first.equals(playerA)){
            setOrd(playerA, playerB);
        } else {
            setOrd(playerB, playerA);
        }
        currentTurn = 0;
    }

    /**
     * Private helper method that actually decides who goes first.
     *
     * @param a Player a
     * @param b Player b
     * @return the user that goes first.
     */
    private User coinToss(User a, User b){
        Random rand = new Random();
        int i = rand.nextInt(2);
        if(i == 1){
            return a;
        }
        return b;
    }

    /**
     * Private helper method that actually sets the turn order.
     *
     * @param a Player a
     * @param b Player b
     */
    private void setOrd(User a, User b){
        turnOrder = new User[4];
        turnOrder[0] = a;
        turnOrder[1] = b;
        turnOrder[2] = b;
        turnOrder[3] = a;
    }

    /**
    * Retrieves an integer representing the size of the board
    * @return an integer representing the size of the board
    */
    public int getBoardSize(){
        return BOARD_SIZE;
    }

    /**
    * Retrieves the next player in the turn order.
    *
    * @return the next player in the turn order.
    */
    public User passTurn(){
        if(currentTurn == 3){
            currentTurn = 0;
        } else {
            currentTurn = currentTurn + 1;
        }
        return turnOrder[currentTurn];
    }

    /**
     * Retrieves the current player in the turn order.
     *
     * @return the current player in the turn order.
     */
    public User currentTurn(){
        return turnOrder[currentTurn];
    }

    /**
    * Checks if a given move object is in accordance with the rules.
    *
    * @param board a board object representing the current game state
    * @param m the move you want to check
    * @return whether or not the move is a legal one
    */
    public boolean isLegal(Board board, Move m){
        return isLegal(board, m.getX(), m.getY());
    }

    /**
     * Checks if a given set of coordinates for a move is in accordance with the rules.
     *
     * @param board an int[][] representation of the current board state.
     * @param x the x coordinate of the move to be checked
     * @param y the y coordinate of the move to be checked
     * @return whether or not the move is a legal one
     */
    public boolean isLegal(Board board, int x, int y){
        if(!((x >= BOARD_SIZE) || (y >= BOARD_SIZE) || (x < 0) || (y < 0))){ // If either the x or y coordinate of the move is less then 0 or greater than or equal to the board size, then the move is illegal.
            return true;
        }
        return false;
    }

    /**
    * Checks if a given move object ends the game.
    *
    * @param board an int[][] representation of the game board. Must be a square with sides the same length as boardSize, or errors may occur.
    * @return if the player won, then the player's identifying integer is returned. Otherwise, 0.
    */
    public User gameOver(Board board){
        Move lastMove = board.getLastMove();
        return gameOver(board, lastMove.getX(), lastMove.getY(), lastMove.getPlayer());
    }

    /**
    * Checks if a given move object ends the game.
    *
    * @param board an int[][] representation of the game board. Must be a square with sides the same length as boardSize, or errors may occur.
    * @param x the x coordinate of the move to be checked
    * @param y the y coordinate of the move to be checked
    * @param lastPlayer the integer used to represent moves on the board made by the player who made the move being checked
    * @return if the player won, then the player's identifying integer is returned. Otherwise, 0.
    */
    public User gameOver(Board board, int x, int y, User lastPlayer){
        if(board.getPosition(x, y).equals(lastPlayer)){
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++ ){
                    if((i != 0) || (j != 0)) {
                        int counter = inRow(board, x, y, i, j) + inRow(board, x, y, -1 * i, -1 * j) - 1;
                        if (counter >= CONNECT_TARGET) {
                            return lastPlayer;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
    * Private helper method that recursively checks all continuous moves by a player in a straight line on a given board.
    *
    * @param board board an int[][] representation of the game board. Must be a square with sides the same length as boardSize, or errors may occur.
     * @param x the x coordinate of the current board location being examined.
    * @param y the y coordinate of the current board location being examined.
    * @param xOffset a value to be added to x when traversing the 2d array. Indicates the current direction of travel.
    * @param yOffset a value to be added to y when traversing the 2d array. Indicates the current direction of travel.
    */
    private int inRow(Board board, int x, int y, int xOffset, int yOffset){
        if(!isLegal(board, x , y )){
            return 0;
        } else if ((!isLegal(board, x + xOffset, y + yOffset)) || (!board.getPosition(x, y).equals(board.getPosition( x + xOffset, y + yOffset)))){
            return 1;
        } else if((isLegal(board, x + xOffset, y + yOffset)) && (board.getPosition(x, y).equals(board.getPosition( x + xOffset, y + yOffset)))){
            return 1 + inRow(board, (x + xOffset), (y + yOffset), xOffset, yOffset);
        } else{
            return 0;
        }
    }
}