package com.paul.service;

import com.paul.spring.ProxyFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.CountDownLatch;

public class CalcParallelRequestThread implements Runnable{
	
	private CountDownLatch singal;
	
	private CountDownLatch finish;
	
	private int taskNumber = 0;

	private ApplicationContext applicationContext;


	public CalcParallelRequestThread(CountDownLatch singal,
			CountDownLatch finish, int taskNumber,ApplicationContext applicationContext) {
		super();
		this.singal = singal;
		this.finish = finish;
		this.taskNumber = taskNumber;
		this.applicationContext = applicationContext;
	}



	@Override
	public void run() {
		try {
			singal.await();
			HelloService helloService = (HelloService) applicationContext.getBean(HelloService.class);
	        int result = helloService.cal(taskNumber,taskNumber);
	        System.out.println("result is:" + result);
	        finish.countDown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
