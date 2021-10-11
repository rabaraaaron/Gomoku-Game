package messenger;

import user.User;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Messenger Class
 *
 * This class manages a conversation of messages between users.
 */
public class Messenger {
    Vector<Message> chatlog;

    public Messenger() {
        chatlog = new Vector<Message>();
    }

    public void addSystemMessage(String s){
        chatlog.add(new Message(s, "SYSTEM"));
    }

    public void addUserMessage(String s, User u){
       chatlog.add(new Message(s, u.getUserName()));
    }

    public Vector<Message> getChatlog(){ return (Vector<Message>) chatlog.clone(); }
    //return Collections.unmodifiableList(chatlog);

    public int getSize(){
        return chatlog.size();
    }
}
