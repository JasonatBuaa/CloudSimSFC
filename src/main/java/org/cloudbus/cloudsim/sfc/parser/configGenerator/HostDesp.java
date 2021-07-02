package org.cloudbus.cloudsim.sfc.parser.configGenerator;

public class HostDesp extends PhysicalTopologyNode {
    public long pes;
    public long mips;
    public long ram;
    public long storage;
    public long bw;
    public double mtbf;
    public double availability;

    public double mttr;
    public double jitterSigma2;

    public double priceRatio;

    public HostDesp(String name, String type, String datacenter, long pes, long mips, long ram, long storage, long bw,
            double mtbf, double mttr, double jitterSigma2, double priceRatio) {
        super(name, type, datacenter);
        this.pes = pes;
        this.mips = mips;
        this.ram = ram;
        this.storage = storage;
        this.bw = bw;
        this.mttr = mttr;
        this.calcAvailability();
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

    public void setMttr(double Mttr) {
        this.mttr = Mttr;
    }

    public double getMtbf() {
        return mtbf;
    }

    public void setMtbf(double mtbf) {
        this.mtbf = mtbf;
    }

    public double getAvailability() {
        return availability;
    }

    public void calcAvailability() {
        this.availability = this.mtbf / (this.mtbf + this.mttr);
    }

    public double getJitterSigma2() {
        return jitterSigma2;
    }

    public void setJitterSigma2(double jitterSigma2) {
        this.jitterSigma2 = jitterSigma2;
    }

    public double getPriceRatio() {
        return priceRatio;
    }

    public void setPriceRatio(double priceRatio) {
        this.priceRatio = priceRatio;
    }

}
