package com.paul.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ApplicationBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ApplicationBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        System.out.println("2");
        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanClass.getName());
        beanDefinition.getPropertyValues().add("name",element.getAttribute("name"));
        return beanDefinition;
    }
}
