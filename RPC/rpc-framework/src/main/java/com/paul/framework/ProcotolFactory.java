package com.paul.framework;

import com.sun.net.httpserver.spi.HttpServerProvider;

public class ProcotolFactory {

    public static Procotol getProcotol(){
        String name = System.getProperty("procotolName");
        if(name == null || name.equals("")){
            name = "http";
        }
        return null;
    }
}
