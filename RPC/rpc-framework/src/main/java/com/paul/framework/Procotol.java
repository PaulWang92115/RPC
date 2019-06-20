package com.paul.framework;

public interface Procotol {

    void start(URL url);
    Object send(URL url, RpcRequest invocation);
}
