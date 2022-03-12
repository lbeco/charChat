package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import message.ChatRequestMessage;
import message.ChatResponseMessage;
import message.LoginRequestMessage;
import message.LoginResponseMessage;
import protocol.MessageCodec;
import protocol.ProtocolFrameDecoder;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        Scanner scanner = new Scanner(System.in);
        AtomicBoolean LOGIN = new AtomicBoolean();
        AtomicBoolean EXIT = new AtomicBoolean();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        // 接收响应消息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);
                            if ((msg instanceof LoginResponseMessage)) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    // 如果登录成功
                                    LOGIN.set(true);
                                    System.out.println("登录成功");
                                }
                                // 唤醒 system in 线程
                                WAIT_FOR_LOGIN.countDown();
                            }
                            else if ((msg instanceof ChatRequestMessage)) {
                                ChatRequestMessage crm = (ChatRequestMessage) msg;
                                System.out.println(crm.getFrom() + " " + crm.getMsg());
                            }
                            else if ((msg instanceof ChatResponseMessage)) {
                                ChatResponseMessage crm = (ChatResponseMessage) msg;
                                if(!crm.getResult()){
                                    System.out.println("Request failed! "+crm.getMessage());
                                }
                            }
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 负责接收用户在控制台的输入，负责向服务器发送各种消息
                            new Thread(() -> {
                                System.out.println("请输入用户名:");
                                String username = scanner.nextLine();

                                System.out.println("请输入密码:");
                                String password = scanner.nextLine();

                                // 构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                System.out.println(message);
                                // 发送消息
                                ctx.writeAndFlush(message);

                                System.out.println("等待后续操作...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!LOGIN.get()) {
                                    ctx.channel().close();
                                    return;
                                }

                                while (true) {
                                    String command;
                                    try {
                                        command = scanner.nextLine();
                                    } catch (Exception e) {
                                        break;
                                    }
                                    String[] s = command.split(" ");

                                    switch(s[0]){
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username,s[1],s[2]));
                                    }

                                }


                            }, "system in").start();
                        }

                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                            EXIT.set(true);
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            EXIT.set(true);
                        }

                    });
                }

            });

            Channel channel = bootstrap.connect("localhost", 12580).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }

    }
}
