package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;

public class PhysicalTopologyLink {
    public String source;
    public String destination;
    public double latency;

    public PhysicalTopologyLink(String source, String destination, double latency) {
        this.source = source;
        this.destination = destination;
        this.latency = latency;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }
}
