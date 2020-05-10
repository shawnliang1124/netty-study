package com.github.shawnliang.netty.nio;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Description :  直接在堆外内存进行修改 .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/5
 */
public class NIOFileChannelDemo5 {

  public static void main(String[] args) throws Exception {

    RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
    FileChannel channel = randomAccessFile.getChannel();

    MappedByteBuffer mappedByteBuffer = channel.map(MapMode.READ_WRITE, 0, channel.size());

    mappedByteBuffer.put(0, (byte) '你');
    mappedByteBuffer.put(1, (byte) '好');

  }

}
