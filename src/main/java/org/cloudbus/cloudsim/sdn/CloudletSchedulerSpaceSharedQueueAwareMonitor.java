
package org.cloudbus.cloudsim.sdn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// import org.apache.commons.math3.analysis.function.Log;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.sdn.virtualcomponents.EncapedResCloudlet;
import org.cloudbus.cloudsim.sdn.virtualcomponents.MemoryQueue;
import org.cloudbus.cloudsim.sdn.virtualcomponents.QueuedVM;
// import org.cloudbus.cloudsim.sdn.virtualcomponents.TheQueue;

/**
 * CloudletSchedulerSpaceShared implements a policy of scheduling performed by a
 * virtual machine to run its {@link Cloudlet Cloudlets}. It consider there will
 * be only one cloudlet per VM. Other cloudlets will be in a waiting list. We
 * consider that file transfer from cloudlets waiting happens before cloudlet
 * execution. I.e., even though cloudlets must wait for CPU, data transfer
 * happens as soon as cloudlets are submitted.
 * 
 * @author Jason Sun
 * @since 2020/11/24
 */

public class CloudletSchedulerSpaceSharedQueueAwareMonitor extends CloudletSchedulerSpaceSharedMonitor {
    // For monitoring
    private double prevMonitoredTime = 0;
    private double timeoutLimit = Double.POSITIVE_INFINITY;

    // Jason: under develop
    private QueuedVM queuedVM = null;

    // public MemoryQueue memQueue;
    public MemoryQueue cloudletWaitingList;
    public long queueSize;

    public CloudletSchedulerSpaceSharedQueueAwareMonitor(long queueSize, double timeOut) {
        super(timeOut);
        this.timeoutLimit = timeOut; // Jason: here inputs the timeout value
        // memQueue = new MemoryQueue(queueSize);
        cloudletWaitingList = new MemoryQueue(queueSize);
    }

    public void GiveAWarningForDebug() {
        System.out.println("==============================================");
        System.out.println("warning!! this message denotes the use of an unexpected function!!!");
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }

    // ----------------------------------------------------------------------------------
    // Jason: Todo 重写这一段代码，将queue aware的特性体现出来。要点：
    // 1、首先，改写后，vm的queueSize要能被感知到；
    // 2、在处理cloudletSubmit的时候，vm的queueSize要被正确的计算（核减）
    // 3、在requestFailed或者requestCompleted之后，vm的queueSize要被正确的计算（提升以恢复原有的queueSize）
    // 4、queue如何进如何出应该可以拓展，即可将queue的进出带优先级

    // Jason：MemQueue使用步骤： 尝试直接将原有的cloudletWaitinglist改造成为使用memory queue的waitinglist
    // 1、判断空间大小，若空间够用则入队；否则放入failed list里面做后续处理，返回double.MAX_VALUE--
    // Todo：检查后续处理是否满足我的要求
    // 2、入队的负载，在继续调度的时候出队 -- 正确统计调度前的排队时间，需要绕过cloudlet原生的waiting queue？

    @Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        long clLength = cloudlet.getCloudletLength();

        // Jason: change of design:
        // Since we have combined our memoryqueue with CloudSimSDN's Waitinglist, we
        // should change the cloudlet processing procedure hereafter.

        // Jason: After changed!!
        // 1. if MemoryQueue has sufficient space, then enQueue the request; else, drop
        // the request(check else)
        // 2. if can exec, then transfer the request from MemoryQueue to execlist
        EncapedResCloudlet ercl;

        // if (!(cloudletWaitingList.getQueueRemainingSize() > clLength) ||
        // !cloudletWaitingList.addCloudlet(cloudlet)) {
        // // Jason: Todo! Check the correctness of this code! insufficient queue
        // // size, drop packet!!!
        // ResCloudlet failedercl = new EncapedResCloudlet(cloudlet, CloudSim.clock());
        // failedercl.setCloudletStatus(Cloudlet.FAILED);
        // getCloudletFailedList().add(failedercl);
        // return Double.MAX_VALUE; // Jason: Use the double max value to denote a
        // // cloudlet submit failure.
        // }

        // it can go to the exec list

