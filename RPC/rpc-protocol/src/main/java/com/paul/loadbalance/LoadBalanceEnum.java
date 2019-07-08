package com.paul.loadbalance;

public enum LoadBalanceEnum {

    //随机算法
    Random("Random"),
    //权重随机算法
    //WeightRandom("WeightRandom"),
    //轮询算法
    Polling("Polling"),
    //权重轮询算法
    //WeightPolling("WeightPolling"),
    //源地址hash算法
    Hash("Hash");

    private LoadBalanceEnum(String desc){
        this.desc = desc;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static LoadBalanceEnum queryByCode(String desc) {
        if (null == desc || "".equals(desc)) {
            return null;
        }
        for (LoadBalanceEnum strategy : values()) {
            if (desc.equals(strategy.getDesc())) {
                return strategy;
            }
        }
        return null;
    }
}
