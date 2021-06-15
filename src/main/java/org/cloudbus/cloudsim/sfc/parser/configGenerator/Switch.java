package org.cloudbus.cloudsim.sfc.parser.configGenerator;

public class Switch extends PhysicalTopologyNode{
    public int iops;
    public int upports;
    public int downports;
    public int bw;


    public Switch(String name, String type, String datacenter,  int iops, int upports, int downports, int bw) {
        super(name, type, datacenter);
        this.iops = iops;
        this.upports = upports;
        this.downports = downports;
        this.bw = bw;
    }

    public int getIops() {
        return iops;
    }

    public void setIops(int iops) {
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

    public int getBw() {
        return bw;
    }

    public void setBw(int bw) {
        this.bw = bw;
    }
}
