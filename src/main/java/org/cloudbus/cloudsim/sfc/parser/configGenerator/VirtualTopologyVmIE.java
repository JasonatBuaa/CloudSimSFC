package org.cloudbus.cloudsim.sfc.parser.configGenerator;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName VirtualTopoployVmIE.java
 * @Description TODO
 * @createTime 2021-06-30 21:46
 */
public class VirtualTopologyVmIE extends VirtualTopologyVM{
    public String dc;

    public VirtualTopologyVmIE(String name, String type, String dc) {
        super(name, type);
        this.dc = dc;
    }

    public String getdc() {
        return dc;
    }

    public void setdc(String dc) {
        this.dc = dc;
    }
}
