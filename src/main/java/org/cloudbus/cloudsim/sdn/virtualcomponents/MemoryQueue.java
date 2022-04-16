package org.cloudbus.cloudsim.sdn.virtualcomponents;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

// import javax.security.auth.login.Configuration;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
// import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.SDNBroker;

/**
 * Jason: Todo!! maybe we could implement a sorted list to support the
 * CloudSimSDN original design.
 */

public class MemoryQueue {
    private final SortedSet<EncapedResCloudlet> cacheQueue = new TreeSet<EncapedResCloudlet>();

    public static boolean ENQUEUE_SUCCESS = true;
    public static boolean ENQUEUE_FAILURE = false;
    /**
     * A incremental number used for {@link EncapedResCloudlet#serial} event
     * attribute.
     */
    private long serial = 0;
    /**
     * Jason: Unit: KiloByte = MegaByte * 1000
     */
    private long queueRemainingSpace; // KB
    private long queueTotalSpace;
    private int multiplerUnit = Configuration.QUEUE_SPACE_MULTIPLY;

    private QueuedVM vmInfotoDebug = null;
    // private QueuedVM owner = null;

    // public TheQueue(QueuedVM owner, long size MB) {
    public MemoryQueue(long size) {
        // this.owner = owner; // Jason : I forget why it need to have an owner!!!
        this.queueTotalSpace = size * multiplerUnit;
//        this.queueTotalSpace = size;
        this.queueRemainingSpace = this.queueTotalSpace;

    }

    /**
     * Adds a new event to the queue. Adding a new event to the queue preserves the
     * temporal order of the events in the queue.
     *
     * @param newCloudlet The event to be put in the queue.
     */
    // public boolean addCloudlet(Cloudlet newCloudlet) {
    // if (newCloudlet.getCloudletLength() > queueRemainingSpace) {
    // Log.print("Not enough queue size!!! Queue in: " + this);
    // return false;
    // }
    // EncapedResCloudlet enCloudlet = new EncapedResCloudlet(newCloudlet,
    // CloudSim.clock(), serial++);
    // // newCloudlet.setSerial(serial++);
    // cacheQueue.add(enCloudlet);
    // queueRemainingSpace -= newCloudlet.getCloudletLength();
    // return true;
    // }
    public EncapedResCloudlet addCloudlet(Cloudlet newCloudlet) {
        EncapedResCloudlet ercl = new EncapedResCloudlet(newCloudlet, CloudSim.clock(), serial++);
        // newCloudlet.setSerial(serial++);
        // cacheQueue.add(enCloudlet);
        // queueRemainingSpace -= newCloudlet.getCloudletLength();
        if (_addEncapedResCloudlet(ercl) == ENQUEUE_SUCCESS) {
            return ercl;
        }
        // return enCloudlet;
        return null;
    }

    /**
     * !!!Jason: dealing with the queue size calculation fault!! Queue size should equal to the received packet size, not the Cloudlet length!!!
     *
     * @param ercl
     * @return
     */
    private boolean _addEncapedResCloudlet(EncapedResCloudlet ercl) {
        long requiredQueueSize = ercl.getCloudlet().getQueueRequired();
//        if (ercl.getCloudletLength() > queueRemainingSpace) {
        if (requiredQueueSize > getQueueRemainingSpace()) {
//            Log.print("111 +++ Not enough queue size!!!" + this + currentTime() + requiredQueueSize + " needed" + "but only has " + getQueueRemainingSpace()
//                    + (this.vmInfotoDebug == null ? "" : ": " + this.vmInfotoDebug));
//            System.out.println("111 +++ Not enough queue size!!!" + currentTime()
//                    + (this.vmInfotoDebug == null ? "" : ": " + this.vmInfotoDebug));
//            System.out.println(requiredQueueSize + " needed, but only has " + getQueueRemainingSpace());
            SDNBroker.dropedCloudletsDueToQueueFull++;

            return ENQUEUE_FAILURE;
        }
        if (cacheQueue.add(ercl)) {
//            queueRemainingSpace -= ercl.getCloudletLength();
//            queueRemainingSpace -= requiredQueueSize;
            setQueueRemainingSpace(getQueueRemainingSpace() - requiredQueueSize);
            return ENQUEUE_SUCCESS;
        } else {
            System.out.println("ERROR!!!!! when adding cloudlet to memoryqueue!!!");
            return ENQUEUE_FAILURE;
        }
    }

