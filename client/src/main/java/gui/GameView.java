package gui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * The main game window, contains all of the UI.
 */
public class GameView extends JFrame {

    /**
     * Server and Client variables
     */
    private SseEventSource chatSource;
    private SseEventSource moveSource;
    private Client client;
    private int port = 5000;
    private String uri = "http://35.212.254.221:";


    /**
     * pertinent data to the user and the current game
     */
    private String playerName = "";
    private Vector<Hashtable<String, Object>> currentGameMoveList = new Vector<Hashtable<String, Object>>();
    private String currentOpponent = "";
    private String whitePlayerName, blackPlayerName;
    private String pieceColor;
    private String gameType;
    private String gameStartTime;
    private int currentGameID;
    private int currMoveListIndex;
    private boolean currentGameCompleted;
    private boolean currentGameRestricted;
    private String turnPlayer = "";


    /**
     * The panel containing the board
     */
    private BoardView boardView;

    /**
     * Panel on the right side of the board
     */
    private SidePanel sidePanel;

    /**
     * Constructs a new main window for the game.
     */
    public GameView() {
        client = ClientBuilder.newClient();
        currMoveListIndex = 0;
        build();
    }

    /**
     * Sets up the gui
     */
    private void build() {
        setTitle("Go Board Games");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Add the menu bar
        this.setJMenuBar(new MenuBar(this));

        boardView = new BoardView(800, this);

        // The board
        JPanel boardPanel = new JPanel(new BorderLayout());
        boardPanel.add(boardView, BorderLayout.CENTER);

        // The side panel
        sidePanel = new SidePanel(this);

        // Side panel goes in the EAST
        this.add(sidePanel, BorderLayout.EAST);

        // The board panel goes in the center
        this.add(boardPanel, BorderLayout.CENTER);
        this.pack();
        this.setResizable(false);
    }

    void setPlayerName(String playerName){ this.playerName = playerName; }
    String getPlayerName(){ return playerName; }
    String[] getGamesList_Columns(){ return null; }
    String[][] getPlayableGamesList_Rows(){  return null; }
    String[][] getViewableGamesList_Rows() { return null; }
    Vector getCurrentGameMoveList(){ return currentGameMoveList; }
    public BoardView getBoardView(){ return boardView;}
    int getCurrentGameID(){ return currentGameID; }
    String getCurrentOpponent(){ return currentOpponent; }
    String getCurrentGameRestricted(){
        if(currentGameRestricted == true) return "Private";
        else return "Public";
    }

    /**
     * Used to populate a game that already has moves. Called in joinGame.
     * @return an arraylist of maps containing data of Move objects.
     */
    public void setGameMoveList(){

        try {
            String gameMoveListJson = client.target(uri + port)
                    .path("game/viewMoveList/" + Integer.toString(currentGameID) + "/" + playerName)
                    .request("application/json")
                    .get(String.class);

            Gson g = new Gson();
            java.lang.reflect.Type resultType = new TypeToken<Vector<Hashtable<String, Object>>>(){}.getType();
            Vector<Hashtable<String, Object>> gameMoveList = g.fromJson(gameMoveListJson, resultType);
            currentGameMoveList = gameMoveList;
        } catch (Exception e ){
            System.out.println(e.getMessage());
        }
    }

    public void login(String username) {
        Response response = client.target(uri + port)
                .path("game/getUserInfo/" + username)
                .request()
                .post(Entity.entity(null, "application/json"));

        if(response.getStatus() == 404){
            this.addMessage("Login failed. Check for typos or create a new user.");
        } else {
            this.playerName = username;
            Map userData = getUserData();
            sidePanel.updateUserInfoTable(userData);
            this.addMessage("SYSTEM: Login successful. Welcome, " + username + ".");
        }
    }
    /**
     * This method creates a new user with the given username.
     *
     * @param username The desired username
     */
    public void newUser(String username) throws InterruptedException{

        //So we would check if playername == "" and create the user if that condition is true - Aaron
        String regex = "^[a-zA-Z0-9]+$";
        if(!username.matches(regex)){
            addMessage("SYSTEM: Please use Alphanumeric (a-z, A-Z, or 0-9) characters only.");
            return;
        }

        if(username.length() < 3 || username.length() > 20){
            addMessage("SYSTEM: Please enter a name between 3 and 20 characters long.");
            return;
        }

        this.playerName = username;

                //Client client = ClientBuilder.newClient();
        Response resp = client.target(uri + port)
                .path("game/createUser/" + username)
                .request()
                .post(Entity.entity(null, "application/json"));

        if(resp.getStatus() != 200){
            System.out.println(resp.toString());
            //todo: return error
        } else {
            Map userData = getUserData();
            sidePanel.updateUserInfoTable(userData);

            System.out.println("User (" + username + ") has been created.");

        }

    }

