package netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

/**
 * Description :  需要注意的是 TextWebSocketFrame ，表示一个文本桢(frame)
 * 因为ws底层就是通过文本桢进行的交互.
 *
 * @author : Phoebe
 * @date : Created in 2020/5/23
 */
@Slf4j
public class WsServerWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

  /**
   * 需要注意的是，服务器和客户端的交互是TextWebSocketFrame
   * 而并非是原来的String
   * @param ctx 上下文
   * @param msg 消息对象
   * @throws Exception  .
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
    String content = msg.text();
    log.info("服务器收到消息：{}", content);
    ctx.channel()
        .writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + "" + msg.text()));
  }

  /**
   * 客户端连接后，就会触发该方法
   *
   * @param ctx 上下文
   */
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    // id表示唯一的值， longText是唯一的
    log.info("handlerAdded 被调用, channelId是 {}", ctx.channel().id().asLongText());
  }

  /**
   * 客户端断开连接之后，就会触发该方法
   * @param ctx 上下文
   * @throws Exception  。
   */
  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    log.info("handlerRemoved 被调用, channelId是 {}", ctx.channel().id().asLongText());
  }

  /**
   * 发生异常，就会触发该方法
   * @param ctx 上下文
   * @throws Exception  。
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("异常发生，信息内容{}", cause.getMessage());
  }
}
