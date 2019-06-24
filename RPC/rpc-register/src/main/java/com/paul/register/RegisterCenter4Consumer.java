package com.paul.register;

public interface RegisterCenter4Consumer {

    /**
     * 消费端初始化服务提供者信息本地缓存
     */
    public void initProviderMap();

    /**
     * 消费端获取服务提供者信息
     * @return
     */
    public Map<String,List<ProviderService>> getServiceMetaDataMap4Consumer();

    /**
     * 消费端将消费者信息注册到 zookeeper 对应的节点下
     * @param invoker
     */
    public void registerConsumer(final ConsumerService invoker);
}
