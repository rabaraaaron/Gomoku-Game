package controller;

import game.Game;
import messenger.Message;
import user.User;

import java.util.*;

/**
 * A controller class for the game.
 *
 */
public class GameController {
    Map<Integer, Game> gameList;
    Map<String, User> userList;
    List<User> connect6Rankings = new ArrayList<User>();
    int lastGameID; //used to keep track of the most recently assigned game id.

    /**
     * Constructs a new GameController object.
     */
    public GameController() {
        gameList = new HashMap<Integer, Game>();  //We may want to consider making these Hashtables instead of maps.
        userList = new HashMap<String, User>();
        lastGameID = 0;
    }

    /**
     * Checks if a username is taken. If it is free, creates a new user object with the given username, and puts it in the map of all users.
     *
     * @param username a String object containing the desired username.
     * @return whether or not the username was available.
     */
    public boolean newUser(String username) {
        if (userList.containsKey(username)) {
            throw new NoSuchElementException(username);
        } else {
            User u = new User(username);
            userList.put(username, u);
            return true;
        }
    }

    /**
     * Creates a new Game object and adds it to the map of all games.
     *
     * @param playerA The first player
     * @param playerB the second player
     */
    public Map<String, Object> newGame(String playerA, String playerB, boolean restricted) {
        User a;
        User b;

        if (userList.containsKey(playerA)) {
            a = userList.get(playerA);
        } else {
            throw new NoSuchElementException(playerA);
        }

        if (userList.containsKey(playerB)) {
            b = userList.get(playerB);
        } else {
            throw new NoSuchElementException(playerB);
        }

        Game g = new Game(a, b, Game.gameEnum.CONNECTSIX, !restricted, lastGameID + 1);

        gameList.put(g.getGameID(), g);
        lastGameID = lastGameID + 1;
        Map map = g.getGameData();

        return map;
    }

    /**
     * Retrieves a string representation of all in-progress games for a given user.
     *
     * @param player The player whose games we are looking at.
     * @return A string representing all in-progress games
     */
    public HashMap<Integer, Game> viewInProgress(String player) {
        User u;
        if (userList.containsKey(player)) {
            u = userList.get(player);
        } else {
            throw new NoSuchElementException(player);
        }
        HashMap<Integer, Game> map = u.getInProgress();
        return map;
    }

    /**
     * Retrieves a list of completed games for the specified player.
     *
     * @param player the player in question
     * @return a string representing all completed games
     */
    public HashMap<Integer, Game> viewComplete(String player) {
        User u;
        if (userList.containsKey(player)) {
            u = userList.get(player);
        } else {
            throw new NoSuchElementException(player);
        }
        HashMap<Integer, Game> map = u.getCompleted();
        return map;
    }

    /**
     * Checks if it is the given players turn, and if it is, makes the specified move.
     *
     * @param x      The move's x coordinate.
     * @param y      the move's y coordinate
     * @param player the username player making the move
     * @param gameID a String containing the gameID of the game in which the move is being made
     * @return the next player in the turn order.
     */
    public boolean makeMove(int x, int y, String player, int gameID) {
        Game g;

        if (!userList.containsKey(player)) throw new NoSuchElementException(player);
        if (gameList.containsKey(gameID)) {
            g = gameList.get(gameID);
        } else {
            throw new NoSuchElementException("Game ID: " + Integer.toString(gameID));
        }

        if (g.getTurnPlayer().getUserName().equals(player)) {
            return g.commitMove(x, y);
        } else {
            throw new IllegalArgumentException(player);
        }
    }

//    /**
//     * Seeds the system with a set of pre-determined users, and randomly assigns those
//     * users to between 5 and 20 games
//     */
//    public void seed() {
//        userList.clear();
//        gameList.clear();
//        lastGameID = 0;
//        ArrayList<String> seednames = new ArrayList<String>(Arrays.asList("mike", "amanda", "sarah", "jo", "ben", "nicki"));
//
//        for (int i = 0; i < seednames.size(); i++) {
//            newUser(seednames.get(i));
//
//        }
//
//        newGame(seednames.get(0), seednames.get(1), true);
//        newGame(seednames.get(0), seednames.get(1), true);
//        newGame(seednames.get(2), seednames.get(3), true);
//        newGame(seednames.get(4), seednames.get(5), true);
//        newGame(seednames.get(0), seednames.get(5), true);
//
//        Game game = gameList.get(1);
//        User user = game.getTurnPlayer();
//
//        String userName = user.getUserName();
//        System.out.println(" it is " + userName + "'s turn to make a move <<<<<<<<<<<-----------");
//
//        makeMove(12, 12, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(3, 12, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(3, 2, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(4, 5, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(4, 6, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(7, 4, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(4, 8, game.getTurnPlayer().getUserName(), game.getGameID());
//
//        game = gameList.get(2);
//
//        makeMove(12, 12, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(4, 5, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(3, 12, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(4, 6, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(7, 4, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(4, 8, game.getTurnPlayer().getUserName(), game.getGameID());
//
//        game = gameList.get(3);
//
//        makeMove(15, 12, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(10, 10, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(14, 12, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(10, 11, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(18, 4, game.getTurnPlayer().getUserName(), game.getGameID());
//        makeMove(8, 9, game.getTurnPlayer().getUserName(), game.getGameID());
//
//    }

