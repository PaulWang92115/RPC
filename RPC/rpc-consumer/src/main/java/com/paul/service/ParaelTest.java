package com.paul.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ParaelTest {
	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("rpc.xml");
		//并行度10000
		int parallel = 2000;

		//开始计时
		long a1 = System.currentTimeMillis();

		CountDownLatch signal = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(parallel);

		for (int index = 0; index < parallel; index++) {
			CalcParallelRequestThread client = new CalcParallelRequestThread(signal, finish, index,applicationContext);
			new Thread(client).start();
		}

		//10000个并发线程瞬间发起请求操作
		signal.countDown();
		finish.await();

		long a2 = System.currentTimeMillis();

		String tip = String.format("RPC调用总共耗时: [%s] 毫秒", a2 - a1);
		System.out.println(tip);

	}

}
