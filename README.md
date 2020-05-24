#### 一.概念问题    
##### 1.1 阻塞和非阻塞  
阻塞和非阻塞是进程在访问数据的时候，数据是否准备就绪的一种处理方式,当数据没有准备的时候。  

**阻塞**：往往需要等待缓冲区中的数据准备好过后才处理其他的事情，否则一直等待在那里。  

**非阻塞**:当我们的进程访问我们的数据缓冲区的时候，如果数据没有准备好则直接返回，不会等待。如果数据已经
准备好，也直接返回。 



 ##### 1.2 BIO与NIO对比  
 BIO的意思就是阻塞IO  
 NIO的意思就是非阻塞IO 
 对比表格如下：  
 

IO 模型 | BIO | NIO
---|---|---
通信 | 面向流(乡村公路)  | 面向缓冲(高速公路，多路复用技术)
处理 |阻塞 IO(多线程)  |  非阻塞 IO(反应堆 Reactor)
触发 | 无 |  选择器(轮询机制)

 

##### 1.3 IO的操作汇总  
IO(BIO) 同步阻塞IO  
NIO 同步非阻塞IO  (使用线程池来进行实现)
AIO Async IO 异步的非阻塞IO(通过事件驱动，回调)


##### 1.4 IO的概念  
BIO 适用于连接数目比较小，而且是固定的架构，程序了解起来也比较简单。  

NIO 适用于连接数目短并且连接比较短的场景，比如说聊天服务器，弹幕系统，服务之间的通讯等。  

AIO适用于连接数目比较多，并且连接时间比较长的场景，比如说电子相册什么的。  


Netty是基于NIO进行的二次封装  


##### 1.5 NIO的基本概念  
NIO有三大核心的部分：  
Channel(通道)  
Buffer(缓冲区)   
Selector(选择器)  
NIO就是面向缓冲区进行编程的，数据是先读到一个缓冲区。并且是事件Event驱动的，会由事件，来驱动Selector去执行不同的Channel。  


#### 二、Buffer的概念  
##### 2.1 基本概念  
缓冲区(Buffer),本质上是一个可以读写数据的内存块，可以理解成是一个容器对象(含数组).该对象提供了一组方法，可以更轻松地使用内存块    


##### 2.2 基本属性 
以下几个属性是所有Buffer子类的基本概念，无论是ByteBuffer,IntBuffer等等，里面都是有这几个基本的属性。 

属性 | 描述
---|---
Capacity | 容量，即可以容纳的最大数据量；在缓冲区创建时被设定并且不能改变
Limit | 表示缓冲区的当前终点，不能对缓冲区超过极限的位置进行读写操作。且极限是可以修改的  
Position | 位置，下一个要被读或写的元素的索引，每次读写缓冲区数据时都会改变改值，为下次读写作准备  
Mark | 标记


#### 三、Channel的概念 
##### 3.1 基本概念  
1.channel是双向的，可以读可以写。  
2.Channel是一个接口，就是一种规范。  
3.Channel的常用抽象类FileChannel, SocketChannel等等  

##### 3.2 代码案例  

##### 3.2.1 FileChannel的使用  

使用FileChannel对文件进行复制，注意的是，FileChannel实际上也是一个抽象类，具体的方法实现，是底层的**FileChannelImpl**实现类去做的。

流程思路：  

方法1：首先，获取输入流，输入流里面有一个Channel(Channel01), 然后把数据读到缓冲流byteBuffer中，输出流的Channel(Channel02)，再写入bytebuffer的数据，写到Channel02通道中，这样就完成了一个文件的复制。
```

/**
 * Description :使用一个Buffer，完成文件的拷贝
 * 将1.txt copy 到2.txt.
 *
 * @author : Phoebe
 * @date : Created in 2020/5/5
 */

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


```


