package game;

import user.User;

/**
 * Rules Interface
 *
 * This interface defines a set of methods necessary for any set of game rules designed to work with the Game Class.
 */
interface Rules {
    //Required to determine who goes first, and how turns proceed.
    abstract void setTurnOrder(User playerA, User playerB);

    //Required to determine the size of the board
    abstract int getBoardSize();

    //Required to determine who goes next in turn order.
    abstract User passTurn();

    //Required to determine who is currently going in turn order.
    abstract User currentTurn();

    //Required to check if a given move is legal:
    abstract boolean isLegal(Board board, int x, int y);

    //Required to check if a given move is legal:
    abstract boolean isLegal(Board board, Move m);

    //Required to check if a given move ends the game. Must return an integer representation of the winning player, or 0 otherwise.
    abstract User gameOver(Board board);

    //Required to check if a given move ends the game. Must return an integer representation of the winning player, or 0 otherwise.
    abstract User gameOver(Board board, int x, int y, User lastPlayer);
}
