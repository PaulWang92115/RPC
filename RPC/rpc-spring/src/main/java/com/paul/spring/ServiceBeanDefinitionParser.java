package com.paul.spring;

import com.paul.framework.Configuration;
import com.paul.framework.ServiceConsumer;
import com.paul.framework.ServiceProvider;
import com.paul.register.RegisterCenter4Provider;
import com.paul.register.zookeeper.ZookeeperRegisterCenter;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.paul.procotol.dubbo.channelpool.NettyChannelPoolFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ServiceBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {



        String interfaces = element.getAttribute("interfaces");
        String ref = element.getAttribute("ref");
        Class clazz = null;
        try {
            clazz = Class.forName(interfaces);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

        definition.getConstructorArgumentValues().addGenericArgumentValue(clazz);

        definition.setBeanClass(ProxyFactory.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        beanDefinitionRegistry.registerBeanDefinition(ref,definition);

        //获取服务注册中心
        ZookeeperRegisterCenter registerCenter4Consumer = ZookeeperRegisterCenter.getInstance();

        //将消费者信息注册到注册中心
        ServiceConsumer invoker = new ServiceConsumer();
        List<ServiceConsumer> consumers = new ArrayList<>();
        consumers.add(invoker);
        invoker.setConsumer(clazz);
        invoker.setServiceObject(interfaces);
        invoker.setGroupName("");
        registerCenter4Consumer.registerConsumer(consumers);

        return definition;
    }
}
