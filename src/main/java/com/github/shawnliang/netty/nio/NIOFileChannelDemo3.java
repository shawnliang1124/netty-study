package com.github.shawnliang.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Description :使用一个Buffer，完成文件的拷贝
 * 将1.txt copy 到2.txt.
 *
 * @author : Phoebe
 * @date : Created in 2020/5/5
 */
public class NIOFileChannelDemo3 {

  public static void main(String[] args) throws Exception{
    FileInputStream fileInputStream = new FileInputStream("1.txt");
    FileOutputStream fileOutputStream = new FileOutputStream("2.txt");

    FileChannel fileChannel01 = fileInputStream.getChannel();
    FileChannel fileChannel02 = fileOutputStream.getChannel();
    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
    while (true) {
      // 这里clear作用是将position重新设置成0，因为在上个循环的时候position的位置是缓冲区数组的最大值
      // 在不进行clear操作的时候，read方法因为没有读取数据，返回的是int 是0，会导致死循环的发生，因为一直跳不出循环
      byteBuffer.clear();
      int read = fileChannel01.read(byteBuffer);
      if (read == -1) {
        break;
      }
      byteBuffer.flip();
      fileChannel02.write(byteBuffer);
    }

    fileOutputStream.close();
    fileInputStream.close();


  }

}
