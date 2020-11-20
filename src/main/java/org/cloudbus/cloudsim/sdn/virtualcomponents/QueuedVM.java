package org.cloudbus.cloudsim.sdn.virtualcomponents;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;

public class QueuedVM extends SDNVm {

    public TheQueue theQueue;
    public long queueSize;
    public double availability;

    public QueuedVM(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
            CloudletScheduler cloudletScheduler, double avail, long queueSize) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
        this.queueSize = queueSize;
        this.availability = avail;
        theQueue = new TheQueue(this, queueSize);
        // TODO Auto-generated constructor stub
    }

    public QueuedVM(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
            CloudletScheduler cloudletScheduler, double startTime, double finishTime, double avail, long queueSize) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler, startTime, finishTime);
        this.queueSize = queueSize;
        this.availability = avail;
        theQueue = new TheQueue(this, queueSize);
    }

    public double getAvailability() {
        SDNHost sdnhost = (SDNHost) getHost();
        if (sdnhost != null)
            this.availability = sdnhost.getAvailability();
        return this.availability;
    }
}