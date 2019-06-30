package com.paul.spring;

import com.paul.framework.Configuration;
import com.paul.framework.ServiceProvider;
import com.paul.loadbalance.LoadBalanceEngine;
import com.paul.loadbalance.LoadStrategy;
import com.paul.procotol.Procotol;
import com.paul.framework.RpcRequest;
import com.paul.framework.URL;
import com.paul.procotol.dubbo.DubboProcotol;
import com.paul.procotol.http.HttpProcotol;
import com.paul.procotol.socket.SocketProcotol;
import com.paul.register.Register;
import com.paul.register.RegisterCenter4Consumer;
import com.paul.register.zookeeper.ZookeeperRegisterCenter;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
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

        //服务接口名称
        String serviceKey = interfaceClass.getName();
        //获取某个接口的服务提供者列表
        RegisterCenter4Consumer registerCenter4Consumer = ZookeeperRegisterCenter.getInstance();
        List<ServiceProvider> providerServices = registerCenter4Consumer.getServiceMetaDataMap4Consumer().get(serviceKey);
        //根据软负载策略,从服务提供者列表选取本次调用的服务提供者
        String stragety = configuration.getStragety();
        if(null == stragety || stragety == ""){
            stragety = "random";
        }
        System.out.println("paul:"+ providerServices.get(0).toString());
        LoadStrategy loadStrategyService = LoadBalanceEngine.queryLoadStrategy(stragety);
        ServiceProvider serviceProvider = loadStrategyService.select(providerServices);
        URL url = new URL(serviceProvider.getIp(),serviceProvider.getPort());
        String impl = serviceProvider.getServiceObject().toString();
        int timeout = 20000;
        RpcRequest invocation = new RpcRequest(UUID.randomUUID().toString(),interfaceClass.getName(),method.getName(),args, method.getParameterTypes(),impl,timeout);
        Object res = procotol.send(url, invocation);
        return res;
    }


}
