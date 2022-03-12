package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.ChatRequestMessage;
import message.ChatResponseMessage;
import message.LoginRequestMessage;
import server.service.SessionFactory;

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
