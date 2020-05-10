package com.github.shawnliang.netty.nio;

import java.nio.IntBuffer;

/**
 * Description :  NIO buffer .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/5
 */
public class BasicBuffer {

  public static void main(String[] args) {
    IntBuffer intBuffer = IntBuffer.allocate(5);

    for (int i = 0; i < intBuffer.capacity(); i++) {
      intBuffer.put(i*2);
    }
    intBuffer.flip();

    while (intBuffer.hasRemaining()) {
      System.out.println(intBuffer.get());
    }
  }

}
