package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.Host;

import java.util.List;
import java.util.Map;

public interface MonitorInterface {
    public Map<String, List<Host>> getRemainingResources();

    public List<Host> getRemainingResourcesByDC(String dcName);

    // public boolean hasEnoughResource(List<Double> demandedResource);
    // 根据DC name查找DC类型

    // public List<Host>
}
