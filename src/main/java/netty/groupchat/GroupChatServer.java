package netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/23
 */
@Slf4j
public class GroupChatServer {

  private int port;

  public GroupChatServer(int port) {
    this.port = port;
  }

  public void run() throws InterruptedException {
    // 创建boss和work线程组
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();

      bootstrap.group(bossGroup, workGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 128)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
              ChannelPipeline pipeline = ch.pipeline();
              // 加入Handler
              pipeline.addLast("decoder", new StringDecoder());
              pipeline.addLast("encoder", new StringEncoder());
              // 加入自己的Handler
              pipeline.addLast(new GroupChatServerHandler());
            }
          });
      System.out.println("netty 服务器开始启动");
      ChannelFuture channelFuture = bootstrap.bind(port).sync();
      // 监听关闭
      channelFuture.channel().closeFuture().sync();
    }finally {
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

  }

  public static void main(String[] args) throws InterruptedException {
    new GroupChatServer(8000).run();
  }

}
