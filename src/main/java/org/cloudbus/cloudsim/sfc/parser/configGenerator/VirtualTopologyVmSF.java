package org.cloudbus.cloudsim.sfc.parser.configGenerator;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName VirtualTopoployServerFunction.java
 * @Description TODO
 * @createTime 2021-06-30 21:45
 */
public class VirtualTopologyVmSF extends VirtualTopologyVM{
   public int size;
   public int pes;
   public int mips;
   public int queuesize;

   public VirtualTopologyVmSF(String name, String type, int size, int pes, int mips, int queuesize) {
      super(name, type);
      this.size = size;
      this.pes = pes;
      this.mips = mips;
      this.queuesize = queuesize;
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
}
