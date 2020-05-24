package netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/24
 */
@Slf4j
public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {

  @Override
  protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out)
      throws Exception {
    log.info("MyMessageEncoder encode方法被调用");
    out.writeInt(msg.getLen());
    out.writeBytes(msg.getContent());
  }
}
