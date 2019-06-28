package com.paul.framework;

import java.awt.*;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class RpcRequest implements Serializable {
	private String requestId;
	private String interfaceName;
	private String methodName;
	private Object[] params;
	private Class[] paramTypes;
	private String impl;
	private int timeout;

	public RpcRequest(String requestId, String interfaceName,
					  String methodName, Object[] params, Class[] paramTypes,String impl,int timeout) {
		super();
		this.requestId = requestId;
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.params = params;
		this.paramTypes = paramTypes;
		this.impl = impl;
		this.timeout = timeout;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	public Class[] getParamTypes() {
		return paramTypes;
	}
	public void setParamTypes(Class[] paramTypes) {
		this.paramTypes = paramTypes;
	}

	public String getImpl() {
		return impl;
	}

	public void setImpl(String impl) {
		this.impl = impl;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


}
