package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.sfc.parser.InOutDc;
// import org.cloudbus.cloudsim.sfc.monitor.impl.MonitorImpl;
import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunction;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunctionChain;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.VirtualTopologyVmIE;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.VirtualTopologyVmSF;

import java.util.ArrayList;
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
public class StaticScheduler extends DeploymentScheduler {
    // private ServiceFunctionChain needSchedule;
    private StaticMonitor monitor;

    public StaticScheduler(List<ServiceFunctionChain> serverFunctionChains, List<SFCWorkload> sfcWorkloads,
            List<Resource> resources) {
        // super(serverFunctionChains, sfcWorkloads, resources);
        super();
        this.monitor = new StaticMonitor(resources);
        monitor.initiateResource(resources);
        generate(serverFunctionChains, sfcWorkloads, resources);
    }

    @Override
    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);

        scheduleByGreedy(serviceFunctionChains);

    }

    public void placeIngressEgressNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {

        // for (ServiceFunctionChain sfc : serviceFunctionChains) {
        // for (InOutDc inDc : sfc.getIngressDCs()) {
        // System.out.println(inDc.getDC());
        // System.out.println(inDc.getName());
        // }

        // }

        for (ServiceFunctionChain sfc : serviceFunctionChains) {
            for (InOutDc inDc : sfc.getIngressDCs()) {
                nodes.add(new VirtualTopologyVmIE(inDc.getName(), "Ingress", inDc.getDC()));
            }

            nodes.add(new VirtualTopologyVmIE(sfc.getEgressDCs().getName(), "Egress", sfc.getEgressDCs().getDC()));

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
                    ServiceFunction serviceFunction = ServiceFunction.serverFunctionMap.get(chain.get(index));
                    inOutRatio *= serviceFunction.getOutputRate() / serviceFunction.getInputRate();
                    int size = 1000;
                    int pes = 1;
                    int mips = 1000;
                    int queueSize = 128;
                    VirtualTopologyVmSF tryFlavor = new VirtualTopologyVmSF(size, pes, mips, queueSize);
                    serviceFunctionTogether.put(serviceFunction, tryFlavor);
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
                int physicalChainCount = 1;
                String physicalChainName = needSchedule.getName() + "_psfc" + physicalChainCount;
                for (ServiceFunction serviceFunction : serviceFunctionTogether.keySet()) {
                    String sfInstanceName = physicalChainName + serviceFunction.getName();
                    VirtualTopologyVmSF tryFlavor = serviceFunctionTogether.get(serviceFunction);
                    nodes.add(new VirtualTopologyVmSF(sfInstanceName, serviceFunction.getName(), tryFlavor.getSize(),
                            tryFlavor.getPes(), tryFlavor.getMips(), tryFlavor.getQueuesize(), selectDc));
                    monitor.occupyResource(selectDc, 1000, 1, 500, 128);

                }
            }
        }

    }

    public boolean isIngressSuitable(String ingressDc, Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctions) {
        Long remainingMips = monitor.getRemainingResourcesByDC(ingressDc);
        return isSatisfy(serviceFunctions, remainingMips);
    }

    public String findOtherSuitableDC(Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctions) {
        Map<String, Long> dcMap = monitor.getRemainingResources();
        for (String dcName : dcMap.keySet()) {
            Long remainingMips = dcMap.get(dcName);
            if (isSatisfy(serviceFunctions, remainingMips)) {
                return dcName;
            }
        }
        return null;
    }

    public boolean isSatisfy(Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctions, Long remainingMips) {
        // todo 支持更多类型的资源,暂时SF定义里没有找到更多的资源参数
        double mipsNeed = 0, mipsHas = remainingMips;
        for (VirtualTopologyVmSF demandedFlavor : serviceFunctions.values()) {
            // mipsNeed += serviceFunction.getOperationalComplexity();
            mipsNeed += demandedFlavor.getMips() * demandedFlavor.getPes();
        }

        if (mipsHas < mipsNeed) {
            return false;
        }
        return true;
    }
}