方法2： 使用FileChannel的transform方法进行操作：  
```
    FileInputStream fileInputStream = new FileInputStream("d:\\file01.txt");
    FileOutputStream fileOutputStream = new FileOutputStream("d:\\file02.txt");

    FileChannel fromChannel = fileInputStream.getChannel();
    FileChannel targetChannel = fileOutputStream.getChannel();

    targetChannel.transferFrom(fromChannel, 0, fromChannel.size());
    fileInputStream.close();
    fileOutputStream.close();
```  



方法3:  RandomAccessFile对堆外内存进行操作

直接操作堆外内存进行文件的修改 
```
   RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
    FileChannel channel = randomAccessFile.getChannel();

    MappedByteBuffer mappedByteBuffer = channel.map(MapMode.READ_WRITE, 0, channel.size());

    mappedByteBuffer.put(0, (byte) '你');
    mappedByteBuffer.put(1, (byte) '好');
```


***

#### 四、Selector（选择器）  

##### 4.1 基本概念  
1、java的NIO，非阻塞IO，用一个线程，处理多个客户端的连接，就会使用到Selector  
2、一个Selector,能够检测到多个注册的通道上是否会有事件发生，如果某个通道(Channel)上，有事件的发生，便获取事件，然后对每个事件进行相应的处理，这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求。  
3、只有在连接有读写事件真正地发生时候，才会进行读写，大大减少了系统的开销，并且不必为每个连接都去创建一个线程。  
4、避免了多线程在切换上下文导致的开销   


##### 4.2 Selector的基本方法
在使用selector的时候，需要注意几个概念。  


```
1. SelectionKey:维护了channel和selector的关系,两者通过这个对象进行了关联。  
而对于SelectionKey来说，里面可以规定它的属性，比如：  
OP_READ， OP_WRITE， OP_CONNECT，OP_ACCEPT  

也就是说，selectionKey可以指定对应的属性，那么，Channel在接收到事件的时候，Selector可以根据以上特定的事件进行响应。

2. Select方法
Selector中的select(time)方法，非阻塞的，在XX秒内拿不到Channel的结果就会马上返回，不会再等待，而去匹配对应的Channel，正是在Selector中维护了一个Set<SelectionKey>的集合，通过key来进行匹配对应的SelectionKey，从而找到对应的Channel。

```




##### 4.2 代码案例   
使用java nio写一个简单的群聊的demo   




#### 五、Reactor模型 
##### 5.1 单Reactor单线程   


##### 5.2 单Reactor多线程

##### 5.2主从Reactor多线程   
方案说明：  
- Reactor主线程MainReactor对象通过select监听连接事件，当收到事件后，通过Acceptor处理连接事件  
- 当Acceptor 处理连接事件后，MainRector将连接分配给SubReactor(一对多的关系)  
-  subReactor将连接加入到连接队列进行监听，并且创建对应的handler对事件进行处理 
-  当有新的事件发生时，subReactor就会调用对应的handler处理  
-  handler通过read读取数据，分发给后面的工作线程work进行处理，并且返回结果 
-  hanlder收到响应的结果后，再通过send方法将结果传输给client客户端  


