package org.cloudbus.cloudsim.sfc.parser.configGenerator;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName VirtualTopologyVM.java
 * @Description TODO
 * @createTime 2021-06-30 21:25
 */
public class VirtualTopologyVM {
    public String name;
    public String type;

    public VirtualTopologyVM(String name, String type) {
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
