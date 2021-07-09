/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.sdn.parsers;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.example.StartAvailability;
import org.cloudbus.cloudsim.sdn.workload.*;
import org.omg.Messaging.SyncScopeHelper;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunctionChainPolicy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.google.common.collect.Table;

/**
 * Parse [request].csv file.
 * 
 * File format : req_time, vm_name(1), pkt_size(1), cloudlet_len(1), vm_name(2),
 * pkt_size(2), cloudlet_len(2), ...
 * 
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */

public class WorkloadParser {
	private static final int NUM_PARSE_EACHTIME = 200;

	private double forcedStartTime = -1;
	private double forcedFinishTime = Double.POSITIVE_INFINITY;

	private final Map<String, Integer> vmNames;
	private final Map<String, Integer> flowNames;
	private String file;
	private int userId;
	private UtilizationModel utilizationModel;

	private List<Workload> parsedWorkloads;

	private WorkloadResultWriter resultWriter = null;

	private int workloadNum = 0;

	private BufferedReader bufReader = null;

	public WorkloadParser(String file, int userId, UtilizationModel cloudletUtilModel, Map<String, Integer> vmNameIdMap,
			Map<String, Integer> flowNameIdMap) {
		this.file = file;
		this.userId = userId;
		this.utilizationModel = cloudletUtilModel;
		this.vmNames = vmNameIdMap;
		this.flowNames = flowNameIdMap;

		String result_file = Configuration.RESULT_FOLDER + getResultFileName(this.file);
		resultWriter = new WorkloadResultWriter(result_file);
		openFile();
	}

	public void forceStartTime(double forcedStartTime) {
		this.forcedStartTime = forcedStartTime;
	}

	public void forceFinishTime(double forcedFinishTime) {
		this.forcedFinishTime = forcedFinishTime;
	}

	public static String getResultFileName(String fileName) {
		String result_file = null;

		// fileName += (StartAvailability.failOverDebug ? "" : "Availability");
		// fileName += (StartAvailability.queueDebug ? "" : "Queue");

		// fileName = (StartAvailability.failOverDebug ? "" : "Availability") +
		// fileName;
		// fileName = (StartAvailability.queueDebug ? "" : "Queue") + fileName;

		int indexSlash = fileName.lastIndexOf("/");
		if (indexSlash != -1) {
			String path_folder = fileName.substring(0, indexSlash + 1);
			String path_file = fileName.substring(indexSlash + 1);
			// result_file = path_folder + "result_" + path_file;

			String now = String.valueOf(new Date().getTime());
			// Long date = new Long
			result_file = path_folder + now.substring(now.length() / 2) + "result_";
			// result_file += (StartAvailability.failOverDebug ? "" : "Availability");
			// result_file += (StartAvailability.queueDebug ? "" : "Queue");

			result_file += (Configuration.DISABLE_FAILURE_RECOVERY ? "" : "FailureRecovery");
			result_file += (Configuration.DISABLE_MEMORY_QUEUE ? "" : "Queue");

			result_file += path_file;

		} else {
			result_file = "result_" + fileName;
		}
		return result_file;
	}

	public void parseNextWorkloads() {
		this.parsedWorkloads = new ArrayList<Workload>();
		parseNextBatch(NUM_PARSE_EACHTIME);
	}

	public List<Workload> getParsedWorkloads() {
		return this.parsedWorkloads;
	}

	public WorkloadResultWriter getResultWriter() {
		return resultWriter;
	}

	private int getVmId(String vmName) {
		Integer vmId = this.vmNames.get(vmName);
		if (vmId == null) {
			System.err.println("Cannot find VM name:" + vmName);
			return -1;
		}
		return vmId;
	}

	private Cloudlet generateCloudlet(long cloudletId, int vmId, int length) {
		int peNum = 1;
		long fileSize = 300;
		long outputSize = 300;
		Cloudlet cloudlet = new Cloudlet((int) cloudletId, length, peNum, fileSize, outputSize, utilizationModel,
				utilizationModel, utilizationModel);
		cloudlet.setUserId(userId);
		cloudlet.setVmId(vmId);

		return cloudlet;
	}

