package com.paul.procotol.socket;

import com.paul.framework.Procotol;
import com.paul.framework.RpcRequest;
import com.paul.framework.URL;

public class SocketProcotol implements Procotol {
    @Override
    public void start(URL url) {
        SocketServer socketServer = SocketServer.getInstance();
        socketServer.publiser(url.getPort());
    }

    @Override
    public Object send(URL url, RpcRequest invocation) {
        SocketClient socketClient = SocketClient.getInstance();
        return socketClient.sendRequest(url.getHost(),url.getPort(),invocation);
    }
}
