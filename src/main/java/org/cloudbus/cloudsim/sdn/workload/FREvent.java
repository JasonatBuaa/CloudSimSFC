/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.sdn.workload;

/**
 * Class to keep workload information parsed from files. This class is used in
 * WorkloadParser
 * 
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */
public class FREvent implements Comparable<FREvent> {
    public int fREventId;

    public double time;
    public double failureTime;
    public double recoveryTime;

    public int hostID;
    // public AvailabilityEventType type;

    public FREvent(int fREventId) {
        this.fREventId = fREventId;
        // this.resultWriter = writer;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public int getHostID() {
        return this.hostID;
    }

    // public WorkloadResultWriter resultWriter;

    public boolean failed = false;

    public void writeResult() {
        // this.resultWriter.writeResult(this);
    }

    // public void setAvailabilityEventType(AvailabilityEventType type) {
    // this.type = type;
    // }

    @Override
    public int compareTo(FREvent that) {
        return this.fREventId - that.fREventId;
    }

    @Override
    public String toString() {
        return "FailOverEventId (ID:" + fREventId + "/" + "HostID:" + hostID + ", Eventtime:" + time
                + "Failure Time:" + failureTime + "Recovery Time:" + recoveryTime;
    }

    // @Override
    // public int compareTo(Workload o) {
    // // TODO Auto-generated method stub
    // return 0;
    // }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(double failureTime) {
        this.failureTime = failureTime;
    }

    public double getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(double recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

}
