package com.paul.procotol.dubbo;



import com.paul.framework.Configuration;
import com.paul.framework.RpcResponse;
import com.paul.serializer.NettyDecoderHandler;
import com.paul.serializer.NettyEncoderHandler;
import com.paul.serializer.SerializeType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class NettyClient {

	private static NettyClient INSTANCE = new NettyClient();

	private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;

	private NettyClient(){};

	public static NettyClient getInstance(){
		return INSTANCE;
	}

	private SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());

	public void start(String host,Integer port){

		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup(parallel);

		try{
			bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>(){

						@Override
						protected void initChannel(SocketChannel arg0) throws Exception {
							ChannelPipeline pipeline = arg0.pipeline();
							//ObjectDecoder的基类半包解码器LengthFieldBasedFrameDecoder的报文格式保持兼容。因为底层的父类LengthFieldBasedFrameDecoder
							//的初始化参数即为super(maxObjectSize, 0, 4, 0, 4);
//							pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
							//利用LengthFieldPrepender回填补充ObjectDecoder消息报文头
//							pipeline.addLast(new LengthFieldPrepender(4));
//							pipeline.addLast(new ObjectEncoder());
							//考虑到并发性能，采用weakCachingConcurrentResolver缓存策略。一般情况使用:cacheDisabled即可
//							pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
							//注册Netty编码器
							System.out.println("11111111:"+serializeType.getSerializeType());
							pipeline.addLast(new NettyEncoderHandler(serializeType));
							//注册Netty解码器
							pipeline.addLast(new NettyDecoderHandler(RpcResponse.class, serializeType));
							pipeline.addLast("handler", new NettyClientHandler());

						}

					});
			ChannelFuture future = bootstrap.connect(host,port).sync();
		}catch(Exception e){
			group.shutdownGracefully();
		}


	}



}
