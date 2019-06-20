package com.paul.procotol.dubbo;

import java.lang.reflect.Method;

import com.paul.framework.RpcRequest;
import com.paul.framework.URL;
import com.paul.register.Register;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyServerHandler extends ChannelInboundHandlerAdapter{
	
	private int loss_connect_time = 0;
	
	private ChannelHandlerContext context;
	
	public void channelRead(ChannelHandlerContext ctx,Object msg)
		throws Exception{
		System.out.println("server channelRead...");
		System.out.println(ctx.channel().remoteAddress() + "->server:" + msg.toString());
		RpcRequest invocation = (RpcRequest)msg;
		InvokeTask it = new InvokeTask(invocation,ctx);
		NettyServer.submit(it);
	}

	
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		this.context = ctx;		
	}
	
}
