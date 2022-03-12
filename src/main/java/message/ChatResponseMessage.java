package message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatResponseMessage extends Message{

    private Boolean result;
    private String message;

    public ChatResponseMessage() {
    }

    public ChatResponseMessage(boolean result, String message) {
        this.result =result;
        this.message = message;
    }

    public boolean isSuccess(){
        return result;
    }

    @Override
    public int getMessageType() {
        return ChatResponseMessage;
    }
}
