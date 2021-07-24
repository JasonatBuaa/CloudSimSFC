package org.cloudbus.cloudsim.sfc.parser;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName InOutDC.java
 * @Description TODO
 * @createTime 2021-07-24 16:49
 */
public class InOutDc {
    private String Name;
    private float Weight;
    private String DC;

    public InOutDc() {

    }

    public InOutDc(String name, float weight) {
        Name = name;
        Weight = weight;
    }

    public InOutDc(String name, float weight, String DC) {
        Name = name;
        Weight = weight;
        this.DC = DC;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public float getWeight() {
        return Weight;
    }

    public void setWeight(float weight) {
        Weight = weight;
    }

    public String getDC() {
        return DC;
    }

    public void setDC(String DC) {
        this.DC = DC;
    }

    @Override
    public String toString() {
        return "InOutDc{" + "Name='" + Name + '\'' + ", Weight=" + Weight + '}';
    }
}
