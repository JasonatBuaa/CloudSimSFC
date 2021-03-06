package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = { "datacenter", "type", "name", "iops", "upports", "downports", "bw" })
public class SwitchDesp extends PhysicalTopologyNode {
    public long iops;
    public int upports;
    public int downports;
    public long bw;

    public SwitchDesp(String name, String type, String datacenter, long iops, int upports, int downports, long bw) {
        super(name, type, datacenter);
        this.iops = iops;
        this.upports = upports;
        this.downports = downports;
        this.bw = bw;
    }

    public long getIops() {
        return iops;
    }

    public void setIops(long iops) {
        this.iops = iops;
    }

    public int getUpports() {
        return upports;
    }

    public void setUpports(int upports) {
        this.upports = upports;
    }

    public int getDownports() {
        return downports;
    }

    public void setDownports(int downports) {
        this.downports = downports;
    }

    public long getBw() {
        return bw;
    }

    public void setBw(long bw) {
        this.bw = bw;
    }
}
