package netty.groupchat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/23
 */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    System.out.println(msg.trim());
  }
}
