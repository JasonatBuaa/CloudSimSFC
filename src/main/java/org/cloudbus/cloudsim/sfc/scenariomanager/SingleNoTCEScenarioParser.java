package org.cloudbus.cloudsim.sfc.scenariomanager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.NotImplementedException;
import org.cloudbus.cloudsim.sfc.resourcemanager.ComparisonScheduler;
import org.cloudbus.cloudsim.sfc.resourcemanager.StaticSchedulerScenario1;
import org.cloudbus.cloudsim.sfc.resourcemanager.StaticSchedulerScenario4;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.CustomPhysicalTopologyGenerator;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.util.JsonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class SingleNoTCEScenarioParser {

//    public String rootPath = Configuration.ROOT_FOLDER;
    public String rootPath = null;

    private String workloadFolder = null;
    private String resourceDescriptionFileName = null;
    private String SFCDemandFileName = null;
    //    private String serviceFunctionFileName = "Comparison_ServiceFunctionDescription.json";
    private String serviceFunctionFileName = null;

    private List<ServiceFunction> serviceFunctions;
    private List<ServiceFunctionChain> serviceFunctionChainDemands;
    private List<Resource> resources;
    private List<SFCWorkload> sfcWorkloads;

    private String compSDNNFVRootPath = null;

//    private List<SFCWorkloadForSDNNFV> sfcWorkloadsForCloudSimSDNNFV;

    public void setRootPath(String folder) {
        this.rootPath = folder;
        this.workloadFolder = rootPath + "workloads/";
        this.resourceDescriptionFileName = "ResourceDesription.json";
        this.SFCDemandFileName = "SFCDemands.json";
        this.serviceFunctionFileName = rootPath + "Comparison_ServiceFunctionDescription.json";
        this.compSDNNFVRootPath = this.rootPath + "sdnnfv/";
        SingleNoTCEScenarioParser.prepareFolder(this.rootPath);
        SingleNoTCEScenarioParser.prepareFolder(this.workloadFolder);
        SingleNoTCEScenarioParser.prepareFolder(this.compSDNNFVRootPath);
    }

    public SingleNoTCEScenarioParser() {
        this.sfcWorkloads = new ArrayList<>();
        prepareSubFolders();
    }

    public SingleNoTCEScenarioParser(String mainFolder, String subFolder) {
        this.sfcWorkloads = new ArrayList<>();
        this.rootPath = mainFolder + subFolder;
        SingleNoTCEScenarioParser.prepareFolder(this.rootPath);

        this.workloadFolder = rootPath + "workloads/";
        SingleNoTCEScenarioParser.prepareFolder(workloadFolder);

        this.resourceDescriptionFileName = this.rootPath + "ResourceDesription.json";
        this.SFCDemandFileName = this.rootPath + "SFCDemands.json";
        this.serviceFunctionFileName = this.rootPath + "Comparison_ServiceFunctionDescription.json";
        this.compSDNNFVRootPath = this.rootPath + "sdnnfv/";
        SingleNoTCEScenarioParser.prepareFolder(this.compSDNNFVRootPath);

        prepareSubFolders();
    }

//    public CompScenarioParser(String rootPath) {
//        this.rootPath = rootPath;
//        sfcWorkloads = new ArrayList<>();
//    }

    public static void prepareFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {// 如果文件夹不存在
            folder.mkdir();// 创建文件夹
        }
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

    public void generateSFCDemands(int demandCount) {
//        int SFCDemandNUM = 20;

        int SFCDemandNUM = demandCount;
        int INGRESSNUM = 1;
        int DESTROYTIME = 1000;
        ArrayList<SFCDemand> sfcs = new ArrayList<SFCDemand>();

            int chainlen = 3;
            SFCDemand a = MakeSFC(1, INGRESSNUM, chainlen, DESTROYTIME);
            sfcs.add(a);
            System.out.println(a);

        String sfcdemands_json = JSON.toJSONString(sfcs);
        JsonUtils.jsonWrite(rootPath + SFCDemandFileName, sfcdemands_json);
        System.out.println(sfcs);
        System.out.println(sfcdemands_json);
    }

    /**
     * @param number      sfc index
     * @param ingressNum  the number of ingress node
     * @param chainLength
     * @param destroyTime
     * @return
     */
    private SFCDemand MakeSFC(int number, int ingressNum, int chainLength, int destroyTime) {
        SFCDemand sfc = new SFCDemand();
//        sfc.setName(number);
        sfc.setIngressDCs(number, ingressNum);
        sfc.setEgressDC(number);
        sfc.setName("ingress" + number, "egress" + number);
        System.out.println(new File(".").getAbsolutePath());
//        sfc.setChain(chainLength);
//        sfc.compSetChain(chainLength, rootPath + serviceFunctionFileName);
        sfc.compSetChain(chainLength, serviceFunctionFileName);
        sfc.setDestroyTime(destroyTime);

        return sfc;
    }

    public void parse() {
        parseResource();
        parseServiceFunction();
        parseServiceFunctionChainDemands();
        SFCWorkload SFCwl;
        SFCWorkloadForSDNNFV SFCwlForSDNNFV;
        for (ServiceFunctionChain serviceFunctionChainDemand : serviceFunctionChainDemands) {
            SFCwl = new SFCWorkload(serviceFunctionChainDemand);
            sfcWorkloads.add(SFCwl);

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

    /**
     * （通过资源调度）生成虚拟机配置
     *
     * @param scenario
     */
    public void deploymentSchedule(String scenario) {
        /**
         */
        DeploymentScheduler customVirtualTopologyGenerator;
        if (scenario.equalsIgnoreCase("scenario1"))
            customVirtualTopologyGenerator = new StaticSchedulerScenario1(serviceFunctionChainDemands, sfcWorkloads,
                    resources);

        else if (scenario.equalsIgnoreCase("scenario4"))
            customVirtualTopologyGenerator = new StaticSchedulerScenario4(serviceFunctionChainDemands, sfcWorkloads,
                    resources);
        else if (scenario.equalsIgnoreCase("revisionComparison")) {
            //            customVirtualTopologyGenerator = new ComparisonScheduler(serviceFunctionChainDemands, sfcWorkloads,
//                    resources);
            customVirtualTopologyGenerator = new ComparisonScheduler(serviceFunctionChainDemands, sfcWorkloads, resources);
            ComparisonScheduler sche = (ComparisonScheduler) (customVirtualTopologyGenerator);
            sche.schedule();
        } else {
            throw new NotImplementedException("Please check!! The specified scheduler is not yet implemented!!!");
        }

        String jsonstr = JSON.toJSONString(customVirtualTopologyGenerator, SerializerFeature.PrettyFormat);
        JsonUtils.jsonWrite(rootPath + "virtualTopology.json", jsonstr);

        JsonUtils.jsonWrite(compSDNNFVRootPath + "virtualTopology.json", jsonstr);
    }

    /**
     * 生成物理实验环境physicalTopology 和负载数据SFCWorkloads
     */
    public void generate() {
        CustomPhysicalTopologyGenerator physicalTopologyGenerator = new CustomPhysicalTopologyGenerator();
        physicalTopologyGenerator.generate(resources);
        JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask();
        String jsonstr = JSON.toJSONString(physicalTopologyGenerator, SerializerFeature.PrettyFormat);
        JsonUtils.jsonWrite(rootPath + "PhysicalResource.json", jsonstr);

        JsonUtils.jsonWrite(compSDNNFVRootPath + "PhysicalResource.json", jsonstr);

        generateWorkload();
        generateWorkloadforCloudSimSDNNFV();
    }

    public void generateWorkloadforCloudSimSDNNFV() {
        for (SFCWorkload sfcWorkload : sfcWorkloads) {
//            String folder = workloadFolder + "sdnnfv/";
            String folder = this.rootPath + "sdnnfv/workloads/";
            SingleNoTCEScenarioParser.prepareFolder(folder);
            workloadsCsvWriterforCloudSimSDNNFV(folder + "workloads_" + sfcWorkload.getTargetChainName() + ".csv", sfcWorkload);
        }
    }

    public void parseResource() {
//        String path = rootPath + resourceDescriptionFileName;
        String path = this.resourceDescriptionFileName;
        String jsonStr = JsonUtils.jsonRead(path);
        resources = JSONObject.parseObject(jsonStr, new TypeReference<List<Resource>>() {
        });
    }

    public void parseServiceFunctionChainDemands() {
        String path = this.SFCDemandFileName;
        String jsonStr = JsonUtils.jsonRead(path);
        serviceFunctionChainDemands = JSONObject.parseObject(jsonStr, new TypeReference<List<ServiceFunctionChain>>() {
        });

    }

    public void parseServiceFunction() {
//        String path = rootPath + serviceFunctionFileName;
        String path = this.serviceFunctionFileName;
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

                line = sfcRequest.getStartTime() + "," + sfcWorkload.getTargetChainName() + ","
                        + sfcRequest.getIngress() + sfcRequest.requestsToString() + "," + sfcRequest.getEgress() + ","
                        + sfcWorkload.getLatencyDemand() + "\n";
                writer.write(line);


            }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void workloadsCsvWriterforCloudSimSDNNFV(String path, SFCWorkload sfcWorkload) {
        try {
            File file = new File(path);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            String line = "";

//            String head = "atime(s), IngressNode, InitialRequestSize, WorkloadOnIngress(0)";
//            for (int i = 1; i <= sfcWorkload.getChainLength(); i++) {
////                head += "," + "Transmission" + i + "(MB)," + "VM" + i + ",cloudlet." + i + "(MI)";
//                head += ", LinkName, VM" + i + ", Transmission" + i + "(MB)" +  ", Cloudlet." + i + "(MI)";
//            }
//            head += ", LinkName, EgressNode, Transmission" + (sfcWorkload.getChainLength() + 1) +
//                    "(MB), WorkloadOnEgress(0), latency_demand(s)\n";

            String head = "atime(s), chainName, IngressNode, packetToIngress(0), WorkloadOnIngress(0)";
            for (int i = 1; i <= sfcWorkload.getChainLength(); i++) {
                head += ", LinkToMe, SFName" + i + ", Packet_To_Me" + i + "(MB)" + ", MyWorkload" + i + "(MI)";
            }
            head += ", LinkToEgress, EgressNode, Packet_To_Egress" + (sfcWorkload.getChainLength() + 1) +
                    "(MB), WorkloadOnEgress(0)\n";

            writer.write(head);
            for (SFCRequest sfcRequest : sfcWorkload.getSfcRequestList()) {
                String linkName = sfcRequest.getIngress() + "-" + sfcRequest.getEgress();

                // line = sfcRequest.getStartTime() + "," + sfcWorkload.getTargetChainName() +
                // ","
                // + sfcRequest.getIngress() + sfcRequest.requestsToString() + "," +
                // sfcRequest.getOutput() + ","
                // + sfcRequest.getEgress() + "," + sfcWorkload.getLatencyDemand() + "\n";


//                line = sfcRequest.requestsToStringForCloudSimSDNNFV(linkName, sfcRequest.getEgress(),sfcWorkload.getTargetChainName()) + "\n";
                String physicalChainName = sfcWorkload.getTargetChainName() + "_psfc1" ;

                line = sfcRequest.requestsToStringForCloudSimSDNNFV(physicalChainName) + "\n";
//                line = sfcRequest.getStartTime() + "," + sfcRequest.getIngress() + ","
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
