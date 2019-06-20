package com.paul.framework;

import java.io.Serializable;
import java.util.Objects;

public class URL implements Serializable {

    private String host;
    private Integer port;

    public URL(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URL url = (URL) o;
        return Objects.equals(host, url.host) &&
                Objects.equals(port, url.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
