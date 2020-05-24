package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/21
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

  /**
   * 这里可以读取客户端发送的信息
   * @param ctx 上下文对象 含有管道pipeline, 通道channel 地址等等
   * @param msg 客户端发送的数据
   * @throws Exception  。
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    log.info("server ctx is [{}]", ctx);
    ByteBuf byteBuf = (ByteBuf) msg;
    log.info("客户端发送的消息体内容：{}", byteBuf.toString(CharsetUtil.UTF_8));
    log.info("客户端地址是：{}", ctx.channel().remoteAddress());


  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(Unpooled.copiedBuffer("hi, 客户端~", CharsetUtil.UTF_8));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }
}
