package netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Scanner;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/23
 */
public class GroupChatClient {

    private String host;
    private int port;

  public GroupChatClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void run() throws Exception {
    NioEventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              // 加入Handler
              pipeline.addLast("decoder", new StringDecoder());
              pipeline.addLast("encoder", new StringEncoder());
              // 加入自己的Handler
              pipeline.addLast(new GroupChatClientHandler());
            }
          });
      ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
      Channel channel = channelFuture.channel();
      System.out.println("-----"+ channel.localAddress() +"--------");
      Scanner scanner = new Scanner(System.in);
      while (scanner.hasNextLine()) {
        String msg = scanner.nextLine();
        // 通过channel发送到服务器端
        channel.writeAndFlush(msg + "\r\n");
      }
    } finally {
      group.shutdownGracefully();
    }

  }

  public static void main(String[] args) throws Exception{
    GroupChatClient groupChatClient = new GroupChatClient("127.0.0.1", 8000);
    groupChatClient.run();
  }
}
