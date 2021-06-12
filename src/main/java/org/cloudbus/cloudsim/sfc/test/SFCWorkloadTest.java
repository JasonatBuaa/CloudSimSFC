package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServerFunction;
import org.cloudbus.cloudsim.sfc.parser.ServerFunctionChain;

import java.util.List;

class SFCWorkloadTest {
    @org.junit.jupiter.api.Test
    public void init(){
        String jsonStr0 = "{\n" +
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
        ServerFunctionChain serverFunctionChain = JSONObject.parseObject(jsonStr0, new TypeReference<ServerFunctionChain>(){});

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
        SFCWorkload sfcWorkload = new SFCWorkload(serverFunctionChain);

        System.out.println(sfcWorkload.toString());
    }

}