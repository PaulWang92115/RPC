package com.paul.service;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("rpc.xml");
        HelloService service = (HelloService) applicationContext.getBean(HelloService.class);
        if(null == service){
            System.out.println("1111null");
        }
//        String res = service.sayHello("paul");
        int res = service.cal(2,2);
        System.out.println("name: " + res);
    }
}
