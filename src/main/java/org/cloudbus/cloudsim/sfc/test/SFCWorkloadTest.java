package org.cloudbus.cloudsim.sfc.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.cloudbus.cloudsim.sfc.scenariomanager.*;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.CustomPhysicalTopologyGenerator;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.DeploymentScheduler;

import java.util.ArrayList;
import java.util.List;

class SFCWorkloadTest {
        @org.junit.jupiter.api.Test
        public void init() {
                String jsonStr0 = "[\n" + "  {\n" + "    \"Name\": \"chain1\",\n" + "    \"Chain\": [\n"
                                + "      \"SF-A\",\n" + "      \"SF-B\",\n" + "      \"SF-C\",\n" + "      \"SF-D\"],\n"
                                + "    \"IngressDCs\": [{\"Name\":\"Ingress1\",\"Weight\":0.5},{\"Name\":\"Ingress2\",\"Weight\":0.5}],\n"
                                + "    \"EgressDC\": [{\"Name\":\"Egress1\",\"Weight\":1}],\n"
                                + "    \"AverageInputSize\": 100,\n" + "    \"CreateTime\": 3700,\n"
                                + "    \"DestroyTime\":10001\n" + "  },\n" + "\n" + "  {\n"
                                + "    \"Name\": \"chain2\",\n" + "    \"Chain\": [\n" + "      \"SF-A\",\n"
                                + "      \"SF-B\",\n" + "      \"SF-D\"],\n"
                                + "    \"IngressDCs\": [{\"Name\":\"Ingress3\",\"Weight\":0.3},{\"Name\":\"Ingress4\",\"Weight\":0.7}],\n"
                                + "    \"EgressDC\": [{\"Name\":\"Egress2\",\"Weight\":1}],\n"
                                + "    \"AverageInputSize\": 110,\n" + "    \"CreateTime\": 0,\n"
                                + "    \"DestroyTime\":10001\n" + "  }\n" + "]";
                List<ServiceFunctionChain> serviceFunctionChains = JSONObject.parseObject(jsonStr0,
                                new TypeReference<List<ServiceFunctionChain>>() {
                                });

                String jsonStr1 = "[\n" + "  {\n" + "    \"Name\": \"SF-A\",\n" + "    \"Type\": \"FireWall\",\n"
                                + "    \"InputRate\": 1.0,\n" + "    \"OutputRate\": 1.5,\n"
                                + "    \"Performance\":1000\n" + "  },\n" + "  {\n" + "    \"Name\": \"SF-B\",\n"
                                + "    \"Type\": \"LoadBalance\",\n" + "    \"InputRate\": 1.5,\n"
                                + "    \"OutputRate\": 1.0,\n" + "    \"Performance\":500\n" + "  },\n" + "  {\n"
                                + "    \"Name\": \"SF-C\",\n" + "    \"Type\": \"ApacheServer\",\n"
                                + "    \"InputRate\": 1.2,\n" + "    \"OutputRate\": 1.0,\n"
                                + "    \"Performance\":100\n" + "  },\n" + "  {\n" + "    \"Name\": \"SF-D\",\n"
                                + "    \"Type\": \"Other\",\n" + "    \"InputRate\": 1.0,\n"
                                + "    \"OutputRate\": 1.0,\n" + "    \"Performance\":300\n" + "  }\n" + "]";
                List<ServiceFunction> serviceFunctions = JSONObject.parseObject(jsonStr1,
                                new TypeReference<List<ServiceFunction>>() {
                                });

                String jsonStr2 = "[\n" + "  {\n" + "    \"Type\": \"Cloud\",\n" + "    \"Name\": \"DC1\",\n"
                                + "    \"InterCloudBW\" : 1000,\n" + "    \"HomogeneousResourceGroups\": [\n"
                                + "      {\n" + "        \"Name\": \"HRG1\",\n" + "        \"PhysicalResources\": [\n"
                                + "          {\n" + "            \"Name\": \"HRG1\",\n"
                                + "            \"ServerNumber\": 20,\n" + "            \"MIPS\": 100000,\n"
                                + "            \"SIGMA2\": 1,\n" + "            \"FastMem\": 1000,\n"
                                + "            \"BW\": 10000000,\n" + "            \"Availability\": 1.0,\n"
                                + "            \"MTTR\": 1.0,\n" + "            \"MTBF\": 1.0,\n"
                                + "            \"MinMIPS\": 10000,\n" + "            \"PriceRatio\": 1.0\n"
                                + "          }\n" + "        ]\n" + "      },\n" + "      {\n"
                                + "        \"Name\": \"HRG2\",\n" + "        \"PhysicalResources\": [\n"
                                + "          {\n" + "            \"Name\": \"HRG2\",\n"
                                + "            \"ServerNumber\": 20,\n" + "            \"MIPS\": 100000,\n"
                                + "            \"SIGMA2\": 1,\n" + "            \"FastMem\": 1000,\n"
                                + "            \"BW\": 10000000,\n" + "            \"Availability\": 1.0,\n"
                                + "            \"MTTR\": 1.0,\n" + "            \"MTBF\": 1.0,\n"
                                + "            \"MinMIPS\": 10000,\n" + "            \"PriceRatio\": 1.0\n"
                                + "          }\n" + "        ]\n" + "      },\n" + "      {\n"
                                + "        \"Name\": \"HRG3\",\n" + "        \"PhysicalResources\": [\n"
                                + "          {\n" + "            \"Name\": \"HRG3\",\n"
                                + "            \"ServerNumber\": 20,\n" + "            \"MIPS\": 100000,\n"
                                + "            \"SIGMA2\": 1,\n" + "            \"FastMem\": 1000,\n"
                                + "            \"BW\": 10000000,\n" + "            \"Availability\": 1.0,\n"
                                + "            \"MTTR\": 1.0,\n" + "            \"MTBF\": 1.0,\n"
                                + "            \"MinMIPS\": 10000,\n" + "            \"PriceRatio\": 1.0\n"
                                + "          }\n" + "        ]\n" + "      },\n" + "      {\n"
                                + "        \"Name\": \"HRG4\",\n" + "        \"PhysicalResources\": [\n"
                                + "          {\n" + "            \"Name\": \"HRG4\",\n"
                                + "            \"ServerNumber\": 20,\n" + "            \"MIPS\": 100000,\n"
                                + "            \"SIGMA2\": 1,\n" + "            \"FastMem\": 1000,\n"
                                + "            \"BW\": 10000000,\n" + "            \"Availability\": 1.0,\n"
                                + "            \"MTTR\": 1.0,\n" + "            \"MTBF\": 1.0,\n"
                                + "            \"MinMIPS\": 10000,\n" + "            \"PriceRatio\": 1.0\n"
                                + "          }\n" + "        ]\n" + "      }\n" + "    ]\n" + "  },\n" + "  {\n"
                                + "    \"Type\": \"Edge\",\n" + "    \"Name\": \"DC2\",\n"
                                + "    \"InterCloudBW\" : 1000,\n" + "    \"HomogeneousResourceGroups\": [\n"
                                + "      {\n" + "        \"Name\": \"HRG5\",\n" + "        \"PhysicalResources\": [\n"
                                + "          {\n" + "            \"Name\": \"HRG5\",\n"
                                + "            \"ServerNumber\": 20,\n" + "            \"MIPS\": 100000,\n"
                                + "            \"SIGMA2\": 1,\n" + "            \"FastMem\": 1000,\n"
                                + "            \"BW\": 10000000,\n" + "            \"Availability\": 1.0,\n"
                                + "            \"MTTR\": 1.0,\n" + "            \"MTBF\": 1.0,\n"
                                + "            \"MinMIPS\": 10000,\n" + "            \"PriceRatio\": 1.0\n"
                                + "          }\n" + "        ]\n" + "      }\n" + "    ]\n" + "  }\n" + "]\n";

                List<Resource> resourceParsers = JSONObject.parseObject(jsonStr2, new TypeReference<List<Resource>>() {
                });
                CustomPhysicalTopologyGenerator physicalTopologyGenerator = new CustomPhysicalTopologyGenerator();
                physicalTopologyGenerator.generate(resourceParsers);

                List<SFCWorkload> sfcWorkloadList = new ArrayList<>();

                for (ServiceFunctionChain serviceFunctionChain : serviceFunctionChains) {
                        SFCWorkload sfcWorkload = new SFCWorkload(serviceFunctionChain);
                        sfcWorkloadList.add(sfcWorkload);
                        for (SFCRequest sfcRequest : sfcWorkload.getSfcRequestList()) {
                                System.out.println(sfcRequest.getStartTime() + "," + sfcWorkload.getTargetChainName()
                                                + "," + sfcRequest.getIngress() + "," + sfcRequest.requestsToString()
                                                + sfcRequest.getEgress() + "," + sfcWorkload.getLatencyDemand());
                        }

                }

                DeploymentScheduler customVirtualTopologyGenerator = new DeploymentScheduler(serviceFunctionChains,
                                sfcWorkloadList, resourceParsers);

        }

}