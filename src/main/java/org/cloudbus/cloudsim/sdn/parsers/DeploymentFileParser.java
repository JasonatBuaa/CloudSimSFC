/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2017, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn.parsers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.sdn.CloudletSchedulerSpaceSharedMonitor;
import org.cloudbus.cloudsim.sdn.CloudletSchedulerSpaceSharedQueueAwareMonitor;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunction;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunctionChainPolicy;
import org.cloudbus.cloudsim.sdn.virtualcomponents.FlowConfig;
import org.cloudbus.cloudsim.sdn.virtualcomponents.QueuedVM;
import org.cloudbus.cloudsim.sdn.virtualcomponents.SDNVm;
import org.cloudbus.cloudsim.sdn.virtualcomponents.SFCDumyNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.jupiter.params.aggregator.ArgumentAccessException;
import org.junit.rules.Timeout;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

/**
 * This class parses Virtual Topology (VMs, Network flows between VMs, and
 * SFCs). It loads Virtual Topology JSON file and creates relevant objects in
 * the simulation.
 * 
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */
public class DeploymentFileParser {

	private Multimap<String, SDNVm> vmList; // the SDNVm has been updated into the class of QueuedVM
	private List<ServiceFunction> sfList = new
	// private List<>
	LinkedList<ServiceFunction>(); // SFs are added in both VM list and SF
									// list
	private List<FlowConfig> arcList = new LinkedList<FlowConfig>();
	private List<ServiceFunctionChainPolicy> policyList = new LinkedList<ServiceFunctionChainPolicy>();

	private String vmsFileName;
	private int userId;

	private String defaultDatacenter;

	private static Table<String, String, ServiceFunctionChainPolicy> logicalSFC2PhisicalSFC = HashBasedTable.create();

	public DeploymentFileParser(String datacenterName, String topologyFileName, int userId) {
		vmList = HashMultimap.create();
		this.vmsFileName = topologyFileName;
		this.userId = userId;
		this.defaultDatacenter = datacenterName;

		parse();
	}

	private void parseDummyNode(JSONObject node, Hashtable<String, Integer> vmNameIdTable) {
		String nodeType = (String) node.get("type");
		String nodeName = (String) node.get("name");

		double starttime = 0;
		double endtime = Double.POSITIVE_INFINITY;
		if (node.get("starttime") != null)
			starttime = (Double) node.get("starttime");
		if (node.get("endtime") != null)
			endtime = (Double) node.get("endtime");

		String dcName = this.defaultDatacenter;
		if (node.get("datacenter") != null)
			dcName = (String) node.get("datacenter");

		// Optional datacenter specifies the alternative data center if 'data center'
		// has no more resource.
		ArrayList<String> optionalDatacenter = null;
		if (node.get("subdatacenters") != null) {
			optionalDatacenter = new ArrayList<>();
			JSONArray subDCs = (JSONArray) node.get("subdatacenters");

			for (int i = 0; i < subDCs.size(); i++) {
				String subdc = subDCs.get(i).toString();
				optionalDatacenter.add(subdc);
			}
		}

		String hostName = "";
		if (node.get("host") != null)
			hostName = (String) node.get("host");
		int vmId = QueuedVM.getUniqueVmId();

		vmNameIdTable.put(nodeName, vmId);
	}

