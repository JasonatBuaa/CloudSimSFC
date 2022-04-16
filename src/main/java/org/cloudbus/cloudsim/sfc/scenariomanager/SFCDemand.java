package org.cloudbus.cloudsim.sfc.scenariomanager;

import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import org.cloudbus.cloudsim.util.JsonUtils;
// import com.alibaba.fastjson.annotation.JSONType;

// @JSONType(orders = { "Name", "Weight", "DC" })
class IEDC {
    @JSONField(name = "Name", ordinal = 1)
    private String Name;
    @JSONField(name = "Weight", ordinal = 2)
    private int Weight = 1;
    @JSONField(name = "DC", ordinal = 3)
    private String DC;

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setWeight(int Weight) {
        this.Weight = Weight;
    }

    public void setDC(String DC) {
        this.DC = DC;
    }

    public String getName() {
        return this.Name;
    }

    public int getWeight() {
        return this.Weight;
    }

    public String getDC() {
        return this.DC;
    }

}

public class SFCDemand {
    @JSONField(name = "Name", ordinal = 1)
    private String name;

    @JSONField(name = "Chain", ordinal = 2)
    private ArrayList<String> Chain = new ArrayList<String>();

    @JSONField(name = "IngressDCs", ordinal = 3)
    private ArrayList<IEDC> IngressDCs = new ArrayList<IEDC>();

    @JSONField(name = "EgressDC", ordinal = 4)
    private IEDC EgressDC = new IEDC();

    @JSONField(name = "AverageInputSize", ordinal = 5)
    private int AverageInputSize = (int) (Math.random() * 6 + 10);

    @JSONField(name = "CreateTime", ordinal = 6)
    private int CreateTime = 0;

    @JSONField(name = "DestroyTime", ordinal = 7)
    private int DestroyTime = 500;

    public IEDC MakeIngress(String ijoinj) {
        IEDC ingress = new IEDC();
        ingress.setDC("DC1");
        ingress.setName("Ingress" + ijoinj);
        return ingress;
    }

    public IEDC MakeEgress(int i) {
        IEDC egress = new IEDC();
        egress.setDC("DC2");
        egress.setName("Egress" + i);
        return egress;
    }

    public void setName(int i) {
        name = "chain" + i;
    }

    public void setName(String ingress, String egress) {
        name = "sfc-" + ingress + egress;
    }

    public void setChain(int chainLen) {
        ArrayList<Integer> no = new ArrayList<Integer>();
        ArrayList<String> chain = new ArrayList<String>();
        ArrayList<Integer> SFs = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            SFs.add(i);
        }
        for (int i = 0; i < chainLen; i++) {
            int a = (SFs.remove(new Random().nextInt(SFs.size())));
            no.add(a);
        }
        no.sort(Comparator.naturalOrder());
        no.forEach((e) -> {
            chain.add("SF-" + (char) (e + (int) 'A'));
        });
        Chain = chain;
    }

    /**
     * Generate a test chain, with a length of {1, 2, 3}
     *
     * @param chainLen
     * @param serviceFunctionDescriptionFileName
     */
    public void compSetChain(int chainLen, String serviceFunctionDescriptionFileName) {
        ArrayList<String> sfc = new ArrayList<String>();
        ArrayList<Integer> SFTypes = new ArrayList<Integer>();

        List<ServiceFunction> serviceFunctions;

        String jsonStr = JsonUtils.jsonRead(serviceFunctionDescriptionFileName);
        serviceFunctions = JSONObject.parseObject(jsonStr, new TypeReference<List<ServiceFunction>>() {
        });

            if(chainLen <= 1){
                // len=1
                sfc.add(serviceFunctions.get(0).getName());
            }
            else if (chainLen <=2){
                // len=2
                sfc.add(serviceFunctions.get(1).getName());
                sfc.add(serviceFunctions.get(2).getName());
            }
            else{ // rdn \in [0.67, 1]
                // len=3
                sfc.add(serviceFunctions.get(0).getName());
                sfc.add(serviceFunctions.get(1).getName());
                sfc.add(serviceFunctions.get(2).getName());
            }

        Chain = sfc;
//        for (int i = 0; i < 5; i++) {
//            SFs.add(i);
//        }
//        for (int i = 0; i < num; i++) {
//            int a = (SFs.remove(new Random().nextInt(SFs.size())));
//            no.add(a);
//        }
//        no.sort(Comparator.naturalOrder());
//        no.forEach((e) -> {
//            chain.add("SF-" + (char) (e + (int) 'A'));
//        });
//        Chain = chain;
    }

    public void loadServiceFunctions(String serviceFunctionDescriptionFileName) {

    }



    /**
     *
     * @param i index of the Ingress node in the current DC
     * @param num sub_index of the Ingress node (i.e., ingress count of the current virtual chain)
     */
    public void setIngressDCs(int i, int num) {
        ArrayList<IEDC> InDCs = new ArrayList<IEDC>();
        for (int j = 1; j < num + 1; j++) {
            String ijoinj = num == 1 ? "" + i : "" + i + "-" + j;
            IEDC ingressDc = MakeIngress(ijoinj);
            InDCs.add(ingressDc);
        }
        IngressDCs = InDCs;
    }

    public void setEgressDC(int i) {
        EgressDC = MakeEgress(i);
    }

    public void setAverageInputSize() {
        int a = (int) (Math.random() * 6 + 10);
        AverageInputSize = a;
    }

    public void setCreateTime(int createTime) {
        CreateTime = createTime;
    }

    public void setDestroyTime(int destroyTime) {
        DestroyTime = destroyTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getChain() {
        return Chain;
    }

    public ArrayList<IEDC> getIngressDCs() {
        return IngressDCs;
    }

    public IEDC getEgressDC() {
        return EgressDC;
    }

    public int getAverageInputSize() {
        return AverageInputSize;
    }

    public int getCreateTime() {
        return CreateTime;
    }

    public int getDestroyTime() {
        return DestroyTime;
    }

}