    /**
     * Returns a game object for the specified gameID.
     *
     * @param gameID of game
     * @return The associated game object.
     */
    public Game getGame(int gameID) {
        if(gameList.containsKey(gameID)){
            Game g = gameList.get(gameID);
            return g;
        } else { throw new NoSuchElementException("Game ID: " + Integer.toString(gameID));}
    }

    /**
     * Returns a game object for the specified gameID.
     *
     * @param user of game
     * @return The associated user object.
     */
    public Map<String, Object> getUserInfo(String user) {
        if(userList.containsKey(user)){
            User u = userList.get(user);
            return u.getUserData();
        } else { throw new NoSuchElementException("User : " + user);}
    }

    //TODO: Write private method that verifies all permissions.
    /**
     * Returns an arraylist containing all move data for the specified game.
     *
     * @param gameID A string representation of the game id.
     * @return An ordered 2d Arraylist of move data. Each sub-list contains a move timestamp, x and y coordinates, the player, and the piece color, in that order.
     */
    public ArrayList getMoveList(int gameID, String viewerName) {
        if(!gameList.containsKey(gameID)) { throw new NoSuchElementException(Integer.toString(gameID)); } //Check if the game exists

        Game g = gameList.get(gameID);
        if(g.isViewable()){ return g.getMoveList(); }
        if(!userList.containsKey(viewerName)) { throw new NoSuchElementException(viewerName); } //Check if the user exists

        //Check if the player is in the game
        if (isPlayerInTheGame(viewerName, gameID)) {
            return gameList.get(gameID).getMoveList();
        } else {
            throw new IllegalArgumentException(viewerName);
        }
    }

//    /**
//     * Gets move data for the last move made in the given game
//     * @param gameID The gameID of the given game
//     * @return A map containing game data for the given game.
//     */
//    public Map getLastMove(int gameID) {
//        if(!gameList.containsKey(gameID)) { throw new NoSuchElementException(Integer.toString(gameID)); } //Check if the game exists
//        Game g = gameList.get(gameID);
//        return g.getLastMove();
//    }

