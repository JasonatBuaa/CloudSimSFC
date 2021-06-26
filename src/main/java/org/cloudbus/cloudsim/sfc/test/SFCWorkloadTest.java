package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.parser.SFCRequest;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServerFunction;
import org.cloudbus.cloudsim.sfc.parser.ServerFunctionChain;

import java.util.List;

class SFCWorkloadTest {
    @org.junit.jupiter.api.Test
    public void init(){
        String jsonStr0 = "[\n" +
                "  {\n" +
                "    \"Name\": \"chain1\",\n" +
                "    \"Chain\": [\n" +
                "      \"SF-A\",\n" +
                "      \"SF-B\",\n" +
                "      \"SF-C\",\n" +
                "      \"SF-D\"],\n" +
                "    \"IngressDCs\": [{\"Name\":\"Ingress1\",\"Weight\":0.5},{\"Name\":\"Ingress2\",\"Weight\":0.5}],\n" +
                "    \"EgressDCs\": [{\"Name\":\"Egress1\",\"Weight\":1}],\n" +
                "    \"AverageInputSize\": 100,\n" +
                "    \"CreateTime\": 3700,\n" +
                "    \"DestroyTime\":10001\n" +
                "  },\n" +
                "\n" +
                "  {\n" +
                "    \"Name\": \"chain2\",\n" +
                "    \"Chain\": [\n" +
                "      \"SF-A\",\n" +
                "      \"SF-B\",\n" +
                "      \"SF-D\"],\n" +
                "    \"IngressDCs\": [{\"Name\":\"Ingress3\",\"Weight\":0.3},{\"Name\":\"Ingress4\",\"Weight\":0.7}],\n" +
                "    \"EgressDCs\": [{\"Name\":\"Egress2\",\"Weight\":1}],\n" +
                "    \"AverageInputSize\": 110,\n" +
                "    \"CreateTime\": 0,\n" +
                "    \"DestroyTime\":10001\n" +
                "  }\n" +
                "]";
        List<ServerFunctionChain> serverFunctionChains = JSONObject.parseObject(jsonStr0, new TypeReference<List<ServerFunctionChain>>(){});

        String jsonStr1 = "[\n" +
                "  {\n" +
                "    \"Name\": \"SF-A\",\n" +
                "    \"Type\": \"FireWall\",\n" +
                "    \"InputRate\": 1.0,\n" +
                "    \"OutputRate\": 1.5,\n" +
                "    \"Performance\":1000\n" +
                "  },\n" +
                "  {\n" +
                "    \"Name\": \"SF-B\",\n" +
                "    \"Type\": \"LoadBalance\",\n" +
                "    \"InputRate\": 1.5,\n" +
                "    \"OutputRate\": 1.0,\n" +
                "    \"Performance\":500\n" +
                "  },\n" +
                "  {\n" +
                "    \"Name\": \"SF-C\",\n" +
                "    \"Type\": \"ApacheServer\",\n" +
                "    \"InputRate\": 1.2,\n" +
                "    \"OutputRate\": 1.0,\n" +
                "    \"Performance\":100\n" +
                "  },\n" +
                "  {\n" +
                "    \"Name\": \"SF-D\",\n" +
                "    \"Type\": \"Other\",\n" +
                "    \"InputRate\": 1.0,\n" +
                "    \"OutputRate\": 1.0,\n" +
                "    \"Performance\":300\n" +
                "  }\n" +
                "]";
        List<ServerFunction> serverFunctions =
                JSONObject.parseObject(jsonStr1, new TypeReference<List<ServerFunction>>(){});

        for(ServerFunctionChain serverFunctionChain : serverFunctionChains){
            SFCWorkload sfcWorkload = new SFCWorkload(serverFunctionChain);
            for(SFCRequest sfcRequest : sfcWorkload.getSfcRequestList()){
                System.out.println(sfcRequest.getStartTime()+ ","+
                                    sfcWorkload.getTargetChainName() + "," +
                                    sfcRequest.getIngress() + "," +
                                    sfcRequest.requestsToString() +
                                    sfcRequest.getEgress() + "," +
                                    sfcWorkload.getLatencyDemand());
            }

        }



    }

}