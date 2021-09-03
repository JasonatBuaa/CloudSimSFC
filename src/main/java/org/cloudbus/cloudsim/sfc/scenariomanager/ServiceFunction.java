package org.cloudbus.cloudsim.sfc.scenariomanager;

import java.util.HashMap;

public class ServiceFunction {
    private String Name;
    private String Type;
    private double InputRate;
    private double OutputRate;
    private int OperationalComplexity;
    public static HashMap<String, ServiceFunction> serviceFunctionMap = new HashMap<>();

    public ServiceFunction(String name, String type, double inputRate, double outputRate, int operationalComplexity) {
        Name = name;
        Type = type;
        InputRate = inputRate;
        OutputRate = outputRate;
        this.OperationalComplexity = operationalComplexity;
        serviceFunctionMap.put(Name, this);
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public double getInputRate() {
        return InputRate;
    }

    public void setInputRate(double inputRate) {
        InputRate = inputRate;
    }

    public double getOutputRate() {
        return OutputRate;
    }

    public void setOutputRate(double outputRate) {
        OutputRate = outputRate;
    }

    public int getOperationalComplexity() {
        return OperationalComplexity;
    }

    public void setOperationalComplexity(int OperationalComplexity) {
        this.OperationalComplexity = OperationalComplexity;
    }

    public double getOutputSize(double inputSize) {
        return new Double(inputSize * (OutputRate / InputRate)).intValue();
    }

    public double getTrafficChangingRatio() {
        return OutputRate / InputRate;
    }

    @Override
    public String toString() {
        return "ServiceFunction{" + "Name='" + Name + '\'' + ", Type='" + Type + '\'' + ", InputRate=" + InputRate
                + ", OutputRate=" + OutputRate + ", OperationalComplexity=" + OperationalComplexity + '}';
    }
}
