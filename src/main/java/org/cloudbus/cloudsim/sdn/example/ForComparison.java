/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.sdn.example;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import org.cloudbus.cloudsim.sdn.CloudSimEx;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.SDNBroker;
import org.cloudbus.cloudsim.sdn.example.topogenerators.FRGenerator;
import org.cloudbus.cloudsim.sdn.monitor.power.PowerUtilizationMaxHostInterface;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystem;
import org.cloudbus.cloudsim.sdn.parsers.PhysicalTopologyParser;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNDatacenter;
import org.cloudbus.cloudsim.sdn.physicalcomponents.switches.Switch;
import org.cloudbus.cloudsim.sdn.policies.selectlink.LinkSelectionPolicy;
import org.cloudbus.cloudsim.sdn.policies.selectlink.LinkSelectionPolicyBandwidthAllocation;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyCombinedLeastFullFirst;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyCombinedMostFullFirst;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyMipsLeastFullFirst;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyMipsMostFullFirst;
import org.cloudbus.cloudsim.sdn.workload.Workload;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CloudSimSDN example main program for InterCloud scenario. This can create
 * multiple cloud data centers and send packets between them.
 *
 * @author Jungmin Son
 * @since CloudSimSDN 3.0
 */
public class ForComparison {

    // protected static String rootFolder = "Scenario1-1/";
//    protected static String rootFolder = Configuration.ROOT_FOLDER;
//    protected static String physicalTopologyFile = rootFolder + "PhysicalResource.json";
//    protected static String deploymentFile = rootFolder + "virtualTopology.json"; // virtual topology
//    protected static String workload_folder = rootFolder + "workloads/";
    protected static String rootFolder = null;
    protected static String physicalTopologyFile = null;
    protected static String deploymentFile = null; // virtual topology
    protected static String workload_folder = null;


    // protected static String[] workload_files = { "workloads_chain1.csv",
    // "workloads_chain1.csv" };
    protected static String[] workload_files;

//    protected static String FR_file = "";

    private static String[] argString = {"LFF", physicalTopologyFile, deploymentFile, "./"};

    //    protected static List<String> FREvents;
    protected static List<String> workloads = new ArrayList<>();

    private static boolean logEnabled = true;

    public interface VmAllocationPolicyFactory {
        public VmAllocationPolicy create(List<? extends Host> list);
    }

    enum VmAllocationPolicyEnum {
        CombLFF, CombMFF, MipLFF, MipMFF, OverLFF, OverMFF, LFF, MFF, Overbooking
    }

    private static void printUsage() {
        String runCmd = "java SDNExample";
        System.out.format("Usage: %s <LFF|MFF> [physical.json] [virtual.json] [workload1.csv] [workload2.csv] [...]\n",
                runCmd);
    }

