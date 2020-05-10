package com.github.shawnliang.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Description :
 * 使用channel 的 transform 方法，拷贝文件.
 * 直接通过channel进行copy，而不用通过使用ByteBuffer缓冲流的形式
 *
 * @author : Phoebe
 * @date : Created in 2020/5/5
 */
public class NIOFileChannelDemo4 {

  public static void main(String[] args) throws Exception{
    FileInputStream fileInputStream = new FileInputStream("d:\\file01.txt");
    FileOutputStream fileOutputStream = new FileOutputStream("d:\\file02.txt");

    FileChannel fromChannel = fileInputStream.getChannel();
    FileChannel targetChannel = fileOutputStream.getChannel();

    targetChannel.transferFrom(fromChannel, 0, fromChannel.size());
    fileInputStream.close();
    fileOutputStream.close();
  }

}
