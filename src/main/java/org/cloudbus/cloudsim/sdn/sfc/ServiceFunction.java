/*
 * Title:        CloudSimSDN + SFC
 * Description:  SFC extension for CloudSimSDN
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2018, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn.sfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.util.concurrent.Service;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.sdn.CloudletSchedulerMonitor;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystem;
import org.cloudbus.cloudsim.sdn.nos.PerformanceJitter;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;
import org.cloudbus.cloudsim.sdn.virtualcomponents.QueuedVM;
import org.cloudbus.cloudsim.sdn.virtualcomponents.SDNVm;

/**
 * ServiceFunction is a class to implement a VNF, which extends SDNVm including
 * ServiceFunctionType and MI/operation. When one network packet passes through
 * a ServiceFunction, it will take additional time here. Additional processing
 * time for a packet : MI per Operation / MIPS allocated for this SF (=VM)
 *
 * 
 * Todo: the additional processing time += queueing time
 * 
 * @author Jungmin Jay Son and Jason Sun
 * @since CloudSimSDN 3.0
 */

// public class ServiceFunction extends SDNVm { // Jason: a small modify here
public class ServiceFunction extends QueuedVM {

	/**
	 * Initial MIPS for this SF. We retain this information for dynamic MIPS
	 * re-allocation.
	 */
	private final double initMips;

	public static Map<Integer, ServiceFunction> sfMap = new HashMap();

	/**
	 * Initial Number of PEs for this SF. We retain this information for dynamic
	 * MIPS re-allocation.
	 */
	private final int initNumberOfPes;

	/**
	 * NOS in charge of this SF in case of multiple DC / NOS running in the
	 * simulation
	 */
	private NetworkOperatingSystem runningNOS;

	/**
	 * MI per operation. Additional process time will be calculated by mipOper /
	 * allocated MIPS
	 */
	private long mipOper = 0;

	private long miperUnitWorkload;

	// private int queueRam;

	public static ServiceFunction findSFById(int id) {
		return sfMap.get(id);
	}

	// public ServiceFunction(int id, int userId, double mips, int numberOfPes, int
	// ram, long bw, long size, String vmm,
	// CloudletScheduler cloudletScheduler, double startTime, double finishTime) {
	// super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler,
	// startTime, finishTime);
	// initMips = mips;
	// initNumberOfPes = numberOfPes;
	// }

	// public ServiceFunction(int id, int userId, double mips, int numberOfPes, int
	// ram, long bw, long size, String vmm,
	// CloudletScheduler cloudletScheduler, double startTime, double finishTime,
	// double avail, int queueSize) {
	// super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler,
	// startTime, finishTime, avail,
	// queueSize);
	// initMips = mips;
	// initNumberOfPes = numberOfPes;

	// }

	/**
	 * Jason: Added availability and queuing size
	 * 
	 * @param id
	 * @param userId
	 * @param mips
	 * @param numberOfPes
	 * @param ram
	 * @param bw
	 * @param size
	 * @param vmm
	 * @param cloudletScheduler
	 * @param startTime
	 * @param finishTime
	 * @param availability
	 * @param queueRam
	 * @throws Exception
	 */
	public ServiceFunction(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
			CloudletScheduler cloudletScheduler, double startTime, double finishTime, double avail, long queueSize)
			throws Exception {
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler, startTime, finishTime, avail,
				queueSize);
		initMips = mips;
		initNumberOfPes = numberOfPes;

		// this.availability = availability;
		// this.queueRam = queueRam;
		if (sfMap.get(id) != null) {
			throw new Exception();
		} else {
			ServiceFunction.sfMap.put(id, this);
		}
	}

	public void setMIperOperation(long mipOperation) {
		this.mipOper = mipOperation; // MI per operation.
	}

	public long getMIperOperation() {
		return this.mipOper;
	}

	public void setMIperUnitWorkload(long miperUnitWorkload) {
		this.miperUnitWorkload = miperUnitWorkload; // MI per operation.
	}

	public long getMIperUnitWorkload() {
		return this.miperUnitWorkload;
	}

	public double getInitialMips() {
		return initMips;
	}

	public int getInitialNumberOfPes() {
		return initNumberOfPes;
	}

	public void setNetworkOperatingSystem(NetworkOperatingSystem nos) {
		this.runningNOS = nos;
	}

	public NetworkOperatingSystem getNetworkOperatingSystem() {
		return this.runningNOS;
	}

	// Jason: add performance jitter here!

	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		double sumMips = 0;
		for (double mips : mipsShare)
			sumMips += mips;

		if (getCloudletScheduler() instanceof CloudletSchedulerMonitor) {
			CloudletSchedulerMonitor cls = (CloudletSchedulerMonitor) getCloudletScheduler();
			long totalGivenPrevTime = (long) (cls.getTimeSpentPreviousMonitoredTime(currentTime) * sumMips);
			long totalProcessingPrevTime = cls.getTotalProcessingPreviousTime(currentTime, mipsShare);

			// Monitoring this VM Jason: totalGivenPrevTime?? totalProcessingPrevTime??
			// Jason : total processing is the real work, total given is the max capacity.
			// The former divided by the latter gets the utilization.
			this.increaseProcessedMIs(totalProcessingPrevTime, totalGivenPrevTime);

			// Monitoring the host hosting this VM
			SDNHost sdnhost = (SDNHost) getHost();
			if (sdnhost != null)
				sdnhost.increaseProcessedMIs(totalProcessingPrevTime);
		}

		List<Double> realMIPSShareWithJitter = new ArrayList();
		for (double mips : mipsShare) {
			double jitterMIPS = perfJitter.sampleComputationPerformance(mips, getPerfJitterSigma());
			realMIPSShareWithJitter.add(jitterMIPS);
		}

		// return super.updateVmProcessing(currentTime, mipsShare);
		return super.updateVmProcessing(currentTime, realMIPSShareWithJitter);
	}

	// public int getQueueRam() {
	// return queueRam;
	// }

	// public void setQueueRam(int queueRam) {
	// this.queueRam = queueRam;
	// }
}