    /**
     * This method gets wins, losses and ties for the current player.
     * @return userData a map that holds the number of wins, losses, and ties.
     */
    public Map getUserData(){

        Map<String, Object> userData = new HashMap<>();

        try {
            String responseString = client.target(uri + port)
                    .path("game/getUserInfo/" + playerName)
                    .request("application/json")
                    .get(String.class);
            Gson g = new Gson();
            java.lang.reflect.Type resultType = new TypeToken<Map<String, Object>>() {
            }.getType();
            userData = g.fromJson(responseString, resultType);

            return userData;

        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.out.println(e.toString());
        }

        return userData;
    }

    /**
     * This method creates a new game with the give users, and at the specified access level.
     *
     * @param player2    The second player in the game
     * @param isRestricted The game's access level -- restricted indicates that only participants can view the game.
     */
    public void newGame(String player2, boolean isRestricted) {
        Gson gson = new Gson();

        Map<String, Object> data = new HashMap<>();
        data.put("player1", playerName);
        data.put("player2", player2);
        data.put("isRestricted", isRestricted);
        data.put("gameType", "connect6");

        String jsonString = gson.toJson(data);

        Response resp = client.target(uri + port)
                .path("game/createGame")
                .request("application/json")
                .post(Entity.entity(jsonString, "application/json"));
        System.out.println("player 1 is :" + playerName + " player2 is " + player2);

        if(resp.getStatus() != 200){
            System.out.println(resp.toString());
            //todo: return error
        } else {
            System.out.println("game was created");
            Gson g = new Gson();
            String responseBody = resp.readEntity(String.class);
            Map map  = g.fromJson(responseBody, Map.class);

            //get the current game id after game creation to update currentGameID
            double dblGameID = (double)map.get("gameID");
            currentGameID = (int)dblGameID; //TODO: Does this work?

            newChatBroadcaster(currentGameID); //Creates a new chat broadcaster on the server, associated with the current game id.
            newMoveBroadcaster(currentGameID); //Creates a new move broadcaster on the server, associated with the current game id.

            joinGame(currentGameID);
            boardView.loadBoard(currentGameMoveList, whitePlayerName, blackPlayerName);
        }
    }

    /**
     * Returns a list of all games that the specified user has permission to view
     * @return A map summarizing all authorized game data
     */
    public List<Map<String, Object>> joinableUserGameList(String username){
        List<Map<String, Object>> gameList;

        try {
            String responseString = client.target(uri + port)
                    .path("game/joinableUserGames/" + username)
                    .request("application/json")
                    .get(String.class);
            Gson g = new Gson();
            java.lang.reflect.Type resultType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            gameList = g.fromJson(responseString, resultType);
            return gameList;

        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.out.println(e.toString());
        }
        return null;
    }


    /**
     * Returns a list of all games that a user is a part of
     * @param username The username of the client attempting to view their completed games
     * @return A map summarizing all authorized game data
     */
    public List<Map<String, Object>> viewableUserGameList(String username){
        List<Map<String, Object>> gameList;

        try {
            String responseString = client.target(uri + port)
                    .path("game/viewableUserGames/" + username)
                    .request("application/json")
                    .get(String.class);
            Gson g = new Gson();
            java.lang.reflect.Type resultType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            gameList = g.fromJson(responseString, resultType);
            return gameList;

        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.out.println(e.toString());
        }
        return null;
    }

    /**
     * Retrieves a list of all games
     * @param username The username of the client attempting to view all games
     * @return A map summarizing all authorized game data
     */
    public List<Map<String, Object>> viewAllGames(String username){
        List<Map<String, Object>> allList;

        try {
            String responseString = client.target(uri + port)
                    .path("game/allGames/")
                    .request("application/json")
                    .get(String.class);
            Gson g = new Gson();
            java.lang.reflect.Type resultType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            allList = g.fromJson(responseString, resultType);
            List<Map<String, Object>> nonRestrictedGameList = new ArrayList<Map<String, Object>>();
            for(int i = 0; i < allList.size(); i++){
                boolean viewable = !(Boolean.valueOf(allList.get(i).get("restricted").toString()));
                boolean isPartOfGame = allList.get(i).get("white").equals(playerName) || allList.get(i).get("black").equals(playerName);
                boolean completed = Boolean.valueOf(allList.get(i).get("completed").toString());
                if(completed && isPartOfGame) nonRestrictedGameList.add(allList.get(i));
                else if(viewable && !isPartOfGame){
                    nonRestrictedGameList.add(allList.get(i));
                }
            }
            return nonRestrictedGameList;

        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.out.println(e.toString());
        }
        return null;
    }

