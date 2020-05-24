package netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/24
 */
@Slf4j
public class MyMessageDecoder extends ReplayingDecoder<Void> {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    log.info("MyMessageDecoder decode 被调用");
    // 需要将得到的二进制字节码 -> MessageProtocol 数据包对象
    int length = in.readInt();
    byte[] content = new byte[length];
    in.readBytes(content);

    // 封装成MessageProtocol对象，放入out，丢给下个handler去进行处理
    MessageProtocol messageProtocol = MessageProtocol.builder()
        .len(length)
        .content(content).build();
    out.add(messageProtocol);

  }
}
