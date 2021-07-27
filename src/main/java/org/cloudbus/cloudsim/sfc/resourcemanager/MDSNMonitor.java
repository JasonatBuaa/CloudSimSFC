package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.sdn.SDNBroker;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNDatacenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDSNMonitor implements MonitorInterface {
    public SDNBroker broker;

    public MDSNMonitor(SDNBroker broker) {
        this.broker = broker;
    }

    @Override
    public Map<String, List<Host>> getRemainingResources() {
        Map<String, List<Host>> remainingResources = new HashMap<>();
        for (SDNDatacenter dc : broker.datacenters.values()) {
            List<Host> hosts = dc.getVmAllocationPolicy().getHostList();
            remainingResources.put(dc.getName(), hosts);
        }
        return remainingResources;
    }

    @Override
    public List<Host> getRemainingResourcesByDC(String dcName) {
        Map<String, List<Host>> remainingResources = getRemainingResources();
        if (remainingResources.containsKey(dcName))
            return remainingResources.get(dcName);
        else
            return null;
    }

    @Override
    public boolean occupyResource(String dcName, int size, int pes, int mips, int queueSize) {

        return true;
    }

    // public List<Host>
}
