package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
//import org.cloudbus.cloudsim.sfc.scenariomanager.InOutDc;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunction;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunctionChain;
import org.cloudbus.cloudsim.sfc.scenariomanager.InOutDc;
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
    private final String serviceFunctionFileName = "ServiceFunctionDescription.json";
    private List<ServiceFunction> serviceFunctions;

    public ServiceFunctionChainGenerator(List<ServiceFunction> serviceFunctions) {
        this.serviceFunctions = serviceFunctions;
    }

    public ServiceFunctionChainGenerator() {
    }

    public void loadServiceFunctions() {
        if (null != serviceFunctions) {
            return;
        }
        String path = rootPath + serviceFunctionFileName;
        String jsonStr = JsonUtils.jsonRead(path);
        serviceFunctions = JSONObject.parseObject(jsonStr, new TypeReference<List<ServiceFunction>>() {
        });
    }

    public List<ServiceFunctionChain> serviceFunctionChainsGenerate() {
        List<ServiceFunctionChain> serviceFunctionChainList = new ArrayList<>();
        int start = 3;// 0x00000011
        int end = 62;// 0x00011110;
        InOutDc ingress = new InOutDc("Ingress1", 1, "edge1");
        InOutDc egress = new InOutDc("Egress1", 1, "edge2");
        for (; start < end; start++) {
            List<String> chain = new ArrayList<>();
            if ((start & 0x00000001) == 1) {
                if (new Random().nextDouble() <= 0.5) {
                    chain.add(serviceFunctions.get(0).getName());
                }
            }

            if ((start & 0x00000002) == 2) {
                chain.add(serviceFunctions.get(1).getName());
            }

            if ((start & 0x00000004) == 4) {
                chain.add(serviceFunctions.get(2).getName());
            }

            if ((start & 0x00000008) == 8) {
                chain.add(serviceFunctions.get(3).getName());
            }

            if ((start & 0x000000010) == 16) {
                chain.add(serviceFunctions.get(4).getName());
            }
            if (chain.size() < 2 || chain.size() > 4) {
                continue;
            }
            ServiceFunctionChain serviceFunctionChain = new ServiceFunctionChain();
            serviceFunctionChain.setName("sfc-" + start);
            serviceFunctionChain.getChain().addAll(chain);
            serviceFunctionChain.setAverageInputSize(110);
            serviceFunctionChain.getIngressDCs().add(ingress);
            // serviceFunctionChain.getEgressDCs().add(egress);
            serviceFunctionChain.setEgressDC(egress);
            serviceFunctionChain.setCreateTime(0);
            serviceFunctionChain.setDestroyTime(10000);
            serviceFunctionChainList.add(serviceFunctionChain);
        }
        return serviceFunctionChainList;
    }

    public void run() {
        loadServiceFunctions();
        List<ServiceFunctionChain> serviceFunctionChainList = serviceFunctionChainsGenerate();
        // JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE &
        // ~SerializerFeature.SortField.getMask();
        // String jsonStr = JSON.toJSONString(serviceFunctionChainList,
        // SerializerFeature.PrettyFormat);
        String jsonStr = JSON.toJSONString(serviceFunctionChainList);
        JsonUtils.jsonWrite(rootPath + SFCDemandFileName, jsonStr);
    }
}
