package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sfc.scenariomanager.InOutDc;
// import org.cloudbus.cloudsim.sfc.monitor.impl.MonitorImpl;
import org.cloudbus.cloudsim.sfc.scenariomanager.Resource;
import org.cloudbus.cloudsim.sfc.scenariomanager.SFCWorkload;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunction;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunctionChain;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.VirtualTopologyLink;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.VirtualTopologyVmIE;
import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.VirtualTopologyVmSF;

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
public class StaticSchedulerScenario4 extends DeploymentScheduler {
    // private ServiceFunctionChain needSchedule;
    private StaticMonitor monitor;
    private Map<ServiceFunctionChain, List<VirtualTopologyVmSF>> physicalChains = new HashMap<>();

    public StaticSchedulerScenario4(List<ServiceFunctionChain> serviceFunctionChains, List<SFCWorkload> sfcWorkloads,
            List<Resource> resources) {
        // super(serviceFunctionChains, sfcWorkloads, resources);
        super();
        this.monitor = new StaticMonitor(resources);
        monitor.initiateResource(resources);
        generate(serviceFunctionChains, sfcWorkloads, resources);
    }

    @Override
    public void generateLinks(List<ServiceFunctionChain> serviceFunctionChains) {
        generateLinksForScenario4_1(serviceFunctionChains);
        // generateLinksForScenario1_2(serviceFunctionChains);
        // generateLinksForScenario1_3(serviceFunctionChains);
        // generateLinksForScenario1_4(serviceFunctionChains);
        // generateLinksForScenario1_5(serviceFunctionChains);
        // generateLinksForScenario1_6(serviceFunctionChains);
    }

