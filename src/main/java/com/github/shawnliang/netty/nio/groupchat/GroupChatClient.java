package com.github.shawnliang.netty.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/10
 */
public class GroupChatClient {

  private final static String HOST = "127.0.0.1";

  private final static int PORT = 6667;

  private Selector selector;

  private SocketChannel socketChannel;

  private String userName;

  private ExecutorService executorService;


  /**
   * 完成初始化的工作
   */
  public GroupChatClient() throws IOException {
    selector = Selector.open();
    socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ);
    userName = socketChannel.getLocalAddress().toString().substring(1);
    executorService = Executors.newFixedThreadPool(1);
    System.out.println("客户端" + userName + "准备好了...");
  }

  /**
   *向服务器端发送消息
   */
  private void sendMsgInfo (String msgInfo) {
     msgInfo = this.userName + "说：" + msgInfo;

     try {
       socketChannel.write(ByteBuffer.wrap(msgInfo.getBytes()));
     } catch (Exception e) {
        e.printStackTrace();
     }
  }

  /**
   * 读取从服务器端回复的消息
   */
  public void readMsgInfo() {
    try {
      int read = selector.select();
      if (read > 0) {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
          SelectionKey selectionKey = iterator.next();
          // key是可读的，就拿到这个channel
          if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(byteBuffer);

            // 读到缓冲区的数据换成字符串
            String msg = new String(byteBuffer.array());
            System.out.println(msg.trim());

            iterator.remove();
          } else {
            System.out.println("没有可以用的通道....");
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args) throws IOException {
    GroupChatClient groupChatClient = new GroupChatClient();
    groupChatClient.executorService.execute(() -> {
      groupChatClient.readMsgInfo();
      try {
        Thread.sleep(2500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });


    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      String msgInfo = scanner.nextLine();
      groupChatClient.sendMsgInfo(msgInfo);
    }
  }

}
