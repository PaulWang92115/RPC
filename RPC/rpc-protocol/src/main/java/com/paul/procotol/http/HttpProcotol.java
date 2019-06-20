package com.paul.procotol.http;

import com.paul.framework.RpcRequest;
import com.paul.framework.Procotol;
import com.paul.framework.URL;

public class HttpProcotol implements Procotol {
    @Override
    public void start(URL url) {
        HttpServer httpServer = HttpServer.getInstance();
        httpServer.start(url.getHost(),url.getPort());
    }

    @Override
    public String send(URL url, RpcRequest invocation) {
        HttpClient httpClient = HttpClient.getInstance();
        return (String)httpClient.post(url.getHost(),url.getPort(),invocation);
    }
}
