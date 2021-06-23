package org.cloudbus.cloudsim.sdn.virtualcomponents;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;

public class QueuedVM extends SDNVm {

    // public TheQueue theQueue;


    /**
     * Jason: when holding a {@link TheQueue} instance
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
    // public QueuedVM(int id, int userId, double mips, int numberOfPes, int ram,
    // long bw, long size, String vmm,
    // CloudletScheduler cloudletScheduler, double avail, long queueSize) {
    // super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
    // this.queueSize = queueSize;
    // this.availability = avail;
    // // theQueue = new TheQueue(this, queueSize);
    // theQueue = new TheQueue(queueSize);
    // // TODO Auto-generated constructor stub
    // }

    /**
     * Jason: when holding a {@link TheQueue} instance
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
     * @param queueSize
     */
    // public QueuedVM(int id, int userId, double mips, int numberOfPes, int ram,
    // long bw, long size, String vmm,
    // CloudletScheduler cloudletScheduler, double startTime, double finishTime,
    // double avail, long queueSize) {
    // super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler,
    // startTime, finishTime);
    // this.queueSize = queueSize;
    // this.availability = avail;
    // // theQueue = new TheQueue(this, queueSize);
    // theQueue = new TheQueue(queueSize);
    // }

    /**
     * Jason: this version do not need a {@link TheQueue} instance
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
    public QueuedVM(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
            CloudletScheduler cloudletScheduler, double avail, long queueSize) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
        this.queueSize = queueSize;
        this.availability = avail;
        // theQueue = new TheQueue(this, queueSize);
        // TODO Auto-generated constructor stub
    }

    /**
     * Jason: this version do not need a {@link TheQueue} instance
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
     * @param avail
     * @param queueSize
     */
    public QueuedVM(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
            CloudletScheduler cloudletScheduler, double startTime, double finishTime, double avail, long queueSize) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler, startTime, finishTime);
        this.queueSize = queueSize;
        this.availability = avail;
        // theQueue = new TheQueue(this, queueSize);
    }

    public double getAvailability() {
        SDNHost sdnhost = (SDNHost) getHost();
        if (sdnhost != null)
            this.availability = sdnhost.getAvailability();
        return this.availability;
    }

    public long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }

    // public TheQueue getTheQueue() {
    // return this.theQueue;
    // }

}