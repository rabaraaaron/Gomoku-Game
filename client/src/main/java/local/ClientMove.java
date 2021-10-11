package local;

import java.sql.Timestamp;

/**
 * Client Move Class
 *
 * This class contains data about moves made during the game. It is responsible for storing this data in the client.
 */
public class ClientMove {
    public enum Color{
        BLACK, WHITE
    }
    private String username;
    private Color color;
    private Timestamp time;
    private int x;
    private int y;

    /**
     * Constructor for ClientMove
     *
     * @param u The username of the user that made the move
     * @param c The color of the player that made the move
     * @param t A timestamp of when the move was made
     * @param xCoord The move's X coordinate
     * @param yCoord The move's Y coordinate
     */
    public ClientMove(String u, Color c, Timestamp t, int xCoord, int yCoord){
        username = u;
        color = c;
        time = t;
        x = xCoord;
        y = yCoord;
    }

    /**
     * Returns the username of the player that made this move.
     * @return the username of the player that made this move.
     */
    public String getUsername(){
        return username;
    }

    /**
     * Returns the color of the piece at this board location.
     * @return the color of the piece at this board location.
     */
    public String getColor(){
        if(color == Color.BLACK){
            return "black";
        } else {
            return "white";
        }
    }

    /**
     * Returns the time this move was made.
     * @return the time this move was made.
     */
    public Timestamp getTime(){
        return time;
    }

    /**
     * Returns the x coordinate of this move.
     * @return the x coordinate of this move.
     */
    public int getX(){
        return x;
    }

    /**
     * Returns the y coordinate of this move.
     * @return the y coordinate of this move.
     */
    public int getY(){
        return y;
    }
}
