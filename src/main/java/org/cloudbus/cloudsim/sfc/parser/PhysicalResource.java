package org.cloudbus.cloudsim.sfc.parser;

public class PhysicalResource {
    private String Name;
    private int serverNumber;
    private int MIPS;
    private double SIGMA;
    private int FastMem;
    private int BW;
    private double Availability;
    private double MTTR;
    private double MTBF;
    private int MinMIPS;
    private double PriceRatio;

    public PhysicalResource(String name, int serverNumber, int MIPS, double SIGMA, int fastMem, int BW,
            double availability, double MTTR, double MTBF, int minMIPS, double priceRatio) {
        this.Name = name;
        this.serverNumber = serverNumber;
        this.MIPS = MIPS;
        this.SIGMA = SIGMA;
        this.FastMem = fastMem;
        this.BW = BW;
        this.Availability = availability;
        this.MTTR = MTTR;
        this.MTBF = MTBF;
        this.MinMIPS = minMIPS;
        this.PriceRatio = priceRatio;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }

    public int getMIPS() {
        return MIPS;
    }

    public void setMIPS(int MIPS) {
        this.MIPS = MIPS;
    }

    public double getSIGMA() {
        return SIGMA;
    }

    public void setSIGMA(double SIGMA) {
        this.SIGMA = SIGMA;
    }

    public int getFastMem() {
        return FastMem;
    }

    public void setFastMem(int fastMem) {
        FastMem = fastMem;
    }

    public int getBW() {
        return BW;
    }

    public void setBW(int BW) {
        this.BW = BW;
    }

    public double getAvailability() {
        return Availability;
    }

    public void setAvailability(double availability) {
        Availability = availability;
    }

    public double getMTTR() {
        return MTTR;
    }

    public void setMTTR(double MTTR) {
        this.MTTR = MTTR;
    }

    public double getMTBF() {
        return MTBF;
    }

    public void setMTBF(double MTBF) {
        this.MTBF = MTBF;
    }

    public int getMinMIPS() {
        return MinMIPS;
    }

    public void setMinMIPS(int minMIPS) {
        MinMIPS = minMIPS;
    }

    public double getPriceRatio() {
        return PriceRatio;
    }

    public void setPriceRatio(double priceRatio) {
        PriceRatio = priceRatio;
    }

    @Override
    public String toString() {
        return "PhysicalResource{" + "Name='" + Name + '\'' + ", serverNumber=" + serverNumber + ", MIPS=" + MIPS
                + ", SIGMA2=" + SIGMA + ", FastMem=" + FastMem + ", BW=" + BW + ", Availability=" + Availability
                + ", MTTR=" + MTTR + ", MTBF=" + MTBF + ", MinMIPS=" + MinMIPS + ", PriceRatio=" + PriceRatio + '}';
    }
}
