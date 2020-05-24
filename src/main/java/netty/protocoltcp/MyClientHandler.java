package netty.protocoltcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/24
 */
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

  private int count;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
    int len = msg.getLen();
    byte[] content = msg.getContent();

    log.info("客户端接受到的消息如下，长度：{}， 内容:{}", len, new String(content, StandardCharsets.UTF_8));
    log.info("客户端一共收到了：{}条消息", this.count++);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    for (int i = 0; i < 10; i++) {
      String msg = "今天天气冷，吃火锅";
      byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
      int length = msg.getBytes(StandardCharsets.UTF_8).length;

      // 创建协议包对象
      MessageProtocol messageProtocol = MessageProtocol.builder().content(bytes).len(length).build();
      ctx.writeAndFlush(messageProtocol);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      log.error("异常消息：{}",cause.getMessage(), cause);
  }
}
