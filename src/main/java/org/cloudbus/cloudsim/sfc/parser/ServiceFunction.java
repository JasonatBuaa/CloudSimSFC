package org.cloudbus.cloudsim.sfc.parser;

import java.util.HashMap;

public class ServiceFunction {
    private String Name;
    private String Type;
    private double InputRate;
    private double OutputRate;
    private int Performance;
    public static HashMap<String, ServiceFunction> serverFunctionMap = new HashMap<>();

    public ServiceFunction(String name, String type, double inputRate, double outputRate, int performance) {
        Name = name;
        Type = type;
        InputRate = inputRate;
        OutputRate = outputRate;
        Performance = performance;
        serverFunctionMap.put(Name, this);
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

    public int getPerformance() {
        return Performance;
    }

    public void setPerformance(int performance) {
        Performance = performance;
    }

    public int getOutputSize(int inputSize) {
        return new Double(inputSize * (OutputRate / InputRate)).intValue();
    }

    @Override
    public String toString() {
        return "ServiceFunction{" + "Name='" + Name + '\'' + ", Type='" + Type + '\'' + ", InputRate=" + InputRate
                + ", OutputRate=" + OutputRate + ", Performance=" + Performance + '}';
    }
}
