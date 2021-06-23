/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.sdn.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.sdn.CloudSimEx;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.HostFactory;
import org.cloudbus.cloudsim.sdn.HostFactoryOverbookable;
import org.cloudbus.cloudsim.sdn.SDNBroker;
import org.cloudbus.cloudsim.sdn.example.topogenerators.FRGenerator;
import org.cloudbus.cloudsim.sdn.monitor.power.PowerUtilizationMaxHostInterface;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystem;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystemSimple;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystemGroupAware;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystemGroupPriority;
import org.cloudbus.cloudsim.sdn.parsers.PhysicalTopologyParser;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNDatacenter;
import org.cloudbus.cloudsim.sdn.physicalcomponents.switches.Switch;
import org.cloudbus.cloudsim.sdn.policies.selecthost.HostSelectionPolicy;
import org.cloudbus.cloudsim.sdn.policies.selecthost.HostSelectionPolicyMostFull;
import org.cloudbus.cloudsim.sdn.policies.selectlink.LinkSelectionPolicy;
import org.cloudbus.cloudsim.sdn.policies.selectlink.LinkSelectionPolicyDestinationAddress;
import org.cloudbus.cloudsim.sdn.policies.selectlink.LinkSelectionPolicyFlowCapacity;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyCombinedLeastFullFirst;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyCombinedMostFullFirst;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyGroupConnectedFirst;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmAllocationPolicyPriorityFirst;
import org.cloudbus.cloudsim.sdn.policies.vmallocation.VmMigrationPolicy;
import org.cloudbus.cloudsim.sdn.workload.Workload;

/**
 * CloudSimSDN example main program. It loads physical topology file,
 * application deployment configuration file and workload files, and run
 * simulation. Simulation result will be shown on the console
 * 
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */
public class StartAvailability {

    protected static List<String> workloads;

    protected static List<String> failOverEvents;

    // protected static String physicalTopologyFile =
    // "SmallSFCDemo/4HostsPhysical.json";
    // protected static String deploymentFile = "SmallSFCDemo/4VMSmallVirtual.json";
    // protected static String workload_file = "SmallSFCDemo/singleSmallWL.csv";

    // protected static String physicalTopologyFile =
    // "SmallSFCDemo/4HostsPhysical.json"; //Jason: this file does not configure the
    // availability value

    protected static String physicalTopologyFile = "SmallSFCDemo/4AvailabilityHostsPhysical.json"; // Jason: this file
                                                                                                   // configures the
                                                                                                   // availability value

    // protected static String deploymentFile = "SmallSFCDemo/SingleSF.json"; //
    // without queue
    protected static String deploymentFile = "SmallSFCDemo/SingleQueuedSF.json";

    // protected static String workload_file = "SmallSFCDemo/TripleWL.csv";
    // protected static String workload_file = "SmallSFCDemo/WLQueue.csv";

    // protected static String workload_file = "SmallSFCDemo/jasontestFile.csv";
    protected static String workload_file = "SmallSFCDemo/demoWorkload.csv";

    protected static String failOver_file = "SmallSFCDemo/FailureEvent.csv";

    protected static String[] workload_files = {};

    private static String[] argString = { "LFF", physicalTopologyFile, deploymentFile, "./", workload_file,
            failOver_file };

    public static boolean failOverDebug = false; // True: Do not injuct fail over event.
    public static boolean queueDebug = false; // True: Do not use MemoryQueue.

    private static boolean logEnabled = true; // Log for debug
    /*
     * / private static boolean logEnabled = false; //
     */

    public interface VmAllocationPolicyFactory {
        public VmAllocationPolicy create(List<? extends Host> list, HostSelectionPolicy hostSelectionPolicy,
                VmMigrationPolicy vmMigrationPolicy);
    }

    enum VmAllocationPolicyEnum {
        LFF, MFF, HPF, MFFGroup, LFFFlow, MFFFlow, HPFFlow, MFFGroupFlow, Random, RandomFlow, END
    }

    private static void printUsage() {
        String runCmd = "java SDNExample";
        System.out.format(
                "Usage: %s <LFF|MFF> <physical.json> <virtual.json> <working_dir> [workload1.csv] [workload2.csv] [...]\n",
                runCmd);
    }

