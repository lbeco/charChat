package message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends Message {
    private Boolean result;
    private String message;

    public LoginResponseMessage() {
    }

    public LoginResponseMessage(boolean result, String message) {

    }

    public boolean isSuccess(){
        return result;
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }


}
