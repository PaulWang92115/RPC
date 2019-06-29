package com.paul.procotol.dubbo;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.paul.framework.Configuration;
import com.paul.framework.RpcRequest;
import com.paul.serializer.NettyDecoderHandler;
import com.paul.serializer.NettyEncoderHandler;
import com.paul.serializer.SerializeType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServer {
	
	private static NettyServer INSTANCE = new NettyServer();
	
	private static Executor executor = Executors.newCachedThreadPool();
	
    private final static int MESSAGE_LENGTH = 4;
    
    private NettyServer(){};
    
    public static NettyServer getInstance(){
    	return INSTANCE;
    }


	private SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());
    
    public static void submit(Runnable t){
    	executor.execute(t);
    }
	
	public void start(String host, Integer port){
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			final ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel arg0) throws Exception {
					ChannelPipeline pipeline = arg0.pipeline();
					 //ObjectDecoder的基类半包解码器LengthFieldBasedFrameDecoder的报文格式保持兼容。因为底层的父类LengthFieldBasedFrameDecoder
			        //的初始化参数即为super(maxObjectSize, 0, 4, 0, 4); 
//			        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, NettyServer.MESSAGE_LENGTH, 0, NettyServer.MESSAGE_LENGTH));
			        //利用LengthFieldPrepender回填补充ObjectDecoder消息报文头
//			        pipeline.addLast(new LengthFieldPrepender(NettyServer.MESSAGE_LENGTH));
//			        pipeline.addLast(new ObjectEncoder());
			        //考虑到并发性能，采用weakCachingConcurrentResolver缓存策略。一般情况使用:cacheDisabled即可
//			        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					//注册解码器NettyDecoderHandler
					pipeline.addLast(new NettyDecoderHandler(RpcRequest.class, serializeType));
					//注册编码器NettyEncoderHandler
					pipeline.addLast(new NettyEncoderHandler(serializeType));
					pipeline.addLast("handler", new NettyServerHandler());
					
				}
				
			});
			ChannelFuture future = bootstrap.bind(host, port).sync();
			System.out.println("Server start listen at " + port);
			future.channel().closeFuture().sync();
		}catch(Exception e){
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}
	

}
