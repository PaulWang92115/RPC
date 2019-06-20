package com.paul.service;


import com.paul.procotol.ProxyFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        String result = helloService.sayHello("123123");
        System.out.println(result);
    }
}
