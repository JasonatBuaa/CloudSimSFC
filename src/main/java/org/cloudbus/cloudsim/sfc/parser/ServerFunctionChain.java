package org.cloudbus.cloudsim.sfc.parser;

import java.util.List;

public class ServerFunctionChain {
    private String Name;
    private List<String> Chain;
    private int AverageInputSize;
    private int CreateTime;
    private int DestroyTime;

    public ServerFunctionChain(String name, List<String> chain, int averageInputSize, int createTime, int destroyTime) {
        Name = name;
        Chain = chain;
        AverageInputSize = averageInputSize;
        CreateTime = createTime;
        DestroyTime = destroyTime;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<String> getChain() {
        return Chain;
    }

    public void setChain(List<String> chain) {
        Chain = chain;
    }

    public int getAverageInputSize() {
        return AverageInputSize;
    }

    public void setAverageInputSize(int averageInputSize) {
        AverageInputSize = averageInputSize;
    }

    public int getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(int createTime) {
        CreateTime = createTime;
    }

    public int getDestroyTime() {
        return DestroyTime;
    }

    public void setDestroyTime(int destroyTime) {
        DestroyTime = destroyTime;
    }

    @Override
    public String toString() {
        String chainStr = "";
        for(String c : Chain) {
            chainStr += c + ",";
        }
        chainStr = "{" + chainStr + "}";
        return "ServerFunctionChain{" +
                "Name='" + Name + '\'' +
                ", Chain=" + chainStr +
                ", AverageInputSize=" + AverageInputSize +
                ", CreateTime=" + CreateTime +
                ", DestroyTime=" + DestroyTime +
                '}';
    }
}
