package org.cloudbus.cloudsim.sfc.scenariomanager;

import org.cloudbus.cloudsim.distributions.UniformDistr;

import java.util.*;

public class SFCWorkloadForSDNNFV {
    public String targetChainName;
    public List<InOutDc> ingress;
    public InOutDc egress;
    public int startTime;
    public int endTime;
    public List<SFCRequest> sfcRequestList;
    public double latencyDemand = 1000;
    public LinkedList<InEgressNode> inEgressNodes;
    public int chainLength;



    public SFCWorkloadForSDNNFV(SFCWorkload SFCwl) {
        this.targetChainName = SFCwl.targetChainName;
        this.ingress = SFCwl.ingress;
        this.egress = SFCwl.egress;
        parseRequestsForSDNNFV(SFCwl.sfcRequestList);
    }

    public void parseRequestsForSDNNFV(List<SFCRequest> sfcRequestListOrigin){
        this.sfcRequestList = new ArrayList<SFCRequest>();
        for(SFCRequest r:sfcRequestListOrigin){
            
        }



    }



    public String getTargetChainName() {
        return targetChainName;
    }

    public void setTargetChainName(String targetChainName) {
        this.targetChainName = targetChainName;
    }

    public List<InOutDc> getIngress() {
        return ingress;
    }

    public void setIngress(List<InOutDc> ingress) {
        this.ingress = ingress;
    }

    public InOutDc getEgress() {
        return egress;
    }

    public void setEgress(InOutDc egress) {
        this.egress = egress;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public List<SFCRequest> getSfcRequestList() {
        return sfcRequestList;
    }

    public void setSfcRequestList(List<SFCRequest> sfcRequestList) {
        this.sfcRequestList = sfcRequestList;
    }

    public double getLatencyDemand() {
        return latencyDemand;
    }

    public void setLatencyDemand(double latencyDemand) {
        this.latencyDemand = latencyDemand;
    }

    public LinkedList<InEgressNode> getInEgressNodes() {
        return inEgressNodes;
    }

    public void setInEgressNodes(LinkedList<InEgressNode> inEgressNodes) {
        this.inEgressNodes = inEgressNodes;
    }

    public int getChainLength() {
        return chainLength;
    }

    public void setChainLength(int chainLength) {
        this.chainLength = chainLength;
    }

    class InEgressNode {
        private String ingress;
        private String egress;
        private int weight;

        public InEgressNode(String ingress, String egress, int weight) {
            this.ingress = ingress;
            this.egress = egress;
            this.weight = weight;
        }
    }
}
