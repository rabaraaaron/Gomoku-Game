package messenger;

import user.User;

import java.sql.Timestamp;

/**
 * Message Model
 *
 * This class logs the data for a single message -- the message body, the user who sent the message, and the time the message was sent.
 */
public class Message {
    private String words;
    private String sender;
    private Timestamp time;

    Message(String s, String username){
        words = s;
        sender = username;
        time = new Timestamp(System.currentTimeMillis());
    }

    public String getWords(){
        return words;
    }

    public String getSender(){
        return sender;
    }

    public Timestamp getTimestamp(){
        return time;
    }
}