    /**
     * Adds a new event to the head of the queue. It's not necessarily the head,
     * because we use the priority value to control the position of the inputed
     * cloudlet.
     *
     * @param newCloudlet The event to be put in the queue.
     */
    public EncapedResCloudlet addCloudletFirst(Cloudlet newCloudlet) {
        EncapedResCloudlet ercl = new EncapedResCloudlet(newCloudlet, CloudSim.clock(), 0);
        // newEvent.setSerial(0);
        // cacheQueue.add(clInQueue);
        // cacheQueue.add(encloudlet);
        if (_addEncapedResCloudlet(ercl))
            return ercl;
        else
            return null;
    }

    // /**
    // *
    // * @return
    // */
    // public Cloudlet consumeCloudlet() {
    // EncapedResCloudlet enCloudlet = cacheQueue.first();
    // cacheQueue.remove(enCloudlet);
    // if (enCloudlet != null) {
    // queueRemainingSpace += enCloudlet.getCloudletLength();
    // }
    // return enCloudlet.getCloudlet();
    // }

    public EncapedResCloudlet consumeCloudlet() {
        EncapedResCloudlet ercl = cacheQueue.first();
        if (ercl != null) {
            cacheQueue.remove(ercl);
            // !!!Jason:this part calculates the queue size, need to be updated to the received packet size!
//            queueRemainingSpace += ercl.getCloudletLength();
            setQueueRemainingSpace(getQueueRemainingSpace() + ercl.getCloudlet().getQueueRequired());
        }
        return ercl;
    }

    public EncapedResCloudlet testConsumeCloudlet() {
        return cacheQueue.first();
    }

    /**
     * Returns an iterator to the queue.
     *
     * @return the iterator
     */
    public Iterator<EncapedResCloudlet> iterator() {
        return cacheQueue.iterator();
    }

    // // public <T extends ResCloudlet> void add(T candidate) {
    // public <T extends ResCloudlet> void add(EncapedResCloudlet candidate) {
    // // Class cl = candidate.getClass();
    // // String[] classname = cl.getName().split(".");
    // // System.out.println(classname[classname.length - 1]);
    // // if
    // //
    // (cl.getName().equals("org.cloudbus.cloudsim.sdn.virtualcomponents.EncapedResCloudlet"))
    // this.cacheQueue.add(candidate);
    // // else {
    // // }
    // }

    /**
     * @param ercl
     */
    public boolean add(EncapedResCloudlet ercl) {
        return this._addEncapedResCloudlet(ercl);
        // this.cacheQueue.add(candidate);
    }

    // Jason: We put a strict restriction on the type, requiring an exact
    // EncapedResCloudlet input.
    // public <T extends ResCloudlet> void addAll(List<T> candidateList) throws
    // InvalidAttributeValueException {
    // for (ResCloudlet rcl : candidateList) {
    // if (rcl instanceof ResCloudlet) {
    // } else if (rcl instanceof EncapedResCloudlet) {
    // } else {
    // throw new InvalidAttributeValueException();
    // }
    // }
    // }

    /**
     * @param candidateList
     */
    public List<EncapedResCloudlet> addAll(List<EncapedResCloudlet> candidateList) {
        List<EncapedResCloudlet> failedToAddList = new ArrayList<>();
        for (EncapedResCloudlet ercl : candidateList) {
            // this.add(ercl);
            if (this._addEncapedResCloudlet(ercl))
                continue;
            else
                failedToAddList.add(ercl);
        }
        return failedToAddList;
    }

