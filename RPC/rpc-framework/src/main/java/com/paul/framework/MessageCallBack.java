package com.paul.framework;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageCallBack {
	
	private RpcRequest rpcRequest;
	
	private RpcResponse rpcResponse;
	
	private Lock lock = new ReentrantLock();
	
	private Condition finish = lock.newCondition();
	
	public MessageCallBack(RpcRequest request) {
        this.rpcRequest = request;
    }

    public Object start() throws InterruptedException {
        try {
            lock.lock();
            //设定一下超时时间，rpc服务器太久没有相应的话，就默认返回空吧。
            finish.await(10*1000, TimeUnit.MILLISECONDS);
            if (this.rpcResponse != null) {
                return this.rpcResponse.getData();
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void over(RpcResponse reponse) {
        try {
            lock.lock();
            finish.signal();
            this.rpcResponse = reponse;
        } finally {
            lock.unlock();
        }
    }

}
