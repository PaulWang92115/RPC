package com.paul.spring;

public class Configuration {

    private String procotol;
    private String name;
    private int port;
    private String role;

    private static Configuration configuration = new Configuration();

    private Configuration(){};

    public static Configuration getInstance(){
        return configuration;
    }


    public String getProcotol() {
        return procotol;
    }

    public void setProcotol(String procotol) {
        this.procotol = procotol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
