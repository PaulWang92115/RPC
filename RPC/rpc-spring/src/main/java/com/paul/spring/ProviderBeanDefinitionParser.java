package com.paul.spring;

import com.paul.framework.URL;
import com.paul.register.Register;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.Source;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ProviderBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ProviderBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanClass.getName());
        String interfaces = element.getAttribute("interf");
        String impl = element.getAttribute("impl");
        int port = Integer.parseInt(beanDefinition.getPropertyValues().get("port").toString());
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            if(port == 0){
                port = 32115;
            }
            URL url = new URL(ip,port);
            Register.regist(url,interfaces,impl);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return beanDefinition;
    }
}
