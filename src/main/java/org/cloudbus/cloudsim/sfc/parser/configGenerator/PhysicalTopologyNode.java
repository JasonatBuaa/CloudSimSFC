package org.cloudbus.cloudsim.sfc.parser.configGenerator;

public class PhysicalTopologyNode {
    public String name;
    public String type;
    private String datacenter;

    public PhysicalTopologyNode(String name, String type, String datacenter) {
        this.name = name;
        this.type = type;
        this.datacenter = datacenter;
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

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }




}
