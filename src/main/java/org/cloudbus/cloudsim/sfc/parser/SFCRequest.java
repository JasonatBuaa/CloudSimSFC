package org.cloudbus.cloudsim.sfc.parser;

import java.util.ArrayList;
import java.util.List;

public class SFCRequest {
    private int startTime;
    private List<Integer> inputSizes;
    private List<Integer> performanceNeeds;
    private List<Integer> outputSizes;

    public SFCRequest(int startTime){
        this.startTime = startTime;
        inputSizes = new ArrayList<>();
        performanceNeeds = new ArrayList<>();
        outputSizes = new ArrayList<>();
    }

    public int getStartTime() {
        return startTime;
    }

    private void addInputSizes(int inputSize) {
        inputSizes.add(inputSize);
    }

    private void addPerformanceNeeds(int performance) {
        performanceNeeds.add(performance);
    }

    private void addOutputSizes(int outputSize) {
        outputSizes.add(outputSize);
    }

    public void fillRequest(int inputSize, int performance, int outputSize){
        inputSizes.add(inputSize);
        performanceNeeds.add(performance);
        outputSizes.add(outputSize);
    }

    @Override
    public String toString() {
        String request = "";
        int index = 0;
        for(;index < inputSizes.size(); index++){
            request += "{" + inputSizes.get(index)
                        + "," + performanceNeeds.get(index)
                        + "," + outputSizes.get(index)
                        +"},";

        }
        request = "[" + request + "]";
        return "SFCRequest{" +
                "startTime=" + startTime +
                ", Request(inputSizes=,performanceNeeds,,outputSizes)=" + request +
                '}';
    }
}