    /**
     * Joins a user to a game, updating everything required to play a specified game.
     * @param  gameID the gameID that is wished to be joined
     * @return A map containing the requested game data
     */
    public boolean joinGame(int gameID){
        closeServerConnection(); // Closes chat and move subscriptions to server for previous game.
        Gson g = new Gson();
        String gameDataResponse = client.target(uri + port)
                .path("game/getGame/" + Integer.toString(gameID)+ "/" + playerName)
                .request("application/json")
                .get(String.class);
        java.lang.reflect.Type resultType = new TypeToken<HashMap<String, Object>>(){}.getType();
        Map<String, Object> map = g.fromJson(gameDataResponse, resultType);

        currentGameID = (int)Double.parseDouble(map.get("gameID").toString());
        gameType = map.get("gameType").toString();
        gameStartTime = map.get("startTime").toString();
        currentGameCompleted = Boolean.parseBoolean(map.get("completed").toString());
        currentGameRestricted = Boolean.parseBoolean(map.get("restricted").toString());
        blackPlayerName = map.get("black").toString();
        whitePlayerName = map.get("white").toString();

        if(blackPlayerName.equals(playerName)){
            pieceColor = "black";
            currentOpponent = map.get("white").toString();
        } else {
            pieceColor = "white";
            currentOpponent = map.get("black").toString();
        }
        setGameMoveList();
        setCurrentMoveIndex();
        subscribeToChat(gameID); //subscribes to chat broadcaster
        subscribeToMove(gameID); //subscribes to move broadcaster

        setTurnPlayer();

        sidePanel.populateChatLog(getChat()); //Populate the chat log with messages tied to the game being joined
        sidePanel.updateUserInfoTable(getUserData()); //Update JTable in SidePanel upon joining another game
        boardView.loadBoard(currentGameMoveList, whitePlayerName, blackPlayerName);
        sidePanel.populateMoveListBox();
        boardView.loadBoard(currentGameMoveList, whitePlayerName, blackPlayerName);

        addMessage("SYSTEM: It is " + turnPlayer + "'s turn."); //TODO: Replace with actual system messages
        return true;
    }

    /**
     * Returns the game data for a specified game and user.
     * @param  gameID the gameID that is wished to be joined
     * @return A map containing the requested game data
     */
    public boolean viewGame(int gameID){
        closeServerConnection(); // Closes chat and move subscriptions to server for previous game.
        Gson g = new Gson();
        String gameDataResponse = client.target(uri + port)
                .path("game/getGame/" + Integer.toString(gameID)+ "/" + playerName)
                .request("application/json")
                .get(String.class);
        java.lang.reflect.Type resultType = new TypeToken<HashMap<String, Object>>(){}.getType();
        Map<String, Object> map = g.fromJson(gameDataResponse, resultType);

        currentGameID = (int)Double.parseDouble(map.get("gameID").toString());
        gameType = map.get("gameType").toString();
        currentOpponent = "";
        gameStartTime = map.get("startTime").toString();
        currentGameCompleted = Boolean.parseBoolean(map.get("completed").toString());
        currentGameRestricted = Boolean.parseBoolean(map.get("restricted").toString());
        blackPlayerName = map.get("black").toString();
        whitePlayerName = map.get("white").toString();

        whitePlayerName = map.get("white").toString();
        blackPlayerName = map.get("black").toString();

        setGameMoveList();
        setCurrentMoveIndex();
        subscribeToChat(gameID); //subscribes to chat broadcaster
        subscribeToMove(gameID); //subscribes to move broadcaster

        setTurnPlayer();

//        sidePanel.populateChatLog(getChat()); //Populate the chat log with messages tied to the game being joined
        sidePanel.updateUserInfoTable(getUserData()); //Update JTable in SidePanel upon joining another game
        sidePanel.populateMoveListBox();
        boardView.loadBoard(currentGameMoveList, whitePlayerName, blackPlayerName);

        return true;
    }

