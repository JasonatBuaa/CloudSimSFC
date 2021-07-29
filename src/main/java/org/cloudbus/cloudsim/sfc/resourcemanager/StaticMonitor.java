package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.distributions.NormalDistr;
import org.cloudbus.cloudsim.sdn.SDNBroker;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNDatacenter;
import org.cloudbus.cloudsim.sfc.parser.HomogeneousResourceGroup;
import org.cloudbus.cloudsim.sfc.parser.PhysicalResource;
import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.HostDesp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticMonitor {
    // public SDNBroker broker;

    Map<String, List<HostDesp>> MDSNResources = new HashMap<>();

    Map<String, Long> remainingResources = new HashMap<>();

    // public StaticMonitor(SDNBroker broker,List<Resource> resources) {
    public StaticMonitor(List<Resource> resources) {
        // this.broker = null;
        initiateResource(resources);
    }

    public Map<String, Long> getRemainingResources() {
        return this.remainingResources;
    }

    public Long getRemainingResourcesByDC(String dcName) {
        if (this.remainingResources.containsKey(dcName))
            return remainingResources.get(dcName);
        else
            return null;
    }

    public boolean occupyResource(String dcName, int size, int pes, int mips, int queueSize) {
        this.remainingResources.get(dcName);

        return false;
    }

    public void initiateResource(List<Resource> resources) {
        for (Resource resource : resources) {
            List<HomogeneousResourceGroup> homogeneousResourceGroups = resource.getHomogeneousResourceGroups();
            // for (HomogeneousResourceGroup homogeneousResourceGroup :
            // homogeneousResourceGroups) {
            // nodeMap.put(homogeneousResourceGroup.getName(), new ArrayList<HostDesp>());
            // }

            for (HomogeneousResourceGroup homogeneousResourceGroup : homogeneousResourceGroups) {
                List<PhysicalResource> physicalResources = homogeneousResourceGroup.getPhysicalResources();
                for (int i = 0; i < physicalResources.size(); i++) {
                    PhysicalResource physicalResource = physicalResources.get(i);
                    int size = physicalResource.getServerNumber();
                    for (int j = 0; j < size; j++) {
                        String name = resource.getName() + '-' + physicalResource.getName() + '-' + (j + 1);
                        String type = "host";
                        String datacenter = resource.getName();
                        // todo need pes config
                        long pes = physicalResource.getMIPS() / physicalResource.getMinMIPS();
                        long mips = physicalResource.getMIPS();
                        long ram = physicalResource.getFastMem();
                        // todo need storage config
                        long storage = physicalResource.getFastMem() * 100;
                        long bw = physicalResource.getBW();
                        double mtbf = physicalResource.getMTBF();
                        double mttr = physicalResource.getMTTR();
                        double jitter_sigma = physicalResource.getSIGMA();
                        double priceRatio = physicalResource.getPriceRatio();
                        HostDesp newHostDesp = new HostDesp(name, type, datacenter, pes, mips, ram, storage, bw, mtbf,
                                mttr, jitter_sigma, priceRatio);
                        if (MDSNResources.containsKey(datacenter))
                            MDSNResources.get(datacenter).add(newHostDesp);
                        else {
                            List<HostDesp> newDc = new ArrayList<HostDesp>();
                            newDc.add(newHostDesp);
                            MDSNResources.put(datacenter, newDc);
                        }
                        if (remainingResources.containsKey(datacenter))
                            remainingResources.put(datacenter, remainingResources.get(datacenter) + (mips * pes));
                        else {
                            List<HostDesp> newDc = new ArrayList<HostDesp>();
                            newDc.add(newHostDesp);
                            remainingResources.put(datacenter, mips * pes);
                        }
                    }
                }
            }
        }

    }

    // public List<Host>
}
