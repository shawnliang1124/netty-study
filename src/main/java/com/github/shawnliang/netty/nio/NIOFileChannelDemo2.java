package com.github.shawnliang.netty.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/5
 */
public class NIOFileChannelDemo2 {

  public static void main(String[] args) throws IOException {
    FileInputStream fileInputStream = new FileInputStream("d:\\file01.txt");

    FileChannel fileChannel = fileInputStream.getChannel();
    ByteBuffer byteBuffer = ByteBuffer.allocate(fileInputStream.available());

    fileChannel.read(byteBuffer);

    System.out.println(new String(byteBuffer.array(), "UTF-8"));
    fileInputStream.close();
  }

}
