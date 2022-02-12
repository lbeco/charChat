package message;

import com.alibaba.fastjson.JSON;

public class Serializer {



    public byte[] encode() {
        String jsonString = JSON.toJSONString(this);

        return jsonString.getBytes();
    }
}


