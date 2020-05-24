package netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Description :   群聊的Handler.
 *
 * @author : Phoebe
 * @date : Created in 2020/5/23
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

  /**
   * 定义一个channel组，管理所有的channel
   */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  /**
   * 表示连接 一旦连接，马上会执行这个方法
   * @param ctx 上下文
   * @throws Exception  .
   */
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    // 将该客户加入的聊天信息马上推送给别的客户端
    // 该方法会将channelGroup中所有的channel遍历，并且发送消息
    channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天\n");
    channelGroup.add(channel);
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "离开了\n");
  }

  /**
   * channel 有活动状态， 提示XX上线
   * @param ctx 上下文
   * @throws Exception .
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "上线了\n");
  }

  /**
   * 提示XX 下线
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "下线了\n");
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    Channel channel = ctx.channel();
    //  把自己的发送的消息进行排除
    channelGroup.forEach(c -> {
      // 不属于当前的channel，直接转发消息
        if (channel != c) {
          c.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息" + msg + "\n");
        } else {
          // todo 自己就不要发送了
        }
    });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
     ctx.close();
  }
}
