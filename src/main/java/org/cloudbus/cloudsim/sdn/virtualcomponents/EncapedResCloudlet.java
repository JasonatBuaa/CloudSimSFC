package org.cloudbus.cloudsim.sdn.virtualcomponents;

import com.google.common.collect.Ordering;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.ResCloudlet;

// public class CloudletInQueue implements Comparable<CloudletInQueue> {

/**
 * Jason: This class holds a cloudlet object, and could sort the cloudlet based
 * on priority, arrivaltime and serial number
 */
public class EncapedResCloudlet extends ResCloudlet implements Comparable<EncapedResCloudlet> {
    public double aTime;
    public int priority;

    private long serial = 0;

    /**
     * Jason: This version need a priority value
     * 
     * @param cloudlet
     * @param arrivalTime
     * @param preSetPriority
     */
    public EncapedResCloudlet(Cloudlet cloudlet, double arrivalTime, long ordering, int preSetPriority) {
        super(cloudlet);
        this.aTime = arrivalTime;
        this.serial = ordering;
        this.priority = preSetPriority;
    }

    /**
     * Jason: This is the non-priority version
     * 
     * @param cloudlet
     * @param arrivalTime
     */
    public EncapedResCloudlet(Cloudlet cloudlet, double arrivalTime, long ordering) {
        super(cloudlet);
        this.aTime = arrivalTime;
        this.serial = ordering;
        this.priority = 0; // priority: the higher, the more prioritized.
    }

    // @Override
    // public Cloudlet getCloudlet() {
    // return super.getCloudlet();
    // }

    @Override
    public int compareTo(EncapedResCloudlet clInQueue) {
        if (clInQueue == null) {
            return 1;
        } else if (priority > clInQueue.priority) { // Jason: this is because that the higher priority comes with a
                                                    // lower priority value
            return -1;
        } else if (priority < clInQueue.priority) {
            return 1;
        } else if (aTime < clInQueue.aTime) {
            return -1;
        } else if (aTime > clInQueue.aTime) {
            return 1;
        } else if (serial < clInQueue.serial) {
            return -1;
        } else if (serial > clInQueue.serial) {
            return 1;
        } else if (this == clInQueue) {
            return 0;
        }
        // TODO Auto-generated method stub
        return 1;
    }
}

// public int compareTo(SimEvent event) {
// if (event == null) {
// return 1;
// } else if (time < event.time) {
// return -1;
// } else if (time > event.time) {
// return 1;
// } else if (serial < event.serial) {
// return -1;
// } else if (this == event) {
// return 0;
// } else {
// return 1;
// }
// }