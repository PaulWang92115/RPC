package com.paul.spring;

import com.paul.framework.Configuration;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("procotol", new ProcotolBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("application", new ApplicationBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("provider", new ProviderBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("role", new ServerBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser(Service.class));
    }
}
