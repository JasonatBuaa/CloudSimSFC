package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.parser.ServerFunctionChain;

import static org.junit.jupiter.api.Assertions.*;

class ServerFunctionChainTest {
    @org.junit.jupiter.api.Test
    public void init(){
        String jsonStr = "{\n" +
                "  \"Name\": \"chain1\",\n" +
                "  \"Chain\": [\n" +
                "    \"SF-A\",\n" +
                "    \"SF-B\",\n" +
                "    \"SF-C\",\n" +
                "    \"SF-D\"],\n" +
                "  \"AverageInputSize\": 100,\n" +
                "  \"CreateTime\": 3700,\n" +
                "  \"DestroyTime\":10001\n" +
                "}";
        ServerFunctionChain serverFunctionChain = JSONObject.parseObject(jsonStr, new TypeReference<ServerFunctionChain>(){});
        System.out.println(serverFunctionChain.toString());
    }

}