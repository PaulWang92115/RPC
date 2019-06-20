package com.paul.service;


import com.paul.procotol.socket.SocketProcotol;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.paul.framework.Procotol;
import com.paul.framework.URL;
import com.paul.procotol.dubbo.DubboProcotol;
import com.paul.procotol.http.HttpProcotol;
import com.paul.procotol.http.HttpServer;
import com.paul.register.Register;

/**
 *
 * 启动服务端，注册服务
 *
 */
public class Provider implements ApplicationContextAware{
	
	private ApplicationContext ctx;

	public static void main(String[] args) {
		//注册服务
		URL url = new URL("localhost", 8080);
		Register.regist(url,HelloService.class.getName(),HelloServiceImpl.class.getName());

		//启动 Tomcat
		Procotol procotol1 = new DubboProcotol();
		procotol1.start(url);
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
        //注册服务
        URL url = new URL("localhost", 8080);
        Register.regist(url,HelloService.class.getName(),HelloServiceImpl.class.getName());

        //启动 Tomcat
        Procotol procotol1 = new SocketProcotol();
        procotol1.start(url);
		
	}

}
