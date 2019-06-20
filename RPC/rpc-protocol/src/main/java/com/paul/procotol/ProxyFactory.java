package com.paul.procotol;


import com.paul.framework.Procotol;
import com.paul.framework.RpcRequest;
import com.paul.framework.URL;
import com.paul.procotol.dubbo.DubboProcotol;
import com.paul.procotol.http.HttpClient;
import com.paul.procotol.http.HttpProcotol;
import com.paul.procotol.socket.SocketProcotol;
import com.paul.register.Register;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class ProxyFactory<T> {

    public static <T> T getProxy(final Class interfaceClass){
        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Procotol procotol = new DubboProcotol();
                URL url = Register.random(interfaceClass.getName());
                String impl = Register.get(url,interfaceClass.getName());
                RpcRequest invocation = new RpcRequest(UUID.randomUUID().toString(),interfaceClass.getName(),method.getName(),args, new Class[]{String.class},impl);
                Object res = procotol.send(url, invocation);
                return res;
            }
        });
    }























}