    /**
     * Makes a move in the specified game, by the specified player.
     *
     * @param x the x coordinate of the move
     * @param y the y coordinate of the move
     * @param username the username of the player making the move
     * @param gameID the gameID if the game in which the move is being made
     */
    public Result makeMove(int x, int y, String username, int gameID){

        if(!username.equals(turnPlayer)){
            addMessage("SYSTEM: It is not your turn.");
            return Result.NotValid;
        }

        Gson g = new Gson();
        Map m = new HashMap();
        m.put("gameID", gameID);
        m.put("xCoord", x);
        m.put("yCoord", y);
        m.put("player", username);

        String jsonString = g.toJson(m);

        Response resp = client.target(uri + port)
                .path("game/makeMove")
                .request()
                .post(Entity.entity(jsonString, "application/json"));

        if (resp.getStatus() == 200){
            System.out.println("("+username +") Your move in game " + gameID +" is ok");
            setGameMoveList();
            currMoveListIndex = currentGameMoveList.size() - 1;

            return Result.Valid;

        } else if (resp.getStatus() == 401) {
            addMessage(": (" + username + ") Your move in game " + gameID + " is not valid");
            System.out.println("(" + username + ") Your move in game " + gameID + " is not valid");
        } else if (resp.getStatus() == 409){

            addMessage(": this location is occupied");
            System.out.println("this location is occupied");

        } else {
            if(gameID == 0) addMessage("SYSTEM: Create or join a game");
            else {
                System.out.println("(Game " + gameID + ") was not found");
                System.out.println(resp.getStatus());
            }
        }
        return Result.NotValid;
    }

    public enum Result {
        Valid, NotValid
    }

    /**
     * Updates the board to reflect its current state, minus 1 move.
     */
    public boolean previousMove(){
        int temp = currMoveListIndex - 1;
        if(temp < 0) {
            return false;
        }
        else {
            currMoveListIndex = currMoveListIndex - 1;
            Vector<Hashtable<String, Object>> subListForMoves = new Vector<Hashtable<String, Object>>();
            for(int i = 0; i < currMoveListIndex; i++){
                subListForMoves.add(currentGameMoveList.get(i));
            }
            //System.out.println(currentGameMoveList);
            //System.out.println(subListForMoves);
            //System.out.println(currMoveListIndex);
            boardView.loadBoard(subListForMoves, whitePlayerName, blackPlayerName);
            return true;
        }
    }

    /**
     * Updates the board to reflect its current state, plus 1 move.
     */
    public boolean nextMove(){
        int temp = currMoveListIndex + 1;
        if(temp > currentGameMoveList.size() || currentGameMoveList.size() == 0) {
            return false;
        }
        else {
            currMoveListIndex = currMoveListIndex + 1;
            Vector<Hashtable<String, Object>> subListForMoves = new Vector<Hashtable<String, Object>>();
            for(int i = 0; i < currMoveListIndex; i++) {
                subListForMoves.add(currentGameMoveList.get(i));
            }
//            Index);
            boardView.loadBoard(subListForMoves, whitePlayerName, blackPlayerName);
            return true;
        }
    }


    /**
     * Updates the board to reflect the most current move made.
     */
    public void setCurrentMoveIndex(){
        currMoveListIndex = currentGameMoveList.size();
    }

    /**
     * Returns a leader board containing the top 20 players, sorted by their win/loss ratio
     * @return A map containing the leader board data
     */
    public Vector<Hashtable<String, Object>> viewLeaderboard(){
        String leaderboardJson = client.target(uri + port)
                .path("game/viewLeaderboard")
                .request("application/json")
                .get(String.class);
        Gson g = new Gson();
        java.lang.reflect.Type resultType = new TypeToken<Vector<Hashtable<String, Object>>>(){}.getType();
        Vector<Hashtable<String, Object>> leaderboardList = g.fromJson(leaderboardJson, resultType);
        return leaderboardList;
        }

    /**
     * Sends a message.
     * @param message The body text of the message
     */
    public void sendChat(String message){
        Gson g = new Gson();
        Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap.put("gameID", currentGameID);
        messageMap.put("player", playerName);
        messageMap.put("message", message);

        String messageJson = g.toJson(messageMap);
        Response response = client.target(uri + port)
                .path("chat/chatMsg/")
                .request()
                .post(Entity.entity(messageJson, "application/json"));
        System.out.println(response);
        if(response.getStatus() == 200){ System.out.println("Sent"); }
        else if(response.getStatus() == 404){System.out.println("GameID not found");}
        else if(response.getStatus() == 401){System.out.println("Player not found");}
        else{System.out.println("Exception not specified");}
    }

    /**
     * Sends a System message.
     * @param message The body text of the message
     */
    public void sendSystemMessage(String message){
        Gson g = new Gson();
        Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap.put("gameID", currentGameID);
        messageMap.put("message", message);

        String messageJson = g.toJson(messageMap);
        Response response = client.target(uri + port)
                .path("chat/sysChatMsg/")
                .request()
                .post(Entity.entity(messageJson, "application/json"));
        System.out.println(response);
        if(response.getStatus() == 200){ System.out.println("Sent"); }
        else if(response.getStatus() == 404){System.out.println("GameID not found");}
        else{System.out.println("Exception not specified");}
    }

