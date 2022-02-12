package message;

import com.alibaba.fastjson.JSON;

public class SerializerUtil {
    public static <T> T decode(Class<T> clazz, byte[] bytes) {
        T obj = JSON.parseObject(bytes, clazz);
        return obj;
    }

    public static byte[] encode(Message msg) {
        String jsonString = JSON.toJSONString(msg);

        return jsonString.getBytes();
    }
}
