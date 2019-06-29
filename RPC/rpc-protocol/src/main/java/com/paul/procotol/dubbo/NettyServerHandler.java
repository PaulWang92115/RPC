package com.paul.procotol.dubbo;

import java.lang.reflect.Method;

import com.paul.framework.RpcRequest;
import com.paul.framework.RpcResponse;
import com.paul.framework.URL;
import com.paul.register.Register;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

	private ChannelHandlerContext context;


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
		System.out.println("server channelRead...");
		System.out.println(ctx.channel().remoteAddress() + "->server:" + rpcRequest.toString());
		InvokeTask it = new InvokeTask(rpcRequest,ctx);
		NettyServer.submit(it);
	}


	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		this.context = ctx;		
	}
	
}