    /**
     * Returns the chat log for the requested game
     *
     * @return A map containing the chat log data for the specified game
     */
    public Vector<Hashtable<String, Object>> getChat(){
        String chatLogJson = client.target(uri + port)
                .path("chat/getChat/" + Integer.toString(currentGameID))
                .request("application/json")
                .get(String.class);
        Gson g = new Gson();
        java.lang.reflect.Type resultType = new TypeToken<Vector<Hashtable<String, Object>>>(){}.getType();
        Vector<Hashtable<String, Object>> chatList = g.fromJson(chatLogJson, resultType);
        return chatList;
    }

    public void addMessage(String message){
        sidePanel.addMessage(message);
    }

    /**
     * Subscribes to the server chat SSE broadcaster
     */
    private void subscribeToChat(int gameID){
        WebTarget target = client.target(uri + port + "/chat/subscribe/" + gameID);
        chatSource = SseEventSource.target(target).build();
        chatSource.register(this::messageReceived);
        chatSource.open();
        System.out.println("Subscribed to Chat");
    }

    /**
     * This class is called whenever the server sends back a chat message.
     * @param event
     */
    private void messageReceived(InboundSseEvent event){
        final String message = event.readData();
        addMessage(message);
        sidePanel.updateUserInfoTable(getUserData());
        System.out.println("Message Received");
    }

    /**
     * Subscribes to the server move broadcaster
     * @param gameID the ID of the game
     */
    private void subscribeToMove(int gameID){
        WebTarget target = client.target(uri + port + "/game/subscribe/" + gameID);
        moveSource = SseEventSource.target(target).build();
        moveSource.register(this::moveReceived);
        moveSource.open();
        System.out.println("Subscribed to moves");
    }

    /**
     * This method is called every time move data is received from the server
     * @param event The sse event received from the server
     */
    private void moveReceived(InboundSseEvent event){
        setGameMoveList();
        boardView.loadBoard(currentGameMoveList, whitePlayerName, blackPlayerName);
        setCurrentMoveIndex();
        setTurnPlayer();
        addMessage("SYSTEM: It is now " + turnPlayer + "'s turn.");
        sidePanel.populateMoveListBox();


        Gson g = new Gson();
        String gameDataResponse = client.target(uri + port)
                .path("game/getGame/" + Integer.toString(currentGameID)+ "/" + playerName)
                .request("application/json")
                .get(String.class);
        java.lang.reflect.Type resultType = new TypeToken<HashMap<String, Object>>(){}.getType();
        Map<String, Object> map = g.fromJson(gameDataResponse, resultType);

        currentGameCompleted = Boolean.valueOf(map.get("completed").toString());
        //check that current game is completed and that the last player who made a turn is this client.
        //This is so that system messages only send once.
        String lastPlayerName = currentGameMoveList.get(currentGameMoveList.size()-1).get("user").toString();
        if(currentGameCompleted && lastPlayerName.equals(playerName))
            sendSystemMessage("Winner is "+playerName+"\nLoser is "+currentOpponent + "\n\n");
    }

    /**
     * This method closes all SseEventSources. It should be called whenever we subscribe to a new game.
     */
    private void closeServerConnection(){
        if(chatSource != null){ chatSource.close(); }
        if(moveSource != null){ moveSource.close(); }
    }

    /**
     * This method creates a new chat broadcaster on the server, associated with the given game id.
     * @param gameID The id of the game
     */
    private void newChatBroadcaster(int gameID){
        Response moveResponse = client.target(uri + port)
                .path("chat/newChatBroadcaster/" + gameID)
                .request()
                .post(Entity.entity(null, "application/json" ));
    }

    /**
     * This method creates a new move broadcaster on the server, associated with the given game id.
     * @param gameID The id of the game.
     */
    private void newMoveBroadcaster(int gameID){
        Response moveResponse = client.target(uri + port)
                .path("game/newMoveBroadcaster/" + gameID)
                .request()
                .post(Entity.entity(null, "application/json" ));
    }

    /**
     * Set's the turn player to the player who's turn it is
     */
    private void setTurnPlayer(){
        String responseString = client.target(uri + port)
                .path("game/getTurnPlayer/" + currentGameID)
                .request("application/json")
                .get(String.class);

        Gson gson = new Gson();

        Map temp = gson.fromJson(responseString, Map.class);
        turnPlayer = (String) temp.get("user");
    }
}
