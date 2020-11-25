
package org.cloudbus.cloudsim.sdn;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.ResCloudlet;
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

    public MemoryQueue memQueue;
    public long queueSize;

    public CloudletSchedulerSpaceSharedQueueAwareMonitor(long queueSize, double timeOut) {
        super(timeOut);
        this.timeoutLimit = timeOut; // Jason: here inputs the timeout value
        memQueue = new MemoryQueue(queueSize);
    }

    // ----------------------------------------------------------------------------------
    // Jason: Todo 重写这一段代码，将queue aware的特性体现出来。要点：
    // 1、首先，改写后，vm的queueSize要能被感知到；
    // 2、在处理cloudletSubmit的时候，vm的queueSize要被正确的计算（核减）
    // 3、在requestFailed或者requestCompleted之后，vm的queueSize要被正确的计算（提升以恢复原有的queueSize）
    // 4、queue如何进如何出应该可以拓展，即可将queue的进出带优先级

    @Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        long clLength = cloudlet.getCloudletLength();
        // Jason: 1. We check if the memory queue is sufficient. When sufficient,
        // enQueue the request. else, drop the packet.
        // 2. if exec list is not full, transfer the request from memQueue to exec
        // queueSize
        // 3. else, transfer the request into the waiting queue.
        if (!(memQueue.getQueueRemainingSize() > clLength) || !memQueue.addCloudlet(cloudlet)) {
            // Jason: Todo! Check the correctness of this code! insufficient queue
            // size, drop packet!!!
            ResCloudlet failedrcl = new ResCloudlet(cloudlet);
            failedrcl.setCloudletStatus(Cloudlet.FAILED);
            getCloudletFailedList().add(failedrcl);
        }
        // it can go to the exec list
        if ((currentCpus - usedPes) >= cloudlet.getNumberOfPes()) {
            ResCloudlet rcl = new ResCloudlet(cloudlet);
            rcl.setCloudletStatus(Cloudlet.INEXEC);
            for (int i = 0; i < cloudlet.getNumberOfPes(); i++) {
                rcl.setMachineAndPeId(0, i);
            }
            getCloudletExecList().add(rcl);
            usedPes += cloudlet.getNumberOfPes();
        } else {// no enough free PEs: go to the waiting queue
            ResCloudlet rcl = new ResCloudlet(cloudlet);
            rcl.setCloudletStatus(Cloudlet.QUEUED);
            getCloudletWaitingList().add(rcl);
            return 0.0;
        }

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

        // use the current capacity to estimate the extra amount of
        // time to file transferring. It must be added to the cloudlet length
        double extraSize = capacity * fileTransferTime;
        long length = cloudlet.getCloudletLength();
        length += extraSize;
        cloudlet.setCloudletLength(length);
        return cloudlet.getCloudletLength() / capacity;
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }

    // --------------------------------------------------------------

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        double ret = super.updateVmProcessing(currentTime, mipsShare);
        processTimeout(currentTime);
        return ret;
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

    // public List<ResCloudlet> getCloudletFailedList() {
    // List<ResCloudlet> rcl = super.getCloudletFailedList();
    // }

    // Jason: Todo! check this part.
    public void setFailureCloudlet(EncapedResCloudlet encapedRcl) {
        List<ResCloudlet> rslList = getCloudletFailedList();
        rslList.add(encapedRcl);
    }

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
            }
            {
                List<ResCloudlet> timeoutCloudlet = new ArrayList<ResCloudlet>();
                for (ResCloudlet rcl : getCloudletWaitingList()) {
                    if (rcl.getCloudletArrivalTime() < timeout) {
                        rcl.setCloudletStatus(Cloudlet.FAILED);
                        rcl.finalizeCloudlet();
                        timeoutCloudlet.add(rcl);
                    }
                }
                getCloudletWaitingList().removeAll(timeoutCloudlet);
                getCloudletFailedList().addAll(timeoutCloudlet);
            }

        }
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
        if (getCloudletWaitingList().size() > 0)
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
    }
}
