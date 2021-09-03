package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName VirtualTopologyPolicy.java
 * @Description TODO
 * @createTime 2021-06-30 21:25
 */
@JSONType(orders = { "name", "flowname", "source", "destination", "sfc_Demand", "starttime", "expectedduration",
        "sfc" })
public class VirtualTopologyPolicy {
    public String name;
    public String source;
    public String sfc_Demand;
    public String destination;
    public String flowname;
    public int starttime;
    public int expectedduration;
    public List<String> sfc;

    /**
     * 
     * @param name
     * @param source
     * @param sfc_Demand
     * @param destination
     * @param flowname
     * @param starttime
     * @param expectedduration
     * @param sfc
     */
    public VirtualTopologyPolicy(String name, String source, String sfc_Demand, String destination, String flowname,
            int starttime, int expectedduration, List<String> sfc) {
        this.name = name;
        this.source = source;
        this.sfc_Demand = sfc_Demand;
        this.destination = destination;
        this.flowname = flowname;
        this.starttime = starttime;
        this.expectedduration = expectedduration;
        this.sfc = sfc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getsfc_Demand() {
        return sfc_Demand;
    }

    public void setsfc_Demand(String sfc_Demand) {
        this.sfc_Demand = sfc_Demand;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFlowname() {
        return flowname;
    }

    public void setFlowname(String flowname) {
        this.flowname = flowname;
    }

    public int getStarttime() {
        return starttime;
    }

    public void setStarttime(int starttime) {
        this.starttime = starttime;
    }

    public int getExpectedduration() {
        return expectedduration;
    }

    public void setExpectedduration(int expectedduration) {
        this.expectedduration = expectedduration;
    }

    public List<String> getSfc() {
        return sfc;
    }

    public void setSfc(List<String> sfc) {
        this.sfc = sfc;
    }
}
