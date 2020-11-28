package org.cloudbus.cloudsim.sdn.virtualcomponents;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.naming.directory.InvalidAttributeValueException;

import com.google.common.collect.Queues;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * Jason: Todo!! maybe we could implement a sorted list to support the
 * CloudSimSDN original design.
 */
public class MemQueue {
    private final SortedSet<EncapedResCloudlet> cacheQueue = new TreeSet<EncapedResCloudlet>();

    /**
     * A incremental number used for {@link EncapedResCloudlet#serial} event
     * attribute.
     */
    private long serial = 0;
    /**
     * Jason: Unit: KiloByte = MegaByte * 1000
     */
    private long queueRemainingSize; // KB
    private long queueTotalSize;
    private int multiplerUnit = 1000;
    // private QueuedVM owner = null;

    // public TheQueue(QueuedVM owner, long size MB) {
    public MemQueue(long size) {
        // this.owner = owner; // Jason : I forget why it need to have an owner!!!
        this.queueTotalSize = size * multiplerUnit;
        this.queueRemainingSize = this.queueTotalSize;

    }

    /**
     * Adds a new event to the queue. Adding a new event to the queue preserves the
     * temporal order of the events in the queue.
     * 
     * @param newEvent The event to be put in the queue.
     */
    public boolean addCloudlet(Cloudlet newCloudlet) {
        if (newCloudlet.getCloudletLength() > queueRemainingSize) {
            Log.print("Not enough queue size!!! Queue in: " + this);
            return false;
        }
        EncapedResCloudlet enCloudlet = new EncapedResCloudlet(newCloudlet, CloudSim.clock(), serial++);
        // newCloudlet.setSerial(serial++);
        cacheQueue.add(enCloudlet);
        queueRemainingSize -= newCloudlet.getCloudletLength();
        return true;
    }

    /**
     * Adds a new event to the head of the queue.
     * 
     * @param newEvent The event to be put in the queue.
     */
    public void addCloudletFirst(Cloudlet newCloudlet) {
        EncapedResCloudlet encloudlet = new EncapedResCloudlet(newCloudlet, CloudSim.clock(), 0);
        // newEvent.setSerial(0);
        // cacheQueue.add(clInQueue);
        cacheQueue.add(encloudlet);
    }

    /**
     * 
     * @return
     */
    public Cloudlet consumeCloudlet() {
        EncapedResCloudlet enCloudlet = cacheQueue.first();
        cacheQueue.remove(enCloudlet);
        if (enCloudlet != null) {
            queueRemainingSize += enCloudlet.getCloudletLength();
        }
        return enCloudlet.getCloudlet();
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
     * 
     * @param candidate
     */
    public void add(EncapedResCloudlet candidate) {
        this.cacheQueue.add(candidate);
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
     * 
     * @param candidateList
     */
    public void addAll(List<EncapedResCloudlet> candidateList) {
        for (EncapedResCloudlet ercl : candidateList) {
            this.add(ercl);
        }
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
     * @param event the event
     * @return true, if successful
     */
    public boolean remove(EncapedResCloudlet encloudlet) {
        return cacheQueue.remove(encloudlet);
    }

    // public boolean remove(int index) {
    // return cacheQueue.re
    // }

    /**
     * Removes all the events from the queue.
     * 
     * @param events the events
     * @return true, if successful
     */
    public boolean removeAll(Collection<EncapedResCloudlet> encloudlets) {
        return cacheQueue.removeAll(encloudlets);
    }

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

    public long getQueueRemainingSize() {
        return queueRemainingSize;
    }

    public void setQueueRemainingSize(long queueRemainingSize) {
        this.queueRemainingSize = queueRemainingSize;
    }

    public long getQueueTotalSize() {
        return queueTotalSize;
    }

    public void setQueueTotalSize(long queueTotalSize) {
        this.queueTotalSize = queueTotalSize;
    }

    public SortedSet<EncapedResCloudlet> getCacheQueue() {
        return cacheQueue;
    }

}