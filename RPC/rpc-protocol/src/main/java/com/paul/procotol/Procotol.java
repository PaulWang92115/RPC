package com.paul.procotol;

import com.paul.framework.RpcRequest;
import com.paul.framework.URL;
import com.paul.serializer.SerializeType;

public interface Procotol {

    void start(URL url);
    Object send(URL url, RpcRequest invocation);
}
