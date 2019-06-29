package com.paul.spring;

import com.paul.framework.Configuration;
import com.paul.procotol.Procotol;
import com.paul.framework.RpcRequest;
import com.paul.framework.URL;
import com.paul.procotol.dubbo.DubboProcotol;
import com.paul.procotol.http.HttpProcotol;
import com.paul.procotol.socket.SocketProcotol;
import com.paul.register.Register;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;


public class Handler<T> implements InvocationHandler{

    private Class<T> interfaceClass;

    public Handler(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Configuration configuration = Configuration.getInstance();

        Procotol procotol;

        if("Dubbo".equalsIgnoreCase(configuration.getProcotol())){
            procotol = new DubboProcotol();
        }else if("Http".equalsIgnoreCase(configuration.getProcotol())){
            procotol = new HttpProcotol();
        }else if("Socket".equalsIgnoreCase(configuration.getProcotol())){
            procotol = new SocketProcotol();
        }else{
            procotol = new DubboProcotol();
        }

        URL url = Register.random(interfaceClass.getName());
        String impl = Register.get(url,interfaceClass.getName());
        int timeout = 20000;
        RpcRequest invocation = new RpcRequest(UUID.randomUUID().toString(),interfaceClass.getName(),method.getName(),args, method.getParameterTypes(),impl,timeout);
        Object res = procotol.send(url, invocation);
        return res;
    }


}
