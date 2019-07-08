package com.paul.procotol.dubbo.channelpool;

import com.paul.framework.Configuration;
import com.paul.framework.RpcResponse;
import com.paul.framework.ServiceProvider;
import com.paul.framework.URL;
import com.paul.procotol.dubbo.NettyClientHandler;
import com.paul.register.Register;
import com.paul.serializer.NettyDecoderHandler;
import com.paul.serializer.NettyEncoderHandler;
import com.paul.serializer.SerializeType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.commons.collections.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class NettyChannelPoolFactory {
	
    //初始化Netty Channel阻塞队列的长度,该值为可配置信息
    private static final int channelConnectSize = 10;
    
    //Key为服务提供者地址,value为Netty Channel阻塞队列
    private static final Map<URL, ArrayBlockingQueue<Channel>> channelPoolMap = new ConcurrentHashMap<>();
	
	private static NettyChannelPoolFactory INSTANCE = new NettyChannelPoolFactory();
	
	private NettyChannelPoolFactory(){}
	
	public static NettyChannelPoolFactory getInstance(){
		return INSTANCE;
	}

	private List<ServiceProvider> serviceMetaDataList = new ArrayList<>();
	
	//根据配置文件里面需要调用的接口信息来初始化 channel
	public void initNettyChannelPoolFactory(Map<String, List<ServiceProvider>> providerMap){

		//将服务提供者信息存入serviceMetaDataList列表
		Collection<List<ServiceProvider>> collectionServiceMetaDataList = providerMap.values();
		for (List<ServiceProvider> serviceMetaDataModels : collectionServiceMetaDataList) {
			if (CollectionUtils.isEmpty(serviceMetaDataModels)) {
				continue;
			}
			serviceMetaDataList.addAll(serviceMetaDataModels);
		}

		//获取服务提供者地址列表
		Set<URL> set = new HashSet<>();
		for (ServiceProvider serviceMetaData : serviceMetaDataList) {
			String serviceIp = serviceMetaData.getIp();
			int servicePort = serviceMetaData.getPort();
			URL url = new URL(serviceIp,servicePort);
			set.add(url);
		}
		
		for(URL url:set){
			//为每个 ip端口 建立多个 channel，并且放入阻塞队列中
			int channelSize = 0;
			while(channelSize < channelConnectSize){
				Channel channel = null;
				while(channel == null){
					channel = registerChannel(url);
				}
				
				channelSize ++;
				
				ArrayBlockingQueue<Channel> queue = channelPoolMap.get(url);
				if(queue == null){
					queue = new ArrayBlockingQueue<Channel>(channelConnectSize);
					channelPoolMap.put(url, queue);
				}
				queue.offer(channel);
				
			}
		}
		
	}

	public Channel registerChannel(URL url) {
		final SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup(10);
		
		try{
			bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel arg0) throws Exception {
					ChannelPipeline pipeline = arg0.pipeline();
					//ObjectDecoder的基类半包解码器LengthFieldBasedFrameDecoder的报文格式保持兼容。因为底层的父类LengthFieldBasedFrameDecoder
			        //的初始化参数即为super(maxObjectSize, 0, 4, 0, 4);
//			        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
			        //利用LengthFieldPrepender回填补充ObjectDecoder消息报文头
//			        pipeline.addLast(new LengthFieldPrepender(4));
//			        pipeline.addLast(new ObjectEncoder());
			        //考虑到并发性能，采用weakCachingConcurrentResolver缓存策略。一般情况使用:cacheDisabled即可
//			        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					pipeline.addLast(new NettyEncoderHandler(serializeType));
					//注册Netty解码器
					pipeline.addLast(new NettyDecoderHandler(RpcResponse.class, serializeType));
					pipeline.addLast("handler", new NettyClientHandler());
					
				}
				
			});
			ChannelFuture future = bootstrap.connect(url.getHost(),url.getPort()).sync();
			Channel channel = future.channel();
		    //等待Netty服务端链路建立通知信号
            final CountDownLatch connectedLatch = new CountDownLatch(1);

            final List<Boolean> isSuccess = new ArrayList<>(1);
			future.addListener(new ChannelFutureListener(){

				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if(future.isSuccess()){
						isSuccess.add(true);
					}else{
						isSuccess.add(false);
					}
                    connectedLatch.countDown();
				}
				
			});
			connectedLatch.await();
			if(isSuccess.get(0)){
				return channel;
			}
		}catch(Exception e){
			group.shutdownGracefully();
			e.printStackTrace();
		}
		return null;
	}
	//根据 url 获取阻塞队列
	public ArrayBlockingQueue<Channel> acqiure(URL url){
		System.out.println(channelPoolMap.toString());
		return channelPoolMap.get(url);
	}
	
	//channel 使用完毕后进行回收
	public void release(ArrayBlockingQueue<Channel> queue, Channel channel, URL url){
		if(queue == null){
			return;
		}
		//需要检查 channel 是否可用，如果不可用，重新注册一个放入阻塞队列中
		if(channel == null || !channel.isActive() || !channel.isOpen()|| !channel.isWritable()){
            if (channel != null) {
                channel.deregister().syncUninterruptibly().awaitUninterruptibly();
                channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
            }
            Channel c = null;
            while(c == null){
            	c = registerChannel(url);
            }
            queue.offer(c);
            return;
		}
		queue.offer(channel);
	}

}