    public static String policyName = "";

    public static void setExpName(String policy) {
        Configuration.experimentName = String.format("cpu%d_net%d_%s_min%d_util%d_",
                (int) (Configuration.CPU_SIZE_MULTIPLY * 100), (int) (Configuration.NETWORK_PACKET_SIZE_MULTIPLY * 100),
                policy, (int) (Configuration.OVERBOOKING_RATIO_MIN * 100),
                (int) (Configuration.OVERBOOKING_RATIO_UTIL_PORTION * 100));
    }

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) throws FileNotFoundException {

        CloudSimEx.setStartTime();
        args = argString;

        // Parse system arguments
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        StartAvailability.printArgs(args, args.length);
        int n = 0;
        System.out.println(System.getProperty("user.dir"));
        File directory =new File("physicalTopologyFile");  
        // System.out.println(directory.getCanonicalPath());//获取标准的路径
        System.out.println(directory.getAbsolutePath());

        // 1. Policy: MFF, LFF, ...
        String policy = args[n++];

        // Configuration.OVERBOOKING_RATIO_INIT = Double.parseDouble(args[n++]);

        setExpName(policy);
        VmAllocationPolicyEnum vmAllocPolicy = VmAllocationPolicyEnum.valueOf(policy);

        // 2. Physical Topology filename
        if (args.length > n)
            physicalTopologyFile = args[n++];

        // 3. Virtual Topology filename
        if (args.length > n)
            deploymentFile = args[n++];

        // 4. Workload files
        // 4-1. Group workloads: <start_index_1> <end_index_1> <file_suffix_1> ...
        // 4-2. Normal workloads: <working_directory> <filename1> <filename2> ...
        if (args.length > n) {
            workloads = new ArrayList<String>();
            if (isInteger(args[n])) {
                // args: <startIndex1> <endIndex1> <filename_suffix1> ...
                int i = n;
                while (i < args.length - 1) {
                    Integer startNum = Integer.parseInt(args[i++]);
                    Integer endNum = Integer.parseInt(args[i++]);
                    String filenameSuffix = args[i++];
                    List<String> names = createGroupWorkloads(startNum, endNum, filenameSuffix);
                    workloads.addAll(names);
                }
            } else {
                int i = n;
                if (args.length > n + 2) {
                    // 4th arg is workload directory.
                    Configuration.workingDirectory = args[n++];
                    i = n;
                }
                // args: <filename1> <filename2> ...
                for (; i < args.length - 1; i++) {
                    workloads.add(args[i]);
                }
            }
        } else {
            workloads = (List<String>) Arrays.asList(workload_files);
        }
        failOverEvents = new ArrayList<>();
        failOverEvents.add(args[args.length - 1]);

        String outputFileName = Configuration.workingDirectory + Configuration.experimentName
                + (failOverDebug ? "" : "Failover") + (!queueDebug ? "" : "Queue") + "log.out.txt";
        FileOutputStream output = new FileOutputStream(outputFileName);
        Log.setOutput(output);

        Log.enable();

        printArguments(physicalTopologyFile, deploymentFile, Configuration.workingDirectory, workloads);
        Log.printLine("Starting CloudSim SDN...");

        try {
            // Initialize
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events
            CloudSim.init(num_user, calendar, trace_flag);

            VmAllocationPolicyFactory vmAllocationFac = null;
            NetworkOperatingSystem nos = null;
            HostFactory hsFac = null;
            HostSelectionPolicy hostSelectionPolicy = null;
            VmMigrationPolicy vmMigrationPolicy = null;
            LinkSelectionPolicy ls = new LinkSelectionPolicyDestinationAddress();
            ;

            switch (vmAllocPolicy) {
                case Random:
                case RandomFlow:
                    vmAllocationFac = new VmAllocationPolicyFactory() {
                        public VmAllocationPolicy create(List<? extends Host> list,
                                HostSelectionPolicy hostSelectionPolicy, VmMigrationPolicy vmMigrationPolicy) {
                            return new VmAllocationPolicyCombinedLeastFullFirst(list);
                        }
                    };
                    nos = new NetworkOperatingSystemSimple();
                    hsFac = new HostFactoryOverbookable();
                    PhysicalTopologyParser.loadPhysicalTopologySingleDC(physicalTopologyFile, nos, hsFac);
                    break;
                case MFF:
                case MFFFlow:
                    vmAllocationFac = new VmAllocationPolicyFactory() {
                        public VmAllocationPolicy create(List<? extends Host> list,
                                HostSelectionPolicy hostSelectionPolicy, VmMigrationPolicy vmMigrationPolicy) {
                            return new VmAllocationPolicyCombinedMostFullFirst(list);
                        }
                    };
                    nos = new NetworkOperatingSystemSimple();
                    hsFac = new HostFactoryOverbookable();
                    PhysicalTopologyParser.loadPhysicalTopologySingleDC(physicalTopologyFile, nos, hsFac);
                    break;
                case LFF:
                case LFFFlow:
                    vmAllocationFac = new VmAllocationPolicyFactory() {
                        public VmAllocationPolicy create(List<? extends Host> list,
                                HostSelectionPolicy hostSelectionPolicy, VmMigrationPolicy vmMigrationPolicy) {
                            return new VmAllocationPolicyCombinedLeastFullFirst(list);
                        }
                    };
                    nos = new NetworkOperatingSystemSimple();
                    hsFac = new HostFactoryOverbookable();
                    PhysicalTopologyParser.loadPhysicalTopologySingleDC(physicalTopologyFile, nos, hsFac);
                    break;
                case MFFGroup:
                case MFFGroupFlow:
                    // Initial placement: overbooking, MFF
                    // Initial placement connectivity: Connected VMs in one host
                    // Migration: none
                    vmAllocationFac = new VmAllocationPolicyFactory() {
                        public VmAllocationPolicy create(List<? extends Host> list,
                                HostSelectionPolicy hostSelectionPolicy, VmMigrationPolicy vmMigrationPolicy) {
                            return new VmAllocationPolicyGroupConnectedFirst(list, hostSelectionPolicy,
                                    vmMigrationPolicy);
                        }
                    };
                    nos = new NetworkOperatingSystemGroupAware();
                    hsFac = new HostFactoryOverbookable();
                    PhysicalTopologyParser.loadPhysicalTopologySingleDC(physicalTopologyFile, nos, hsFac);
                    hostSelectionPolicy = new HostSelectionPolicyMostFull();
                    vmMigrationPolicy = null;
                    break;
                case HPF: // High Priority First
                case HPFFlow:
                    // Initial placement: overbooking, MFF
                    // Initial placement connectivity: Connected VMs in one host
                    // Migration: none
                    vmAllocationFac = new VmAllocationPolicyFactory() {
                        public VmAllocationPolicy create(List<? extends Host> list,
                                HostSelectionPolicy hostSelectionPolicy, VmMigrationPolicy vmMigrationPolicy) {
                            return new VmAllocationPolicyPriorityFirst(list, hostSelectionPolicy, vmMigrationPolicy);
                        }
                    };
                    nos = new NetworkOperatingSystemGroupPriority();
                    hsFac = new HostFactoryOverbookable();
                    PhysicalTopologyParser.loadPhysicalTopologySingleDC(physicalTopologyFile, nos, hsFac);
                    hostSelectionPolicy = new HostSelectionPolicyMostFull();
                    vmMigrationPolicy = null;
                    break;
                default:
                    System.err.println("Choose proper VM placement polilcy!");
                    printUsage();
                    System.exit(1);
            }

            switch (vmAllocPolicy) {
                case MFFFlow:
                case LFFFlow:
                case MFFGroupFlow:
                case HPFFlow:
                case RandomFlow:
                    ls = new LinkSelectionPolicyFlowCapacity();
                    break;
                default:
                    break;
            }

            nos.setLinkSelectionPolicy(ls);
            // snos.setMonitorEnable(false);

            // Create a Datacenter
            // SDNDatacenter datacenter = createSDNDatacenter("Datacenter_0",
            // physicalTopologyFile, nos, vmAllocationFac,
            // hostSelectionPolicy, vmMigrationPolicy);

            SDNDatacenter datacenter = createSDNDatacenter("dc1", physicalTopologyFile, nos, vmAllocationFac,
                    hostSelectionPolicy, vmMigrationPolicy);

            // Broker
            SDNBroker broker = createBroker();
            int brokerId = broker.getId();

            // Submit virtual topology
            broker.submitDeployApplication(datacenter, deploymentFile);

            // Submit: include individual workloads into the corresponding path
            submitWorkloads(broker);

            submitAvailabilityEvent(broker); // Jason: submit failOVer (Availability) into the cloudsim simulation
                                             // system.

            FRGenerator fg = new FRGenerator("failover_file.csv");
            fg.test();

            // Sixth step: Starts the simulation
            if (!StartAvailability.logEnabled)
                Log.disable();

            double finishTime = CloudSim.startSimulation();
            CloudSim.stopSimulation();
            Log.enable();

            Log.printLine(finishTime + ": ========== EXPERIMENT FINISHED ===========");

            // Print results when simulation is over
            List<Workload> wls = broker.getWorkloads(); // Jason: Workload的computing delay 和 communication delay如何计算？
            if (wls != null)
                LogPrinter.printWorkloadList(wls);

            // Print hosts' and switches' total utilization.
            List<Host> hostList = datacenter.getHostList();
            List<Switch> switchList = nos.getSwitchList();
            LogPrinter.printEnergyConsumption(hostList, switchList, finishTime);

            LogPrinter.printConfiguration();
            LogPrinter.printTotalEnergy();

            Log.printLine("Simultanously used hosts:" + maxHostHandler.getMaxNumHostsUsed());

            broker.printResult();

            Log.printLine("CloudSim SDN finished!");

            System.out.println("Elapsed time for simulation: " + CloudSimEx.getElapsedTimeString());
            System.out.println(Configuration.experimentName + " simulation finished.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    public static void submitWorkloads(SDNBroker broker) {
        // Submit workload files individually
        if (workloads != null) {
            for (String workload : workloads)
                broker.submitRequests(workload);
        }
    }

    public static void submitAvailabilityEvent(SDNBroker broker) {
        // Submit workload files individually
        if (failOverEvents != null) {
            for (String failOverEvent : failOverEvents)
                broker.submitFailOverEvents(failOverEvent);
        }
    }

    public static void printArguments(String physical, String virtual, String dir, List<String> workloads) {
        Log.printLine("Data center infrastructure (Physical Topology) : " + physical);
        Log.printLine("Virtual Machine and Network requests (Virtual Topology) : " + virtual);
        Log.printLine("Workloads in " + dir + " :");
        for (String work : workloads)
            Log.printLine("  " + work);
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     *
     * @return the datacenter
     */
    protected static NetworkOperatingSystem nos;
    protected static PowerUtilizationMaxHostInterface maxHostHandler = null;

    protected static SDNDatacenter createSDNDatacenter(String name, String physicalTopology,
            NetworkOperatingSystem snos, VmAllocationPolicyFactory vmAllocationFactory,
            HostSelectionPolicy hostSelectionPolicy, VmMigrationPolicy vmMigrationPolicy) {
        // In order to get Host information, pre-create NOS.
        nos = snos;
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
            VmAllocationPolicy vmPolicy = vmAllocationFactory.create(hostList, hostSelectionPolicy, vmMigrationPolicy);
            maxHostHandler = (PowerUtilizationMaxHostInterface) vmPolicy;
            datacenter = new SDNDatacenter(name, characteristics, vmPolicy, storageList, 0, nos);

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

    public static void printArgs(String[] args, int len) {
        String[] confName = { "policy", "physicalTopologyFile", "deploymentFile (i.e., virtual File)",
                "workingDirectory", "WorkLoad File Name" };
        for (int i = 0; i < len; i++) {
            if (i < confName.length)
                System.out.println(confName[i] + " : " + args[i]);
            else
                System.out.println(" + " + args[i]);
        }
    }

    private static List<String> createGroupWorkloads(int start, int end, String filename_suffix_group) {
        List<String> filenameList = new ArrayList<String>();

        for (int set = start; set <= end; set++) {
            String filename = Configuration.workingDirectory + set + "_" + filename_suffix_group;
            filenameList.add(filename);
        }
        return filenameList;
    }

    public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}