        // EncapedResCloudlet ercl = new EncapedResCloudlet(cloudlet, CloudSim.clock(),
        // 0);
        ercl = getCloudletWaitingList(true).addCloudlet(cloudlet);
        if (ercl == null) {
            System.out.println("Jason: warning!!! the memory queue" + this + " in" + this.queuedVM + " is full");
            dropCloudletRequest(cloudlet);
            return Double.MAX_VALUE; // Jason: Use the double max value to denote a queue-full event
        }
        // ResCloudlet rcl = new ResCloudlet(cloudlet);
        // rcl.setCloudletStatus(Cloudlet.QUEUED);
        ercl.setCloudletStatus(Cloudlet.QUEUED);
        // getCloudletWaitingList().add(rcl); // Jason: encapsulate this function to
        // support memory queue features.

        if ((currentCpus - usedPes) >= this.getCloudletWaitingList(true).testConsumeCloudlet().getNumberOfPes()) {
            List<EncapedResCloudlet> toRemoveinWaitingList = new ArrayList<EncapedResCloudlet>();
            for (int i = 0; !getCloudletWaitingList(true).isEmpty()
                    && (currentCpus - usedPes) >= getCloudletWaitingList(true).testConsumeCloudlet()
                            .getNumberOfPes();) {
                ercl = getCloudletWaitingList(true).consumeCloudlet();
                if (ercl == null)
                    System.out.println("Fatel error!!!!!!!!! need debug");
                ercl.setCloudletStatus(Cloudlet.INEXEC);
                for (int k = 0; k < ercl.getNumberOfPes(); k++) {
                    ercl.setMachineAndPeId(0, i);
                }
                getCloudletExecList().add(ercl);
                usedPes += ercl.getNumberOfPes();
                toRemoveinWaitingList.add(ercl);
            }
            getCloudletWaitingList(true).removeAll(toRemoveinWaitingList);

        } else
            // no enough free PEs: remain in the waiting queue
            return 0.0;

        // calculate the expected time for cloudlet completion
        double capacity = 0.0;
        int cpus = 0;
        for (Double mips :

        getCurrentMipsShare()) {
            capacity += mips;
            if (mips > 0) {
                cpus++;
            }
        }
        // LinkedList l = null;

        currentCpus = cpus;
        capacity /= cpus;

