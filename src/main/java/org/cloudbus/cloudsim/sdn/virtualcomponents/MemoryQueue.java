package org.cloudbus.cloudsim.sdn.virtualcomponents;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Queues;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

public class MemoryQueue {
    private final SortedSet<EncapedCloudlet> cacheQueue = new TreeSet<EncapedCloudlet>();

    /**
     * A incremental number used for {@link EncapedCloudlet#serial} event attribute.
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
    public MemoryQueue(long size) {
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
        EncapedCloudlet enCloudlet = new EncapedCloudlet(newCloudlet, CloudSim.clock(), serial++);
        // newCloudlet.setSerial(serial++);
        cacheQueue.add(enCloudlet);
        queueRemainingSize -= newCloudlet.getCloudletLength();
        return true;
    }

    public Cloudlet consumeCloudlet() {
        EncapedCloudlet enCloudlet = cacheQueue.first();
        cacheQueue.remove(enCloudlet);
        if (enCloudlet != null) {
            queueRemainingSize += enCloudlet.cl.getCloudletLength();
        }
        return enCloudlet.cl;
    }

    /**
     * Adds a new event to the head of the queue.
     * 
     * @param newEvent The event to be put in the queue.
     */
    public void addCloudletFirst(Cloudlet newCloudlet) {
        EncapedCloudlet encloudlet = new EncapedCloudlet(newCloudlet, CloudSim.clock(), 0);
        // newEvent.setSerial(0);
        // cacheQueue.add(clInQueue);
        cacheQueue.add(encloudlet);
    }

    /**
     * Returns an iterator to the queue.
     * 
     * @return the iterator
     */
    public Iterator<EncapedCloudlet> iterator() {
        return cacheQueue.iterator();
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
    public boolean remove(EncapedCloudlet encloudlet) {
        return cacheQueue.remove(encloudlet);
    }

    /**
     * Removes all the events from the queue.
     * 
     * @param events the events
     * @return true, if successful
     */
    public boolean removeAll(Collection<EncapedCloudlet> encloudlets) {
        return cacheQueue.removeAll(encloudlets);
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

}