package com.paul.procotol.dubbo;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.paul.framework.MessageCallBack;
import com.paul.framework.RpcRequest;
import com.paul.framework.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter{
	
	private ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();
	
    private ChannelHandlerContext context;
    
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        String res = (String)msg;
//        RpcResponse rpcResponse = (RpcResponse)msg;
//        String responseId = rpcResponse.getResponseId();
        System.out.println("123:"+res);
        String responseId = "123";
        MessageCallBack callBack = mapCallBack.get(responseId);
        if(callBack != null){
        	mapCallBack.remove(responseId);
//        	callBack.over(rpcResponse);
        }

    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("停止时间是："+new Date());
        System.out.println("HeartBeatClientHandler channelInactive");
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
        System.out.println("激活时间是："+new Date());
        NettyClient.getInstance().setNettyClientHandler(this);  
    }
    
    public MessageCallBack sendMessage(RpcRequest invocation){
    	MessageCallBack callback = new MessageCallBack(invocation);
    	mapCallBack.put(invocation.getRequestId(), callback);
    	if(context!=null){
    		context.writeAndFlush(invocation);
    	}
    	return callback;
    }
    

}
