package com.paul.loadbalance.impl;

import com.paul.framework.ServiceProvider;
import com.paul.loadbalance.LoadStrategy;
import jdk.management.resource.internal.ResourceNatives;

import java.util.List;
import java.util.Random;

public class RandomLoadStrategy implements LoadStrategy {
    @Override
    public ServiceProvider select(List<ServiceProvider> providers) {
        int m = providers.size();
        Random r = new Random();
        int index = r.nextInt(m);
        return providers.get(index);
    }
}
