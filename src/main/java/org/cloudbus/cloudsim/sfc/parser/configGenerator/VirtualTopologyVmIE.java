package org.cloudbus.cloudsim.sfc.parser.configGenerator;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName VirtualTopoployVmIE.java
 * @Description TODO
 * @createTime 2021-06-30 21:46
 */
// public class VirtualTopologyVmIE extends VirtualTopologyVM{
// public String dc;

// public VirtualTopologyVmIE(String name, String type, String dc) {
// super(name, type);
// this.dc = dc;
// }

// public VirtualTopologyVmSF(String name, String type, int size, int pes, int
// mips, int queuesize)
@JSONType(orders = { "datacenter", "name", "type", "size", "pes", "mips", "queuesize", "sfc" })
public class VirtualTopologyVmIE extends VirtualTopologyVmSF {
    public String datacenter;

    // public int size = 0;
    // public int pes = 0;
    // public int mips = 0;
    // public int queueSize = 0;

    public VirtualTopologyVmIE(String name, String type, String datacenter) {
        super(name, type, 0, 0, 0, 0); // size = 0, pes = 0, mips = 0, queueSize = 0.

        this.datacenter = datacenter;
    }

    public String getdc() {
        return datacenter;
    }

    public void setdc(String datacenter) {
        this.datacenter = datacenter;
    }
}
