package org.cloudbus.cloudsim.sfc.parser.configGenerator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.parser.InOutDc;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunction;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunctionChain;
import org.cloudbus.cloudsim.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName ServiceFunctionChainGenerator.java
 * @Description TODO
 * @createTime 2021-07-24 15:36
 */
public class ServiceFunctionChainGenerator {
    private final String rootPath = "SFCExampleConfig2/";
    private final String SFCDemandFileName = "SFCDemandFromAuto.json";
    private final String serverFunctionFileName = "ServiceFunctionDescription.json";
    private List<ServiceFunction> serverFunctions;

    public ServiceFunctionChainGenerator(List<ServiceFunction> serviceFunctions) {
        this.serverFunctions = serviceFunctions;
    }

    public ServiceFunctionChainGenerator() {
    }

    public void loadServiceFunctions(){
        if (null != serverFunctions) {
            return;
        }
        String path = rootPath + serverFunctionFileName;
        String jsonStr = JsonUtils.jsonRead(path);
        serverFunctions = JSONObject.parseObject(jsonStr, new TypeReference<List<ServiceFunction>>() {});
    }

    public List<ServiceFunctionChain> serverFunctionChainsGenerate(){
        List<ServiceFunctionChain> serviceFunctionChainList = new ArrayList<>();
        int start = 3;//0x00000011
        int end = 62;//0x00011110;
        InOutDc ingress = new InOutDc("Ingress1",1,"edge1");
        InOutDc egress  = new InOutDc("Egress1",1,"edge2");
        for (; start < end; start++) {
            List<String> chain = new ArrayList<>();
            if ((start & 0x00000001) == 1) {
                if (new Random().nextDouble() <= 0.5){
                    chain.add(serverFunctions.get(0).getName());
                }
            }

            if ((start & 0x00000002) == 2) {
                chain.add(serverFunctions.get(1).getName());
            }

            if ((start & 0x00000004) == 4) {
                chain.add(serverFunctions.get(2).getName());
            }

            if ((start & 0x00000008) == 8) {
                chain.add(serverFunctions.get(3).getName());
            }

            if ((start & 0x000000010) == 16) {
                chain.add(serverFunctions.get(4).getName());
            }
            if(chain.size() < 2 || chain.size() > 4) {
                continue;
            }
            ServiceFunctionChain serviceFunctionChain = new ServiceFunctionChain();
            serviceFunctionChain.setName("sfc-"+start);
            serviceFunctionChain.getChain().addAll(chain);
            serviceFunctionChain.setAverageInputSize(110);
            serviceFunctionChain.getIngressDCs().add(ingress);
            serviceFunctionChain.getEgressDCs().add(egress);
            serviceFunctionChain.setCreateTime(0);
            serviceFunctionChain.setDestroyTime(10000);
            serviceFunctionChainList.add(serviceFunctionChain);
        }
        return serviceFunctionChainList;
    }

    public void run(){
        loadServiceFunctions();
        List<ServiceFunctionChain> serviceFunctionChainList = serverFunctionChainsGenerate();
        //JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask();
        //String jsonStr = JSON.toJSONString(serviceFunctionChainList, SerializerFeature.PrettyFormat);
        String jsonStr = JSON.toJSONString(serviceFunctionChainList);
        JsonUtils.jsonWrite(rootPath + SFCDemandFileName, jsonStr);
    }
}
