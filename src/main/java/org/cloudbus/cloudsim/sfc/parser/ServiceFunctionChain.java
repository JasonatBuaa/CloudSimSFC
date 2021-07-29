package org.cloudbus.cloudsim.sfc.parser;

import java.util.ArrayList;
import java.util.List;

public class ServiceFunctionChain {
    private String Name;
    private List<String> Chain;
    private int AverageInputSize;
    private List<InOutDc> IngressDCs;
    private InOutDc EgressDC;
    private int CreateTime;
    private int DestroyTime;

    public ServiceFunctionChain(String name, List<String> chain, int averageInputSize, List<InOutDc> ingressDCs,
            InOutDc egressDC, int createTime, int destroyTime) {
        Name = name;
        Chain = chain;
        AverageInputSize = averageInputSize;
        IngressDCs = ingressDCs;
        EgressDC = egressDC;
        CreateTime = createTime;
        DestroyTime = destroyTime;
    }

    public ServiceFunctionChain() {
        Chain = new ArrayList<>();
        IngressDCs = new ArrayList<>();
        // EgressDC = new ArrayList<>();
    }

    public List<InOutDc> getIngressDCs() {
        return IngressDCs;
    }

    public void setIngressDCs(List<InOutDc> ingressDCs) {
        IngressDCs = ingressDCs;
    }

    public void addIngressDCs() {

    }

    public InOutDc getEgressDC() {
        return EgressDC;
    }

    public void setEgressDC(InOutDc egressDC) {
        EgressDC = egressDC;
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
        for (String c : Chain) {
            chainStr += c + ",";
        }
        chainStr = "{" + chainStr + "}";

        String inDcs = "";
        for (InOutDc inDc : IngressDCs) {
            inDcs += inDc.toString() + ",";
        }
        inDcs = '[' + inDcs + ']';

        // String outDcs = "";
        // for (InOutDc outDc : EgressDC) {
        // outDcs += outDc.toString() + ",";
        // }

        // outDcs = '[' + outDcs + ']';

        String outDcs = EgressDC.toString();

        return "ServiceFunctionChain{" + "\nName='" + Name + "'," + "\nChain=" + chainStr + ',' + "\nIngressDCs="
                + inDcs + ',' + "\nEgressDC=" + outDcs + ',' + "\nAverageInputSize=" + AverageInputSize + ','
                + "\nCreateTime=" + CreateTime + ',' + "\nDestroyTime=" + DestroyTime + ',' + "}\n";
    }

}
