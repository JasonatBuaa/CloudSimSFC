package org.cloudbus.cloudsim.sfc.parser.configGenerator;

import org.cloudbus.cloudsim.sfc.parser.InOutDc;
import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunctionChain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengr
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021-06-30 21:24
 */
// public class CustomVirtualTopologyGenerator {
public class DeploymentScheduler {
    public List<VirtualTopologyVM> nodes;
    public List<VirtualTopologyLink> links;
    public List<VirtualTopologyPolicy> policies;

    public DeploymentScheduler(List<ServiceFunctionChain> serverFunctionChains, List<SFCWorkload> sfcWorkloads,
            List<Resource> resources) {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
        policies = new ArrayList<>();
        generate(serverFunctionChains, sfcWorkloads, resources);
    }

    public void generate(List<ServiceFunctionChain> serverFunctionChains, List<SFCWorkload> sfcWorkloads,
            List<Resource> resources) {
        generateNodes(sfcWorkloads, resources, serverFunctionChains);
        generateLinks(serverFunctionChains);
        generatePolicies(serverFunctionChains);
    }

    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        // Generate Ingress,Egress
        String inDc = resources.get(0).getName();
        String outDc = resources.get(resources.size() - 1).getName();
        for (SFCWorkload sfcWorkload : sfcWorkloads) {
            for (InOutDc inOutDc : sfcWorkload.getIngress()) {
                nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Ingress", inDc));
            }

            for (InOutDc inOutDc : sfcWorkload.getEgress()) {
                nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Egress", outDc));
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
        // for (String sf : ServiceFunction.serverFunctionMap.keySet()) {
        // // generate two case for each serviceFunction
        // nodes.add(new VirtualTopologyVmSF(sf + "_1", sf, 1000, 1, 500, 128));
        // nodes.add(new VirtualTopologyVmSF(sf + "_2", sf, 1000, 1, 500, 128));
        // }

    }

    public void generateLinks(List<ServiceFunctionChain> serverFunctionChains) {
        for (ServiceFunctionChain serviceFunctionChain : serverFunctionChains) {
            int count = 1;
            for (InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                int count_ingress = 1;
                for (InOutDc egerss : serviceFunctionChain.getEgressDCs()) {
                    int count_egerss = 1;
                    VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                            ingress.getName() + "-" + egerss.getName(), ingress.getName(), egerss.getName(), 1000);
                    count_egerss++;
                    links.add(virtualTopologyLink);
                }
                count_ingress++;
            }
        }

    }

    public void generatePolicies(List<ServiceFunctionChain> serverFunctionChains) {
        for (ServiceFunctionChain serviceFunctionChain : serverFunctionChains) {
            int count = 1;
            for (InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                String physicalChainName = serviceFunctionChain.getName() + "_psfc" + count;
                for (InOutDc egerss : serviceFunctionChain.getEgressDCs()) {
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
                    count++;
                }
            }
        }

    }
}
