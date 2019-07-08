package com.paul.loadbalance;

import com.paul.framework.ServiceProvider;

import java.util.List;

/**
 * 负载均衡算法接口
 */
public interface LoadStrategy {

    public ServiceProvider select(List<ServiceProvider> providers);

}
