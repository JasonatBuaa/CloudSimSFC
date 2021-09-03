package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.sfc.scenariomanager.Resource;
import org.cloudbus.cloudsim.sfc.scenariomanager.SFCWorkload;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunctionChain;

import java.util.List;
import java.util.Map;

public interface SchedulingInterface {
    public void schedule();

    // public boolean hasEnoughResource(List<Double> demandedResource);
    // 根据DC name查找DC类型

    // public List<Host>


    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
                              List<ServiceFunctionChain> serviceFunctionChains);
    public void generateLinks(List<ServiceFunctionChain> serviceFunctionChains);

}
