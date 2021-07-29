package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunctionChain;

import java.util.List;

class ServiceFunctionChainTest {
    @org.junit.jupiter.api.Test
    public void init() {
        String jsonStr = "[\n" + "  {\n" + "    \"Name\": \"chain1\",\n" + "    \"Chain\": [\n" + "      \"SF-A\",\n"
                + "      \"SF-B\",\n" + "      \"SF-C\",\n" + "      \"SF-D\"],\n"
                + "    \"IngressDCs\": [{\"Name\":\"Ingress1\",\"Weight\":0.5},{\"Name\":\"Ingress2\",\"Weight\":0.5}],\n"
                + "    \"EgressDC\": [{\"Name\":\"Egress1\",\"Weight\":1}],\n" + "    \"AverageInputSize\": 100,\n"
                + "    \"CreateTime\": 3700,\n" + "    \"DestroyTime\":10001\n" + "  },\n" + "\n" + "  {\n"
                + "    \"Name\": \"chain2\",\n" + "    \"Chain\": [\n" + "      \"SF-A\",\n" + "      \"SF-B\",\n"
                + "      \"SF-D\"],\n"
                + "    \"IngressDCs\": [{\"Name\":\"Ingress3\",\"Weight\":0.3},{\"Name\":\"Ingress4\",\"Weight\":0.7}],\n"
                + "    \"EgressDC\": [{\"Name\":\"Egress2\",\"Weight\":1}],\n" + "    \"AverageInputSize\": 110,\n"
                + "    \"CreateTime\": 0,\n" + "    \"DestroyTime\":10001\n" + "  }\n" + "]";
        List<ServiceFunctionChain> serverFunctionChains = JSONObject.parseObject(jsonStr,
                new TypeReference<List<ServiceFunctionChain>>() {
                });
        for (ServiceFunctionChain serviceFunctionChain : serverFunctionChains) {
            System.out.println(serviceFunctionChain.toString());
        }

    }

}