    // /**
    // * Returns the size of this event queue.
    // *
    // * @return the size
    // */
    // public int size() {
    // return cacheQueue.size();
    // }

    /**
     * Removes the EncapsulatedCloudlet from the queue.
     *
     * @param encloudlet the cloudlet to be encapsulated
     * @return true, if successful
     */
    public boolean remove(EncapedResCloudlet encloudlet) {
        long length = 0;
        if (cacheQueue.contains(encloudlet))
            length = encloudlet.getCloudletLength();
        if (this.getQueueRemainingSpace() + length > this.getQueueTotalSpace())
            System.out.println("ERROR!!! in memoryqueue remove");
        this.setQueueRemainingSpace(this.getQueueRemainingSpace() + length);
        return cacheQueue.remove(encloudlet);
    }

    // public boolean remove(int index) {
    // return cacheQueue.re
    // }

    /**
     * Removes all the events from the queue.
     * !!!Jason: This part calculates the length of queue, which should base on the last transmitted packet size!!!
     * We have updated this logic
     *
     * @param encloudlets the encapsulated cloudlet that is to be removed
     * @return true, if successful
     */
    public boolean removeAll(Collection<EncapedResCloudlet> encloudlets) {
        long totalLength = 0;
        for (EncapedResCloudlet ercl : encloudlets) {
            if (cacheQueue.contains(ercl)) {
                totalLength += ercl.getCloudlet().getQueueRequired();
                if (ercl.getCloudlet().getQueueRequired() == 0) {
                    throw new InvalidParameterException();
                }
            }
        }
        if (this.getQueueRemainingSpace() + totalLength > this.getQueueTotalSpace()) {
            System.out.println("ERROR!!! in memoryqueue removeAll");
            throw new RuntimeException("queue error");
        }
        this.setQueueRemainingSpace(this.getQueueRemainingSpace() + totalLength);
        return cacheQueue.removeAll(encloudlets);
    }


//    public boolean removeAll(Collection<EncapedResCloudlet> encloudlets) {
//        long totalLength = 0;
//        for (EncapedResCloudlet ercl : encloudlets) {
//            if (cacheQueue.contains(ercl))
//                totalLength += ercl.getCloudletLength();
//        }
//        if (this.getQueueRemainingSpace() + totalLength > this.getQueueTotalSpace()) {
//            System.out.println("ERROR!!! in memoryqueue removeAll");
//            throw new RuntimeException("queue error");
//        }
//        this.setQueueRemainingSpace(this.getQueueRemainingSpace() + totalLength);
//        return cacheQueue.removeAll(encloudlets);
//    }

    public int size() {
        return this.cacheQueue.size();
    }

    public boolean isEmpty() {
        return this.cacheQueue.size() == 0 ? true : false;
    }

    /**
     * Clears the queue.
     */
    public void clear() {
        cacheQueue.clear();
    }

    public long getQueueRemainingSpace() {
        return queueRemainingSpace;
    }

    public void setQueueRemainingSpace(long queueRemainingSpace) {
        this.queueRemainingSpace = queueRemainingSpace;
        if (this.queueRemainingSpace > this.queueTotalSpace) {
            System.out.println("wrong argument here!!!");
            System.out.println("check the algorithm");
            throw new RuntimeException("queue error!!!");
        }
    }

    public long getQueueTotalSpace() {
        return queueTotalSpace;
    }

    public void setQueueTotalSpace(long queueTotalSpace) {
        this.queueTotalSpace = queueTotalSpace;
    }

    public SortedSet<EncapedResCloudlet> getCacheQueue() {
        return cacheQueue;
    }

    public QueuedVM getVmInfotoDebug() {
        return vmInfotoDebug;
    }

    public void setVmInfotoDebug(QueuedVM vmInfotoDebug) {
        this.vmInfotoDebug = vmInfotoDebug;
    }

    public String currentTime() {
        return "   Current time:" + CloudSim.clock() + "   ";
    }

}