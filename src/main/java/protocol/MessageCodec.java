package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import message.Message;
import message.SerializerUtil;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeBytes(new byte[]{'c', 'h', 'a', 'r'});
        out.writeBytes(new byte[]{1});          // 协议版本
        out.writeBytes(new byte[]{1});          // 加密
        out.writeInt(msg.getMessageType());     // 消息类型
        out.writeInt(msg.getSequenceId());      // 序列id
        out.writeInt(msg.getTime());            // 时间戳
        out.writeByte(0xff);
        out.writeByte(0xff);

//        out.writeInt(1);
//        out.writeInt(1);
//        out.writeInt(1);
//        out.writeInt(1);

        // 6. 获取内容的字节数组
        byte[] bytes = SerializerUtil.encode(msg);
        out.writeInt(bytes.length);             // 长度
        out.writeBytes(bytes);
        outList.add(out);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte encrypt = in.readByte();
        int messageType = in.readInt();
        int time = in.readInt();
        int sequenceId = in.readInt();
        in.readByte();

//        in.readInt();
//        in.readInt();
//        in.readInt();
//        in.readInt();

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = SerializerUtil.decode(messageClass,bytes);
//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.debug("{}", message);
        out.add(message);
    }
}
