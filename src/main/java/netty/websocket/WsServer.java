package netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/23
 */
@Slf4j
public class WsServer {

  public static void main(String[] args) throws InterruptedException {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();

      serverBootstrap.group(bossGroup, workerGroup);
      serverBootstrap.channel(NioServerSocketChannel.class);
      serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
      serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          ChannelPipeline pipeline = ch.pipeline();

          // 使用http编码和解码器
          pipeline.addLast(new HttpServerCodec());
          pipeline.addLast(new ChunkedWriteHandler());
          // http在传输的过程中是分段的，这个处理器可以将多个段进行聚合。
          // 这就是为啥，浏览器在发送大量请求的时候，会发送多个http
          pipeline.addLast(new HttpObjectAggregator(8192));
          // 对应websocket，数据是以桢的形式进行传递
          pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
          // 处理业务的Handler
          pipeline.addLast(new WsServerWebSocketHandler());
        }
      });
      ChannelFuture channelFuture = serverBootstrap.bind(8010).sync();
      channelFuture.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

}
