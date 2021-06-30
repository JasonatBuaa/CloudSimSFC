package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.CustomPhysicalTopologyGenerator;

import java.util.List;

class ResourceTest {
    @org.junit.jupiter.api.Test
    public void init(){
        String jsonStr = "[\n" +
                "  {\n" +
                "    \"Type\": \"Cloud\",\n" +
                "    \"Name\": \"DC1\",\n" +
                "    \"InterCloudBW\" : 1000,\n" +
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
                "    \"InterCloudBW\" : 1000,\n" +
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
                "]\n";

        List<Resource> resourceParsers =
                JSONObject.parseObject(jsonStr,new TypeReference<List<Resource>>(){});
        CustomPhysicalTopologyGenerator physicalTopologyGenerator = new CustomPhysicalTopologyGenerator();
        physicalTopologyGenerator.generate(resourceParsers);


        JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE &~ SerializerFeature.SortField.getMask();
        String jsonstr = JSON.toJSONString(physicalTopologyGenerator, SerializerFeature.PrettyFormat);
        for(Resource resourceParser : resourceParsers){
            System.out.println(resourceParser.toString());
        }
        System.out.println(jsonstr);
    }

}