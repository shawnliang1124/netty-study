package netty.protocoltcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;


/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/24
 */
@Slf4j
public class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol>{
    private int count;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

        //接收到数据，并处理
        int len = msg.getLen();
        byte[] content = msg.getContent();

        log.info("服务端接受到的信息如下： 长度{},  内容{}", len, new String(content, StandardCharsets.UTF_8));
        log.info("服务器接受到消息包数量：{}", count++);

        // 回复客户端消息
        String response = UUID.randomUUID().toString();
        int length = response.getBytes(StandardCharsets.UTF_8).length;
        // 构建自定义协议包 messageProtocol
        MessageProtocol messageProtocol = MessageProtocol.builder()
            .len(length)
            .content(response.getBytes(StandardCharsets.UTF_8)).build();

        ctx.writeAndFlush(messageProtocol);



    }
}
