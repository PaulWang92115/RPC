# RPC

一个可插拔式高可用 RPC 框架。
RPC is a highly available pluggable architecture for remote calls.

### RPC 中文简介
1. RPC 基于 Java 编写，网络通信依赖与 netty，http，socket。
2. 支持基于配置的底层协议切换，可以选择 netty，http，socket。
3. 基于 Spring 开发，接口代理类自动注入到客户端，使用 @Autowired 注入即可，用户无需关注底层实现。
4. 支持 Spring xml 格式配置，通过 xml 完成代理类注入，服务端启动，通信协议选择。
5. 服务端使用线程池提高并发能力。

开发中：
1. 加入 Zookeeper，redis 注册中心，以及对应的负载均衡以及容错机制。
2. 增加对象序列化机制，目前只有原生的序列化机制。
3. netty 并发优化。
4. 多线程模型优化。

### RPC introduction
1. RPC is written in Java, and network communication depends on netty, http, socket.
2. Support configuration-based underlying protocol switching, you can choose netty, http, socket.
3. Based on Spring development, the interface proxy class is automatically injected into the client, using @Autowired injection, users do not need to pay attention to the underlying implementation.
4. Support Spring xml format configuration, complete proxy class injection through xml, server startup, communication protocol selection.
5. Server-side thread pool improves concurrency.

In development:
1. Add Zookeeper, redis registry, and corresponding load balancing and fault tolerance mechanisms.
2. Increasing the object serialization mechanism, currently only the native serialization mechanism.
3.netty concurrency optimization.
4. Multi-threaded model optimization.

结构图：
![avatar](https://github.com/PaulWang92115/RPC/blob/master/doc/RPC%20(1).png)

