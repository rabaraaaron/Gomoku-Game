package game;

import messenger.Messenger;
import user.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Game Model
 *
 * The game class models a game played using standard Go equipment, and allows for various operations such as making moves and determining winners.
 */

public class Game {
    public final Messenger CHAT = new Messenger();

    private int gameID;
    private boolean gameCompleted;
    private Timestamp startTime;
    private boolean viewable;

    private User winner;
    private User loser;
    private User[] players;

    private Board currentBoard;

    private gameEnum gameType;
    private Rules gameRules;
    public enum gameEnum { CONNECTSIX, GOGAME, CONNECTFOUR, GOMOKU }


    //getters for this class
    public User getLoser() { return loser; }
    public User getWinner() { return winner; }
    public int getGameID() { return gameID; }
    public boolean isGameCompleted(){ return gameCompleted; }
    public boolean isViewable() { return viewable; }
    public User[] getPlayers() { return players; }

    //getters for Move class
    public User getLastMovePlayer(){ return currentBoard.getLastMove().getPlayer(); }
    public Timestamp getTimestamp(){ return currentBoard.getLastMove().getTimestamp(); }

    //getters for connect6Rules class
    public User getTurnPlayer(){ return gameRules.currentTurn(); }

    //getters for board class
    public boolean getMutable(){ return currentBoard.getMutable(); }
    public int[] getMoveCoordinates(){
        int[] moveCoordinates = { currentBoard.getLastMove().getX(), currentBoard.getLastMove().getY() };
        return moveCoordinates;
    }
    
    //setters
    public void setLoser(User loser) { this.loser = loser; } //we might need to set this method to private, i'll just leave it public for now for testing
    public void setWinner(User winner) { this.winner = winner; } //we might need to set this method to private, i'll just leave it public for now for testing
    //public void setGameCompleted(){}// not sure if we should make this takes winner and loser and sets them then set gameCompleted var.

    /**
     * Constructor for the Game class, basic instatiation
     * @param playerOne user object class for user one
     * @param playerTwo user object class for user two
     * @param givenGame the game type that the player wants
     */
    private Game(User playerOne, User playerTwo, gameEnum givenGame, int id) {
        gameID = id;
        gameCompleted = false;
        viewable = true;
        players = new User[2];
        players[0] = playerOne;
        players[1] = playerTwo;
        startTime = new Timestamp(System.currentTimeMillis());
        playerOne.setInProgressGames(this);
        playerTwo.setInProgressGames(this);

        switch (givenGame) {
            case CONNECTSIX:
                gameType = givenGame;
                gameRules = new Connect6Rules(playerOne, playerTwo);
                System.out.println("Starting: Connect Six");
            case GOGAME:
                gameType = givenGame;
                System.out.println("Starting: Go");
            case CONNECTFOUR:
                gameType = givenGame;
                System.out.println("Starting: Connect Four");
            case GOMOKU:
                gameType = givenGame;
                System.out.println("Starting: Gomoku");
        }

        currentBoard = new Board(gameRules.getBoardSize(), playerOne, playerTwo);

    }

    /**
     * Constructor for the Game class, if boolean is passed for if game is viewable or not
     * @param isViewable
     */
    public Game(User playerOne, User playerTwo, gameEnum givenGame, boolean isViewable, int id) {
        this(playerOne, playerTwo, givenGame, id);
        viewable = isViewable;
    }

    /**
     * Checks if a given move is legal, and updates the current board state and move history if it is.
     *
     * @param x The x coordinate to be checked for legality
     * @param y The y coordinate to be checked for legality
     * @return returns true if the operation was successful, and false otherwise
     */
    public boolean commitMove(int x, int y){
        if (gameCompleted == true) return false;
        if (!gameRules.isLegal(currentBoard, x, y)) return false;
        if (!isOccupied(x, y)) {
            updateCurrent(x, y);
            return true;
        }
        else {
                return false;
            }
        }

        /**
         * Updates the current board state and move history with a given move.
         * @param x The x coord of the move
         * @param y The y coord of the moe
         */
        private void updateCurrent(int x, int y){
            currentBoard.putMove(x, y, gameRules.currentTurn());
            if(gameRules.gameOver(currentBoard, x, y, gameRules.currentTurn()) == players[0]) {
                winner = players[0];
                loser = players[1];
                gameCompleted = true;
//            viewable = false;
            players[0].addWin();
            players[0].setCompletedGames(this);
            players[0].removeInProgressGame(this);
            players[1].addLoss();
            players[1].setCompletedGames(this);
            players[1].removeInProgressGame(this);

        }
        else if(gameRules.gameOver(currentBoard, x, y, gameRules.currentTurn()) == players[1]) {
            winner = players[1];
            loser = players[0];
            gameCompleted = true;
//            viewable = false;
            players[0].addLoss();
            players[0].setCompletedGames(this);
            players[0].removeInProgressGame(this);
            players[1].addWin();
            players[1].setCompletedGames(this);
            players[1].removeInProgressGame(this);
        }
        gameRules.passTurn();
    }

    /**
     * Checks if a specific place on the board is occupied by another peice
     * @param x The x coordinate to be checked for vacancy
     * @param y The y coordinate to be checked for vacancy
     * @return true or false that the space is occupied
     */
    public boolean isOccupied(int x, int y) {
        if(currentBoard.getPosition(x, y) == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Gets a list of all moves associated with the game. Each move and its data is stored in an arraylist, and each arraylist is then loaded into a larger arraylist that is ordered by when each move was made.
     * @return An ordered 2d arraylist of all moves.
     */
    public ArrayList getMoveList(){
        ArrayList moves = currentBoard.getMovesList();
        HashMap moveData;
        for(int i = 0; i < moves.size(); i++){
            moveData = (HashMap) moves.get(i);
            if(players[0].getUserName().equals(moveData.get("user"))){
                moveData.put("color", "white");
            } else { moveData.put("color", "black"); }
        }
        return moves;
    }

    /**
     * Gets the last move made.
     * @return A map containing move data for the last move made.
     */
    public Map getLastMove(){
        ArrayList moves = currentBoard.getMovesList();
        HashMap moveData = (HashMap) moves.get(moves.size() - 1);
        if(players[0].getUserName().equals(moveData.get("user"))){
            moveData.put("color", "white");
        } else { moveData.put("color", "black"); }
        return moveData;
    }

    /**
     * Returns an map containing the game's surface-level data
     * @return an map containing game data
     */
    public Map<String, Object> getGameData(){
        Map<String, Object> temp = new HashMap<String, Object>(); //TODO: In a future iteration, change this to some sort of ordered map.
        temp.put("gameID", Integer.valueOf(gameID));
        temp.put("gameType", gameType.name());
        temp.put("startTime", startTime.toString());
        temp.put("white", players[0].getUserName());
        temp.put("black", players[1].getUserName());
        temp.put("restricted", Boolean.toString(!viewable));
        temp.put("completed", Boolean.toString(gameCompleted));

        return temp;
    }
}