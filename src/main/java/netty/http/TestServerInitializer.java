package netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/22
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    // 向管道中加入处理器
    // 得到管道
    ChannelPipeline pipeline = socketChannel.pipeline();
    // 1.netty提供的http编解码器
    pipeline.addLast("我的HttpServerCodec", new HttpServerCodec());
    // 2.自定义处理器
    pipeline.addLast("我的httpServerHandler", new TestHttpServerHandler());
  }
}
