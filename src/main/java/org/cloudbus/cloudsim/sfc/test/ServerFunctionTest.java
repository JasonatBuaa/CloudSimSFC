package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.parser.ServerFunction;

import java.util.List;

class ServerFunctionTest {
    @org.junit.jupiter.api.Test
    public void init(){
        String jsonStr = "[\n" +
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
                JSONObject.parseObject(jsonStr, new TypeReference<List<ServerFunction>>(){});
        for(ServerFunction serverFunction : serverFunctions){
            System.out.println(serverFunction.toString());
        }
    }

}