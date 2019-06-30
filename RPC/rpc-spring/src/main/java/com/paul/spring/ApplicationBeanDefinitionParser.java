package com.paul.spring;

import com.paul.framework.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
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
        Configuration.getInstance().setName(element.getAttribute("name"));
        return null;
    }
}
