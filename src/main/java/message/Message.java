package message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    private int time;

    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;

    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;

    static {
        messageClasses.put(LoginRequestMessage, LoginRequestMessage.class);
        messageClasses.put(LoginResponseMessage, LoginResponseMessage.class);
        messageClasses.put(ChatRequestMessage, ChatRequestMessage.class);
        messageClasses.put(ChatResponseMessage, ChatResponseMessage.class);
    }


}
