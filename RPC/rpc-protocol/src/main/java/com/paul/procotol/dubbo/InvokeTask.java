package com.paul.procotol.dubbo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import com.paul.framework.RpcRequest;
import com.paul.framework.RpcResponse;
import com.paul.framework.URL;
import com.paul.register.Register;
import org.apache.commons.beanutils.MethodUtils;

public class InvokeTask implements Runnable{
	
	private RpcRequest invocation;
	private ChannelHandlerContext ctx;

	public InvokeTask(RpcRequest invocation,ChannelHandlerContext ctx) {
		super();
		this.invocation = invocation;
		this.ctx = ctx;
	}


	@Override
	public void run() {
		
        // 从注册中心根据接口，找接口的实现类
        String interFaceName = invocation.getInterfaceName();
		Class impClass = null;
		try {
			impClass = Class.forName(invocation.getImpl());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Method method;
        Object result = null;
		try {
			method = impClass.getMethod(invocation.getMethodName(),invocation.getParamTypes());
			//这块考虑实现类，是不是应该在 spring 里面拿
	        result = method.invoke(impClass.newInstance(),invocation.getParams());
		} catch (Exception e) {
			e.printStackTrace();
		}
		RpcResponse rpcResponse = new RpcResponse();
		rpcResponse.setResponseId(invocation.getRequestId());
		rpcResponse.setData(result);
        ctx.writeAndFlush(rpcResponse).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("RPC Server Send message-id respone:" + invocation.getRequestId());
            }
        });

	}

}
