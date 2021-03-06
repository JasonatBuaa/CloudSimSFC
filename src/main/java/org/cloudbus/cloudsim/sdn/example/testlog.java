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
import java.util.*;

/**
 * CloudSimSDN example main program for InterCloud scenario. This can create
 * multiple cloud data centers and send packets between them.
 * 
 * @author Jungmin Son
 * @since CloudSimSDN 3.0
 */
public class testlog {

	// protected static String rootFolder = "Scenario1-1/";
	protected static String rootFolder = "Compare/";
	protected static String physicalTopologyFile = rootFolder + "PhysicalResource.json";
	protected static String deploymentFile = rootFolder + "virtualTopology.json"; // virtual topology
	protected static String workload_folder = rootFolder + "workloads/";
	// protected static String[] workload_files = { "workloads_chain1.csv",
	// "workloads_chain1.csv" };
	protected static String[] workload_files;

	protected static String FR_file = "";

	private static String[] argString = { "LFF", physicalTopologyFile, deploymentFile, "./" };

	protected static List<String> FREvents;
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
		CloudSimEx.setStartTime();

		args = argString;

		VmAllocationPolicyEnum vmAllocPolicy = VmAllocationPolicyEnum.valueOf(args[0]);

		workload_files = getWorkloadFile(workload_folder);
		// workloads = (List<String>) Arrays.asList(workload_files);

		for (String str : workload_files) {
			workloads.add(workload_folder + str);
		}

		printArguments(physicalTopologyFile, deploymentFile, workloads);
		System.out.println(System.getProperty("user.dir"));
		FREvents = new ArrayList<>();
		FREvents.add(FR_file);

		String outputFileName = Configuration.workingDirectory + rootFolder + "results/" + Configuration.experimentName
				+ (Configuration.DISABLE_FAILURE_RECOVERY ? "" : "-with_FR_events-")
				+ (Configuration.DISABLE_MEMORY_QUEUE ? "" : "-with_Queue-") + "log.out.txt";
		FileOutputStream output = new FileOutputStream(outputFileName);
		Log.setOutput(output);

		Log.enable();

		Log.printLine("Starting CloudSim SDN...");

		Log.printLine("Starting CloudSim SDN...");

		try {
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

			FRGenerator fg = new FRGenerator("failures_and_recoveries_file.csv");
			fg.generate();

			// Submit individual workloads
			submitWorkloads(broker);

			submitFailueRecoveryEvent(broker); // Jason: submit FR (Availability) into the cloudsim simulation
			// system.

			// Sixth step: Starts the simulation
			if (!logEnabled)
				Log.disable();

			startSimulation(broker, dcs.values());

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	public static void startSimulation(SDNBroker broker, Collection<SDNDatacenter> dcs) {
		double finishTime = CloudSim.startSimulation();
		CloudSim.stopSimulation();

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
		LogPrinter.printEnergyConsumption(hostList, switchList, finishTime);

		Log.printLine("Simultanously used hosts:" + maxHostHandler.getMaxNumHostsUsed());
		Log.printLine("CloudSim SDN finished!");
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
				if(!workload.contains("result") && workload.contains("csv"))
				broker.submitRequests(workload);
		}

		// Or, Submit groups of workloads
		// submitGroupWorkloads(broker, WORKLOAD_GROUP_NUM, WORKLOAD_GROUP_PRIORITY,
		// WORKLOAD_GROUP_FILENAME, WORKLOAD_GROUP_FILENAME_BG);
	}

	public static void submitFailueRecoveryEvent(SDNBroker broker) {
		// Submit workload files individually
		if (FREvents != null) {
			for (String frEvent : FREvents)
				broker.submitFREvents(frEvent);
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
	 *
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