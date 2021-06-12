package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.parser.Resource;

import java.util.List;

class ResourceTest {
    @org.junit.jupiter.api.Test
    public void init(){
        String jsonStr = "[\n" +
                "  {\n" +
                "    \"Type\": \"Cloud\",\n" +
                "    \"Name\": \"DC1\",\n" +
                "    \"HomogeneousResourceGroups\": [\n" +
                "      {\n" +
                "        \"Name\": \"HRG1\",\n" +
                "        \"PhysicalResources\": [\n" +
                "          {\n" +
                "            \"Name\": \"HRG1\",\n" +
                "            \"ServerNumber\": 20,\n" +
                "            \"MIPS\": 100000,\n" +
                "            \"SIGMA2\": 1,\n" +
                "            \"FastMem\": 1000,\n" +
                "            \"BW\": 10000000,\n" +
                "            \"Availability\": 1.0,\n" +
                "            \"MTTR\": 1.0,\n" +
                "            \"MTBF\": 1.0,\n" +
                "            \"MinMIPS\": 10000,\n" +
                "            \"PriceRatio\": 1.0\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"Name\": \"HRG2\",\n" +
                "        \"PhysicalResources\": [\n" +
                "          {\n" +
                "            \"Name\": \"HRG2\",\n" +
                "            \"ServerNumber\": 20,\n" +
                "            \"MIPS\": 100000,\n" +
                "            \"SIGMA2\": 1,\n" +
                "            \"FastMem\": 1000,\n" +
                "            \"BW\": 10000000,\n" +
                "            \"Availability\": 1.0,\n" +
                "            \"MTTR\": 1.0,\n" +
                "            \"MTBF\": 1.0,\n" +
                "            \"MinMIPS\": 10000,\n" +
                "            \"PriceRatio\": 1.0\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"Name\": \"HRG3\",\n" +
                "        \"PhysicalResources\": [\n" +
                "          {\n" +
                "            \"Name\": \"HRG3\",\n" +
                "            \"ServerNumber\": 20,\n" +
                "            \"MIPS\": 100000,\n" +
                "            \"SIGMA2\": 1,\n" +
                "            \"FastMem\": 1000,\n" +
                "            \"BW\": 10000000,\n" +
                "            \"Availability\": 1.0,\n" +
                "            \"MTTR\": 1.0,\n" +
                "            \"MTBF\": 1.0,\n" +
                "            \"MinMIPS\": 10000,\n" +
                "            \"PriceRatio\": 1.0\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"Name\": \"HRG4\",\n" +
                "        \"PhysicalResources\": [\n" +
                "          {\n" +
                "            \"Name\": \"HRG4\",\n" +
                "            \"ServerNumber\": 20,\n" +
                "            \"MIPS\": 100000,\n" +
                "            \"SIGMA2\": 1,\n" +
                "            \"FastMem\": 1000,\n" +
                "            \"BW\": 10000000,\n" +
                "            \"Availability\": 1.0,\n" +
                "            \"MTTR\": 1.0,\n" +
                "            \"MTBF\": 1.0,\n" +
                "            \"MinMIPS\": 10000,\n" +
                "            \"PriceRatio\": 1.0\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"Type\": \"Edge\",\n" +
                "    \"Name\": \"DC2\",\n" +
                "    \"HomogeneousResourceGroups\": [\n" +
                "      {\n" +
                "        \"Name\": \"HRG5\",\n" +
                "        \"PhysicalResources\": [\n" +
                "          {\n" +
                "            \"Name\": \"HRG5\",\n" +
                "            \"ServerNumber\": 20,\n" +
                "            \"MIPS\": 100000,\n" +
                "            \"SIGMA2\": 1,\n" +
                "            \"FastMem\": 1000,\n" +
                "            \"BW\": 10000000,\n" +
                "            \"Availability\": 1.0,\n" +
                "            \"MTTR\": 1.0,\n" +
                "            \"MTBF\": 1.0,\n" +
                "            \"MinMIPS\": 10000,\n" +
                "            \"PriceRatio\": 1.0\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";

        List<Resource> resourceParsers =
                JSONObject.parseObject(jsonStr,new TypeReference<List<Resource>>(){});
        for(Resource resourceParser : resourceParsers){
            System.out.println(resourceParser.toString());
        }
    }

}