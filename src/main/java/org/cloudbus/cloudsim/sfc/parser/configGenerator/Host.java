package org.cloudbus.cloudsim.sfc.parser.configGenerator;

public class Host extends PhysicalTopologyNode{
    public long pes;
    public long mips;
    public long ram;
    public long storage;
    public long bw;

    public Host(String name, String type, String datacenter, long pes, long mips, long ram, long storage, long bw) {
        super(name, type, datacenter);
        this.pes = pes;
        this.mips = mips;
        this.ram = ram;
        this.storage = storage;
        this.bw = bw;
    }

    public long getPes() {
        return pes;
    }

    public void setPes(long pes) {
        this.pes = pes;
    }

    public long getMips() {
        return mips;
    }

    public void setMips(long mips) {
        this.mips = mips;
    }

    public long getRam() {
        return ram;
    }

    public void setRam(long ram) {
        this.ram = ram;
    }

    public long getStorage() {
        return storage;
    }

    public void setStorage(long storage) {
        this.storage = storage;
    }

    public long getBw() {
        return bw;
    }

    public void setBw(long bw) {
        this.bw = bw;
    }
}
