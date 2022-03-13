package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import message.ChatRequestMessage;
import message.ChatResponseMessage;
import message.LoginRequestMessage;
import server.service.SessionFactory;

@Slf4j
@ChannelHandler.Sharable
public class ChatRequestHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        String message = msg.getMsg();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if(channel!=null){
            channel.writeAndFlush(new ChatRequestMessage(msg.getFrom(), msg.getTo(), message));
            ctx.writeAndFlush(new ChatResponseMessage(true,"success"));
        }else{
            ctx.writeAndFlush(new ChatResponseMessage(false,"user not found"));
        }


    }
}