    @Override
    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        generateNodesForScenario4_1(sfcWorkloads, resources, serviceFunctionChains);
        // generateNodesForScenario1_2(sfcWorkloads, resources, serviceFunctionChains);
        // generateNodesForScenario1_3(sfcWorkloads, resources, serviceFunctionChains);
        // generateNodesForScenario1_4(sfcWorkloads, resources, serviceFunctionChains);
        // generateNodesForScenario1_5(sfcWorkloads, resources, serviceFunctionChains);
        // generateNodesForScenario1_6(sfcWorkloads, resources, serviceFunctionChains);
    }

    // Jason : Scenario 1-1
    // ```````````````````````````````````````````````````````````````````````````````````````````
    // description: the best
    // Bandwidth :the best greedy scheduling
    // computation: TCAWARE_mipsAWARE cool~

    public void generateLinksForScenario4_1(List<ServiceFunctionChain> serviceFunctionChains) {
        generateLinkTCAware(serviceFunctionChains);

        // generateLinkIngressBW(serviceFunctionChains);
        // generateLinkMaxBW(serviceFunctionChains);

    }

    public void generateNodesForScenario4_1(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);
        TCAwareMIPSAware(serviceFunctionChains);
        // TCAwareMIPSAgnost(serviceFunctionChains);
        // TCAgnostMIPSAware(serviceFunctionChains);
        // TCAgnostMIPSAgnost(serviceFunctionChains);
    }
    // ```````````````````````````````````````````````````````````````````````````````````````````

    // Jason: Scenario 1-2
    // ```````````````````````````````````````````````````````````````````````````````````````````
    // description: ??
    // bandwidth: maxmum the link capacity, thus cost more dollors
    // computation: TCAWARE_mipsAWARE cool~

    public void generateLinksForScenario1_2(List<ServiceFunctionChain> serviceFunctionChains) {
        // generateLinkTCAware(serviceFunctionChains);

        // generateLinkIngressBW(serviceFunctionChains);
        generateLinkMaxBW(serviceFunctionChains);

    }

    public void generateNodesForScenario1_2(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);
        TCAwareMIPSAware(serviceFunctionChains);
        // TCAwareMIPSAgnost(serviceFunctionChains);
        // TCAgnostMIPSAware(serviceFunctionChains);
        // TCAgnostMIPSAgnost(serviceFunctionChains);
    }
    // ```````````````````````````````````````````````````````````````````````````````````````````

    // Jason: Scenario 1-3
    // ```````````````````````````````````````````````````````````````````````````````````````````
    // description:??
    // bandwidth : all links allocate ingress link bandwidth, thus perform poor
    // computation: TCAWARE_mipsAWARE cool~
    public void generateLinksForScenario1_3(List<ServiceFunctionChain> serviceFunctionChains) {
        // generateLinkTCAware(serviceFunctionChains);

        generateLinkIngressBW(serviceFunctionChains);
        // generateLinkMaxBW(serviceFunctionChains);

    }

    public void generateNodesForScenario1_3(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);
        TCAwareMIPSAware(serviceFunctionChains);
        // TCAwareMIPSAgnost(serviceFunctionChains);
        // TCAgnostMIPSAware(serviceFunctionChains);
        // TCAgnostMIPSAgnost(serviceFunctionChains);
    }
    // ```````````````````````````````````````````````````````````````````````````````````````````

    // Jason: Scenario 1-4
    // description: use best bandwidth policy, change the computation resource
    // allocation policy
    // bandwidth : allocate the best choice
    // computation: TCAwareMIPSAgnost
    // ```````````````````````````````````````````````````````````````````````````````````````````
    public void generateLinksForScenario1_4(List<ServiceFunctionChain> serviceFunctionChains) {
        generateLinkTCAware(serviceFunctionChains);
        // generateLinkIngressBW(serviceFunctionChains);
        // generateLinkMaxBW(serviceFunctionChains);

    }

    public void generateNodesForScenario1_4(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);
        // TCAwareMIPSAware(serviceFunctionChains);
        TCAwareMIPSAgnost(serviceFunctionChains);
        // TCAgnostMIPSAware(serviceFunctionChains);
        // TCAgnostMIPSAgnost(serviceFunctionChains);
    }
    // ```````````````````````````````````````````````````````````````````````````````````````````

    // Jason: Scenario 1-5
    // description: use best bandwidth policy, change the computation resource
    // allocation policy
    // bandwidth : allocate the best choice
    // computation: TCAgnostMIPSAware
    // ```````````````````````````````````````````````````````````````````````````````````````````
    public void generateLinksForScenario1_5(List<ServiceFunctionChain> serviceFunctionChains) {
        generateLinkTCAware(serviceFunctionChains);
        // generateLinkIngressBW(serviceFunctionChains);
        // generateLinkMaxBW(serviceFunctionChains);

    }

    public void generateNodesForScenario1_5(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);
        // TCAwareMIPSAware(serviceFunctionChains);
        // TCAwareMIPSAgnost(serviceFunctionChains);
        TCAgnostMIPSAware(serviceFunctionChains);
        // TCAgnostMIPSAgnost(serviceFunctionChains);
    }
    // ```````````````````````````````````````````````````````````````````````````````````````````

    // Jason: Scenario 1-6
    // description: use best bandwidth policy, change the computation resource
    // allocation policy
    // bandwidth : allocate the best choice
    // computation: All agnostic
    // ```````````````````````````````````````````````````````````````````````````````````````````
    public void generateLinksForScenario1_6(List<ServiceFunctionChain> serviceFunctionChains) {
        generateLinkTCAware(serviceFunctionChains);
        // generateLinkIngressBW(serviceFunctionChains);
        // generateLinkMaxBW(serviceFunctionChains);

    }

    public void generateNodesForScenario1_6(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        placeIngressEgressNodes(sfcWorkloads, resources, serviceFunctionChains);
        // TCAwareMIPSAware(serviceFunctionChains);
        // TCAwareMIPSAgnost(serviceFunctionChains);
        // TCAgnostMIPSAware(serviceFunctionChains);
        TCAgnostMIPSAgnost(serviceFunctionChains);
    }
    // ```````````````````````````````````````````````````````````````````````````````````````````

    /**
     * Jason: complete
     * 
     * @param serviceFunctionChains
     */
    public void generateLinkTCAware(List<ServiceFunctionChain> serviceFunctionChains) {
        int count = 1;
        for (ServiceFunctionChain serviceFunctionChain : serviceFunctionChains)
            for (InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                int count_ingress = 1;

                InOutDc egress = serviceFunctionChain.getEgressDC();
                int requestSize = serviceFunctionChain.getAverageInputSize();

                VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                        ingress.getName() + "-" + egress.getName(), ingress.getName(), egress.getName(),
                        (int) (requestSize * 1.2) + 1);
                links.add(virtualTopologyLink);
                count_ingress++;
            }

        for (ServiceFunctionChain sfc : physicalChains.keySet()) {
            List<VirtualTopologyVmSF> serviceFunctions = physicalChains.get(sfc);
            InOutDc ingress = sfc.getIngressDCs().get(0);

            double requestSizeAccu = sfc.getAverageInputSize();
            int bwDemand = sfc.getAverageInputSize() * (int) Configuration.NETWORK_PACKET_SIZE_MULTIPLY;

            VirtualTopologyVmSF firstSF = serviceFunctions.get(0);

            // Ingress to SF1
            VirtualTopologyLink ingressLink = new VirtualTopologyLink(ingress.getName() + "-" + firstSF.getName(),
                    ingress.getName(), firstSF.getName(), (int) (bwDemand * 1.2) + 1);
            links.add(ingressLink);
            requestSizeAccu *= (ServiceFunction.serviceFunctionMap.get(firstSF.getType()).getOutputRate()
                    / ServiceFunction.serviceFunctionMap.get(firstSF.getType()).getInputRate());

            // SF1 to SFn
            for (int i = 0; i < serviceFunctions.size() - 1; i++) {
                bwDemand = ((int) requestSizeAccu + 1) * (int) Configuration.NETWORK_PACKET_SIZE_MULTIPLY;
                VirtualTopologyVmSF l = serviceFunctions.get(i);
                VirtualTopologyVmSF r = serviceFunctions.get(i + 1);
                VirtualTopologyLink sf2sf = new VirtualTopologyLink(l.getName() + "-" + r.getName(), l.getName(),
                        r.getName(), (int) (bwDemand * 1.2 + 1));

                links.add(sf2sf);
                double ratio = ServiceFunction.serviceFunctionMap.get(r.getType()).getOutputRate()
                        / ServiceFunction.serviceFunctionMap.get(r.getType()).getInputRate();
                System.out.println("============ ratio: =============" + ratio);
                requestSizeAccu *= ratio; // 方法2-- link的bandwidth从头到尾是变化的
            }

            bwDemand = ((int) requestSizeAccu + 1) * (int) Configuration.NETWORK_PACKET_SIZE_MULTIPLY;

            // SFn to Egress
            InOutDc egress = sfc.getEgressDC();
            VirtualTopologyLink egressLink = new VirtualTopologyLink(
                    serviceFunctions.get(serviceFunctions.size() - 1).getName() + "-" + egress.getName(),
                    serviceFunctions.get(serviceFunctions.size() - 1).getName(), egress.getName(),
                    (int) (bwDemand * 1.2 + 1));
            links.add(egressLink);
        }
    }

    /**
     * Jason: complete
     * 
     * @param serviceFunctionChains
     */
    public void generateLinkIngressBW(List<ServiceFunctionChain> serviceFunctionChains) {

        // Use ingress bandwidth as the bandwidth
        int count = 1;
        int bwDemand = 1;
        for (ServiceFunctionChain serviceFunctionChain : serviceFunctionChains)
            for (InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                int count_ingress = 1;

                InOutDc egress = serviceFunctionChain.getEgressDC();
                int requestSize = serviceFunctionChain.getAverageInputSize();

                bwDemand = requestSize * (int) Configuration.NETWORK_PACKET_SIZE_MULTIPLY;
                VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                        ingress.getName() + "-" + egress.getName(), ingress.getName(), egress.getName(), bwDemand);
                links.add(virtualTopologyLink);
                count_ingress++;
            }

        for (ServiceFunctionChain sfc : physicalChains.keySet()) {
            List<VirtualTopologyVmSF> serviceFunctions = physicalChains.get(sfc);
            InOutDc ingress = sfc.getIngressDCs().get(0);
            int requestSize = sfc.getAverageInputSize();
            bwDemand = requestSize * (int) Configuration.NETWORK_PACKET_SIZE_MULTIPLY;

            // Ingress to SF1
            VirtualTopologyLink ingressLink = new VirtualTopologyLink(
                    ingress.getName() + "-" + serviceFunctions.get(0).getName(), ingress.getName(),
                    serviceFunctions.get(0).getName(), bwDemand);
            links.add(ingressLink);

            // SF1 to SFn
            for (int i = 0; i < serviceFunctions.size() - 1; i++) {
                VirtualTopologyVmSF l = serviceFunctions.get(i);
                VirtualTopologyVmSF r = serviceFunctions.get(i + 1);

                VirtualTopologyLink sf2sf = new VirtualTopologyLink(l.getName() + "-" + r.getName(), l.getName(),
                        r.getName(), bwDemand);

                links.add(sf2sf);
                double ratio = ServiceFunction.serviceFunctionMap.get(l.getType()).getOutputRate()
                        / ServiceFunction.serviceFunctionMap.get(l.getType()).getInputRate();
                System.out.println("============ ratio: =============" + ratio);
                // requestSize = requestSize; // 方法1-- link的bandwidth从头到尾都是一致的情况
            }

            // SFn to Egress
            InOutDc egress = sfc.getEgressDC();
            VirtualTopologyLink egressLink = new VirtualTopologyLink(
                    serviceFunctions.get(serviceFunctions.size() - 1).getName() + "-" + egress.getName(),
                    serviceFunctions.get(serviceFunctions.size() - 1).getName(), egress.getName(), bwDemand);
            links.add(egressLink);
        }
    }

    /**
     * Jason: complete
     * 
     * @param serviceFunctionChains
     */
    public void generateLinkMaxBW(List<ServiceFunctionChain> serviceFunctionChains) {
        // Use the max bandwidth requirement in the chain as the bandwidth

        for (ServiceFunctionChain serviceFunctionChain : serviceFunctionChains)
            for (InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                int count_ingress = 1;

                InOutDc egress = serviceFunctionChain.getEgressDC();
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

            double maxRatio = 1;
            double tempRatio = 1;
            int maxBw = requestSize;
            for (VirtualTopologyVmSF curSf : serviceFunctions) {
                double curRatio = ServiceFunction.serviceFunctionMap.get(curSf.getType()).getOutputRate()
                        / ServiceFunction.serviceFunctionMap.get(curSf.getType()).getInputRate();
                tempRatio *= curRatio;
                if (tempRatio > maxRatio)
                    maxRatio = tempRatio;
            }
            maxBw *= maxRatio;

            // Ingress to SF1
            int bwDemand = maxBw * (int) Configuration.NETWORK_PACKET_SIZE_MULTIPLY;
            VirtualTopologyLink ingressLink = new VirtualTopologyLink(
                    ingress.getName() + "-" + serviceFunctions.get(0).getName(), ingress.getName(),
                    serviceFunctions.get(0).getName(), bwDemand);
            links.add(ingressLink);

            // SF1 to SFn
            for (int i = 0; i < serviceFunctions.size() - 1; i++) {
                VirtualTopologyVmSF l = serviceFunctions.get(i);
                VirtualTopologyVmSF r = serviceFunctions.get(i + 1);
                VirtualTopologyLink sf2sf = new VirtualTopologyLink(l.getName() + "-" + r.getName(), l.getName(),
                        r.getName(), bwDemand);
                links.add(sf2sf);
            }

            // SFn to Egress
            InOutDc egress = sfc.getEgressDC();
            VirtualTopologyLink egressLink = new VirtualTopologyLink(
                    serviceFunctions.get(serviceFunctions.size() - 1).getName() + "-" + egress.getName(),
                    serviceFunctions.get(serviceFunctions.size() - 1).getName(), egress.getName(), bwDemand);
            links.add(egressLink);
        }
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

            nodes.add(new VirtualTopologyVmIE(sfc.getEgressDC().getName(), "Egress", sfc.getEgressDC().getDC()));

        }

    }

    /**
     * Best solution
     * 
     * @param serviceFunctionChains
     */
    public void TCAwareMIPSAware(List<ServiceFunctionChain> serviceFunctionChains) {

        // iterate all the SFC demands, once a virtual chain (SFC demand)
        for (ServiceFunctionChain needSchedule : serviceFunctionChains) {
            List<String> chain = needSchedule.getChain();
            int index = 0;

            List<VirtualTopologyVmSF> physicalChain = new LinkedList<>();

            int avarageInputSize = needSchedule.getAverageInputSize();

            double inOutRatioCurrent = 1.0;
            double inOutRatioPrevious = 1.0;

            boolean headOfChain = true;
            // iterate all the SFs in the chain
            for (; index < chain.size();) {

                Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctionTogether = new LinkedHashMap<>();
                inOutRatioPrevious *= inOutRatioCurrent;

                inOutRatioCurrent = 1.0;
                // 0. 确定资源配额
                // 如果chain的某一个SF 出/进>1,就继续循环,找下一个捆绑部署,直到 出/进 < 1;如果某一个SF 出/进 < 1，就单独部署
                do {
                    ServiceFunction serviceFunction = ServiceFunction.serviceFunctionMap.get(chain.get(index));
                    int size = 1000;
                    int pes = 0;
                    int mipsPerPe = 1000;
                    int cpx = serviceFunction.getOperationalComplexity();
                    int totalReqMips = (int) (1.2 * avarageInputSize * inOutRatioPrevious * inOutRatioCurrent * cpx);

                    pes = totalReqMips / mipsPerPe + 1; // e.g., totalReqMips = 600, mipsPerPe = 1000. pes = 1;

                    mipsPerPe = totalReqMips / pes;

                    int queueSize = 128;
                    VirtualTopologyVmSF tryFlavor = new VirtualTopologyVmSF(size, pes, mipsPerPe, queueSize);
                    serviceFunctionTogether.put(serviceFunction, tryFlavor);
                    inOutRatioCurrent *= serviceFunction.getOutputRate() / serviceFunction.getInputRate();
                    index++;
                } while (inOutRatioCurrent > 1 && index < chain.size());
                String ingressDc;
                if (headOfChain == true && inOutRatioCurrent < 1)
                    ingressDc = needSchedule.getIngressDCs().get(0).getDC();
                else
                    ingressDc = needSchedule.getEgressDC().getDC();
                headOfChain = false;
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

                    int debugPes = tryFlavor.getPes();
                    int debugMips = tryFlavor.getMips();
                    int debugQueueSize = tryFlavor.getQueuesize();

                    VirtualTopologyVmSF newSF = new VirtualTopologyVmSF(sfInstanceName, serviceFunction.getName(),
                            tryFlavor.getSize(), tryFlavor.getPes(), tryFlavor.getMips(), tryFlavor.getQueuesize(),
                            selectDc);
                    nodes.add(newSF);
                    physicalChain.add(newSF);
                    monitor.occupyResource(selectDc, tryFlavor.getSize(), tryFlavor.getPes(), tryFlavor.getMips(), 128);
                }
            }
            physicalChains.put(needSchedule, physicalChain);
        }

    }

    /**
     * Jason: CloudOnly: completed
     * 
     * @param serviceFunctionChains
     */
    public void TCAgnostMIPSAware(List<ServiceFunctionChain> serviceFunctionChains) {

        // iterate all the SFC demands, once a virtual chain (SFC demand)
        for (ServiceFunctionChain needSchedule : serviceFunctionChains) {
            List<String> chain = needSchedule.getChain();
            int index = 0;

            List<VirtualTopologyVmSF> physicalChain = new LinkedList<>();

            int avarageInputSize = needSchedule.getAverageInputSize();

            // iterate all the SFs in the chain
            for (index = 0; index < chain.size(); index++) {
                String cloudDC = needSchedule.getEgressDC().getDC();
                ServiceFunction serviceFunction = ServiceFunction.serviceFunctionMap.get(chain.get(index));
                int size = 1000;
                int pes = 0;
                int mipsPerPe = 1000;
                int queueSize = 128;

                int cpx = serviceFunction.getOperationalComplexity();
                int totalReqMips = (int) (avarageInputSize * cpx);
                pes = totalReqMips / mipsPerPe + 1; // e.g., totalReqMips = 600, mipsPerPe = 1000. pes = 1;
                mipsPerPe = totalReqMips / pes;

                int physicalChainCount = 1;
                String physicalChainName = needSchedule.getName() + "_psfc" + physicalChainCount;
                String sfInstanceName = physicalChainName + serviceFunction.getName();
                VirtualTopologyVmSF newSF = new VirtualTopologyVmSF(sfInstanceName, serviceFunction.getName(), size,
                        pes, mipsPerPe, queueSize, cloudDC);
                // serviceFunctionTogether.put(serviceFunction, trysFlavor);
                physicalChain.add(newSF);
                nodes.add(newSF);
            }
            physicalChains.put(needSchedule, physicalChain);
        }
    }

    /**
     * Jason: Cloud-Edge
     * 
     * @param serviceFunctionChains
     */
    public void TCAwareMIPSAgnost(List<ServiceFunctionChain> serviceFunctionChains) {

        // iterate all the SFC demands, once a virtual chain (SFC demand)
        for (ServiceFunctionChain needSchedule : serviceFunctionChains) {
            List<String> chain = needSchedule.getChain();
            int index = 0;

            List<VirtualTopologyVmSF> physicalChain = new LinkedList<>();

            int avarageInputSize = needSchedule.getAverageInputSize();

            double inOutRatioCurrent = 1.0;
            boolean headOfChain = true;

            // iterate all the SFs in the chain
            for (; index < chain.size();) {

                Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctionTogether = new LinkedHashMap<>();

                inOutRatioCurrent = 1.0;
                // 0. 确定资源配额
                // 如果chain的某一个SF 出/进>1,就继续循环,找下一个捆绑部署,直到 出/进 < 1;如果某一个SF 出/进 < 1，就单独部署
                do {
                    ServiceFunction serviceFunction = ServiceFunction.serviceFunctionMap.get(chain.get(index));
                    int size = 1000;
                    int pes = 1;
                    int mips = 1000;
                    int queueSize = 128;
                    VirtualTopologyVmSF tryFlavor = new VirtualTopologyVmSF(size, pes, mips, queueSize);
                    serviceFunctionTogether.put(serviceFunction, tryFlavor);
                    inOutRatioCurrent *= serviceFunction.getOutputRate() / serviceFunction.getInputRate();
                    index++;
                } while (inOutRatioCurrent > 1 && index < chain.size());
                String ingressDc;
                if (headOfChain == true)
                    ingressDc = needSchedule.getIngressDCs().get(0).getDC();
                else
                    ingressDc = needSchedule.getEgressDC().getDC();
                headOfChain = false;

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
                    monitor.occupyResource(selectDc, tryFlavor.getSize(), tryFlavor.getPes(), tryFlavor.getMips(), 128);
                }
            }
            physicalChains.put(needSchedule, physicalChain);
        }
    }

    /**
     * Jason: CloudOnly, MIPS = 1000/10 * averageInputSize: completed
     * 
     * @param serviceFunctionChains
     */
    public void TCAgnostMIPSAgnost(List<ServiceFunctionChain> serviceFunctionChains) {

        // iterate all the SFC demands, once a virtual chain (SFC demand)
        for (ServiceFunctionChain needSchedule : serviceFunctionChains) {
            List<String> chain = needSchedule.getChain();
            int index = 0;

            List<VirtualTopologyVmSF> physicalChain = new LinkedList<>();

            int avarageInputSize = needSchedule.getAverageInputSize();

            // iterate all the SFs in the chain
            for (index = 0; index < chain.size(); index++) {
                String cloudDC = needSchedule.getEgressDC().getDC();
                ServiceFunction serviceFunction = ServiceFunction.serviceFunctionMap.get(chain.get(index));
                int size = 1000;
                int pes = 1;
                int mipsPerPe = 1000 / 10 * avarageInputSize;
                int queueSize = 128;

                int physicalChainCount = 1;
                String physicalChainName = needSchedule.getName() + "_psfc" + physicalChainCount;
                String sfInstanceName = physicalChainName + serviceFunction.getName();
                VirtualTopologyVmSF newSF = new VirtualTopologyVmSF(sfInstanceName, serviceFunction.getName(), size,
                        pes, mipsPerPe, queueSize, cloudDC);
                // serviceFunctionTogether.put(serviceFunction, trysFlavor);
                physicalChain.add(newSF);
                nodes.add(newSF);
            }
            physicalChains.put(needSchedule, physicalChain);
        }
    }

    public void balabala(List<ServiceFunctionChain> serviceFunctionChains) {

        // iterate all the SFC demands, once a virtual chain (SFC demand)
        for (ServiceFunctionChain needSchedule : serviceFunctionChains) {
            List<String> chain = needSchedule.getChain();
            int index = 0;

            List<VirtualTopologyVmSF> physicalChain = new LinkedList<>();

            int avarageInputSize = needSchedule.getAverageInputSize();

            double inOutRatioCurrent = 1.0;

            // iterate all the SFs in the chain
            for (; index < chain.size();) {

                Map<ServiceFunction, VirtualTopologyVmSF> serviceFunctionTogether = new LinkedHashMap<>();

                inOutRatioCurrent = 1.0;
                // 0. 确定资源配额
                // 如果chain的某一个SF 出/进>1,就继续循环,找下一个捆绑部署,直到 出/进 < 1;如果某一个SF 出/进 < 1，就单独部署
                do {
                    ServiceFunction serviceFunction = ServiceFunction.serviceFunctionMap.get(chain.get(index));
                    int size = 1000;
                    int pes = 1;
                    int mips = 1000;
                    int queueSize = 128;
                    VirtualTopologyVmSF tryFlavor = new VirtualTopologyVmSF(size, pes, mips, queueSize);
                    serviceFunctionTogether.put(serviceFunction, tryFlavor);
                    inOutRatioCurrent *= serviceFunction.getOutputRate() / serviceFunction.getInputRate();
                    index++;
                } while (inOutRatioCurrent > 1 && index < chain.size());

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
                    monitor.occupyResource(selectDc, tryFlavor.getSize(), tryFlavor.getPes(), tryFlavor.getMips(), 128);
                }
            }
            physicalChains.put(needSchedule, physicalChain);
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
                    ServiceFunction serviceFunction = ServiceFunction.serviceFunctionMap.get(chain.get(index));
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
