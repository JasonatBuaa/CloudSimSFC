package org.cloudbus.cloudsim.sfc.monitor.impl;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.sfc.monitor.MonitorInterface;

import java.util.List;
import java.util.Map;

public class MonitorImpl implements MonitorInterface {
    public Map<String, List<Host>> getRemainingResources() {
        return null;
    }

    public List<Host> getRemainingResourcesByDC(String dcName) {
        return null;
    }

    public String getDCTypeByName(String name) {
        return null;
    }
}
