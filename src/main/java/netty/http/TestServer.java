package netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/22
 */
public class TestServer {

  public static void main(String[] args) throws InterruptedException {
    NioEventLoopGroup boss = new NioEventLoopGroup(1);
    NioEventLoopGroup work = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(boss, work).channel(NioServerSocketChannel.class)
          .childHandler(new TestServerInitializer());

      ChannelFuture channelFuture = bootstrap.bind(8080).sync();
      System.out.println("成功建立连接");
      channelFuture.channel().closeFuture().sync();
    } finally {
      System.out.println("终止连接");
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
  }

}
