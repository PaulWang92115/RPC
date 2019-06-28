package com.paul.framework;

import java.lang.reflect.Method;

public class ServiceConsumer {
	
	private Class<?> consumer;
	private Object serviceObject;
	private Method serviceMethod;
	private String ip;
	private int port;
	private long timeout;
	//服务提供者唯一标识
	private String applicationName;
	//服务分组组名
	private String groupName = "default";
	public Class<?> getConsumer() {
		return consumer;
	}
	public void setConsumer(Class<?> consumer) {
		this.consumer = consumer;
	}
	public Object getServiceObject() {
		return serviceObject;
	}
	public void setServiceObject(Object serviceObject) {
		this.serviceObject = serviceObject;
	}
	public Method getServiceMethod() {
		return serviceMethod;
	}
	public void setServiceMethod(Method serviceMethod) {
		this.serviceMethod = serviceMethod;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
	
	

}