        // use the current capacity to estimate the extra amount of
        // time to file transferring. It must be added to the cloudlet length
        double extraSize = capacity * fileTransferTime;
        long length = cloudlet.getCloudletLength();
        length += extraSize;
        cloudlet.setCloudletLength(length);
        return cloudlet.getCloudletLength() / capacity;
    }

    @Override
    public int getCloudletStatus(int cloudletId) {
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus();
            }
        }

        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus();
            }
        }

        for (EncapedResCloudlet ercl : getCloudletWaitingList(true).getCacheQueue()) {
            if (ercl.getCloudletId() == cloudletId) {
                return ercl.getCloudletStatus();
            }
        }

        return -1;
    }

    @Override
    public double cloudletResume(int cloudletId) {
        GiveAWarningForDebug();
        boolean found = false;
        int position = 0;

        // look for the cloudlet in the paused list
        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            ResCloudlet rcl = getCloudletPausedList().remove(position);

            // it can go to the exec list
            if ((currentCpus - usedPes) >= rcl.getNumberOfPes()) {
                rcl.setCloudletStatus(Cloudlet.INEXEC);
                for (int i = 0; i < rcl.getNumberOfPes(); i++) {
                    rcl.setMachineAndPeId(0, i);
                }

                long size = rcl.getRemainingCloudletLength();
                size *= rcl.getNumberOfPes();
                rcl.getCloudlet().setCloudletLength(size);

                getCloudletExecList().add(rcl);
                usedPes += rcl.getNumberOfPes();

                // calculate the expected time for cloudlet completion
                double capacity = 0.0;
                int cpus = 0;
                for (Double mips : getCurrentMipsShare()) {
                    capacity += mips;
                    if (mips > 0) {
                        cpus++;
                    }
                }
                currentCpus = cpus;
                capacity /= cpus;

                long remainingLength = rcl.getRemainingCloudletLength();
                double estimatedFinishTime = CloudSim.clock() + (remainingLength / (capacity * rcl.getNumberOfPes()));

                return estimatedFinishTime;
            } else {// no enough free PEs: go to the waiting queue
                rcl.setCloudletStatus(Cloudlet.QUEUED);

                long size = rcl.getRemainingCloudletLength();
                size *= rcl.getNumberOfPes();
                rcl.getCloudlet().setCloudletLength(size);

                if (rcl instanceof EncapedResCloudlet)
                    getCloudletWaitingList(true).add((EncapedResCloudlet) rcl);
                return 0.0;
            }

        }

        // not found in the paused list: either it is in in the queue, executing or not
        // exist
        return 0.0;

    }

    // --------------------------------------------------------------

    // Jason: todo!! check this part after finishing its main functionalities.
    // This part is very important for the correctness of this simulation system.

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        double returnValue = 0.0;
        setCurrentMipsShare(mipsShare);
        double timeSpam = currentTime - getPreviousTime(); // time since last update
        double capacity = 0.0;
        int cpus = 0;

        for (Double mips : mipsShare) { // count the CPUs available to the VMM
            capacity += mips;
            if (mips > 0) {
                cpus++;
            }
        }
        currentCpus = cpus;
        capacity /= cpus; // average capacity of each cpu

        // each machine in the exec list has the same amount of cpu
        for (ResCloudlet rcl : getCloudletExecList()) {
            rcl.updateCloudletFinishedSoFar((long) (capacity * timeSpam * rcl.getNumberOfPes() * Consts.MILLION));
        }

        // no more cloudlets in this scheduler
        if (getCloudletExecList().size() == 0 && getCloudletWaitingList(true).size() == 0) {
            setPreviousTime(currentTime);
            returnValue = 0.0;
        } else {
            // update each cloudlet
            int finished = 0;
            List<ResCloudlet> toRemove = new ArrayList<ResCloudlet>();
            for (ResCloudlet rcl : getCloudletExecList()) {
                // finished anyway, rounding issue...
                if (rcl.getRemainingCloudletLength() == 0) {
                    toRemove.add(rcl);
                    cloudletFinish(rcl);
                    finished++;
                }
            }
            getCloudletExecList().removeAll(toRemove);

            // for each finished cloudlet, add a new one from the waiting list

            // Jason: updated logic:
            // Jason: In this part, we linearly search for the next Cloudlet in the
            // waitinglist. To be specific, the ordering (sequence) of the linear search is
            // given by the default ordering given by the EncapedResCloudlet in the
            // MemoryQueue
            if (!getCloudletWaitingList(true).isEmpty()) {
                EncapedResCloudlet ercl;
                List<EncapedResCloudlet> toRemoveinWaitingList = new ArrayList<EncapedResCloudlet>();
                for (int i = 0; !getCloudletWaitingList(true).isEmpty()
                        && (currentCpus - usedPes) >= getCloudletWaitingList(true).testConsumeCloudlet()
                                .getNumberOfPes();) {
                    toRemove.clear();
                    ercl = getCloudletWaitingList(true).consumeCloudlet();
                    ercl.setCloudletStatus(Cloudlet.INEXEC);
                    for (int k = 0; k < ercl.getNumberOfPes(); k++) {
                        ercl.setMachineAndPeId(0, i);
                    }
                    getCloudletExecList().add(ercl);
                    usedPes += ercl.getNumberOfPes();
                    toRemoveinWaitingList.add(ercl);
                }
                getCloudletWaitingList(true).removeAll(toRemoveinWaitingList);
            }

            // estimate finish time of cloudlets in the execution queue
            double nextEvent = Double.MAX_VALUE;
            for (ResCloudlet rcl : getCloudletExecList()) {
                double remainingLength = rcl.getRemainingCloudletLength();
                double estimatedFinishTime = currentTime + (remainingLength / (capacity * rcl.getNumberOfPes()));
                if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
                    estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
                }
                if (estimatedFinishTime < nextEvent) {
                    nextEvent = estimatedFinishTime;
                }
            }
            setPreviousTime(currentTime);

            returnValue = nextEvent;
        }

        processTimeout(currentTime);
        return returnValue;
    }

    @Override
    public List<Cloudlet> getFailedCloudlet() {
        List<Cloudlet> failed = new ArrayList<Cloudlet>();
        for (ResCloudlet cl : getCloudletFailedList()) {
            failed.add(cl.getCloudlet());
        }
        getCloudletFailedList().clear();
        return failed;
    }

    // public void dropCloudletRequest(EncapedResCloudlet encapedRcl) {
    // this.setDisabledCloudlet(encapedRcl);
    // }

    public void dropCloudletRequest(Cloudlet cloudlet) {
        ResCloudlet resCloudlet = new ResCloudlet(cloudlet);
        this.setDisabledCloudlet(resCloudlet);
        System.out.println("Warning!!! cloudlet dropped because of insufficient memory queue space!!!");
        Log.printLine("Warning!!! cloudlet dropped because of insufficient memory queue space!!!");
    }

    // Jason: Todo! check this part.
    // Jason: what is a disabled cloudlet? meaning dropped because of insufficient
    // queue ??

    public void setDisabledCloudlet(ResCloudlet resCloudlet) {
        List<ResCloudlet> rslList = getCloudletFailedList();
        rslList.add(resCloudlet);
        resCloudlet.setCloudletStatus(Cloudlet.FAILED);
        resCloudlet.finalizeCloudlet();
    }

    // public void setDisabledCloudlet(EncapedResCloudlet encapedRcl) {
    // List<ResCloudlet> rslList = getCloudletFailedList();
    // rslList.add(encapedRcl);
    // encapedRcl.setCloudletStatus(Cloudlet.FAILED);
    // encapedRcl.finalizeCloudlet();
    // }

    protected void processTimeout(double currentTime) {
        // Check if any cloudlet is timed out.
        if (timeoutLimit > 0 && Double.isFinite(timeoutLimit)) {
            double timeout = currentTime - this.timeoutLimit; // Jason: timeoutlimit是相对时间，即任务的最大执行超时时间
            {
                List<ResCloudlet> timeoutCloudlet = new ArrayList<ResCloudlet>();
                for (ResCloudlet rcl : getCloudletExecList()) {
                    if (rcl.getCloudletArrivalTime() < timeout) {
                        rcl.setCloudletStatus(Cloudlet.FAILED);
                        rcl.finalizeCloudlet();
                        timeoutCloudlet.add(rcl);
                        usedPes -= rcl.getNumberOfPes();
                    }
                }
                getCloudletExecList().removeAll(timeoutCloudlet);
                getCloudletFailedList().addAll(timeoutCloudlet);
                if (timeoutCloudlet.size() > 0) {
                    System.out.println("here may be error");
                }
            }
            {
                List<EncapedResCloudlet> timeoutCloudlet = new ArrayList<EncapedResCloudlet>();
                for (EncapedResCloudlet ercl : getCloudletWaitingList(true).getCacheQueue()) {
                    if (ercl.getCloudletArrivalTime() < timeout) {
                        ercl.setCloudletStatus(Cloudlet.FAILED);
                        ercl.finalizeCloudlet();
                        timeoutCloudlet.add(ercl);
                    }
                }
                if (timeoutCloudlet.size() > 0) {
                    System.out.println("here may be error");
                }
                getCloudletWaitingList(true).removeAll(timeoutCloudlet);
                getCloudletFailedList().addAll(timeoutCloudlet);
            }

        }
    }

    @Override
    public Cloudlet cloudletCancel(int cloudletId) {
        GiveAWarningForDebug();
        // First, looks in the finished queue
        for (ResCloudlet rcl : getCloudletFinishedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                getCloudletFinishedList().remove(rcl);
                return rcl.getCloudlet();
            }
        }

        // Then searches in the exec list
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                getCloudletExecList().remove(rcl);
                if (rcl.getRemainingCloudletLength() == 0) {
                    cloudletFinish(rcl);
                } else {
                    rcl.setCloudletStatus(Cloudlet.CANCELED);
                }
                return rcl.getCloudlet();
            }
        }

        // Now, looks in the paused queue
        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                getCloudletPausedList().remove(rcl);
                return rcl.getCloudlet();
            }
        }

        // Finally, looks in the waiting list
        for (EncapedResCloudlet ercl : getCloudletWaitingList(true).getCacheQueue()) {
            if (ercl.getCloudletId() == cloudletId) {
                ercl.setCloudletStatus(Cloudlet.CANCELED);
                getCloudletWaitingList(true).remove(ercl);
                return ercl.getCloudlet();
            }
        }

        return null;

    }

    @Override
    public boolean cloudletPause(int cloudletId) {
        GiveAWarningForDebug();
        boolean found = false;
        int position = 0;

        // first, looks for the cloudlet in the exec list
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            // moves to the paused list
            ResCloudlet rgl = getCloudletExecList().remove(position);
            if (rgl.getRemainingCloudletLength() == 0) {
                cloudletFinish(rgl);
            } else {
                rgl.setCloudletStatus(Cloudlet.PAUSED);
                getCloudletPausedList().add(rgl);
            }
            return true;

        }

        // now, look for the cloudlet in the waiting list
        position = 0;
        found = false;
        EncapedResCloudlet foundedElement = null;
        for (EncapedResCloudlet ercl : getCloudletWaitingList(true).getCacheQueue()) {
            if (ercl.getCloudletId() == cloudletId) {
                found = true;
                foundedElement = ercl;
                break;
            }
        }

        if (found) {
            // moves to the paused list
            getCloudletWaitingList(true).remove(foundedElement);
            if (foundedElement.getRemainingCloudletLength() == 0) {
                cloudletFinish(foundedElement);
            } else {
                foundedElement.setCloudletStatus(Cloudlet.PAUSED);
                getCloudletPausedList().add(foundedElement);
            }
            return true;

        }

        return false;
    }

    // @Override
    public MemoryQueue getCloudletWaitingList(boolean memoryQueueEnabled) {
        if (memoryQueueEnabled)
            return this.cloudletWaitingList;
        else {
            System.out.println("ERROR!!!! in" + Thread.currentThread().getStackTrace()[0]
                    + Thread.currentThread().getStackTrace()[1]);
            return null;
        }
    }

    @Override
    @Deprecated
    public List<Cloudlet> getCloudletWaitingList() {
        System.out.println("this method is deprecated!!!");
        return null;
    }

    @Override
    public long getTotalProcessingPreviousTime(double currentTime, List<Double> mipsShare) {
        long totalProcessedMIs = 0;
        double timeSpent = currentTime - prevMonitoredTime;
        double capacity = getCapacity(mipsShare);

        for (ResCloudlet rcl : getCloudletExecList()) {
            totalProcessedMIs += (long) (capacity * timeSpent * rcl.getNumberOfPes() * Consts.MILLION);
        }

        prevMonitoredTime = currentTime;
        return totalProcessedMIs;
    }

    protected double getCapacity(List<Double> mipsShare) {
        double capacity = 0.0;
        int cpus = 0;
        for (Double mips : mipsShare) {
            capacity += mips;
            if (mips > 0.0) {
                cpus++;
            }
        }
        capacity /= cpus;
        return capacity;
    }

    @Override
    public boolean isVmIdle() {
        if (runningCloudlets() > 0)
            return false;
        if (getCloudletWaitingList(true).size() > 0)
            return false;
        return true;
    }

    @Override
    public double getTimeSpentPreviousMonitoredTime(double currentTime) {
        double timeSpent = currentTime - prevMonitoredTime;
        return timeSpent;
    }

    @Override
    public int getCloudletTotalPesRequested() {
        return getCurrentMipsShare().size();
    }

    public int getNumAllCloudlets() {
        return super.cloudletExecList.size() + super.cloudletFailedList.size() + super.getCloudletFinishedList().size()
                + super.cloudletPausedList.size() + super.cloudletWaitingList.size();
    }

    public QueuedVM getQueuedVM() {
        return queuedVM;
    }

    public void setQueuedVM(QueuedVM queuedVM) {
        this.queuedVM = queuedVM;
        this.getCloudletWaitingList(true).setVmInfotoDebug(queuedVM);
    }
}
