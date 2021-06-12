package org.cloudbus.cloudsim.sfc.parser;

public class PhysicalResource {
    private String Name;
    private int serverNumber;
    private int MIPS;
    private int SIGMA2;
    private int FastMem;
    private int BW;
    private double Availability;
    private double MTTR;
    private double MTBF;
    private int MinMIPS;
    private double PriceRatio;

    public PhysicalResource(String name, int serverNumber, int MIPS, int SIGMA2, int fastMem, int BW, double availability, double MTTR, double MTBF, int minMIPS, double priceRatio) {
        Name = name;
        this.serverNumber = serverNumber;
        this.MIPS = MIPS;
        this.SIGMA2 = SIGMA2;
        FastMem = fastMem;
        this.BW = BW;
        Availability = availability;
        this.MTTR = MTTR;
        this.MTBF = MTBF;
        MinMIPS = minMIPS;
        PriceRatio = priceRatio;
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

    public int getSIGMA2() {
        return SIGMA2;
    }

    public void setSIGMA2(int SIGMA2) {
        this.SIGMA2 = SIGMA2;
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
        return "PhysicalResource{" +
                "Name='" + Name + '\'' +
                ", serverNumber=" + serverNumber +
                ", MIPS=" + MIPS +
                ", SIGMA2=" + SIGMA2 +
                ", FastMem=" + FastMem +
                ", BW=" + BW +
                ", Availability=" + Availability +
                ", MTTR=" + MTTR +
                ", MTBF=" + MTBF +
                ", MinMIPS=" + MinMIPS +
                ", PriceRatio=" + PriceRatio +
                '}';
    }
}
