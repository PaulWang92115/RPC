package com.paul.framework;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 
 * 服务提供者向注册中心注册的信息
 * @author swang18
 *
 */
public class ServiceProvider implements Serializable{
	
	private Class<?> provider;
	private transient Object serviceObject;
	private transient Method serviceMethod;
	private String ip;
	private int port;
	private long timeout;
	//服务提供者权重
	private int weight;
	//服务端线程数
	private int workerThreads;
	//服务唯一标识
	private String applicationName;
	//服务分组名称
	private String groupName;
	
	public Class<?> getProvider() {
		return provider;
	}
	public void setProvider(Class<?> provider) {
		this.provider = provider;
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
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getWorkerThreads() {
		return workerThreads;
	}
	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
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
