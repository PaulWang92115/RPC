package com.paul.spring;

import com.paul.framework.Procotol;
import com.paul.framework.URL;
import com.paul.procotol.dubbo.DubboProcotol;
import com.paul.procotol.http.HttpProcotol;
import com.paul.procotol.socket.SocketProcotol;
import com.paul.register.Register;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ServerBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }




    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        System.out.println("4");
        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanClass.getName());
        beanDefinition.getPropertyValues().add("role",element.getAttribute("role"));
        String pro = (String)beanDefinition.getPropertyValues().get("procotol");
        int port = Integer.parseInt(beanDefinition.getPropertyValues().get("port").toString());
        Procotol procotol = null;
        if("provider".equalsIgnoreCase(element.getAttribute("role"))){
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
       //         Register.regist(url,HelloService.class.getName(),HelloServiceImpl.class.getName());
                procotol.start(url);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return beanDefinition;
    }
}
