package org.cloudbus.cloudsim.sfc.parser;

import java.util.ArrayList;
import java.util.List;

public class SFCRequest {
    private float startTime;
    private String ingress;
    private List<Integer> transmissions;
    private List<Integer> cloudletLengths;
    private String egress;

    public SFCRequest(float startTime) {
        this.startTime = startTime;
        transmissions = new ArrayList<>();
        cloudletLengths = new ArrayList<>();
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public String getIngress() {
        return ingress;
    }

    public void setIngress(String ingress) {
        this.ingress = ingress;
    }

    public List<Integer> getTransmissions() {
        return transmissions;
    }

    public void setTransmissions(List<Integer> transmissions) {
        this.transmissions = transmissions;
    }

    public List<Integer> getCloudletLengths() {
        return cloudletLengths;
    }

    public void setCloudletLengths(List<Integer> cloudletLengths) {
        this.cloudletLengths = cloudletLengths;
    }

    public String getEgress() {
        return egress;
    }

    public void setEgress(String egress) {
        this.egress = egress;
    }

    public void fillRequest(int inputSize, int performance){
        transmissions.add(inputSize);
        cloudletLengths.add(performance);
    }

    public String requestsToString(){
        String request = "";
        for(int index = 0;index < transmissions.size(); index++){
            request += "," + transmissions.get(index) + "," + cloudletLengths.get(index) ;
        }
        return request;

    }
    @Override
    public String toString() {
        String request = "";
        for(int index = 0;index < transmissions.size(); index++){
            request += "{" + transmissions.get(index)
                        + "," + cloudletLengths.get(index)
                        +"},";
        }
        request = "[" + request + "]";
        return "SFCRequest{" +
                "startTime=" + startTime +
                ", Request(transmissions,cloudletLengths)=" + request +
                '}';
    }
}
