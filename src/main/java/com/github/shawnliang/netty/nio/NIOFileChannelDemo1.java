package com.github.shawnliang.netty.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/5
 */
public class NIOFileChannelDemo1 {

  public static void main(String[] args) throws Exception {
    String str = "Hello world";
    // 创建输出流-> 包装到Channel
    FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");
    FileChannel channel = fileOutputStream.getChannel();

    // 创建一个缓冲区
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    // 将String 放到buffer
    byteBuffer.put(str.getBytes());
    // 开始反转，开始读取
    byteBuffer.flip();

    // 将缓冲区数据写到channel里面。
    channel.write(byteBuffer);
    fileOutputStream.close();

  }

}
