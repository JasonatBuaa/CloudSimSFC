package org.cloudbus.cloudsim.sfc.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.CustomPhysicalTopologyGenerator;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.CustomVirtualTopologyGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    private String rootPath = "SFCExampleConfig2/";
    private String resourceFileName = "ResourceDesription.json";
    private String SFCDemandFileName = "SFCDemands.json";
    private String serverFunctionFileName = "ServerFunctionDescription.json";
    private List<ServerFunction> serverFunctions;
    private List<ServerFunctionChain> serverFunctionChains;
    private List<Resource> resources;
    private List<SFCWorkload> sfcWorkloads;

    public FileParser() {
        sfcWorkloads = new ArrayList<>();
    }

    public FileParser(String rootPath) {
        this.rootPath = rootPath;
        sfcWorkloads = new ArrayList<>();
    }

    public void parse() {
        parseResource();
        parseServerFunction();
        parseServerFunctionChain();
        for (ServerFunctionChain serverFunctionChain : serverFunctionChains) {
            sfcWorkloads.add(new SFCWorkload(serverFunctionChain));
        }

    }

    public void generate() {
        CustomPhysicalTopologyGenerator physicalTopologyGenerator = new CustomPhysicalTopologyGenerator();
        physicalTopologyGenerator.generate(resources);
        JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask();
        String jsonstr = JSON.toJSONString(physicalTopologyGenerator, SerializerFeature.PrettyFormat);
        jsonWrite(rootPath + "PhysicalResource.json", jsonstr);

        for (SFCWorkload sfcWorkload : sfcWorkloads) {
            workloadsCsvWriter(rootPath + "workloads_" + sfcWorkload.getTargetChainName() + ".csv", sfcWorkload);
        }

        CustomVirtualTopologyGenerator customVirtualTopologyGenerator = new CustomVirtualTopologyGenerator(
                serverFunctionChains, sfcWorkloads, resources);
        jsonstr = JSON.toJSONString(customVirtualTopologyGenerator, SerializerFeature.PrettyFormat);
        jsonWrite(rootPath + "virtualTopology.json", jsonstr);
    }

    public void parseResource() {
        String path = rootPath + resourceFileName;
        String jsonStr = jsonRead(path);
        resources = JSONObject.parseObject(jsonStr, new TypeReference<List<Resource>>() {
        });
    }

    public void parseServerFunctionChain() {
        String path = rootPath + SFCDemandFileName;
        String jsonStr = jsonRead(path);
        serverFunctionChains = JSONObject.parseObject(jsonStr, new TypeReference<List<ServerFunctionChain>>() {
        });

    }

    public void parseServerFunction() {
        String path = rootPath + serverFunctionFileName;
        String jsonStr = jsonRead(path);
        serverFunctions = JSONObject.parseObject(jsonStr, new TypeReference<List<ServerFunction>>() {
        });
    }

    public String jsonRead(String path) {
        String jsonStr = "";
        try {
            File jsonFile = new File(path);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile));
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();

            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void jsonWrite(String path, String jsonStr) {
        try {
            File jsonFile = new File(path);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonFile));
            writer.write(jsonStr);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void workloadsCsvWriter(String path, SFCWorkload sfcWorkload) {
        try {
            File file = new File(path);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            String line = "";
            String head = "time(s),target_sfc,Ingress";
            for (int i = 1; i <= sfcWorkload.getChainLength(); i++) {
                head += "," + "Transmission" + i + "(MB)," + "VM" + i + ",cloudlet." + i + "(MI)";
            }
            head += ",Transmission" + (sfcWorkload.getChainLength() + 1) + "(MB),Egress,latency_demand(s)\n";

            writer.write(head);
            for (SFCRequest sfcRequest : sfcWorkload.getSfcRequestList()) {
                line = sfcRequest.getStartTime() + "," + sfcWorkload.getTargetChainName() + ","
                        + sfcRequest.getIngress() + sfcRequest.requestsToString() + "," + sfcRequest.getOutput() + ","
                        + sfcRequest.getEgress() + "," + sfcWorkload.getLatencyDemand() + "\n";
                writer.write(line);
            }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void print() {
        System.out.println("====================Resource=================");

        for (Resource resource : resources) {
            System.out.println(resource.toString());
        }
        System.out.println();

        System.out.println("====================ServerFunction=================");
        for (ServerFunction serverFunction : serverFunctions) {
            System.out.println(serverFunction.toString());
        }
        System.out.println();

        System.out.println("====================ServerFunctionChain=================");
        // System.out.println(serverFunctionChain.toString());
        System.out.println();

        System.out.println("====================SFCWorkload=================");
        // System.out.println(sfcWorkload.toString());
        System.out.println();
    }
}
