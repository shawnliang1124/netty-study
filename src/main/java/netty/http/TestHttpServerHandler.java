package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/22
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  /**
   * 读取客户端数据
   * @param ctx 上下文
   * @param msg 客户端传输的数据
   * @throws Exception  .
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

      if (msg instanceof HttpRequest) {
        System.out.println("客户端地址"+ ctx.channel().remoteAddress());
        ChannelPipeline pipeline = ctx.pipeline();
        Channel channel = ctx.channel();
        // 回复信息给浏览器(http)
        ByteBuf respContent = Unpooled.copiedBuffer("hello，这里是服务器", CharsetUtil.UTF_8);
        // 构造一个httpResponse
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.OK, respContent);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, respContent.readableBytes());
        // 将构建好的response进行返回
        ctx.writeAndFlush(response);
      }
  }
}
