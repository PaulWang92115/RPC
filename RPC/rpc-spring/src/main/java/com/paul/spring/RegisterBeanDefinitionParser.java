package com.paul.spring;

import com.paul.framework.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class RegisterBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public RegisterBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        System.out.println("21");
        Configuration.getInstance().setAddress(element.getAttribute("address"));
        return null;
    }
}
