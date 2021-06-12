package org.cloudbus.cloudsim.sfc.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SFCWorkload {
    private String targetChainName;
    private int chainInputSize;
    private int startTime;
    private int endTime;
    private List<SFCRequest> sfcRequestList;

    public SFCWorkload(ServerFunctionChain serverFunctionChain){
        targetChainName = serverFunctionChain.getName();
        chainInputSize = serverFunctionChain.getAverageInputSize();
        startTime = serverFunctionChain.getCreateTime();
        endTime = serverFunctionChain.getDestroyTime();
        sfcRequestList = new ArrayList<>();

        generateRequest(serverFunctionChain);
    }

    private void generateRequest(ServerFunctionChain serverFunctionChain){
        int time = startTime;
        Random random = new Random();
        List<String> chain = serverFunctionChain.getChain();
        while(time <= endTime){
            SFCRequest sfcRequest = new SFCRequest(time);
            int index = 0,performance,outputSize;
            int inputSize = chainInputSize + random.nextInt(2)*10;

            for(;index < chain.size();index++){
                ServerFunction serverFunction = ServerFunction.serverFunctionMap.get(chain.get(index));
                performance = serverFunction.getPerformance()*inputSize;
                outputSize = serverFunction.getOutputSize(inputSize);
                sfcRequest.fillRequest(inputSize, performance, outputSize);
                inputSize = outputSize;
            }

            sfcRequestList.add(sfcRequest);
            time += 10;
        }


    }

    public String getTargetChainName() {
        return targetChainName;
    }

    public void setTargetChainName(String targetChainName) {
        this.targetChainName = targetChainName;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndtTime() {
        return endTime;
    }

    public void setEndtTime(int endtTime) {
        this.endTime = endtTime;
    }

    public List<SFCRequest> getSfcRequestList() {
        return sfcRequestList;
    }

    public void setSfcRequestList(List<SFCRequest> sfcRequestList) {
        this.sfcRequestList = sfcRequestList;
    }

    public int getChainInputSize() {
        return chainInputSize;
    }

    public void setChainInputSize(int chainInputSize) {
        this.chainInputSize = chainInputSize;
    }

    @Override
    public String toString() {
        Random random = new Random();
        String sfcRequests = "";
        for(int i =0; i < 10; i++){
            int index = random.nextInt(sfcRequestList.size());
            sfcRequests += sfcRequestList.get(index).toString() + ',';
        }
        sfcRequests = '{' + sfcRequests + '}';

        return "SFCWorkload{" +
                "targetChainName='" + targetChainName + '\'' +
                ", chainInputSize=" + chainInputSize +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", sfcRequestList(ten of all)=" + sfcRequests +
                '}';
    }
}