#### 六、netty模型
##### 6.1 netty的工作模型    
 
 
 看一个netty的关键架构图：  
 
 
 ![image](https://wx4.sinaimg.cn/mw1024/007R8l8Fgy1gev7mopvefj30sm0nytq0.jpg)



##### 6.2文字解析    



**Boss  Group:** Netty中专门负责处理客户端连接的线程池    
```
执行步骤如下：  
    1.轮询accept事件 
    2.处理accept事件，将连接的客户端，生成好NioSocketChannel,并且将它注册到某个WorkerNioChannel上的selector上 
    3. 处理任务队列中的任务
```



**Work Group:** Netty中负责网络读写的线程池     
```
执行步骤如下：
    1.轮询read/write事件  
    2.处理IO事件，即read/write事件。在对应的NioSocketChannel上进行处理 
    3.处理任务队列的任务，即runTask
```



**NioEventGroup:** 相当于是一个事件循环组，这个组中有多个循环，每一个循环事件都是一个NioEventLoop  


**PipeLine:** 在NioEventGroup处理业务的时候，会使用到PipeLine,然后里面包含了很多的Channel,通过PipeLine就可以拿到对应的管道。  


### 七、netty的使用  
##### 7.1 异步模型   
**Future**: 也就是netty的异步模型，在netty中，包括Bind, Write,Connect等操作都会返回一个ChannelFuture。调用者并不能马上得到结果，而是通过**Future-Listener**的机制，用户可以<font color="red">主动获取或者通知</font>的机制获得IO的结果  

图解分析说明，其实对于netty来讲，每handler都能做到异步，只需要进行监听即可。下图的每个过程都能做到异步。 

![image](https://wx4.sinaimg.cn/mw1024/007R8l8Fgy1gf0t918bqoj30rw08aq7e.jpg)



调用者直接通过FutureHandler来获取操作执行的状态，注册监听的函数。   

API  | 说明
---|---
isDone | 操作是否已经完成
isSuccess| 操作是否成功 
getCause| 当前失败的原因  
isCancelled | 判断当前操作是否被取消  


代码示例：  

``` 
    ChannelFuture channelFuture = serverBootstrap.bind(6666).sync();

    channelFuture.addListener((ChannelFutureListener) cf -> {
        if (channelFuture.isSuccess()) {
          System.out.println("监听6666端口成功");
        } else if (channelFuture.isCancelled()) {
          System.out.println("监听6666端口失败");
        }
    });

```

### 八、netty的核心模块API介绍  
##### 8.1 BootStrap  
主要使用在客户端 

##### 8.2 ServerBootStrap  
主要使用在服务端

##### 8.3 Future,ChannelFuture  
**定位：** 因为netty中所有的IO操作都是异步的，不能马上知道消息内否被正确处理，但是过一会等它执行完成或者直接可以注册监听，当操作成功或者失败的时候，会自动触发对应的监听事件。。 
```
    ChannelFuture channelFuture = serverBootstrap.bind(6666).sync();

    channelFuture.addListener((ChannelFutureListener) cf -> {
        if (channelFuture.isSuccess()) {
          System.out.println("监听6666端口成功");
        } else if (channelFuture.isCancelled()) {
          System.out.println("监听6666端口失败");
        }
    });
```

##### 8.4 Channel  
**定位：**  netty网络通信的组件，执行网络IO的操作  
**不同的协议有不同的类型**   


##### 8.5 selector  
**定位：** 基于selector对象实现IO多路复用， 一个selector线程可以监听多个连接的channel事件  
通过自身的轮询机制，不断查询已经注册好的channel是否有已经就绪的IO事件，这样程序就可以比较高效地管理多个channel.

##### 8.6 channelHandler及其实现类  
![image](https://wx3.sinaimg.cn/mw1024/007R8l8Fgy1gf28sk0o2uj30ok0gcdg3.jpg)

##### 8.6 Pipeline和ChannelPipeline  
**定位**实际上是一个拦截器，会把每个ChannelHandler进行拦截，channelHandeler中的inBound和outBound方法等等，都会被pipeline进行拦截，拦截到想要的handler后，会执行对应的handler方法，让它做想要的事情  

![image](https://wx3.sinaimg.cn/mw1024/007R8l8Fgy1gf29ewgho8j30mh09yjx2.jpg)

***  

##### 8.6 ChannelHandlerContext (chx) 
**定位:** 保存Channel相关的所有上下文信息，注意的是，包含**一个具体**的事件处理器ChannelHandler，同时Chx中也绑定了对应的Pipeline和channel的信息，方便对handler进行使用

```
ChannelPipeline pipeline = ctx.pipeline();
 Channel channel = ctx.channel();
```  


##### 8.7 Unpooled类  



#### 九 netty的编码和解码  

##### 9.1 google的protobuf   
简单来说,protobuf就是传输效率高，跨语言。  
官网：  
```
https://developers.google.cn/protocol-buffers/
```


#### 五、笔记示例图  
https://www.processon.com/diagraming/5b04299de4b0595cc89ed45d  



#### 六、TODO 
零拷贝  
mmap(内存映射)
sendFile
