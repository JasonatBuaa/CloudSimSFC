package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName VirtualTopologyLink.java
 * @Description TODO
 * @createTime 2021-06-30 21:25
 */

@JSONType(orders = { "name", "source", "destination", "bandwidth" })
public class VirtualTopologyLink {
   public String name;
   public String source;
   public String destination;
   public int bandwidth;

   public VirtualTopologyLink(String name, String source, String destination, int bandwidth) {
      this.name = name;
      this.source = source;
      this.destination = destination;
      this.bandwidth = bandwidth;
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

   public String getDestination() {
      return destination;
   }

   public void setDestination(String destination) {
      this.destination = destination;
   }

   public int getBandwidth() {
      return bandwidth;
   }

   public void setBandwidth(int bandwidth) {
      this.bandwidth = bandwidth;
   }
}
