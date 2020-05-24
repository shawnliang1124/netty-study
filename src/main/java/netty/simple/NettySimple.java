package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/15
 */
public class NettySimple {

  public static void main(String[] args) throws InterruptedException {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    NioEventLoopGroup workGroup = new NioEventLoopGroup();

    // 创建服务端启动对象，配置参数
    ServerBootstrap serverBootstrap = new ServerBootstrap();

    serverBootstrap.group(bossGroup, workGroup)
        // 使用NioServerSocketChannel 作为服务器的通道实现
        .channel(NioServerSocketChannel.class)
        // 使用NIOSocketChannel
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        // 为workGroup设置对应的处理器
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(null);
          }
        });

    System.out.println("服务器已经准备完毕");

    ChannelFuture channelFuture = serverBootstrap.bind(6666).sync();

    channelFuture.addListener((ChannelFutureListener) cf -> {
        if (channelFuture.isSuccess()) {
          System.out.println("监听6666端口成功");
        } else if (channelFuture.isCancelled()) {
          System.out.println("监听6666端口失败");
        }
    });

    // 对关闭通道进行监听
    channelFuture.channel().closeFuture().sync();
  }

}
