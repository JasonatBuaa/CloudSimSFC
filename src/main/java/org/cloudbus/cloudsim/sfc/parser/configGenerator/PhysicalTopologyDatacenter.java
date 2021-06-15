package org.cloudbus.cloudsim.sfc.parser.configGenerator;

public class PhysicalTopologyDatacenter {
    public String name;
    public String type;

    public PhysicalTopologyDatacenter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
