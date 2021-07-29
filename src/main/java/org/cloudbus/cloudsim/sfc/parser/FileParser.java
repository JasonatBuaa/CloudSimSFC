package org.cloudbus.cloudsim.sfc.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.apache.commons.lang3.NotImplementedException;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.CustomPhysicalTopologyGenerator;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.sfc.resourcemanager.StaticSchedulerScenario1;
import org.cloudbus.cloudsim.sfc.resourcemanager.StaticSchedulerScenario2;
import org.cloudbus.cloudsim.util.JsonUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    // private String rootPath = "Scenario1/";
    // private String workloadFolder = rootPath + "workloads/";
    // private String resourceFileName = "ResourceDesription.json";
    // private String SFCDemandFileName = "SFCDemands.json";
    // private String serviceFunctionFileName = "ServiceFunctionDescription.json";

    // public String rootPath = "Scenario1/";
    public String rootPath = Configuration.ROOT_FOLDER;
    private String workloadFolder = rootPath + "workloads/";
    private String resourceFileName = "ResourceDesription.json";
    private String SFCDemandFileName = "SFCDemands.json";
    private String serviceFunctionFileName = "ServiceFunctionDescription.json";

    private List<ServiceFunction> serviceFunctions;
    private List<ServiceFunctionChain> serviceFunctionChainDemands;
    private List<Resource> resources;
    private List<SFCWorkload> sfcWorkloads;

    public void setRootPath(String folder) {
        this.rootPath = folder;
    }

    public FileParser() {
        sfcWorkloads = new ArrayList<>();
        prepareSubFolders();
    }

    public FileParser(String rootPath) {
        this.rootPath = rootPath;
        sfcWorkloads = new ArrayList<>();
    }

    public void prepareSubFolders() {
        File wFolder = new File(workloadFolder);
        if (!wFolder.exists()) {// 如果文件夹不存在
            wFolder.mkdir();// 创建文件夹
        }
        File rFolder = new File(rootPath + "results/");
        if (!rFolder.exists()) {// 如果文件夹不存在
            rFolder.mkdir();// 创建文件夹
        }

    }

    public void generateSFCDemands() {
        int SFCDemandNUM = 20;
        int INGRESSNUM = 1;
        int DESTROYTIME = 500;
        ArrayList<SFCDemand> sfcs = new ArrayList<SFCDemand>();

        for (int i = 1; i <= SFCDemandNUM; i++) {
            int chainlen = (int) (Math.random() * 3 + 2);
            SFCDemand a = MakeSFC(i, INGRESSNUM, chainlen, DESTROYTIME);
            sfcs.add(a);
            System.out.println(a);
        }

        String sfcdemands_json = JSON.toJSONString(sfcs);
        JsonUtils.jsonWrite(rootPath + SFCDemandFileName, sfcdemands_json);
        System.out.println(sfcs);
        System.out.println(sfcdemands_json);
    }

    private SFCDemand MakeSFC(int number, int ingressNum, int chainLength, int destroyTime) {
        SFCDemand sfc = new SFCDemand();
        sfc.setName(number);
        sfc.setIngressDCs(number, ingressNum);
        sfc.setEgressDC(number);
        sfc.setChain(chainLength);
        sfc.setDestroyTime(destroyTime);

        return sfc;
    }

    public void parse() {
        parseResource();
        parseServiceFunction();
        parseServiceFunctionChainDemands();
        for (ServiceFunctionChain serviceFunctionChainDemand : serviceFunctionChainDemands) {
            sfcWorkloads.add(new SFCWorkload(serviceFunctionChainDemand));
        }

    }

    public void generatePhysicalResource() {
        CustomPhysicalTopologyGenerator physicalTopologyGenerator = new CustomPhysicalTopologyGenerator();
        physicalTopologyGenerator.generate(resources);
        JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask();
        String jsonstr = JSON.toJSONString(physicalTopologyGenerator, SerializerFeature.PrettyFormat);
        JsonUtils.jsonWrite(rootPath + "PhysicalResource.json", jsonstr);
    }

    public void generateWorkload() {
        for (SFCWorkload sfcWorkload : sfcWorkloads) {
            // workloadsCsvWriter(rootPath + "workloads_" + sfcWorkload.getTargetChainName()
            // + ".csv", sfcWorkload);
            workloadsCsvWriter(workloadFolder + "workloads_" + sfcWorkload.getTargetChainName() + ".csv", sfcWorkload);
        }
    }

    public void deploymentSchedule(String scenario) {
        /**
         */
        DeploymentScheduler customVirtualTopologyGenerator;
        if (scenario.equalsIgnoreCase("scenario1"))
            customVirtualTopologyGenerator = new StaticSchedulerScenario1(serviceFunctionChainDemands, sfcWorkloads,
                    resources);

        else if (scenario.equalsIgnoreCase("scenario2"))
            customVirtualTopologyGenerator = new StaticSchedulerScenario2(serviceFunctionChainDemands, sfcWorkloads,
                    resources);
        // else if (scenario.equalsIgnoreCase("scenario3"))
        // customVirtualTopologyGenerator = new
        // StaticSchedulerScenario1(serviceFunctionChainDemands, sfcWorkloads,
        // resources);
        else {
            throw new NotImplementedException("Please check!! The specified scheduler is not yet implemented!!!");
        }

        String jsonstr = JSON.toJSONString(customVirtualTopologyGenerator, SerializerFeature.PrettyFormat);
        JsonUtils.jsonWrite(rootPath + "virtualTopology.json", jsonstr);
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

        DeploymentScheduler customVirtualTopologyGenerator = new StaticSchedulerScenario1(serviceFunctionChainDemands,
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
