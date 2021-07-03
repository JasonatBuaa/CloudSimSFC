package org.cloudbus.cloudsim.sfc.parser.configGenerator;

import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunction;
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
        generateNodes(sfcWorkloads, resources);
        generateLinks(serverFunctionChains);
        generatePolicies(serverFunctionChains);
    }

    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources) {
        // Generate Ingress,Egress
        String inDc = resources.get(0).getName();
        String outDc = resources.get(resources.size() - 1).getName();
        for (SFCWorkload sfcWorkload : sfcWorkloads) {
            for (ServiceFunctionChain.InOutDc inOutDc : sfcWorkload.getIngress()) {
                nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Ingress", inDc));
            }

            for (ServiceFunctionChain.InOutDc inOutDc : sfcWorkload.getEgress()) {
                nodes.add(new VirtualTopologyVmIE(inOutDc.getName(), "Egress", outDc));
            }
        }

        // Generate serviceFunction
        for (String sf : ServiceFunction.serverFunctionMap.keySet()) {
            // generate two case for each serviceFunction
            nodes.add(new VirtualTopologyVmSF(sf + "_1", sf, 1000, 1, 500, 128));
            nodes.add(new VirtualTopologyVmSF(sf + "_2", sf, 1000, 1, 500, 128));
        }
    }

    public void generateLinks(List<ServiceFunctionChain> serverFunctionChains) {
        for (ServiceFunctionChain serviceFunctionChain : serverFunctionChains) {
            int count = 1;
            for (ServiceFunctionChain.InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                int count_ingress = 1;
                for (ServiceFunctionChain.InOutDc egerss : serviceFunctionChain.getEgressDCs()) {
                    int count_egerss = 1;
                    VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                            ingress.getName() + "-" + egerss.getName(), ingress.getName(), egerss.getName(), 1000);
                    count_egerss++;
                }
                count_ingress++;
            }
        }

    }

    public void generatePolicies(List<ServiceFunctionChain> serverFunctionChains) {
        for (ServiceFunctionChain serviceFunctionChain : serverFunctionChains) {
            int count = 1;
            for (ServiceFunctionChain.InOutDc ingress : serviceFunctionChain.getIngressDCs()) {
                for (ServiceFunctionChain.InOutDc egerss : serviceFunctionChain.getEgressDCs()) {
                    VirtualTopologyPolicy virtualTopologyPolicy = new VirtualTopologyPolicy(
                            serviceFunctionChain.getName() + "_" + count, ingress.getName(),
                            serviceFunctionChain.getName(), egerss.getName(),
                            ingress.getName() + "-" + egerss.getName(), serviceFunctionChain.getCreateTime(),
                            serviceFunctionChain.getDestroyTime() - serviceFunctionChain.getCreateTime(),
                            serviceFunctionChain.getChain());
                    policies.add(virtualTopologyPolicy);
                    count++;
                }
            }
        }

    }
}