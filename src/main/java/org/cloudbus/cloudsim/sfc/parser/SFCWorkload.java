package org.cloudbus.cloudsim.sfc.parser;

import java.util.*;

public class SFCWorkload {
    public String targetChainName;
    public List<ServerFunctionChain.InOutDc> ingress;
    public List<ServerFunctionChain.InOutDc> egress;
    public int chainInputSize;
    public int startTime;
    public int endTime;
    public List<SFCRequest> sfcRequestList;
    public double latencyDemand = 10;
    public LinkedList<InEgressNode> inEgressNodes;
    public int baseWeight;
    public int chainLength;

    public SFCWorkload(ServerFunctionChain serverFunctionChain){
        startTime = serverFunctionChain.getCreateTime();
        endTime = serverFunctionChain.getDestroyTime();
        ingress = serverFunctionChain.getIngressDCs();
        egress = serverFunctionChain.getEgressDCs();
        targetChainName = serverFunctionChain.getName();
        chainInputSize = serverFunctionChain.getAverageInputSize();
        chainLength = serverFunctionChain.getChain().size();
        inEgressNodes = new LinkedList<>();
        sfcRequestList = new ArrayList<>();
        initInEgressNode();
        generateRequest(serverFunctionChain);
    }

    private void generateRequest(ServerFunctionChain serverFunctionChain){
        int requestSize = (endTime - startTime)/10;
        int count = 0;
        Random random = new Random();
        List<String> chain = serverFunctionChain.getChain();
        while(count <= requestSize){
            int time = count * 10 + startTime;
            SFCRequest sfcRequest = new SFCRequest(time);
            setInEgress(sfcRequest,count);
            int index = 0,performance,outputSize=0;
            int inputSize = chainInputSize + random.nextInt(2)*10;

            for(;index < chain.size();index++){
                ServerFunction serverFunction = ServerFunction.serverFunctionMap.get(chain.get(index));
                performance = serverFunction.getPerformance()*inputSize;
                outputSize = serverFunction.getOutputSize(inputSize);
                sfcRequest.fillRequest(chain.get(index),inputSize, performance);
                inputSize = outputSize;
            }
            //transmission to Egress
            sfcRequest.setOutput(outputSize);
            sfcRequestList.add(sfcRequest);
            count++;
        }


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

    public void initInEgressNode(){
        float inWeight = 0, outWeight = 0;
        for(ServerFunctionChain.InOutDc in : ingress){
            inWeight += in.getWeight() * 10;
        }
        for(ServerFunctionChain.InOutDc out : egress){
            outWeight += out.getWeight() * 10;
        }
        baseWeight = new Float(inWeight * outWeight).intValue();

        int  ingressSize = ingress.size();
        int egressSize = egress.size();
        for(int i = 0; i < ingressSize; i++){
            for(int j = 0; j < egressSize; j++){
                inEgressNodes.addLast(new InEgressNode(ingress.get(i).getName(),egress.get(j).getName(),new Float(ingress.get(i).getWeight()* egress.get(j).getWeight()*100).intValue()));
            }
        }
    }

    public void setInEgress(SFCRequest sfcRequest,int count){
        count %= baseWeight;
        for(Iterator<InEgressNode> iterator = inEgressNodes.iterator();iterator.hasNext();){
            InEgressNode now = iterator.next();
            if(count<now.weight || count <= 0){
                sfcRequest.setIngress(now.ingress);
                sfcRequest.setEgress(now.egress);
                break;
            }
            count -= now.weight;
        }
    }

    public String getTargetChainName() {
        return targetChainName;
    }

    public void setTargetChainName(String targetChainName) {
        this.targetChainName = targetChainName;
    }

    public List<ServerFunctionChain.InOutDc> getIngress() {
        return ingress;
    }

    public void setIngress(List<ServerFunctionChain.InOutDc> ingress) {
        this.ingress = ingress;
    }

    public List<ServerFunctionChain.InOutDc> getEgress() {
        return egress;
    }

    public void setEgress(List<ServerFunctionChain.InOutDc> egress) {
        this.egress = egress;
    }

    public int getChainInputSize() {
        return chainInputSize;
    }

    public void setChainInputSize(int chainInputSize) {
        this.chainInputSize = chainInputSize;
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

    public int getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(int baseWeight) {
        this.baseWeight = baseWeight;
    }

    public int getChainLength() {
        return chainLength;
    }

    public void setChainLength(int chainLength) {
        this.chainLength = chainLength;
    }

    class InEgressNode{
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
