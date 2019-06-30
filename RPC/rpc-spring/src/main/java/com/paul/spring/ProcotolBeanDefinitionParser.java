package com.paul.spring;

import com.paul.framework.Configuration;
import com.paul.framework.ServiceProvider;
import com.paul.framework.URL;
import com.paul.procotol.Procotol;
import com.paul.procotol.dubbo.DubboProcotol;
import com.paul.procotol.dubbo.channelpool.NettyChannelPoolFactory;
import com.paul.procotol.http.HttpProcotol;
import com.paul.procotol.socket.SocketProcotol;
import com.paul.register.zookeeper.ZookeeperRegisterCenter;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import sun.security.krb5.Config;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class ProcotolBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ProcotolBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        System.out.println("1");
        String pro = element.getAttribute("procotol");
        int port = Integer.parseInt(element.getAttribute("port"));
        Configuration.getInstance().setProcotol(pro);
        Configuration.getInstance().setPort(port);
        Configuration.getInstance().setSerialize(element.getAttribute("serialize"));
        Configuration.getInstance().setStragety(element.getAttribute("stragety"));
        Configuration.getInstance().setRole(element.getAttribute("role"));
        Configuration.getInstance().setAddress(element.getAttribute("address"));
        if("provider".equals(element.getAttribute("role"))){
            Procotol procotol = null;
                if("Dubbo".equalsIgnoreCase(pro)){
                    procotol = new DubboProcotol();
                }else if("Http".equalsIgnoreCase(pro)){
                    procotol = new HttpProcotol();
                }else if("Socket".equalsIgnoreCase(pro)){
                    procotol = new SocketProcotol();
                }else{
                    procotol = new DubboProcotol();
                }

                try {
                    InetAddress addr = InetAddress.getLocalHost();
                    String ip = addr.getHostAddress();
                    if(port == 0){
                        port = 32115;
                    }
                    URL url = new URL(ip,port);
                    procotol.start(url);

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }else{
            //获取服务注册中心
            ZookeeperRegisterCenter registerCenter4Consumer = ZookeeperRegisterCenter.getInstance();
            //初始化服务提供者列表到本地缓存
            registerCenter4Consumer.initProviderMap();
            //初始化Netty Channel
            Map<String, List<ServiceProvider>> providerMap = registerCenter4Consumer.getServiceMetaDataMap4Consumer();
            if (MapUtils.isEmpty(providerMap)) {
                throw new RuntimeException("service provider list is empty.");
            }
            NettyChannelPoolFactory.getInstance().initNettyChannelPoolFactory(providerMap);
        }
        return null;
    }
}
