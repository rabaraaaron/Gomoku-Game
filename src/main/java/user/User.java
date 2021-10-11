package user;

import game.Game;

import java.util.HashMap;
import java.util.Map;

/**
 * User Class
 *
 * This class models a user, storing the IDs of their associated games, a win/loss/tie ratio, as well as other relevant user data and operations.
 */
public class User {
    private String userName;
    private HashMap<Integer, Integer> inProgressGames = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> completedGames = new HashMap<Integer, Integer>();
    private int wins=0;
    private int losses=0;
    private int ties=0;

    //constructor
    public User(String userName) {
        this.userName = userName;
    }

   //setters
    public void addWin() { this.wins++; }
    public void addLoss() { this.losses++; }
    public void addTie() { this.ties++; }

    public void setInProgressGames( Game g) {
        inProgressGames.put(g.getGameID(), g.getGameID());
    }
    public void removeInProgressGame(Game g){inProgressGames.remove(g.getGameID());}
    public void setCompletedGames( Game g) { completedGames.put(g.getGameID(), g.getGameID()); }

    //getters
    public String getUserName() { return userName; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getTies() { return ties; }

    //returns map for viewing other games in the UI
    public HashMap getInProgress(){ return inProgressGames; }
    //returns map for viewing complete games in the ui
    public HashMap getCompleted(){ return completedGames; }
    //returns an in progress game
    public int getActiveGame(int gameID){ return inProgressGames.get(gameID); }

    public Map<String, Object> getUserData() {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("username", userName);
        temp.put("wins", Integer.valueOf(wins));
        temp.put("losses", Integer.valueOf(losses));
        temp.put("ties", Integer.valueOf(ties));

        return temp;
    }
}
