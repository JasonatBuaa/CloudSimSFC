package org.cloudbus.cloudsim.sfc.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.CustomPhysicalTopologyGenerator;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.sfc.resourcemanager.StaticScheduler;
import org.cloudbus.cloudsim.util.JsonUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    private String rootPath = "Scenario1/";
    private String workloadFolder = rootPath + "workloads/";
    private String resourceFileName = "ResourceDesription.json";
    private String SFCDemandFileName = "SFCDemands.json";
    private String serviceFunctionFileName = "ServiceFunctionDescription.json";
    private List<ServiceFunction> serviceFunctions;
    private List<ServiceFunctionChain> serviceFunctionChainDemands;
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
        parseServiceFunction();
        parseServiceFunctionChainDemands();
        for (ServiceFunctionChain serviceFunctionChainDemand : serviceFunctionChainDemands) {
            sfcWorkloads.add(new SFCWorkload(serviceFunctionChainDemand));
        }

    }

    public void generate() {
        CustomPhysicalTopologyGenerator physicalTopologyGenerator = new CustomPhysicalTopologyGenerator();
        physicalTopologyGenerator.generate(resources);
        JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask();
        String jsonstr = JSON.toJSONString(physicalTopologyGenerator, SerializerFeature.PrettyFormat);
        JsonUtils.jsonWrite(rootPath + "PhysicalResource.json", jsonstr);

        for (SFCWorkload sfcWorkload : sfcWorkloads) {
            // workloadsCsvWriter(rootPath + "workloads_" + sfcWorkload.getTargetChainName()
            // + ".csv", sfcWorkload);
            workloadsCsvWriter(workloadFolder + "workloads_" + sfcWorkload.getTargetChainName() + ".csv", sfcWorkload);
        }

        // DeploymentScheduler customVirtualTopologyGenerator = new
        // DeploymentScheduler(serviceFunctionChainDemands,
        // sfcWorkloads, resources);

        DeploymentScheduler customVirtualTopologyGenerator = new StaticScheduler(serviceFunctionChainDemands,
                sfcWorkloads, resources);

        jsonstr = JSON.toJSONString(customVirtualTopologyGenerator, SerializerFeature.PrettyFormat);
        JsonUtils.jsonWrite(rootPath + "virtualTopology.json", jsonstr);

        // DeploymentScheduler customVirtualTopologyGenerator = new
        // DeploymentScheduler(serviceFunctionChainDemands,
        // sfcWorkloads, resources);
        // jsonstr = JSON.toJSONString(customVirtualTopologyGenerator,
        // SerializerFeature.PrettyFormat);
        // JsonUtils.jsonWrite(rootPath + "virtualTopology.json", jsonstr);
    }

    public void parseResource() {
        String path = rootPath + resourceFileName;
        String jsonStr = JsonUtils.jsonRead(path);
        resources = JSONObject.parseObject(jsonStr, new TypeReference<List<Resource>>() {
        });
    }

    public void parseServiceFunctionChainDemands() {
        String path = rootPath + SFCDemandFileName;
        String jsonStr = JsonUtils.jsonRead(path);
        serviceFunctionChainDemands = JSONObject.parseObject(jsonStr, new TypeReference<List<ServiceFunctionChain>>() {
        });

    }

    public void parseServiceFunction() {
        String path = rootPath + serviceFunctionFileName;
        String jsonStr = JsonUtils.jsonRead(path);
        serviceFunctions = JSONObject.parseObject(jsonStr, new TypeReference<List<ServiceFunction>>() {
        });
    }

    public void workloadsCsvWriter(String path, SFCWorkload sfcWorkload) {
        try {
            File file = new File(path);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            String line = "";
            String head = "time(s),target_sfc,Ingress,InitialRequestSize";
            for (int i = 1; i <= sfcWorkload.getChainLength(); i++) {
                head += "," + "Transmission" + i + "(MB)," + "VM" + i + ",cloudlet." + i + "(MI)";
            }
            head += ",Transmission" + (sfcWorkload.getChainLength() + 1) + "(MB),Egress,latency_demand(s)\n";

            writer.write(head);
            for (SFCRequest sfcRequest : sfcWorkload.getSfcRequestList()) {
                // line = sfcRequest.getStartTime() + "," + sfcWorkload.getTargetChainName() +
                // ","
                // + sfcRequest.getIngress() + sfcRequest.requestsToString() + "," +
                // sfcRequest.getOutput() + ","
                // + sfcRequest.getEgress() + "," + sfcWorkload.getLatencyDemand() + "\n";

                line = sfcRequest.getStartTime() + "," + sfcWorkload.getTargetChainName() + ","
                        + sfcRequest.getIngress() + sfcRequest.requestsToString() + "," + sfcRequest.getEgress() + ","
                        + sfcWorkload.getLatencyDemand() + "\n";
                writer.write(line);
                // System.out.println(line);
                // System.out.println(line);
                // System.out.println(sfcRequest.getOutput());
                // System.out.println(sfcRequest.getEgress());

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

        System.out.println("====================ServiceFunction=================");
        for (ServiceFunction serviceFunction : serviceFunctions) {
            System.out.println(serviceFunction.toString());
        }
        System.out.println();

        System.out.println("====================ServiceFunctionChain=================");
        // System.out.println(serviceFunctionChain.toString());
        System.out.println();

        System.out.println("====================SFCWorkload=================");
        // System.out.println(sfcWorkload.toString());
        System.out.println();
    }
}
