package com.paul.register;

import java.util.List;
import java.util.Map;

import com.paul.framework.ServiceConsumer;
import com.paul.framework.ServiceProvider;

public interface RegisterCenter4Consumer {

    /**
     * 消费端初始化服务提供者信息本地缓存
     */
    public void initProviderMap();

    /**
     * 消费端获取服务提供者信息
     * @return
     */
    public Map<String,List<ServiceProvider>> getServiceMetaDataMap4Consumer();

    /**
     * 消费端将消费者信息注册到 zookeeper 对应的节点下
     * @param invokers
     */
    public void registerConsumer(final List<ServiceConsumer> invokers);
}
