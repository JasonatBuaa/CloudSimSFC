/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.sdn.example.StartAvailability;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystem;
import org.cloudbus.cloudsim.sdn.parsers.DeploymentFileParser;
import org.cloudbus.cloudsim.sdn.parsers.FREventParser;
import org.cloudbus.cloudsim.sdn.parsers.PhysicalTopologyParser;
import org.cloudbus.cloudsim.sdn.parsers.VirtualTopologyParser;
import org.cloudbus.cloudsim.sdn.parsers.WorkloadParser;
// import org.cloudbus.cloudsim.sdn.physicalcomponents.PhysicalTopology;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNDatacenter;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunction;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunctionChainPolicy;
import org.cloudbus.cloudsim.sdn.virtualcomponents.FlowConfig;
import org.cloudbus.cloudsim.sdn.virtualcomponents.SDNVm;
import org.cloudbus.cloudsim.sdn.workload.FREvent;
import org.cloudbus.cloudsim.sdn.workload.Request;
import org.cloudbus.cloudsim.sdn.workload.Workload;
import org.cloudbus.cloudsim.sdn.workload.WorkloadResultWriter;

/**
 * Broker class for CloudSimSDN example. This class represents a broker (Service
 * Provider) who uses the Cloud data center.
 * 
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */
public class SDNBroker extends SimEntity {

	public static double experimentStartTime = -1;
	public static double experimentFinishTime = Double.POSITIVE_INFINITY;

	public static int lastAppId = 0;

	public static Map<String, SDNDatacenter> datacenters = new HashMap<String, SDNDatacenter>();
	private static Map<Integer, SDNDatacenter> vmIdToDc = new HashMap<Integer, SDNDatacenter>();

	private String applicationFileName = null;
	private HashMap<WorkloadParser, Integer> workloadId = null;

	private HashMap<FREventParser, Integer> FRIds = null; // Jason: For FR Events
	private HashMap<Long, Workload> requestMap = null;
	private List<String> workloadFileNames = null;

	// private List<String> availEventFileNames = null;
	private List<String> FRFileNames = null;

	public SDNBroker(String name) throws Exception {
		super(name);
		this.workloadFileNames = new ArrayList<String>();

		this.FRFileNames = new ArrayList<String>();
		workloadId = new HashMap<WorkloadParser, Integer>();
		FRIds = new HashMap<FREventParser, Integer>();
		requestMap = new HashMap<Long, Workload>();

	}

	@Override
	public void startEntity() {
		sendNow(getId(), CloudSimTagsSDN.APPLICATION_SUBMIT, this.applicationFileName);
	}

