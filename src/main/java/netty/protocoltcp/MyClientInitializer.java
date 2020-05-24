package netty.protocoltcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/24
 */
public class MyClientInitializer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new MyMessageEncoder());
    pipeline.addLast(new MyMessageDecoder());
    pipeline.addLast(new MyClientHandler());
  }
}
