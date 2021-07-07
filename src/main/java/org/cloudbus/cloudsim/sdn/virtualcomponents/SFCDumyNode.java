package org.cloudbus.cloudsim.sdn.virtualcomponents;

import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunction;

public class SFCDumyNode extends ServiceFunction {

    // Jason:
    // including Ingress and Egress nodes
    // Virtual node, no resource constraints.

    /**
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
     * @param avail
     * @param queueSize
     */

    // CloudletScheduler cloudletScheduler = null;

    // Jason: hard coding !!!!
    // static int numberOfPes = 0;
    // static double mips = 0;
    // static int ram = 0;
    // static long bw = 0;
    // static long size = 0;
    // static String vmm = "kvm";

    // static double availability = 1;
    // static long queueSize = 0;
    // Jason: hard coding end !!!

    private String nodeType = null;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    // public ServiceFunction(int id, int userId, double mips, int numberOfPes, int
    // ram, long bw, long size, String vmm,
    // CloudletScheduler cloudletScheduler, double startTime, double finishTime,
    // double avail, long queueSize)
    // throws Exception {
    // super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler,
    // startTime, finishTime, avail,
    // queueSize);

    // public SFCDumyNode(int id, int userId, CloudletScheduler cloudletScheduler,
    // double start_time, double end_time, String nodeType) throws Exception {

    public SFCDumyNode(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
            CloudletScheduler cloudletScheduler, double start_time, double end_time, double availability,
            long queueSize, String nodeType) throws Exception {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler, start_time, end_time, availability,
                queueSize);

        this.nodeType = nodeType;
        // theQueue = new TheQueue(this, queueSize);
        // TODO Auto-generated constructor stub
    }

    // @Override
    // public double updateVmProcessing(double currentTime, List<Double>
    // ) {
    // return 0.0;
    // }
}