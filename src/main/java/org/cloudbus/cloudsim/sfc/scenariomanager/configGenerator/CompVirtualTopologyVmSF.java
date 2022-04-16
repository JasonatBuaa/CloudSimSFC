package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName VirtualTopoployServiceFunction.java
 * @Description TODO
 * @createTime 2021-06-30 21:45
 */

@JSONType(orders = { "datacenter", "name", "type", "size", "pes", "mips", "ram", "queuesize","Mipo" })
public class CompVirtualTopologyVmSF extends VirtualTopologyVM {
   public int size;
   public int pes;
   public int mips;
   public int queuesize;
   private int mipo;
   public String datacenter;

   // Jason: this one is used for the attemptive scheduling
   public CompVirtualTopologyVmSF(int size, int pes, int mips, int queuesize) {
      super("attemptive_scheduling", null);
      this.size = size;
      this.pes = pes;
      this.mips = mips;
      this.queuesize = queuesize;
      this.setMipo(0);
   }

   /**
    *
    * @param name
    * @param type
    * @param size
    * @param pes
    * @param mips
    * @param queuesize
    */
   public CompVirtualTopologyVmSF(String name, String type, int size, int pes, int mips, int queuesize, int mipo) {
      super(name, type);
      this.size = size;
      this.pes = pes;
      this.mips = mips;
      this.queuesize = queuesize;
      this.setMipo(mipo);
   }

   public CompVirtualTopologyVmSF(String name, String type, int size, int pes, int mips, int queuesize,
                                  String dc, int mipo) {
      super(name, type);
      this.size = size;
      this.pes = pes;
      this.mips = mips;
      this.queuesize = queuesize;
      this.datacenter = dc;
      this.setMipo(mipo);
   }

   public int getSize() {
      return size;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public int getPes() {
      return pes;
   }

   public void setPes(int pes) {
      this.pes = pes;
   }

   public int getMips() {
      return mips;
   }

   public void setMips(int mips) {
      this.mips = mips;
   }

   public int getQueuesize() {
      return queuesize;
   }

   public void setQueuesize(int queuesize) {
      this.queuesize = queuesize;
   }

   public String getDatacenter() {
      return datacenter;
   }

   public void setDatacenter(String datacenter) {
      this.datacenter = datacenter;
   }

   public int getMipo() {
      return mipo;
   }

   public void setMipo(int mipo) {
      this.mipo = mipo;
   }
}
