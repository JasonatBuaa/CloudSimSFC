package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;

import org.cloudbus.cloudsim.sfc.resourcemanager.SchedulingInterface;
import org.cloudbus.cloudsim.sfc.scenariomanager.InOutDc;
import org.cloudbus.cloudsim.sfc.scenariomanager.Resource;
import org.cloudbus.cloudsim.sfc.scenariomanager.SFCWorkload;
import org.cloudbus.cloudsim.sfc.scenariomanager.ServiceFunctionChain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengr
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021-06-30 21:24
 */
// public class CustomVirtualTopologyGenerator {
public class DeploymentScheduler implements SchedulingInterface {
    public List<VirtualTopologyVM> nodes;
    public List<VirtualTopologyLink> links;
    public List<VirtualTopologyPolicy> policies;

    public DeploymentScheduler(List<ServiceFunctionChain> serviceFunctionChains, List<SFCWorkload> sfcWorkloads,
            List<Resource> resources) {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
        policies = new ArrayList<>();
        schedule(serviceFunctionChains, sfcWorkloads, resources);
    }

    public DeploymentScheduler() {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
        policies = new ArrayList<>();
    }

    @Override
    public void schedule(List<ServiceFunctionChain> serviceFunctionChains, List<SFCWorkload> sfcWorkloads,
                         List<Resource> resources) {
        generateNodes(sfcWorkloads, resources, serviceFunctionChains);
        generateLinks(serviceFunctionChains);
        generatePolicies(serviceFunctionChains);
    }

    @Override
    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        // Generate Ingress,Egress
        // String inDc = resources.get(0).getName();
        // String outDc = resources.get(resources.size() - 1).getName();
        // for (SFCWorkload sfcWorkload : sfcWorkloads) {
        // for (InOutDc inOutDc : sfcWorkload.getIngress()) {
        // nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Ingress", inDc));
        // }

        // // for (InOutDc inOutDc : sfcWorkload.getEgress()) {
        // // nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Egress", outDc));
        // // }
        // nodes.add(new VirtualTopologyVmIE(sfcWorkload.getEgress().getName(),
        // "Egress", outDc));
        // }

        // Jason: todo! finish this
        for (ServiceFunctionChain sfc : serviceFunctionChains) {
            for (InOutDc inDc : sfc.getIngressDCs()) {
                System.out.println(inDc.getDC());
                System.out.println(inDc.getName());
            }

        }

        for (ServiceFunctionChain sfc : serviceFunctionChains) {

            List<String> sfs = sfc.getChain();
            int count = 1;
            for (InOutDc ingress : sfc.getIngressDCs()) {
                String physicalChainName = sfc.getName() + "_psfc" + count;
                for (String sf : sfs) {
                    String sfInstanceName = physicalChainName + sf;
                    nodes.add(new VirtualTopologyVmSF(sfInstanceName, sf, 1000, 1, 500, 128));
                }
                count++;
            }
        }

        // Generate serviceFunction
        // for (String sf : ServiceFunction.serviceFunctionMap.keySet()) {
        // // generate two case for each serviceFunction
        // nodes.add(new VirtualTopologyVmSF(sf + "_1", sf, 1000, 1, 500, 128));
        // nodes.add(new VirtualTopologyVmSF(sf + "_2", sf, 1000, 1, 500, 128));
        // }

    }

    @Override
    public void generateLinks(List<ServiceFunctionChain> serviceFunctionChains) {
        for (ServiceFunctionChain serviceFunctionChain : serviceFunctionChains) {
            int count = 1;
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
                InOutDc egress = serviceFunctionChain.getEgressDC();
                int requestSize = serviceFunctionChain.getAverageInputSize();
                // VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                // ingress.getName() + "-" + egress.getName(), ingress.getName(),
                // egress.getName(), 1000);
                VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                        ingress.getName() + "-" + egress.getName(), ingress.getName(), egress.getName(), requestSize);
                links.add(virtualTopologyLink);
                count_ingress++;
            }
        }

    }

    @Override
    public void generatePolicies(List<ServiceFunctionChain> serviceFunctionChains) {
        for (ServiceFunctionChain serviceFunctionChain : serviceFunctionChains) {
            int count = 1;
            for (InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                String physicalChainName = serviceFunctionChain.getName() + "_psfc" + count;
                // for (InOutDc egerss : serviceFunctionChain.getEgressDCs()) {
                // List<String> includedSFs = new ArrayList<>();
                // for (String logicalSF : serviceFunctionChain.getChain()) {
                // String sfInstanceName = physicalChainName + logicalSF;
                // includedSFs.add(sfInstanceName);
                // }

                // VirtualTopologyPolicy virtualTopologyPolicy = new
                // VirtualTopologyPolicy(physicalChainName,
                // ingress.getName(), serviceFunctionChain.getName(), egerss.getName(),
                // ingress.getName() + "-" + egerss.getName(),
                // serviceFunctionChain.getCreateTime(),
                // serviceFunctionChain.getDestroyTime() - serviceFunctionChain.getCreateTime(),
                // includedSFs);
                // policies.add(virtualTopologyPolicy);
                // count++;
                // }
                // Jason : one egress for each SFC
                InOutDc egerss = serviceFunctionChain.getEgressDC();
                List<String> includedSFs = new ArrayList<>();
                for (String logicalSF : serviceFunctionChain.getChain()) {
                    String sfInstanceName = physicalChainName + logicalSF;
                    includedSFs.add(sfInstanceName);
                }

                VirtualTopologyPolicy virtualTopologyPolicy = new VirtualTopologyPolicy(physicalChainName,
                        ingress.getName(), serviceFunctionChain.getName(), egerss.getName(),
                        ingress.getName() + "-" + egerss.getName(), serviceFunctionChain.getCreateTime(),
                        serviceFunctionChain.getDestroyTime() - serviceFunctionChain.getCreateTime(), includedSFs);
                policies.add(virtualTopologyPolicy);

            }
        }

    }
}