	@Override
	public void shutdownEntity() {
		for (SDNDatacenter datacenter : datacenters.values()) {
			List<Vm> vmList = datacenter.getVmList();
			for (Vm vm : vmList) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Shuttingdown.. VM:" + vm.getId());
			}
		}
	}

	public void printResult() {
		int numWorkloads = 0, numWorkloadsCPU = 0, numWorkloadsNetwork = 0, numWorkloadsOver = 0,
				numWorkloadsNetworkOver = 0, numWorkloadsCPUOver = 0, numTimeout = 0;
		double totalServetime = 0, totalServetimeCPU = 0, totalServetimeNetwork = 0;

		// For group analysis
		int[] groupNumWorkloads = new int[SDNBroker.lastAppId];
		double[] groupTotalServetime = new double[SDNBroker.lastAppId];
		double[] groupTotalServetimeCPU = new double[SDNBroker.lastAppId];
		double[] groupTotalServetimeNetwork = new double[SDNBroker.lastAppId];

		for (WorkloadParser wp : workloadId.keySet()) {
			WorkloadResultWriter wrw = wp.getResultWriter();
			if (wrw == null) {
				try {
					throw new Exception();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			wrw.printStatistics();

			numWorkloads += wrw.getWorklaodNum();
			numTimeout += wrw.getTimeoutNum();
			numWorkloadsOver += wrw.getWorklaodNumOvertime();
			numWorkloadsCPU += wrw.getWorklaodNumCPU();
			numWorkloadsCPUOver += wrw.getWorklaodNumCPUOvertime();
			numWorkloadsNetwork += wrw.getWorklaodNumNetwork();
			numWorkloadsNetworkOver += wrw.getWorklaodNumNetworkOvertime();

			totalServetime += wrw.getServeTime();
			totalServetimeCPU += wrw.getServeTimeCPU();
			totalServetimeNetwork += wrw.getServeTimeNetwork();

			// For group analysis
			groupNumWorkloads[wp.getGroupId()] += wrw.getWorklaodNum();
			groupTotalServetime[wp.getGroupId()] += wrw.getServeTime();
			groupTotalServetimeCPU[wp.getGroupId()] += wrw.getServeTimeCPU();
			groupTotalServetimeNetwork[wp.getGroupId()] += wrw.getServeTimeNetwork();
		}

		Log.printLine("============= SDNBroker.printResult() =============================");
		Log.printLine("Workloads Num: " + numWorkloads);
		Log.printLine("Workloads CPU Num: " + numWorkloadsCPU);
		Log.printLine("Workloads Network Num: " + numWorkloadsNetwork);
		Log.printLine("Workloads Timed Out Num: " + numTimeout);

		Log.printLine("Total serve time: " + totalServetime);
		Log.printLine("Total serve time CPU: " + totalServetimeCPU);
		Log.printLine("Total serve time Network: " + totalServetimeNetwork);
		if (numWorkloads != 0) {
			Log.printLine("Avg serve time: " + totalServetime / numWorkloads);
			Log.printLine("Overall overtime percentage: " + (double) numWorkloadsOver / numWorkloads);
		}
		if (numWorkloadsCPU != 0) {
			Log.printLine("Avg serve time CPU: " + totalServetimeCPU / numWorkloadsCPU);
			Log.printLine("CPU overtime percentage: " + (double) numWorkloadsCPUOver / numWorkloadsCPU);
		}
		if (numWorkloadsNetwork != 0) {
			Log.printLine("Avg serve time Network: " + totalServetimeNetwork / numWorkloadsNetwork);
			Log.printLine("Network overtime percentage: " + (double) numWorkloadsNetworkOver / numWorkloadsNetwork);
		}

		// For group analysis
		Log.printLine("============= SDNBroker.printResult() Group analysis =======================");
		for (int i = 0; i < SDNBroker.lastAppId; i++) {
			if (groupNumWorkloads[i] != 0) {
				Log.printLine("Group num: " + i + ", groupNumWorkloads:" + groupNumWorkloads[i]);
				Log.printLine("Group num: " + i + ", groupTotalServetime:" + groupTotalServetime[i]);
				Log.printLine("Group num: " + i + ", groupTotalServetimeCPU:" + groupTotalServetimeCPU[i]);
				Log.printLine("Group num: " + i + ", groupTotalServetimeNetwork:" + groupTotalServetimeNetwork[i]);
				Log.printLine(
						"Group num: " + i + ", group avg Serve time:" + groupTotalServetime[i] / groupNumWorkloads[i]);
				Log.printLine("Group num: " + i + ", group avg Serve time CPU:"
						+ groupTotalServetimeCPU[i] / groupNumWorkloads[i]);
				Log.printLine("Group num: " + i + ", group avg Serve time Network:"
						+ groupTotalServetimeNetwork[i] / groupNumWorkloads[i]);
			}

		}
	}

	public void submitDeployApplication(SDNDatacenter dc, String filename) {
		SDNBroker.datacenters.put(dc.getName(), dc); // default DC
		this.applicationFileName = filename;
	}

	public void submitDeployApplication(Collection<SDNDatacenter> dcs, String filename) {
		for (SDNDatacenter dc : dcs) {
			if (dc != null)
				SDNBroker.datacenters.put(dc.getName(), dc); // default DC
		}
		this.applicationFileName = filename;
	}

	public void submitRequests(String filename) {
		this.workloadFileNames.add(filename);
	}

	public void submitFREvents(String filename) {
		this.FRFileNames.add(filename);
	}

	@Override
	public void processEvent(SimEvent ev) {
		int tag = ev.getTag();

		switch (tag) {
			case CloudSimTags.VM_CREATE_ACK:
				processVmCreate(ev);
				break;
			case CloudSimTagsSDN.APPLICATION_SUBMIT:
				processApplication(ev.getSource(), (String) ev.getData());
				break;
			case CloudSimTagsSDN.APPLICATION_SUBMIT_ACK:
				applicationSubmitCompleted(ev);
				hostFR(ev); // Jason: Todo! check
				break;
			case CloudSimTagsSDN.REQUEST_COMPLETED:
				requestCompleted(ev);
				break;
			case CloudSimTagsSDN.REQUEST_FAILED:
				requestFailed(ev);
				break;
			case CloudSimTagsSDN.REQUEST_OFFER_MORE:
				requestOfferMode(ev);
				break;
			default:
				System.out.println("Unknown event received by " + super.getName() + ". Tag:" + ev.getTag());
				break;
		}
	}

	private void processVmCreate(SimEvent ev) {

	}

	private void requestFailed(SimEvent ev) {
		Request req = (Request) ev.getData();
		Workload wl = requestMap.remove(req.getRequestId());
		wl.failed = true;
		wl.writeResult();
	}

	private void requestCompleted(SimEvent ev) {
		Request req = (Request) ev.getData();
		Workload wl = requestMap.remove(req.getRequestId());
		wl.writeResult();
	}

	private void applicationSubmitCompleted(SimEvent ev) {

		for (String filename : this.workloadFileNames) {
			WorkloadParser wParser = startWorkloadParser(filename);
			workloadId.put(wParser, SDNBroker.lastAppId);
			SDNBroker.lastAppId++;

			scheduleRequest(wParser);
		}
	}

	// private void injectFREvents(SimEvent ev) {

	// }

	/**
	 * Jason: host availability event
	 * 
	 * Gets from applicationSubmitCompleted
	 * 
	 * @param ev
	 */
	private void hostFR(SimEvent ev) {

		// if (StartAvailability.FRDebug)
		if (Configuration.DISABLE_FAILURE_RECOVERY)
			return;
		for (String filename : this.FRFileNames) {
			FREventParser frEventParser = startFREventParser(filename);
			FRIds.put(frEventParser, SDNBroker.lastAppId);
			SDNBroker.lastAppId++;

			injectFREvents(frEventParser);
		}
	}

	/**
	 * Jason: initilize the regarding data structures, including: VMs, arcs, NFC
	 * policy, nos
	 * 
	 * @param userId
	 * @param vmsFileName
	 */
	private void processApplication(int userId, String vmsFileName) {
		SDNDatacenter defaultDC = SDNBroker.datacenters.entrySet().iterator().next().getValue();
		DeploymentFileParser parser = new DeploymentFileParser(defaultDC.getName(), vmsFileName, userId);

		for (String dcName : SDNBroker.datacenters.keySet()) {
			SDNDatacenter dc = SDNBroker.datacenters.get(dcName);
			NetworkOperatingSystem nos = dc.getNOS();

			for (SDNVm vm : parser.getVmList(dcName)) {
				nos.addVm(vm);
				if (vm instanceof ServiceFunction) {
					ServiceFunction sf = (ServiceFunction) vm;
					sf.setNetworkOperatingSystem(nos);
				}
				SDNBroker.vmIdToDc.put(vm.getId(), dc);
			}
			// System.out.println("test");
		}

		for (FlowConfig arc : parser.getArcList()) {
			SDNDatacenter srcDc = SDNBroker.vmIdToDc.get(arc.getSrcId());
			SDNDatacenter dstDc = SDNBroker.vmIdToDc.get(arc.getDstId());

			if (srcDc.equals(dstDc)) {
				// Intra-DC traffic: create a virtual flow inside the DC
				srcDc.getNOS().addFlow(arc);
			} else {
				// Inter-DC traffic: Create it in inter-DC N.O.S.
				srcDc.getNOS().addFlow(arc);
				dstDc.getNOS().addFlow(arc);
			}
		}

		// Add parsed ServiceFunctionChainPolicy
		for (ServiceFunctionChainPolicy policy : parser.getSFCPolicyList()) {
			SDNDatacenter srcDc = SDNBroker.vmIdToDc.get(policy.getSrcId());
			SDNDatacenter dstDc = SDNBroker.vmIdToDc.get(policy.getDstId());
			if (srcDc.equals(dstDc)) {
				// Intra-DC traffic: create a virtual flow inside the DC
				srcDc.getNOS().addSFCPolicy(policy);
			} else {
				// Inter-DC traffic: Create it in inter-DC N.O.S.
				srcDc.getNOS().addSFCPolicy(policy);
				dstDc.getNOS().addSFCPolicy(policy);
			}
		}

		for (String dcName : SDNBroker.datacenters.keySet()) {
			SDNDatacenter dc = SDNBroker.datacenters.get(dcName);
			NetworkOperatingSystem nos = dc.getNOS();
			nos.startDeployApplicatoin();
		}

		send(userId, 0, CloudSimTagsSDN.APPLICATION_SUBMIT_ACK, vmsFileName);
	}

	public static SDNDatacenter getDataCenterByName(String dcName) {
		return SDNBroker.datacenters.get(dcName);
	}

	public static SDNDatacenter getDataCenterByVmID(int vmId) {
		return SDNBroker.vmIdToDc.get(vmId);
	}

	private void requestOfferMode(SimEvent ev) {
		WorkloadParser wp = (WorkloadParser) ev.getData();
		scheduleRequest(wp);
	}

	/**
	 * Jason: Todo! find host_name to host_id relationship
	 * 
	 * @param FREventFile
	 * @return
	 */
	private FREventParser startFREventParser(String FREventFile) {
		Map<String, Integer> hostIdMap = new HashMap<>(); // Jason:Todo! finish this part!!
		FREventParser FREventParser = new FREventParser(FREventFile);
		// (FREventFile,

		for (SDNDatacenter datacenter : datacenters.values()) {
			List<? extends Host> hostList = datacenter.getVmAllocationPolicy().getHostList();
			for (Host targetHost : hostList) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Shuttingdown.. VM:" + targetHost.getId());
			}
		}

		// System.err.println("SDNBroker.startWorkloadParser : DEBUGGGGGGGGGGG REMOVE
		// here!");
		FREventParser.forceStartTime(experimentStartTime);
		FREventParser.forceFinishTime(experimentFinishTime);
		return FREventParser;

	}

	private WorkloadParser startWorkloadParser(String workloadFile) {
		// WorkloadParser workParser = new WorkloadParser(workloadFile, this.getId(),
		// new UtilizationModelFull(),
		// NetworkOperatingSystem.getVmNameToIdMap(),
		// NetworkOperatingSystem.getFlowNameToIdMap());
		WorkloadParser workParser = new WorkloadParser(workloadFile, this.getId(), new UtilizationModelFull(),
				NetworkOperatingSystem.getVmNameToIdMap());

		// System.err.println("SDNBroker.startWorkloadParser : DEBUGGGGGGGGGGG REMOVE
		// here!");
		workParser.forceStartTime(experimentStartTime);
		workParser.forceFinishTime(experimentFinishTime);
		return workParser;

	}

	private void scheduleRequest(WorkloadParser workParser) {
		int workloadId = this.workloadId.get(workParser);
		workParser.parseNextWorkloads();
		List<Workload> parsedWorkloads = workParser.getParsedWorkloads();

		if (parsedWorkloads.size() > 0) {
			// Schedule the parsed workloads
			for (Workload wl : parsedWorkloads) {
				double scehduleTime = wl.time - CloudSim.clock();
				if (scehduleTime < 0) {
					// throw new IllegalArgumentException("SDNBroker.scheduleRequest(): Workload's
					// start time is negative: " + wl);
					Log.printLine("**" + CloudSim.clock() + ": SDNBroker.scheduleRequest(): abnormal start time." + wl);
					continue;
				}
				wl.appId = workloadId;
				SDNDatacenter dc = SDNBroker.vmIdToDc.get(wl.submitVmId);
				send(dc.getId(), scehduleTime, CloudSimTagsSDN.REQUEST_SUBMIT, wl.request);
				requestMap.put(wl.request.getTerminalRequest().getRequestId(), wl);
			}

			// this.cloudletList.addAll(workParser.getParsedCloudlets());
			// this.workloads.addAll(parsedWorkloads);

			// Schedule the next workload submission
			Workload lastWorkload = parsedWorkloads.get(parsedWorkloads.size() - 1);
			send(this.getId(), lastWorkload.time - CloudSim.clock(), CloudSimTagsSDN.REQUEST_OFFER_MORE, workParser);
		}
	}
	// List<FREvent> parsedFREvents =

	/**
	 * Jason: Gets from Schedule Requests
	 * 
	 * @param FREventParser
	 */
	private void injectFREvents(FREventParser FREventParser) {
		int FRId = this.FRIds.get(FREventParser);
		FREventParser.parseNextFREvents();
		List<FREvent> parsedFREvents = FREventParser.getParsedFREvents();

		if (parsedFREvents.size() > 0) {
			// Schedule the parsed workloads
			for (FREvent fow : parsedFREvents) {
				double injectTime = fow.getTime() - CloudSim.clock();
				if (injectTime < 0) {
					// throw new IllegalArgumentException("SDNBroker.scheduleRequest(): Workload's
					// start time is negative: " + wl);
					Log.printLine(
							"**" + CloudSim.clock() + ": SDNBroker.scheduleRequest(): abnormal start time." + fow);
					continue;
				}
				fow.fREventId = FRId;
				int hostId = fow.getHostID();

				double failureTime = fow.getFailureTime();
				double recoveryTime = fow.getRecoveryTime();
				if (failureTime > recoveryTime) {
					// throw new IllegalArgumentException("SDNBroker.scheduleRequest(): Workload's
					// start time is negative: " + wl);
					Log.printLine("** ERROR!!" + CloudSim.clock()
							+ ": SDNBroker.scheduleRequest(): abnormal failure and recovery time." + fow);
					continue;
				}

				// String dcName;
				// SDNDatacenter dc = SDNBroker.vmIdToDc.get(fow.submitVmId);
				// SDNHost host = (SDNHost)
				// SDNBroker.getDataCenterByName(dcName).getHostList().get(hostId);
				SDNHost host = null;
				String dc_name = null;

				// Jason: locate the failure host
				// for(SDNHost h: PhysicalTopologyParser.deployedHosts.values()){
				// if(h.getId() == hostId)
				// host = h;
				// }

				for (Map.Entry<String, SDNHost> h : PhysicalTopologyParser.deployedHosts.entries()) {

					System.out.println("--- " + h.getKey());
					if (h.getValue().getId() == hostId) {
						host = h.getValue();
						dc_name = h.getKey();
						break;
					}
				}

				if (host == null) {
					System.out.println("ERROR!!! Could not find the corresponding host!!!");
					continue;
				} else {
					SDNDatacenter dc = getDataCenterByName(dc_name);
					send(dc.getId(), failureTime, CloudSimTagsSDN.SDN_HOST_FAIL, host);
					send(dc.getId(), recoveryTime, CloudSimTagsSDN.SDN_HOST_RECOVER, host);
				}

				// Jason: Todo! Complete the following steps
				// steps: 1-2 need test, 3 need to be done
				// 1. find the corresponding host
				// 2. generate a host_failure event, and send it (the source is SDNBroker, the
				// dest is SDNDataCenter)
				// 3. go to the SDNDataCenter for further event processing

				// requestMap.put(wl.request.getTerminalRequest().getRequestId(), wl);
			}
		}
	}

	public List<Workload> getWorkloads() {
		// return workloads;
		return null;
	}
}
