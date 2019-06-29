package com.paul.procotol.http;

import com.paul.framework.RpcRequest;
import com.paul.procotol.Procotol;
import com.paul.framework.URL;

public class HttpProcotol implements Procotol {
    @Override
    public void start(URL url) {
        HttpServer httpServer = HttpServer.getInstance();
        httpServer.start(url.getHost(),url.getPort());
    }

    @Override
    public Object send(URL url, RpcRequest invocation) {
        HttpClient httpClient = HttpClient.getInstance();
        return httpClient.post(url.getHost(),url.getPort(),invocation);
    }
}
