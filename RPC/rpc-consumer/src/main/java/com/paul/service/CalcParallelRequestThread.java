package com.paul.service;

import com.paul.spring.ProxyFactory;

import java.util.concurrent.CountDownLatch;

public class CalcParallelRequestThread implements Runnable{
	
	private CountDownLatch singal;
	
	private CountDownLatch finish;
	
	private int taskNumber = 0;
	ProxyFactory proxyFactory = new ProxyFactory(HelloService.class);

	HelloService helloService;

	{
		try {
			helloService = (HelloService) proxyFactory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CalcParallelRequestThread(CountDownLatch singal,
			CountDownLatch finish, int taskNumber) {
		super();
		this.singal = singal;
		this.finish = finish;
		this.taskNumber = taskNumber;
	}



	@Override
	public void run() {
		try {
			singal.await();
	        int result = helloService.cal(taskNumber,taskNumber);
	        System.out.println("result is:" + result);
	        finish.countDown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
