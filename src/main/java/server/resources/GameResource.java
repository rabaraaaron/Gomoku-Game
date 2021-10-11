package server.resources;

import com.google.gson.Gson;
import controller.GameController;
import controller.GameControllerManager;
import game.Game;
import user.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.*;

/**
 * A resource class for the server and GameController
 */

@Path("game")
public class GameResource {
    GameController controller = GameControllerManager.getController();
    private Sse sse;
    private static Hashtable<Integer, SseBroadcaster> moveBroadcasters = null;

    public GameResource(@Context final Sse sse){

        this.sse = sse;
        if(moveBroadcasters == null){
            this.moveBroadcasters = new Hashtable<Integer, SseBroadcaster>();
        }
    }

    @POST
    @Path("newMoveBroadcaster/{gameID}")
    public Response newMoveBroadcaster(@PathParam("gameID") int gameID){
        if(moveBroadcasters.containsKey(Integer.valueOf(gameID))){
            return Response.ok("Move broadcaster already exists.").build();
        }
        moveBroadcasters.put(Integer.valueOf(gameID), sse.newBroadcaster());
        return Response.ok().build();
    }

    @GET
    @Path("subscribe/{gameID}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink eventSink, @PathParam("gameID") int gameID){
        this.moveBroadcasters.get(Integer.valueOf(gameID)).register(eventSink);
    }

    // Seed with test data.
//    @DELETE
//    @Path("_seed")
//    @Produces("text/plain")
//    public Response clearListAndSeed() {
//
//        try{
//            controller.seed();
//            return Response.ok("3 games, 6 users and 18 moves  have been created.").build();
//        } catch (IllegalArgumentException e){
//            System.out.println( "Error with seeding : " + e.toString());
//            return Response.status(404).build();
//        }
//    }

    /**
     * Calls controller to create a new user.
     * @param userName is the desired username in json format.
     * @return 200 OK response if the method executes.
     */
    @POST
    @Path("createUser/{userName}")
    public Response createUser(@PathParam("userName") String userName) {
        boolean creationSuccess = false;
        try{
            creationSuccess = controller.newUser(userName);
        } catch (NoSuchElementException e ){
            return Response.status(406).build();
        }

        if(creationSuccess){
            return Response.ok().build();
        } else {
            return Response.status(406).build();
        }
    }

    /**
     * Calls controller to create a new game.
     * @param data a json object containing the gameType, and the usernames for
     * players 1 and 2.
     * @return 200 OK response if the method executes
     */
    @POST
    @Path("createGame")
    @Produces("application/json")
    public Response createGame(String data) {
        Gson g = new Gson();
        Map<String,Object> m = g.fromJson(data, Map.class);
        String gameType = m.get("gameType").toString();
        String player1 = m.get("player1").toString();
        String player2 = m.get("player2").toString();
        boolean isRestricted = (boolean) m.get("isRestricted");

        //check if the player is trying to play against him self by passing his name as player 1 an player 2
        if(player1.equals(player2)) {
           return Response.status(406).build();
        }else {
            try{
                Map map = controller.newGame(player1, player2, isRestricted);

                return Response.ok(g.toJson(map)).build();
            } catch(NoSuchElementException e) {
                return Response.status(406).build();
            }
        }

    }

    /**
     * Calls controller to view the leaderboard.
     * @return 200 OK response if a recognizable gameType is passed.
     * 404 Not Found if an unrecognizable gameType is passed.
     */
    @GET
    @Path("viewLeaderboard")
    @Produces("application/json")
    public Response viewLeaderboard() {
        Gson g = new Gson();
        Vector<Hashtable<String, Object>> leaderboard = controller.getLeaderboard();
        return Response.ok(g.toJson(leaderboard)).build();
    }

    /** Calls controller to view a private game that you are a part of
     * @param gameID a json object containing the gameID of the desired game to join or view, and
     * @param user username of the user wishing to join or view the game.
     * @return 200 OK response if the method executes and a json containing the list of moves in the game.
     *         404 Not Found if an unrecognizable game is is found
     *        401 Unauthorized - if the player does not exist.
     *        403 Forbidden - if the user is trying to access a private game which he/she is not part of.
     */
    @GET
    @Path("viewMoveList/{gameID}/{user}")
    @Produces("application/json")
    public Response viewMoveList(@PathParam("gameID") int gameID, @PathParam("user") String user) {

        try {
            ArrayList moveList = controller.getMoveList(gameID, user);
            return Response.ok(new Gson().toJson(moveList)).build();

            } catch (IllegalArgumentException i) {
                return Response.status(403).build();

            } catch (NoSuchElementException n){
                if(n.getMessage().equals(Integer.toString(gameID))){
                return Response.status(404).build();
                }
            if(n.getMessage().equals(user)){

                return Response.status(401).build();
            }
        }
        return null;
    }

    /** Calls controller to view a private game that you are a part of
     * @param gameID a json object containing the gameID of the desired game to join or view, and
     * @param user username of the user wishing to join or view the game.
     * @return 200 OK response if the method executes and a json containing the list of moves in the game.
     *         404 Not Found if an unrecognizable game is is found
     *        401 Unauthorized - if the player does not exist.
     *        403 Forbidden - if the user is trying to access a private game which he/she is not part of.
     */
    @GET
    @Path("getGame/{gameID}/{user}")
    @Produces("application/json")
    public Response getGame(@PathParam("gameID") int gameID, @PathParam("user") String user) {

        try {
            Map gameDataList = controller.gameData(gameID, user);
            return Response.ok(new Gson().toJson(gameDataList)).build();

        } catch (IllegalArgumentException i) {
            return Response.status(403).build();

        } catch (NoSuchElementException n){
            if(n.getMessage().equals(Integer.toString(gameID))){
                return Response.status(404).build();
            }
            if(n.getMessage().equals(user)){
                return Response.status(401).build();
            }
        }
        return null;
    }

