package com.paul.procotol.dubbo;

import com.paul.framework.MessageCallBack;
import com.paul.framework.RpcRequest;
import com.paul.framework.Procotol;
import com.paul.framework.URL;
import com.paul.procotol.http.HttpClient;
import com.paul.procotol.http.HttpServer;

public class DubboProcotol implements Procotol {
    @Override
    public void start(URL url) {
    	NettyServer nettyServer = NettyServer.getInstance();
    	nettyServer.start(url.getHost(),url.getPort());
    }

    @Override
    public Object send(URL url, RpcRequest invocation) {
        NettyClient nettyClient = NettyClient.getInstance();
        nettyClient.start(url.getHost(), url.getPort());
        NettyClientHandler handler = null;
        try {
			handler = nettyClient.getNettyClientHandler();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        MessageCallBack  callBack = handler.sendMessage(invocation);
        try {
			return callBack.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        return null;
    }
}
