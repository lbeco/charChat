package message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatRequestMessage extends Message{

    private String from;
    private String to;
    private String msg;

    public ChatRequestMessage() {
    }

    public ChatRequestMessage(String from, String to, String msg) {
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    @Override
    public int getMessageType() {
        return ChatRequestMessage;
    }
}
