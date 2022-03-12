package protocol;

import config.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;

@Slf4j
public class HttpServerTest {

    public static void main(String[] args) {
        // 两个eventLoop
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // serverSocket来处理http协议
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    // httpServer编解码器
                    ch.pipeline().addLast(new HttpServerCodec());
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<DefaultHttpRequest>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest msg) throws Exception {
                            log.debug("{}", msg.uri());
                            // 返回 HelloWorld
                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                            byte[] bytes = ("<h1>hello!"  + "</h1>").getBytes();
                            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
                            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                            response.content().writeBytes(bytes);
                            ctx.writeAndFlush(response);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(Config.PORT).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("server error", e);
        } finally {
            // 优雅地关掉（字面意思）
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
