package protocol;

import io.netty.handler.logging.LoggingHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import message.LoginRequestMessage;
import message.Message;
import message.SerializerUtil;

public class TestMessageCodec {

    public static void main(String[] args)  {
        MessageCodec CODEC = new MessageCodec();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
//        channel.writeOutbound(message);
        ByteBuf buf = messageToByteBuf(message);
        channel.writeInbound(buf);

    }

    public static ByteBuf messageToByteBuf(Message msg) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{'c', 'h', 'a', 'r'});
        out.writeBytes(new byte[]{1});          // 协议版本
        out.writeBytes(new byte[]{1});          // 加密
        out.writeInt(msg.getMessageType());     // 消息类型
        out.writeInt(msg.getSequenceId());      // 序列id
        out.writeInt(msg.getTime());            // 时间戳
        out.writeByte(0xff);
        out.writeByte(0xff);

        byte[] bytes = SerializerUtil.encode(msg);
        out.writeInt(bytes.length);             // 长度
        out.writeBytes(bytes);
        return out;
    }
}


