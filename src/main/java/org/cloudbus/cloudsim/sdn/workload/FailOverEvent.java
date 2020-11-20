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
public class FailOverEvent implements Comparable<FailOverEvent> {
    public int failOverEventId;

    public double time;
    public double failureTime;
    public double recoveryTime;

    public int hostID;
    // public AvailabilityEventType type;

    public FailOverEvent(int failOverEventId) {
        this.failOverEventId = failOverEventId;
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
    public int compareTo(FailOverEvent that) {
        return this.failOverEventId - that.failOverEventId;
    }

    @Override
    public String toString() {
        return "FailOverEventId (ID:" + failOverEventId + "/" + "HostID:" + hostID + ", Eventtime:" + time
                + "Failure Time:" + failureTime + "Recovery Time:" + recoveryTime;
    }

    // @Override
    // public int compareTo(Workload o) {
    // // TODO Auto-generated method stub
    // return 0;
    // }

    // public static enum AvailabilityEventType {
    // Failure("failure"), Recovery("recovery");

    // private AvailabilityEventType(String type) {
    // this.type = type;
    // }

    // // 成员变量
    // private String type;

    // public String getType() {
    // return type;
    // }

    // public void setType(String type) {
    // this.type = type;
    // }

    // @Override
    // public String toString() {
    // return this.type;
    // }
    // }

    // public static void main(String[] args) {
    // FailOverEvent a = new FailOverEvent(1);
    // System.out.println(FailOverEventType.Failure);
    // System.out.println(FailOverEventType.Recovery);

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

    // public AvailabilityEventType getType() {
    // return type;
    // }

    // public void setType(AvailabilityEventType type) {
    // this.type = type;
    // }
}
