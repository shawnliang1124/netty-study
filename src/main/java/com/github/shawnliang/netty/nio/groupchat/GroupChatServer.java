package com.github.shawnliang.netty.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/10
 */
public class GroupChatServer {

  private Selector selector;
  private ServerSocketChannel listenChannel;
  private static final Integer PORT = 6667;


  /**
   * 构造函数初始化一些属性
   */
  public GroupChatServer() {
    try {
      selector = Selector.open();
      listenChannel = ServerSocketChannel.open();
      listenChannel.socket().bind(new InetSocketAddress(PORT));
      listenChannel.configureBlocking(false);
      listenChannel.register(selector, SelectionKey.OP_ACCEPT);

    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 监听方法
   */
  public void listen () {
    try {
      while (true) {
        // 两秒就去轮询一次，没有响应就马上返回
        int count = selector.select();
        if (count > 0) {
          // 证明channel中有事件要进行处理
          Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
          while (keyIterator.hasNext()) {
            SelectionKey selectionKey = keyIterator.next();
            // 监听到accept
            if (selectionKey.isAcceptable()) {
              SocketChannel socketChannel = listenChannel.accept();
              socketChannel.configureBlocking(false);
              // 将channel注册到selector
              socketChannel.register(selector, SelectionKey.OP_READ);
              System.out.println(socketChannel.getRemoteAddress() + " 上线");
            }
            if (selectionKey.isReadable()) {
              // 通道发送read事件，通道就是可读的状态
              readData(selectionKey);
            }

            // 最后把selectionKey删除
            keyIterator.remove();
          }

        } else {
          System.out.println("等待中...");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 读取客户端的消息
   * @param key selection和selector的绑定关系
   */
  private void readData (SelectionKey key) {
    SocketChannel socketChannel = null;
    try {
      socketChannel = (SocketChannel) key.channel();
      ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
      int count = socketChannel.read(byteBuffer);
      // 根据count值做处理
      if (count > 0) {
        String msg = new String(byteBuffer.array());
        System.out.println("来自客户端：" + msg);

        // 向其它的客户端转发消息（通知群组），专门写个方法来处理
        sendMsgToOthersClient(msg, socketChannel);
      }
    } catch (IOException e) {
        try {
          System.out.println(socketChannel.getRemoteAddress() + "离线了...");
          key.cancel();
          socketChannel.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }
  }

  /**
   * 给其它客户端发送消息
   * @param msg 消息内容
   * @param self  通道本身
   */
  private void sendMsgToOthersClient(String msg, SocketChannel self){
    System.out.println("服务器转发消息中....");
    // 获取所有注册到selector上的socketChannel, 并且排除自己
    selector.keys().forEach(key -> {
       // 通过key， 取出对应的socketChannel
      SelectableChannel channel = key.channel();

      if (channel instanceof SocketChannel && channel != self ) {
        SocketChannel targetChannel = (SocketChannel) channel;
        try {
          targetChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }


  public static void main(String[] args) {
    GroupChatServer groupChatServer = new GroupChatServer();
    groupChatServer.listen();
  }

}
