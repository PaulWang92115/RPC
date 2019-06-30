# RPC

  一个可插拔式高可用 RPC 框架，分布式服务框架。
  
  RPC is a highly available pluggable architecture for remote calls.

### RPC 中文简介
1. RPC 基于 Java 编写，网络通信依赖与 netty，http，socket。
2. 支持基于配置的底层协议切换，可以选择 netty，http，socket。
3. 基于 Spring 开发，接口代理类自动注入到客户端，使用 @Autowired 注入即可，用户无需关注底层实现。
4. 支持 Spring xml 格式配置，通过 xml 完成代理类注入，服务端启动，通信协议选择。
5. 服务端使用线程池提高并发能力。
6. 客户端使用 channel 缓存提高并发能力。
7. 支持多序列化协议，多负载均衡协议选择。

开发中：
1. 加入 redis 注册中心，以及对应的服务治理容错机制。
2. 监控模块。
3. Spring 启动逻辑优化。

### RPC introduction
1. RPC is written in Java, and network communication depends on netty, http, socket.
2. Support configuration-based underlying protocol switching, you can choose netty, http, socket.
3. Based on Spring development, the interface proxy class is automatically injected into the client, using @Autowired injection, users do not need to pay attention to the underlying implementation.
4. Support Spring xml format configuration, complete proxy class injection through xml, server startup, communication protocol selection.
5. Server-side thread pool improves concurrency.
6. The client uses the channel cache to improve concurrency.
7. Support multi-serialization protocol, multi-load balancing protocol selection.

In development:
1. Add the redis registry and the corresponding service governance fault tolerance mechanism.
2. Monitoring module.
3. Spring starts logic optimization.

### 结构图
![avatar](https://github.com/PaulWang92115/RPC/blob/PAUL_RELEASE_1906/doc/20190630164543928.png)

### 性能表现
首先说明，性能表现测试根据不同的机器和不同的网络环境可能会有所不同，下面的测试结果是基于我自己的机器的。
我的电脑最多起 2000 个并发线程，多了就 OOM 了，在公司的电脑尝试过起 10000 个并发线程，没有任何问题，下面看 2000 个并发线程的表现。
测试类：
```java
	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("rpc.xml");
		//并行度10000
		int parallel = 2000;

		//开始计时
		long a1 = System.currentTimeMillis();

		CountDownLatch signal = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(parallel);

		for (int index = 0; index < parallel; index++) {
			CalcParallelRequestThread client = new CalcParallelRequestThread(signal, finish, index,applicationContext);
			new Thread(client).start();
		}

		//n个并发线程瞬间发起请求操作
		signal.countDown();
		finish.await();

		long a2 = System.currentTimeMillis();

		String tip = String.format("RPC调用总共耗时: [%s] 毫秒", a2 - a1);
		System.out.println(tip);

	}
```
![avatar](https://github.com/PaulWang92115/RPC/blob/PAUL_RELEASE_1906/doc/countdown.png)
2000 并发 1秒多，还是比较快的。

### 模块介绍
![avatar](https://github.com/PaulWang92115/RPC/blob/PAUL_RELEASE_1906/doc/modules.png)
1. rpc-consumer,rpc-provider 是我们的测试模块，服务提供者和服务消费者。
2. rpc-register 注册中心，里面目前只有 zookeeper 实现。
3. rpc-procotol 网络协议模块，包括协议，序列化方式，负载均衡。
4. rpc-framework 一些通用的类。
5. rpc-spring 与 Spring 结合的核心模块，不论是客户端还是只需要导入这个包就可以了。

### 快速开始
1. 不论是客户端还是服务端都需要导入 rpc-spring jar 包。
2. 在使用的 spring 的配置文件里加入针对 rpc 的配置。
3. 客户端配置，注意 rpc:procotol 必须比 rpc:service 先配置，rpc:procotol 里面的 role 不配置就代表客户端。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:rpc="http://paul.com/schema" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://paul.com/schema http://paul.com/schema/rpc.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <rpc:procotol procotol="Dubbo" port="3230" serialize="ProtoStuff" address="47.107.56.23:2181"/>
    <rpc:application name="rpc-client" />
    <rpc:service interfaces="com.paul.service.HelloService" ref="helloService" timeout="5000"/>
</beans>
```
4. 服务端配置, rpc:procotol 必须比 rpc:provider 先配置，role="provider" 代表是服务端。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:rpc="http://paul.com/schema" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://paul.com/schema http://paul.com/schema/rpc.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <rpc:procotol procotol="Dubbo" port="3230" serialize="ProtoStuff" role="provider" address="47.107.56.23:2181"/>
    <rpc:application name="rpc-server" />
    <rpc:provider interf="com.paul.service.HelloService" impl="com.paul.service.HelloServiceImpl" />
    <rpc:provider interf="com.paul.service.UserService" impl="com.paul.service.UserServiceImpl" />
</beans>
```
