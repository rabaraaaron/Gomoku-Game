package server.resources;

import controller.GameController;
import controller.GameControllerManager;

import javax.inject.Singleton;
import com.google.gson.Gson;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

@Singleton
@Path("chat")
public class ChatResource {
    private GameController gc = GameControllerManager.getController();
    private Sse sse;
    private static Hashtable<Integer, SseBroadcaster> gameBroadcasters = null;

    public ChatResource(@Context final Sse sse){
        this.sse = sse;
        if(gameBroadcasters == null){
            this.gameBroadcasters = new Hashtable<Integer, SseBroadcaster>();
        }
    }

    /**
     * Creates a new broadcaster for a specified game. This method MUST be called after creating a game, or the chat window will not function.
     * @param gameID The gameID
     * @return 200 if the broadcaster is created successfully.
     */
    @POST
    @Path("newChatBroadcaster/{gameID}")
    public Response newChatBroadcaster(@PathParam("gameID") int gameID){
        if(gameBroadcasters.containsKey(Integer.valueOf(gameID))){
            return Response.ok("Chat broadcaster already exists.").build();
        }
        gameBroadcasters.put(Integer.valueOf(gameID), sse.newBroadcaster());
        return Response.ok().build();
    }

    /**
     * Subscribes to a specified game's chat.
     *
     * @param eventSink event sink to register to broadcaster
     * @param gameID gameID of the desired chat window
     */
    @GET
    @Path("subscribe/{gameID}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink eventSink, @PathParam("gameID") int gameID){
        this.gameBroadcasters.get(Integer.valueOf(gameID)).register(eventSink);
    }

   /**
     * Calls controller to add a message to chat log
     * @param data is a json object containing the message that the user is sending to chat box.
     * @return  200 OK - When successful.
     *          403 Forbidden-If the player sending the message is not one of the players in the game.
     */
    @POST
    @Path("chatMsg")
    @Produces("text/plain")
    public Response sendChatMsg(String data) {
        Gson g = new Gson();
        Map m = g.fromJson(data, Map.class);
        int gameID = (int)Double.parseDouble(m.get("gameID").toString());
        String sendingPlayer = m.get("player").toString();
        String message = m.get("message").toString();

        try{
            gc.sendChat(gameID, sendingPlayer, message);
            final OutboundSseEvent event = sse.newEventBuilder()
                    .name("message")
                    .data(String.class, sendingPlayer + ": " + message)
                    .build();
            gameBroadcasters.get(Integer.valueOf(gameID)).broadcast(event);
            return Response.ok(sendingPlayer + ": " + message).build();

        } catch (IllegalArgumentException i) {
            return Response.status(403).build(); //Denies permissions if player isn't allowed to send chat

        } catch (NoSuchElementException n){
            if(n.getMessage().equals(Integer.toString(gameID))){
                return Response.status(404).build(); //Returns error if game does not exist
            }
            if(n.getMessage().equals(sendingPlayer)){
                return Response.status(401).build(); //Returns error if user does not exist
            }
        }
        return Response.status(418).build(); //Returns generic non-500 error
    }

    /**
     * Calls controller to add a message to chat log
     * @param data is a json object containing the message that the user is sending to chat box.
     * @return  200 OK - When successful.
     *          403 Forbidden-If the player sending the message is not one of the players in the game.
     */
    @POST
    @Path("sysChatMsg")
    @Produces("text/plain")
    public Response sendSystemChatMsg(String data) {
        Gson g = new Gson();
        Map m = g.fromJson(data, Map.class);
        int gameID = (int)Double.parseDouble(m.get("gameID").toString());
        String message = m.get("message").toString();

        try{
            gc.sendSystemMessage(gameID, message);
            final OutboundSseEvent event = sse.newEventBuilder()
                    .name("message")
                    .data(String.class,  "System: " + message)
                    .build();
            gameBroadcasters.get(Integer.valueOf(gameID)).broadcast(event);
            return Response.ok(message).build();

        } catch (NoSuchElementException n){
            if(n.getMessage().equals(Integer.toString(gameID))){
                return Response.status(404).build(); //Returns error if game does not exist
            }
        }
        return Response.status(418).build(); //Returns generic non-500 error
    }

    /** Calls controller to view the whole chat log
     * @return 200 OK response if the method executes and a json containing the chat log
     *
     */
    @GET
    @Path("getChat/{gameID}")
    @Produces("application/json")
    public Response getChatLog(@PathParam("gameID") int gameID) {
        Gson gson = new Gson();
        try {
            Vector<Hashtable<String, Object>> list = gc.getChatList(gameID);
            return Response.ok(gson.toJson(list)).build();
        } catch(NoSuchElementException n){
            return Response.status(404).build();
        }
    }
}