    /** Calls controller to get user information
     * @return 200 OK response if the method executes and a json containing the information of the requested user.
     *         404 Not Found: if the user was not found in the list.
     */
    @GET
    @Path("getUserInfo/{user}")
    @Produces("application/json")
    public Response getUserInfo(@PathParam("user") String user) {
        try{
            Map userInfo = controller.getUserInfo(user);
            Gson gson = new Gson();
            return Response.ok(gson.toJson(userInfo)).build();
        } catch(NoSuchElementException n){
            return Response.status(404).build(); // Return 404 if the username does not exist.
        }
    }

    /**
     * Gets the username of the player who's turn it currently is.
     * @param gameID id of the game in question
     * @return String containing the username of the player who's turn it is.
     */
    @GET
    @Path("getTurnPlayer/{gameID}")
    @Produces("application/json")
    public Response getTurnPlayer(@PathParam("gameID") int gameID){
        try{
            String username = controller.getTurnPlayer(gameID);
            Map m = new HashMap<String, String>();
            m.put("user", username);
            Gson gson = new Gson();
            return Response.ok(gson.toJson(m)).build();
        } catch (NoSuchElementException n){
            return Response.status(404).build();
        }
    }

    /** Calls controller to view a list of games
     * @return 200 OK response if the method executes and a json containing the gametype and players involved.
     *         404 Not Found if an list is found
     */
    @GET
    @Path("allGames")
    @Produces("application/json")
    public Response viewAllGames() {

        ArrayList gameList = controller.getAllGameData();
        Gson gson = new Gson();
        return Response.ok(gson.toJson(gameList)).build();

    }

    /** Calls controller to view a list of games for a single player
     * @param user is the usernam of the player
     * @return 200 OK response if the method executes and a json containing the list of games.
     *         404 Not Found if the player is.
     */
    @GET
    @Path("viewableUserGames/{user}")
    @Produces("application/json")
    public Response viewableUserGames(@PathParam("user") String user) {
        try {
            ArrayList userGames = controller.getUserViewableGameData(user);
            Gson gson = new Gson();
            return Response.ok(gson.toJson(userGames)).build();
        } catch (IllegalArgumentException i) {
            return Response.status(403).build();

        } catch (NoSuchElementException n){
            return Response.status(401).build();
        }
    }

    /** Calls controller to view a list of joinable games for a single player
     * @param user is the usernam of the player
     * @return 200 OK response if the method executes and a json containing the list of games.
     *         404 Not Found if the player is.
     */
    @GET
    @Path("joinableUserGames/{user}")
    @Produces("application/json")
    public Response joinableUserGames(@PathParam("user") String user) {
        try {
            ArrayList userGames = controller.getUserJoinableGameData(user);
            Gson gson = new Gson();
            return Response.ok(gson.toJson(userGames)).build();
        } catch (IllegalArgumentException i) {
            return Response.status(403).build();

        } catch (NoSuchElementException n){
            return Response.status(401).build();
        }
    }

    /**
     * Calls controller to make a move
     * @param data is the json object containing the x and y coordinates of a single move and the player making it.
     * @return 200 OK - if the move is legal.
     *         401 Conflict - if the move was made on a location where a piece already exists.
     *         403 Forbidden - If the player is making a move when it is not his/her turn.
     *                        If the move coordinates do not exist on the board.
     */
    @POST
    @Path("makeMove")
    @Produces("text/plain")
    public Response makeMove(String data) {
        Gson g = new Gson();
        Map m = g.fromJson(data, Map.class);


        int gameID = ((Double)m.get("gameID")).intValue();
        int xCoord = ((Double)m.get("xCoord")).intValue();
        int yCoord = ((Double)m.get("yCoord")).intValue();
        String user = (String)m.get("player");


        try {
            //Checks if the game exists -- responds with 404 if it doesn't.
            Game game = controller.getGame(gameID);
            if(game == null){ return Response.status(404).build(); }

            //Checks if the given user is the turn player -- responds with 401 if they aren't.
            User u = game.getTurnPlayer();
            if(!u.getUserName().equals(user)){ return Response.status(401).build(); }

            //Checks if the move was successfully made -- responds with 409 if it wasn't.
            boolean moveMade = controller.makeMove(xCoord, yCoord, user, gameID);
            if(!moveMade){ return Response.status(409).build(); }

            HashMap moveData = (HashMap)game.getLastMove();
            xCoord = (int) moveData.get("x");
            yCoord = (int) moveData.get("y");
            String time = (String) moveData.get("time");
            user = (String) moveData.get("user");
            String color = (String) moveData.get("color");
            String moveResponse = xCoord + " " + yCoord + " " + time + " " + user + " " + color;

            //Broadcasts an SSE containing the move data
            final OutboundSseEvent event = sse.newEventBuilder()
                    .name("move")
                    .data(String.class, moveResponse)
                    .build();
            moveBroadcasters.get(Integer.valueOf(gameID)).broadcast(event);

            //Responds OK, stating that the move was made successfully.
            return Response.ok("Move made at " + xCoord + ", " + yCoord).build();

        } catch (Exception e ){
            e.printStackTrace();
            System.out.println("error from createMove API: " + e.toString() );
            return Response.status(404).build();
        }
    }
}
