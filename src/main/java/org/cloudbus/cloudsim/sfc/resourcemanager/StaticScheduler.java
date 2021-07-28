package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.sfc.parser.InOutDc;
// import org.cloudbus.cloudsim.sfc.monitor.impl.MonitorImpl;
import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunction;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunctionChain;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.VirtualTopologyLink;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.VirtualTopologyVmIE;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.VirtualTopologyVmSF;
import org.omg.Messaging.SyncScopeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
    private Map<ServiceFunctionChain, List<VirtualTopologyVmSF>> physicalChains = new HashMap<>();

    public StaticScheduler(List<ServiceFunctionChain> serverFunctionChains, List<SFCWorkload> sfcWorkloads,
            List<Resource> resources) {
        // super(serverFunctionChains, sfcWorkloads, resources);
        super();
        this.monitor = new StaticMonitor(resources);
        monitor.initiateResource(resources);
        generate(serverFunctionChains, sfcWorkloads, resources);
    }

    public void generateLinksForScenario1A(List<ServiceFunctionChain> serverFunctionChains) {
        int count = 1;
        for (ServiceFunctionChain serviceFunctionChain : serverFunctionChains)
            for (InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                int count_ingress = 1;
                // for (InOutDc egerss : serviceFunctionChain.getEgressDCs()) {
                // int count_egerss = 1;
                // VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                // ingress.getName() + "-" + egerss.getName(), ingress.getName(),
                // egerss.getName(), 1000);
                // count_egerss++;
                // links.add(virtualTopologyLink);
                // }
                InOutDc egress = serviceFunctionChain.getEgressDCs();
                int requestSize = serviceFunctionChain.getAverageInputSize();

                VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                        ingress.getName() + "-" + egress.getName(), ingress.getName(), egress.getName(), requestSize);
                links.add(virtualTopologyLink);
                count_ingress++;
            }

        for (ServiceFunctionChain sfc : physicalChains.keySet()) {
            List<VirtualTopologyVmSF> serviceFunctions = physicalChains.get(sfc);
            InOutDc ingress = sfc.getIngressDCs().get(0);
            int requestSize = sfc.getAverageInputSize();

            // Ingress to SF1
            VirtualTopologyLink ingressLink = new VirtualTopologyLink(
                    ingress.getName() + "-" + serviceFunctions.get(0).getName(), ingress.getName(),
                    serviceFunctions.get(0).getName(), requestSize);
            links.add(ingressLink);

            // SF1 to SFn
            for (int i = 0; i < serviceFunctions.size() - 1; i++) {
                VirtualTopologyVmSF l = serviceFunctions.get(i);
                VirtualTopologyVmSF r = serviceFunctions.get(i + 1);
                VirtualTopologyLink sf2sf = new VirtualTopologyLink(l.getName() + "-" + r.getName(), l.getName(),
                        r.getName(), requestSize);

                links.add(sf2sf);
                double ratio = ServiceFunction.serverFunctionMap.get(l.getType()).getOutputRate()
                        / ServiceFunction.serverFunctionMap.get(l.getType()).getInputRate();
                System.out.println("============ ratio: =============" + ratio);
                requestSize = requestSize; // 方法1-- link的bandwidth从头到尾都是一致的情况
            }

            // SFn to Egress
            InOutDc egress = sfc.getEgressDCs();
            VirtualTopologyLink egressLink = new VirtualTopologyLink(
                    serviceFunctions.get(serviceFunctions.size() - 1).getName() + "-" + egress.getName(),
                    serviceFunctions.get(serviceFunctions.size() - 1).getName(), egress.getName(), requestSize);
            links.add(egressLink);
        }
    }

    public void generateNodesForScenario1A(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);
        scheduleByGreedy(serviceFunctionChains);
    }

    @Override
    public void generateLinks(List<ServiceFunctionChain> serverFunctionChains) {
        generateLinksForScenario1A(serverFunctionChains);
    }

    @Override
    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        generateNodesForScenario1A(sfcWorkloads, resources, serviceFunctionChains);
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
        // iterate all the SFC demands, once a virtual chain (SFC demand)
        for (ServiceFunctionChain needSchedule : serviceFunctionChains) {
            List<String> chain = needSchedule.getChain();
            int index = 0;

            List<VirtualTopologyVmSF> physicalChain = new LinkedList<>();

            // iterate all the SFs in the chain
            for (; index < chain.size();) {
                Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctionTogether = new LinkedHashMap<>();
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

                    VirtualTopologyVmSF newSF = new VirtualTopologyVmSF(sfInstanceName, serviceFunction.getName(),
                            tryFlavor.getSize(), tryFlavor.getPes(), tryFlavor.getMips(), tryFlavor.getQueuesize(),
                            selectDc);
                    nodes.add(newSF);
                    physicalChain.add(newSF);
                    monitor.occupyResource(selectDc, 1000, 1, 500, 128);
                }
            }
            physicalChains.put(needSchedule, physicalChain);
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