	// Cloud_Len -> /FlowId/ -> ToVmId -> PktSize
	// Iterative calling this function
	private Request parseRequest(int fromVmId, Queue<String> lineitems, List<Integer> physicalVMs) {
		if (lineitems.size() <= 0) {
			System.err.println("No REQUEST! ERROR");
			return null;
		}

		Request req = new Request(userId);
		// if (lineitems.size() > 3) {

		String logicalSFName = lineitems.poll();

		long cloudletLen = Long.parseLong(lineitems.poll());
		cloudletLen *= Configuration.CPU_SIZE_MULTIPLY;

		Cloudlet cl = generateCloudlet(req.getRequestId(), fromVmId, (int) cloudletLen);
		// this.parsedCloudlets.add(cl);

		Processing proc = new Processing(cl);
		req.addActivity(proc);

		// in this version,there is no flowId,set to default value;
		Integer flowId = Configuration.DEFAULT_FLOW_ID;

		long pktSize = Long.parseLong(lineitems.poll());
		pktSize *= Configuration.NETWORK_PACKET_SIZE_MULTIPLY;
		if (pktSize < 0)
			pktSize = 0;

		Request nextReq = null;
		int toVmId;
		if (physicalVMs.size() > 0) {
			int nextPhysicalVmId = physicalVMs.remove(0);
			toVmId = nextPhysicalVmId;
			nextReq = parseRequest(toVmId, lineitems, physicalVMs);
		} else // the next request is the last transmission to the egress node.
		{
			toVmId = this.getVmId(lineitems.poll());
			System.out.println(
					"next request is the final transmission, currently the lineitems size is :" + lineitems.size());
			nextReq = new Request(userId);
		}
		Transmission trans = new Transmission(fromVmId, toVmId, pktSize, flowId, nextReq);
		req.addActivity(trans);

		return req;
	}

	private void openFile() {
		try {
			bufReader = new BufferedReader(new FileReader(Configuration.workingDirectory + file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			@SuppressWarnings("unused")
			String head = bufReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseNextBatch(int numRequests) {
		String line;

		try {
			while (((line = bufReader.readLine()) != null) && (parsedWorkloads.size() < numRequests)) {
				// System.out.println("parsing:"+line);
				Workload wl = new Workload(workloadNum++, this.resultWriter);

				String[] splitLine = line.split(",");
				Queue<String> lineitems = new LinkedList<String>(Arrays.asList(splitLine));

				wl.time = Double.parseDouble(lineitems.poll());
				// String targetChain = "";
				// For debug only
				if (wl.time < this.forcedStartTime || wl.time > this.forcedFinishTime) // Skip Workloads before the
																						// set
																						// start time
					continue;

				wl.sfcName = lineitems.poll(); // Jason: Logical sfc name

				String ingress = lineitems.poll();
				wl.submitVmId = getVmId(ingress);

				// wl.submitPktSize = Integer.parseInt(lineitems.poll());
				wl.submitPktSize = Integer.parseInt(lineitems.poll());

				List<Integer> physicalVmList = DeploymentFileParser.getLogicalSFC2PhisicalSFC().get(wl.sfcName, ingress)
						.getServiceFunctionChain();
				// System.out.println(physicalVmList.size());
				// Queue<Integer>

				List<Integer> physicalVmListCopy = new LinkedList<>(physicalVmList);

				wl.request = parseHeadRequest(wl.submitVmId, lineitems, physicalVmListCopy);

				parsedWorkloads.add(wl);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// bufReader.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public Request parseHeadRequest(int ingressVmId, Queue<String> lineitems, List<Integer> physicalVMs) {
		// return new Request(1);
		if (lineitems.size() <= 0) {
			System.err.println("No REQUEST! ERROR");
			return null;
		}

		Request req = new Request(userId);
		;

		long headPktSize = Long.parseLong(lineitems.poll());
		headPktSize *= Configuration.NETWORK_PACKET_SIZE_MULTIPLY;
		if (headPktSize < 0)
			headPktSize = 0;

		// String firstLogicalSFName = lineitems.poll();
		// int virtualVmId = getVmId(virtualVmName);
		int firstPhysicalSFId = physicalVMs.remove(0);

		// in this version,there is no flowId,set to default value;
		int flowId = Configuration.DEFAULT_FLOW_ID;

		Request nextReq = parseRequest(firstPhysicalSFId, lineitems, physicalVMs);

		Transmission trans = new Transmission(ingressVmId, firstPhysicalSFId, headPktSize, flowId, nextReq);
		req.addActivity(trans);

		return req;

	}

	/*
	 * private String getOutputFilename(String filename) { String ext =
	 * LogWriter.getExtension(this.file); String dir = LogWriter.getPath(this.file);
	 * String name = "result_"+LogWriter.getBaseName(this.file);
	 * System.err.println(dir+"/"+name+"."+ext); return dir+"/"+name+"."+ext; }
	 */

	public int getWorkloadNum() {
		return workloadNum;
	}

	public int getGroupId() {
		String first_word = this.file.split("_")[0];
		int groupId = 0;
		try {
			groupId = Integer.parseInt(first_word);
		} catch (NumberFormatException e) {
			// Do nothing
		}
		return groupId;
	}
}