    /**
     * Checks if the user is one of the players in a given game.
     * @param gameID -> the ID of the game that needs to be cheched.
     * @param playerName -> is the userName will be used to compare to the
     *                  two player names found in the game.
     *
     * @return true if the name matches, and false if it does't not.
     */
    public boolean isPlayerInTheGame(String playerName, int gameID) {
        boolean playerIsFound = playerExists(playerName);
        boolean gameIsFound = gameExists(gameID);

        if (playerIsFound && gameIsFound) {
            Game game = gameList.get(gameID);
            User[] players = game.getPlayers();

            if (players[0].getUserName().equals(playerName) || players[1].getUserName().equals(playerName)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if the player exists in the userList.
     * @param playerName -> the userName will be used to check to compaire with keys
     *                   in the userList map.
     * @return boolean
     */
    public boolean playerExists(String playerName){
        if(userList.containsKey(playerName)){ return true; } return false;
    }

    /**
     * Checks if the game exists in the userList.
     * @param gameID of the game.
     * @return boolean
     */
    public boolean gameExists(int gameID){
        if(gameList.containsKey(gameID)){ return true; } return false;
    }

    /**
     * Adds a single message to the chat log
     * @param gameID ID of the game
     * @param sendingPlayer player sending the message
     * @param message String of the message
     */
    public void sendChat(int gameID, String sendingPlayer, String message) {

        boolean playerInTheGame = isPlayerInTheGame(sendingPlayer, gameID);
        boolean playerExists = playerExists(sendingPlayer);
        boolean gameExists = gameExists(gameID);

        if (gameExists){
            if(playerExists){
                if(playerInTheGame){
                    gameList.get(gameID).CHAT.addUserMessage(message, userList.get(sendingPlayer));
                } else {
                    throw new IllegalArgumentException(sendingPlayer); }
            } else {
                throw new NoSuchElementException(sendingPlayer); }
        } else {
            throw new NoSuchElementException(Integer.toString(gameID)); }
    }

    /**
     * Adds a single System message to the chat log
     * @param gameID ID of the game
     * @param message String of the message
     */
    public void sendSystemMessage(int gameID, String message) {
        boolean gameExists = gameExists(gameID);

        if (gameExists){
           gameList.get(gameID).CHAT.addSystemMessage(message);
        } else {
            throw new NoSuchElementException(Integer.toString(gameID)); }
    }

    /**
     * Retrieves the whole chat log
     * @param gameID ID of the game
     * @return a list of the chat log
     */
    public Vector<Hashtable<String, Object>> getChatList(int gameID){
        if(gameExists(gameID)) {
            Vector<Message> tempVec = gameList.get(gameID).CHAT.getChatlog();
            Vector<Hashtable<String, Object>> chatListVec = new Vector<Hashtable<String, Object>>();
            Hashtable<String, Object> tempTable = new Hashtable<String, Object>();
            for(int i = 0; i < tempVec.size(); i++){
                tempTable.clear();
                tempTable.put("sender", tempVec.get(i).getSender());
                tempTable.put("words", tempVec.get(i).getWords());
                tempTable.put("time", tempVec.get(i).getTimestamp());
                chatListVec.add(tempTable);
            }
            return chatListVec;
        }
        else{ throw new NoSuchElementException(Integer.toString(gameID));}
    }

    /**
     * Retrieves game data for the specified gameID
     * @param gameID of the game in question
     * @return Arraylist containing game data
     */
    public Map<String, Object> gameData(int gameID, String user){

        if(gameExists(gameID)){
            if(playerExists(user)){
                if(isPlayerInTheGame(user, gameID)){
                    return gameList.get(gameID).getGameData();
                } else if(gameList.get(gameID).isViewable()){
                    return gameList.get(gameID).getGameData();
                }
                else {
                    throw new IllegalArgumentException(user); }
            }else { throw new NoSuchElementException(user); }
        }else { throw new NoSuchElementException("Game ID: " + Integer.toString(gameID)); }
    }

    /**
     * Retrieves all game data for all games.
     *
     * @return Arraylist containing all gamedata for all games.
     */
    public ArrayList getAllGameData(){
        ArrayList list = new ArrayList();
        gameList.forEach((k, v) -> list.add(v.getGameData()));

        return list;
    }

    /**
     * Gets all game data for the specified user
     * @param player the user whose games we are looking at
     * @return An arraylist containing all gamedata for the specified user.
     */
    public ArrayList getUserViewableGameData(String player){

        if(playerExists(player)){
            ArrayList list = new ArrayList();
            userList.get(player).getInProgress().forEach((k, v) -> list.add(gameList.get(k).getGameData()));
            userList.get(player).getCompleted().forEach((k, v) -> list.add(gameList.get(k).getGameData()));
            return list;
        } else { throw new NoSuchElementException(player); }
    }

    /**
     * Gets all game data for the specified user
     * @param player the user whose games we are looking at
     * @return An arraylist containing all gamedata for the specified user.
     */
    public ArrayList getUserJoinableGameData(String player){

        if(playerExists(player)){
            ArrayList list = new ArrayList();
            userList.get(player).getInProgress().forEach((k, v) -> list.add(gameList.get(k).getGameData()));
            return list;
        } else { throw new NoSuchElementException(player); }
    }

    /**
     * Retrieves the username of the turn player.
     * @param gameID The game id of the game in question
     * @return A string containing the username of the turn player.
     */
    public String getTurnPlayer(int gameID){
        if(gameList.containsKey(gameID)){
            return gameList.get(gameID).getTurnPlayer().getUserName();
        } else {
            throw new NoSuchElementException(Integer.toString(gameID));
        }
    }

    /**
     * Gets the leaderboard in form of a list of Maps.
     * @return an ordered rankings list of maps with user maps and their data as values
     */
    public Vector<Hashtable<String, Object>> getLeaderboard(){
        connect6Rankings.clear();
        List<User> users = new ArrayList<User>(userList.values());
        for(int i = 0; i < users.size(); i++){

            updateConnect6Rankings(users.get(i));
        }
        return toVectorOfTables(connect6Rankings);
    }

    /**
     * This method sorts the connect6 Rankings by taking in a user and adding them
     * where they belong in the list, or not adding them.
     * @param user The user that will either be inserted or not into the top 20 ranking
     */
    public void updateConnect6Rankings(User user){
        //TODO: Place users correctly when they have the same stats.
        //TODO: Seniority should be implemented. When dealing with this.
        if(connect6Rankings.isEmpty()) connect6Rankings.add(user);
        else if(connect6Rankings.size() < 20){
            for(int i = 0; i < connect6Rankings.size(); i++){
                if(compareUserWinPercentages(connect6Rankings.get(i), user).equals(user)){
                    connect6Rankings.add(i, user);
                    break;
                }
                else if(i + 1 == connect6Rankings.size()){
                    connect6Rankings.add(user);
                    break;
                }
            }
        }
        else{
            for(int i = 0; i < connect6Rankings.size(); i++){
                if(compareUserWinPercentages(connect6Rankings.get(i), user).equals(user)){
                    connect6Rankings.add(i, user);
                    if(connect6Rankings.size() > 20) connect6Rankings.remove(20);
                    break;
                }
            }
        }
    }

    /**
     * Compares win percentage between users to see who is higher on the rankings
     * @param user1 First user (Existing user in the leaderboard list)
     * @param user2 Second user (New user to potentially be inserted into leaderboard list)
     * @return The user that has a better record, or user that is to be higher on leaderboard
     */
    private User compareUserWinPercentages(User user1, User user2){
        double u1WinPercentage = (double)(user1.getWins() + user1.getTies()/2) / ((user1.getWins() + user1.getLosses() + user1.getTies()/2));
        double u2WinPercentage = (double)(user2.getWins() + user2.getTies()/2) / ((user2.getWins() + user2.getLosses() + user2.getTies()/2));

        if(Double.isNaN(u1WinPercentage)) u1WinPercentage = 0.0;
        if(Double.isNaN(u2WinPercentage)) u2WinPercentage = 0.0;

        if(u1WinPercentage > u2WinPercentage) return user1;
        else if(u1WinPercentage < u2WinPercentage) return user2;
        else{
            if(user1.getWins() > user2.getWins()) return user1;
            else if(user1.getWins() < user2.getWins()) return user2;
            else if(user1.getLosses() > user2.getLosses()) return user2;
            else if(user1.getLosses() < user2.getLosses()) return user1;
            else return user1; //case: records are the exact same -> send user that is already on the leaderboard
        }
    }

     /**
     * Creates a list containing maps of users and their corresponding data
     * @param user arraylist to be iterated through
     * @return 2D String representation of the leaderboard in the format of {rank&username, wins, losses, ties} per rank
     */
    private Vector<Hashtable<String, Object>> toVectorOfTables(List<User> user){

        Vector<Hashtable<String, Object>> leaderboardVector = new Vector<Hashtable<String, Object>>();

        for(int i=0 ; i<connect6Rankings.size(); i++){
            Hashtable<String, Object> userMap = new Hashtable<String, Object>();
            userMap.put("username", user.get(i).getUserName());
            userMap.put("wins", user.get(i).getWins());
            userMap.put("losses", user.get(i).getLosses());
            userMap.put("ties", user.get(i).getTies());
            leaderboardVector.add(userMap);
        }
        return leaderboardVector;
    }
}