    public static String[] getWorkloadFile(String path) {
        java.io.File file = new java.io.File(path);
        String[] wfiles = file.list();
        // for (String workload_file : wfiles)
        // System.out.println(workload_file);
        return wfiles;
    }

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) throws FileNotFoundException {

        /**
         * experiment parameters:
         *
         * rootFolder --> change before each iteration
         * FREvents --> clear at initialization
         * workloads --> clear at initialization
         * workload_folder --> change before each iteration
         * deploymentFile -- remain unchanged
         * deploymentFile --> change before each iteration
         * physicalTopologyFile --> change before each iteration
         * argString -- do not need to change
         */
//        List<String> outerFolder = Arrays.asList("CompareExperimentTiny", "CompareExperimentSmall", "CompareExperimentMedium");

//        List<String> innerFolder = Arrays.asList("10", "20", "30", "40");
//        List<String> innerFolder = Arrays.asList("10");

//        List<String> scales = Arrays.asList("Tiny", "Small", "Medium");
//        List<String> scales = Arrays.asList("Tiny","Small");
        List<String> scales = Arrays.asList("Small");
        List<String> outerFolder = scales.stream().map(s -> "CompareExperiment".concat(s).concat("/")).collect(Collectors.toList());
        String[] temp = new String[12];
        for (int i = 0; i < 12; i++) {
            temp[i] = String.valueOf(i * 5 + 5);
        }
//        String[] temp = {"55", "60"};
//        String[] temp = {"45"};


        List<String> exps = Arrays.asList(temp);
        List<String> innerFolder = exps.stream().map(s -> String.valueOf(s).concat("demands/")).collect(Collectors.toList());

        List<String> resultList = new ArrayList<>();

        String head = "ResourceScale," + String.join(",", exps);
        head += "\n";
        resultList.add(head);

        for (String o : outerFolder) {
            String line = scales.get(outerFolder.indexOf(o));
            for (String i : innerFolder) {
                rootFolder = o + i;
//                Configuration.ROOT_FOLDER = rootFolder;
                Configuration.setRootFolder(rootFolder);

                workloads.clear();
//                workload_folder = rootFolder + "workloads/";
                workload_files = null;
//                deploymentFile = rootFolder + "virtualTopology.json";

                deploymentFile = "CompareExperimentSmall/" + i + "virtualTopology.json";
                workload_folder = "CompareExperimentSmall/" + i + "workloads/";

                String FR_file = rootFolder + "FR.csv";
//                rootFolder + "virtualTopology.json"; // virtual topology

                physicalTopologyFile = rootFolder + "PhysicalResource.json";


                CloudSimEx.setStartTime();
                SDNBroker.dropedCloudletsDueToQueueFull = 0;

                // workloads = new ArrayList<String>();

                args = argString;

                VmAllocationPolicyEnum vmAllocPolicy = VmAllocationPolicyEnum.valueOf(args[0]);

                workload_files = getWorkloadFile(workload_folder);
                if (workload_files == null || workload_files.length == 0)
                    throw new NullPointerException();

                for (String str : workload_files) {
                    workloads.add(workload_folder + str);
                }

                printArguments(physicalTopologyFile, deploymentFile, workloads);
                System.out.println(System.getProperty("user.dir"));
//                FREvents =

                String outputFolder = Configuration.workingDirectory + rootFolder + "results/";

                String outputFileName = outputFolder + Configuration.experimentName
                        + (Configuration.DISABLE_FAILURE_RECOVERY ? "" : "-with_FR_events-")
                        + (Configuration.DISABLE_MEMORY_QUEUE ? "" : "-with_Queue-") + "log.out.txt";

                java.io.File of = new java.io.File(outputFolder);
                if (!of.exists())
                    of.mkdir();

                FileOutputStream output = new FileOutputStream(outputFileName);
                Log.setOutput(output);

                Log.enable();

                Log.printLine("Starting CloudSim SDN...");

                try {
                    // Initialize
                    int num_user = 1; // number of cloud users
                    Calendar calendar = Calendar.getInstance();
                    boolean trace_flag = false; // mean trace events
                    CloudSim.init(num_user, calendar, trace_flag);

                    VmAllocationPolicyFactory vmAllocationFac = null;
                    LinkSelectionPolicy ls = null;
                    switch (vmAllocPolicy) {
                        case CombMFF:
                        case MFF:
                            vmAllocationFac = new VmAllocationPolicyFactory() {
                                public VmAllocationPolicy create(List<? extends Host> hostList) {
                                    return new VmAllocationPolicyCombinedMostFullFirst(hostList);
                                }
                            };
                            ls = new LinkSelectionPolicyBandwidthAllocation();
                            break;
                        case CombLFF:
                        case LFF:
                            vmAllocationFac = new VmAllocationPolicyFactory() {
                                public VmAllocationPolicy create(List<? extends Host> hostList) {
                                    return new VmAllocationPolicyCombinedLeastFullFirst(hostList);
                                }
                            };
                            ls = new LinkSelectionPolicyBandwidthAllocation();
                            break;
                        case MipMFF:
                            vmAllocationFac = new VmAllocationPolicyFactory() {
                                public VmAllocationPolicy create(List<? extends Host> hostList) {
                                    return new VmAllocationPolicyMipsMostFullFirst(hostList);
                                }
                            };
                            ls = new LinkSelectionPolicyBandwidthAllocation();
                            break;
                        case MipLFF:
                            vmAllocationFac = new VmAllocationPolicyFactory() {
                                public VmAllocationPolicy create(List<? extends Host> hostList) {
                                    return new VmAllocationPolicyMipsLeastFullFirst(hostList);
                                }
                            };
                            ls = new LinkSelectionPolicyBandwidthAllocation();
                            break;
                        default:
                            System.err.println("Choose proper VM placement polilcy!");
                            printUsage();
                            System.exit(1);
                    }

                    Configuration.monitoringTimeInterval = Configuration.migrationTimeInterval = 1;

                    // Create multiple Datacenters
                    Map<NetworkOperatingSystem, SDNDatacenter> dcs = createPhysicalTopology(physicalTopologyFile, ls,
                            vmAllocationFac);

                    // Broker
                    SDNBroker broker = createBroker();
                    int brokerId = broker.getId();

                    // Submit virtual topology
                    broker.submitDeployApplication(dcs.values(), deploymentFile);

                    // Submit workloads
                    submitWorkloads(broker);

                    // Failure and Recovery events generate and inject
//                    FRGenerator fg = new FRGenerator("failures_and_recoveries_file.csv");
                    FRGenerator fg = new FRGenerator(FR_file);
                    fg.generate();
                    List<String> FRFiles = new ArrayList<>();
                    FRFiles.add(FR_file);
                    submitFailueRecoveryEvent(broker, FRFiles);

                    // Sixth step: Starts the simulation
                    if (!logEnabled)
                        Log.disable();
                    long elaspedTime = startSimulation(broker, dcs.values());
                    String result = workload_folder + " elaspedTime is :" + elaspedTime;
                    System.out.println(result);
//                    resultList.add(result);
                    line += "," + elaspedTime;

                    System.out.println("The total counts for calling Normal distribution is :" + Configuration.normal_dist_call_frequency);
                    System.out.println("vm frequency is :" + Configuration.vm_freq);
                    System.out.println("channel frequency is :" + Configuration.channel_freq);

                    System.out.println("bw samplings frequency is :" + Configuration.total_bw_samplings);
                    System.out.println("mips samplings frequency is :" + Configuration.total_mips_samplings);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.printLine("Unwanted errors happen");
                }

            }
            resultList.add(line);
        }

        System.out.println(resultList);
        java.io.File f = new java.io.File(outerFolder.get(0) + "perf_result");
        try {
            if (!f.exists())
                f.createNewFile();
            java.io.FileWriter fw = new FileWriter(f);
            for (String line : resultList)
                fw.write(line + "\n");
            fw.flush();
            fw.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static long startSimulation(SDNBroker broker, Collection<SDNDatacenter> dcs) {
        long sTime = System.currentTimeMillis();
        double finishTime = CloudSim.startSimulation();
        CloudSim.stopSimulation();
        long eTime = System.currentTimeMillis();

        long elaspedTime = eTime - sTime;

        Log.enable();
        broker.printResult();

        Log.printLine(finishTime + ": ========== EXPERIMENT FINISHED ===========");

        // Print results when simulation is over
        List<Workload> wls = broker.getWorkloads();
        if (wls != null)
            LogPrinter.printWorkloadList(wls);

        // Print hosts' and switches' total utilization.
        List<Host> hostList = getAllHostList(dcs);
        List<Switch> switchList = getAllSwitchList(dcs);
//        LogPrinter.printEnergyConsumption(hostList, switchList, finishTime);

        Log.printLine("Simultanously used hosts:" + maxHostHandler.getMaxNumHostsUsed());
        Log.printLine("CloudSim SDN finished!");
        System.out.println("Elasped time: " + String.valueOf(elaspedTime));
        return elaspedTime;
    }

    private static List<Switch> getAllSwitchList(Collection<SDNDatacenter> dcs) {
        List<Switch> allSwitch = new ArrayList<Switch>();
        for (SDNDatacenter dc : dcs) {
            allSwitch.addAll(dc.getNOS().getSwitchList());
        }

        return allSwitch;
    }

    private static List<Host> getAllHostList(Collection<SDNDatacenter> dcs) {
        List<Host> allHosts = new ArrayList<Host>();
        for (SDNDatacenter dc : dcs) {
            if (dc.getNOS().getHostList() != null)
                allHosts.addAll(dc.getNOS().getHostList());
        }

        return allHosts;
    }

    public static Map<NetworkOperatingSystem, SDNDatacenter> createPhysicalTopology(String physicalTopologyFile,
                                                                                    LinkSelectionPolicy ls, VmAllocationPolicyFactory vmAllocationFac) {
        HashMap<NetworkOperatingSystem, SDNDatacenter> dcs = new HashMap<NetworkOperatingSystem, SDNDatacenter>();
        // This funciton creates Datacenters and NOS inside the data cetner.
        Map<String, NetworkOperatingSystem> dcNameNOS = PhysicalTopologyParser
                .loadPhysicalTopologyMultiDC(physicalTopologyFile);

        for (String dcName : dcNameNOS.keySet()) {
            NetworkOperatingSystem nos = dcNameNOS.get(dcName);
            nos.setLinkSelectionPolicy(ls);
            SDNDatacenter datacenter = createSDNDatacenter(dcName, nos, vmAllocationFac);
            dcs.put(nos, datacenter);
        }
        return dcs;
    }

    public static void submitWorkloads(SDNBroker broker) {
        // Submit workload files individually
        if (workloads != null) {
            for (String workload : workloads)
                if (workload.contains("csv") && !workload.contains("result"))
                    broker.submitRequests(workload);
        }

        // Or, Submit groups of workloads
        // submitGroupWorkloads(broker, WORKLOAD_GROUP_NUM, WORKLOAD_GROUP_PRIORITY,
        // WORKLOAD_GROUP_FILENAME, WORKLOAD_GROUP_FILENAME_BG);
    }

    public static void submitFailueRecoveryEvent(SDNBroker broker, List<String> FRFiles) {
        // Submit workload files individually
        if (FRFiles != null) {
            for (String frFile : FRFiles)
                broker.submitFREvents(frFile);
        }
    }

    public static void printArguments(String physical, String virtual, List<String> workloads) {
        System.out.println("Data center infrastructure (Physical Topology) : " + physical);
        System.out.println("Virtual Machine and Network requests (Virtual Topology) : " + virtual);
        System.out.println("Workloads: ");
        for (String work : workloads)
            System.out.println("  " + work);
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     * @return the datacenter
     */
    protected static PowerUtilizationMaxHostInterface maxHostHandler = null;

    protected static SDNDatacenter createSDNDatacenter(String name, NetworkOperatingSystem nos,
                                                       VmAllocationPolicyFactory vmAllocationFactory) {
        // In order to get Host information, pre-create NOS.
        List<Host> hostList = nos.getHostList();

        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";

        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
        // devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
                cost, costPerMem, costPerStorage, costPerBw);

        // Create Datacenter with previously set parameters
        SDNDatacenter datacenter = null;
        try {
            VmAllocationPolicy vmPolicy = null;
            // if(hostList.size() != 0)
            {
                vmPolicy = vmAllocationFactory.create(hostList);
                maxHostHandler = (PowerUtilizationMaxHostInterface) vmPolicy;
                datacenter = new SDNDatacenter(name, characteristics, vmPolicy, storageList, 0, nos);
            }

            nos.setDatacenter(datacenter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    // We strongly encourage users to develop their own broker policies, to
    // submit vms and cloudlets according
    // to the specific rules of the simulated scenario

    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    protected static SDNBroker createBroker() {
        SDNBroker broker = null;
        try {
            broker = new SDNBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    // static String WORKLOAD_GROUP_FILENAME = "workload_10sec_100_default.csv"; //
    // group 0~9
    // static String WORKLOAD_GROUP_FILENAME_BG = "workload_10sec_100.csv"; // group
    // 10~29
    // static int WORKLOAD_GROUP_NUM = 50;
    // static int WORKLOAD_GROUP_PRIORITY = 1;

    public static void submitGroupWorkloads(SDNBroker broker, int workloadsNum, int groupSeperateNum,
                                            String filename_suffix_group1, String filename_suffix_group2) {
        for (int set = 0; set < workloadsNum; set++) {
            String filename = filename_suffix_group1;
            if (set >= groupSeperateNum)
                filename = filename_suffix_group2;

            filename = set + "_" + filename;
            broker.submitRequests(filename);
        }
    }
}