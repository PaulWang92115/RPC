package com.paul.framework;

public class Configuration {



    private String procotol;
    private String name;
    private int port;
    private String role;
    private String serialize;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }
}