	private void parse() {

		try {
			JSONObject doc = (JSONObject) JSONValue.parse(new FileReader(vmsFileName));

			Hashtable<String, Integer> vmNameIdTable = parseVMs(doc);
			Hashtable<String, Integer> flowNameIdTable = parseLinks(doc, vmNameIdTable);
			parseSFCPolicies(doc, vmNameIdTable, flowNameIdTable);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Hashtable<String, Integer> parseVMs(JSONObject doc) {
		Hashtable<String, Integer> vmNameIdTable = new Hashtable<String, Integer>();

		// Parse VM nodes
		JSONArray nodes = (JSONArray) doc.get("nodes");

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iter = nodes.iterator();
		while (iter.hasNext()) {
			JSONObject node = iter.next();

			String nodeType = (String) node.get("type");
			String nodeName = (String) node.get("name");

			// if (nodeType.equalsIgnoreCase("Ingress") ||
			// nodeType.equalsIgnoreCase("Egress")) {
			// parseDummyNode(node, vmNameIdTable);
			// continue;
			// }

			int pes = new BigDecimal((Long) node.get("pes")).intValueExact();
			long mips = (Long) node.get("mips");
			// int ram = new BigDecimal((Long) node.get("ram")).intValueExact();
			int ram = 0;
			long size = (Long) node.get("size");
			long bw = 0;

			if (node.get("bw") != null)
				bw = (Long) node.get("bw");

			int queueSize = 0;
			if (node.get("queuesize") != null)
				queueSize = new BigDecimal((Long) node.get("queuesize")).intValueExact();

			double starttime = 0;
			double endtime = Double.POSITIVE_INFINITY;

			if (node.get("starttime") != null)
				starttime = (Double) node.get("starttime");
			if (node.get("endtime") != null)
				endtime = (Double) node.get("endtime");

			String dcName = this.defaultDatacenter;
			if (node.get("datacenter") != null)
				dcName = (String) node.get("datacenter");

			// Optional datacenter specifies the alternative data center if 'data center'
			// has no more resource.
			ArrayList<String> optionalDatacenter = null;
			if (node.get("subdatacenters") != null) {
				optionalDatacenter = new ArrayList<>();
				JSONArray subDCs = (JSONArray) node.get("subdatacenters");

				for (int i = 0; i < subDCs.size(); i++) {
					String subdc = subDCs.get(i).toString();
					optionalDatacenter.add(subdc);
				}
			}

			String hostName = "";
			if (node.get("host") != null)
				hostName = (String) node.get("host");

			long nums = 1;
			if (node.get("nums") != null)
				nums = (Long) node.get("nums");

			for (int n = 0; n < nums; n++) {
				String nodeName2 = nodeName;
				if (nums > 1) {
					// Nodename should be numbered.
					nodeName2 = nodeName + n;
				}

				// Jason: this one is hard coding presently.
				// I can not figure out a more deligated way to accomplish the queue.
				CloudletScheduler clSch;
				// if (!StartAvailability.queueDebug)
				if (!Configuration.DISABLE_MEMORY_QUEUE) // enable memory queue
					// CloudletScheduler clSch = new
					// CloudletSchedulerSpaceSharedQueueAwareMonitor(queueSize,
					// Configuration.TIME_OUT);
					clSch = new CloudletSchedulerSpaceSharedQueueAwareMonitor(queueSize, Configuration.TIME_OUT);
				else
					clSch = new CloudletSchedulerSpaceSharedMonitor(Configuration.TIME_OUT);
				// CloudletScheduler clSch = new CloudletSchedulerTimeSharedMonitor(mips);

				// int vmId = SDNVm.getUniqueVmId();
				int vmId = QueuedVM.getUniqueVmId();
				double initialAvail = 0.0d;

				QueuedVM newVM = null;

				if (nodeType.equalsIgnoreCase("Ingress") || nodeType.equalsIgnoreCase("Egress")) {
					SFCDumyNode dumyNode;
					try {
						// dumyNode = new SFCDumyNode(vmId, 0, null, starttime, endtime, nodeType);
						dumyNode = new SFCDumyNode(vmId, userId, mips, pes, ram, bw, size, "VMM", clSch, starttime,
								endtime, initialAvail, queueSize, nodeType);

						// sf = new ServiceFunction(vmId, userId, mips, pes, ram, bw, size, "VMM",
						// clSch, starttime,
						// endtime, initialAvail, queueSize);

						// long mipOperation = (Long) node.get("mipoper");
						// long miperUnitWorkload = (Long) node.get("impperunitworkload");

						dumyNode.setName(nodeName2);
						dumyNode.setHostName(hostName);
						dumyNode.setOptionalDatacenters(optionalDatacenter);
						// sf.setMIperOperation(mipOperation);

						vmList.put(dcName, dumyNode);
						sfList.add(dumyNode);

						newVM = dumyNode;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (nodeType.equalsIgnoreCase("vm")) {
					// Create VM objects
					// Jason: Todo! update the vm into queuedVM
					// SDNVm vm = new SDNVm(vmId, userId, mips, pes, ram, bw, size, "VMM", clSch,
					// starttime, endtime);

					QueuedVM vm = new QueuedVM(vmId, userId, mips, pes, ram, bw, size, "VMM", clSch, starttime, endtime,
							initialAvail, queueSize);
					vm.setName(nodeName2);
					vm.setHostName(hostName);
					vm.setOptionalDatacenters(optionalDatacenter);
					vmList.put(dcName, vm);
					newVM = vm;

					throw new ArgumentAccessException("this branch is invalid");

					// Jason: Check: 1. here use the new
					// CloudletSchedulerSpaceSharedQueueAwareMonitor as the new scheduler.

				} else {
					// Create ServiceFunction objects
					ServiceFunction sf;
					try {
						sf = new ServiceFunction(vmId, userId, mips, pes, ram, bw, size, "VMM", clSch, starttime,
								endtime, initialAvail, queueSize);

						// long mipOperation = (Long) node.get("mipoper");
						// long miperUnitWorkload = (Long) node.get("impperunitworkload");

						sf.setName(nodeName2);
						sf.setHostName(hostName);
						sf.setOptionalDatacenters(optionalDatacenter);
						// sf.setMIperOperation(mipOperation);
						// sf.setMIperUnitWorkload(miperUnitWorkload);

						sf.setMiddleboxType(nodeType);
						vmList.put(dcName, sf);
						sfList.add(sf);

						newVM = sf;

						// Jason: Check: 2. here use the new
						// CloudletSchedulerSpaceSharedQueueAwareMonitor as the new scheduler.

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Jason: ???
				if (clSch instanceof CloudletSchedulerSpaceSharedQueueAwareMonitor) {
					((CloudletSchedulerSpaceSharedQueueAwareMonitor) clSch).setQueuedVM(newVM);
				}

				vmNameIdTable.put(nodeName2, vmId);
			}
		}

		return vmNameIdTable;
	}

	private Hashtable<String, Integer> parseLinks(JSONObject doc, Hashtable<String, Integer> vmNameIdTable) {
		Hashtable<String, Integer> flowNameIdTable = new Hashtable<String, Integer>();

		// Parse VM-VM links
		JSONArray links = (JSONArray) doc.get("links");

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> linksIter = links.iterator();
		while (linksIter.hasNext()) {
			JSONObject link = linksIter.next();
			String name = (String) link.get("name");
			String src = (String) link.get("source");
			String dst = (String) link.get("destination");

			Object reqLat = link.get("latency");
			Object reqBw = link.get("bandwidth");

			double lat = 0.0;
			long bw = 0;

			if (reqLat != null)
				lat = (Double) reqLat;
			if (reqBw != null)
				bw = (Long) reqBw;

			if (bw == 0) {
				System.out.println("Error!");
			}
			int srcId = vmNameIdTable.get(src);
			int dstId = vmNameIdTable.get(dst);

			int flowId = -1;

			if (name == null || "default".equalsIgnoreCase(name)) {
				// default flow.
				flowId = -1;
			} else {
				// flowId = flowNumbers++;
				flowId = 0;
			}

			FlowConfig arc = new FlowConfig(srcId, dstId, flowId, bw, lat);
			if (flowId != -1) {
				arc.setName(name);
			}

			arcList.add(arc);
			flowNameIdTable.put(name, flowId);
		}
		return flowNameIdTable;
	}

	private void parseSFCPolicies(JSONObject doc, Hashtable<String, Integer> vmNameIdTable,
			Hashtable<String, Integer> flowNameIdTable) {
		// Parse SFC policies
		JSONArray policies = (JSONArray) doc.get("policies");

		if (policies == null)
			return;

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> policyIter = policies.iterator();
		while (policyIter.hasNext()) {
			JSONObject policy = policyIter.next();
			String name = (String) policy.get("name");
			String src = (String) policy.get("source");
			String dst = (String) policy.get("destination");
			String logicalSFC = (String) policy.get("sfc_Demand");
			String flowname = (String) policy.get("flowname");
			Long start_time = (Long) policy.get("starttime");
			Long expected_duration = (Long) policy.get("expectedduration");
			if (expected_duration == null) {
				expected_duration = Long.MAX_VALUE;
			}

			int srcId = vmNameIdTable.get(src);
			int dstId = vmNameIdTable.get(dst);
			int flowId = flowNameIdTable.get(flowname);

			JSONArray sfc = (JSONArray) policy.get("sfc");
			ArrayList<Integer> sfcList = new ArrayList<Integer>();
			for (int i = 0; i < sfc.size(); i++) {
				String sfName = sfc.get(i).toString();
				int sfVmId = vmNameIdTable.get(sfName);
				sfcList.add(sfVmId);
			}

			ServiceFunctionChainPolicy pol = new ServiceFunctionChainPolicy(srcId, dstId, flowId, sfcList,
					expected_duration);
			if (name != null)
				pol.setName(name);

			policyList.add(pol);
			logicalSFC2PhisicalSFC.put(logicalSFC, src, pol);
		}
		for (String str : logicalSFC2PhisicalSFC.columnKeySet()) {
			Map<String, ServiceFunctionChainPolicy> aColumn = logicalSFC2PhisicalSFC.column(str);
			for (String str2 : aColumn.keySet()) {
				ServiceFunctionChainPolicy sfcPolicy = logicalSFC2PhisicalSFC.get(str2, str);
				System.out.println(str + "," + str2 + "," + sfcPolicy);

			}
		}
		System.out.println("test");
	}

	public Collection<SDNVm> getVmList(String dcName) {
		return vmList.get(dcName);
	}

	public List<FlowConfig> getArcList() {
		return arcList;
	}

	public List<ServiceFunction> getSFList() {
		return sfList;
	}

	public List<ServiceFunctionChainPolicy> getSFCPolicyList() {
		return policyList;
	}

	public static Table<String, String, ServiceFunctionChainPolicy> getLogicalSFC2PhisicalSFC() {
		return logicalSFC2PhisicalSFC;
	}

	// public void setLogicalSFC2PhisicalSFC(Table<String, String,
	// ServiceFunctionChainPolicy> logicalSFC2PhisicalSFC) {
	// this.logicalSFC2PhisicalSFC = logicalSFC2PhisicalSFC;
	// }
}
