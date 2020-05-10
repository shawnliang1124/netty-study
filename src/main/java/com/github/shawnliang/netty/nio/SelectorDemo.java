package com.github.shawnliang.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/8
 */
public class SelectorDemo {


  public static void main(String[] args) throws Exception {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    SocketAddress socketAddress = new InetSocketAddress("127.0.0.1" , 6000);
    serverSocketChannel.socket().bind(socketAddress);
    serverSocketChannel.configureBlocking(false);

    Selector selector = Selector.open();
    SelectionKey register = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
      if (selector.select(1000) == 0) {
        System.out.println("1S内没有收到来自Channel的请求");
        continue;
      }

      // 如果获得了Channel的请求,在selectionKeys 里面中找到对应的channel
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
      while (keyIterator.hasNext()) {
        SelectionKey selectionKey = keyIterator.next();
        // 证明获得了对应的事件
        if (selectionKey.isAcceptable()) {
          SocketChannel socketChannel = serverSocketChannel.accept();
          socketChannel.configureBlocking(false);
          socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
          System.out.println("客户端连接后 ，注册的selectionkey 数量=" + selector.keys().size());
        }
        if (selectionKey.isReadable()) {
          SocketChannel channel = (SocketChannel) selectionKey.channel();

          ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
          channel.read(byteBuffer);
          System.out.println("form 客户端 " + new String(byteBuffer.array()));
        }

        // 最后要把集合中的selectionKey 中移除
        // todo ? 这样不是把关系都给移除了吗
        keyIterator.remove();

      }


    }

  }
}
