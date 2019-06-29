package com.paul.spring;

import com.paul.framework.Configuration;
import com.paul.framework.ServiceProvider;
import com.paul.framework.URL;
import com.paul.register.Register;
import com.paul.register.RegisterCenter4Provider;
import com.paul.register.zookeeper.ZookeeperRegisterCenter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.Source;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ProviderBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ProviderBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
//        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
//        BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanClass.getName());
        String applicationName = Configuration.getInstance().getName();
        String interfaces = element.getAttribute("interf");
        System.out.println("1111:"+interfaces);
        String impl = element.getAttribute("impl");
        int port = Configuration.getInstance().getPort();
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            if(port == 0) {
                port = 32115;
            }
            URL url = new URL(ip,port);
//            Register.regist(url,interfaces,impl);
            List<ServiceProvider> providerList = new ArrayList<>();
            ServiceProvider providerService = new ServiceProvider();
            providerService.setProvider(Class.forName(interfaces));
            providerService.setServiceObject(impl);
            providerService.setIp(ip);
            providerService.setPort(port);
            providerService.setTimeout(5000);
            providerService.setServiceMethod(null);
            providerService.setApplicationName(applicationName);
            providerService.setGroupName("nettyrpc");
            providerList.add(providerService);

            //注册到zk,元数据注册中心
            RegisterCenter4Provider registerCenter4Provider = ZookeeperRegisterCenter.getInstance();
            registerCenter4Provider.registerProvider(providerList);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
