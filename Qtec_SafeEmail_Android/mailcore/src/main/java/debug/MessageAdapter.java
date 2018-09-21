package debug;


import com.gongw.mailcore.message.LocalMessage;

/**
 * Created by hoa on 1/9/15.
 */
public class MessageAdapter {
    LocalMessage message;

    public MessageAdapter(LocalMessage msg) {
        message = msg;
    }

    public String toString() {
        return message.getSender() + " " + message.getSubject();
    }
}
