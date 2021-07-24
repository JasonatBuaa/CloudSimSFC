package org.cloudbus.cloudsim.sfc.monitor;

import org.cloudbus.cloudsim.Host;

import java.util.List;
import java.util.Map;

public interface MonitorInterface {
    public Map<String, List<Host>> getRemainingResources();

    public List<Host> getRemainingResourcesByDC(String dcName);
}
