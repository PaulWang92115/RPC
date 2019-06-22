package com.paul.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ProcotolBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ProcotolBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        System.out.println("1");
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().add("procotol",element.getAttribute("procotol"));
        beanDefinition.getPropertyValues().add("port",element.getAttribute("port"));
        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        beanDefinitionRegistry.registerBeanDefinition(beanClass.getName(),beanDefinition);
        return beanDefinition;
    }
}
