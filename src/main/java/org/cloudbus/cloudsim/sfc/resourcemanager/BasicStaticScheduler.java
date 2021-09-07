package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.sfc.scenariomanager.InOutDc;
// import org.cloudbus.cloudsim.sfc.monitor.impl.MonitorImpl;
import org.cloudbus.cloudsim.sfc.scenariomanager.Resource;
import org.cloudbus.cloudsim.sfc.scenariomanager.SFCWorkload;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunction;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunctionChain;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.VirtualTopologyVmIE;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.VirtualTopologyVmSF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName Scheduler.java
 * @Description TODO
 * @createTime 2021-07-24 22:20
 */
// 继承DeploymentScheduler避免重复开发,后面需要自定义产生link和policy的方法的话,可以把继承去了重写
public class BasicStaticScheduler extends DeploymentScheduler {
    // private ServiceFunctionChain needSchedule;
    private MonitorInterface monitor;

    public BasicStaticScheduler(List<ServiceFunctionChain> serviceFunctionChains, List<SFCWorkload> sfcWorkloads,
                                List<Resource> resources, MonitorInterface monitor) {
        super(serviceFunctionChains, sfcWorkloads, resources);
        this.monitor = monitor;
    }

    @Override
    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources);

        scheduleByGreedy(serviceFunctionChains);

    }

    public void placeIngressEgressNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources) {
        String inDc = resources.get(0).getName();
        String outDc = resources.get(resources.size() - 1).getName();
        for (SFCWorkload sfcWorkload : sfcWorkloads) {
            for (InOutDc inOutDc : sfcWorkload.getIngress()) {
                nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Ingress", inDc));
            }
            // for (InOutDc inOutDc : sfcWorkload.getEgress()) {
            // nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Egress", outDc));
            // }
            nodes.add(new VirtualTopologyVmIE(sfcWorkload.getEgress().getName(), "Egress", outDc));
        }
    }

    public void scheduleByGreedy(List<ServiceFunctionChain> serviceFunctionChains) {
        double inOutRatio = 1.0;
        for (ServiceFunctionChain needSchedule : serviceFunctionChains) {
            List<String> chain = needSchedule.getChain();
            int index = 0;
            for (; index < chain.size();) {
                Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctionTogether = new HashMap<>();
                inOutRatio = 1.0;

                // 0. 确定资源配额

                // 如果chain的某一个SF 出/进>1,就继续循环,找下一个捆绑部署,直到 出/进 < 1;如果某一个SF 出/进 < 1，就单独部署
                do {
                    ServiceFunction serviceFunction = ServiceFunction.serviceFunctionMap.get(chain.get(index));
                    inOutRatio *= serviceFunction.getOutputRate() / serviceFunction.getInputRate();
                    int size = 1000;
                    int pes = 1;
                    int mips = 1000;
                    int queueSize = 128;
                    VirtualTopologyVmSF neededFlavor = new VirtualTopologyVmSF(size, pes, mips, queueSize);
                    serviceFunctionTogether.put(serviceFunction, neededFlavor);
                    index++;
                } while (inOutRatio > 1 && index < chain.size());

                String ingressDc = needSchedule.getIngressDCs().get(0).getDC();
                String selectDc = "";
                // 优先在ingress中尝试部署
                if (isIngressSuitable(ingressDc, serviceFunctionTogether)) {
                    selectDc = ingressDc;
                } else {
                    selectDc = findOtherSuitableDC(serviceFunctionTogether);
                }
                String physicalChainName = needSchedule.getName() + "_psfc";
                for (ServiceFunction serviceFunction : serviceFunctionTogether.keySet()) {
                    String sfInstanceName = physicalChainName + serviceFunction.getName();
                    VirtualTopologyVmSF demandedFlavor = serviceFunctionTogether.get(serviceFunction);
                    nodes.add(new VirtualTopologyVmSF(sfInstanceName, serviceFunction.getName(),
                            demandedFlavor.getSize(), demandedFlavor.getPes(), demandedFlavor.getMips(),
                            demandedFlavor.getQueuesize(), selectDc));
                    monitor.occupyResource(selectDc, 1000, 1, 500, 128);

                }
            }
        }

    }

    public boolean isIngressSuitable(String ingressDc, Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctions) {
        List<Host> hosts = monitor.getRemainingResourcesByDC(ingressDc);
        return isSatisfy(serviceFunctions, hosts);
    }

    public String findOtherSuitableDC(Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctions) {
        Map<String, List<Host>> dcMap = monitor.getRemainingResources();
        for (String dcName : dcMap.keySet()) {
            List<Host> resource = dcMap.get(dcName);
            if (isSatisfy(serviceFunctions, resource)) {
                return dcName;
            }
        }
        return null;
    }

    public boolean isSatisfy(Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctions, List<Host> hosts) {
        // todo 支持更多类型的资源,暂时SF定义里没有找到更多的资源参数
        double mipsNeed = 0, mipsHas = 0;
        for (VirtualTopologyVmSF demandedFlavor : serviceFunctions.values()) {
            // mipsNeed += serviceFunction.getOperationalComplexity();
            mipsNeed += demandedFlavor.getMips() * demandedFlavor.getPes();
        }

        for (Host host : hosts) {
            mipsHas += host.getAvailableMips();
        }

        if (mipsHas < mipsNeed) {
            return false;
        }
        return true;
    }
}
