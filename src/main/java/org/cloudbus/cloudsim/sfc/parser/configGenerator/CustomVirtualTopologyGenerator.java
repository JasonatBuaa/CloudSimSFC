package org.cloudbus.cloudsim.sfc.parser.configGenerator;

import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServerFunction;
import org.cloudbus.cloudsim.sfc.parser.ServerFunctionChain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengr
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021-06-30 21:24
 */
public class CustomVirtualTopologyGenerator {
    public List<VirtualTopologyVM> nodes;
    public List<VirtualTopologyLink> links;
    public List<VirtualTopologyPolicy> policies;

    public CustomVirtualTopologyGenerator(List<ServerFunctionChain> serverFunctionChains, List<SFCWorkload> sfcWorkloads, List<Resource> resources){
        nodes = new ArrayList<>();
        links = new ArrayList<>();
        policies = new ArrayList<>();
        generate(serverFunctionChains, sfcWorkloads, resources);
    }

    public void generate(List<ServerFunctionChain> serverFunctionChains, List<SFCWorkload> sfcWorkloads, List<Resource> resources){
        generateNodes(sfcWorkloads, resources);
        generateLinks(serverFunctionChains);
        generatePolicies(serverFunctionChains);
    }

    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources){
        // Generate Ingress,Egress
        String inDc = resources.get(0).getName();
        String outDc = resources.get(resources.size()-1).getName();
        for(SFCWorkload sfcWorkload : sfcWorkloads){
            for(ServerFunctionChain.InOutDc inOutDc : sfcWorkload.getIngress()){
                nodes.add(new VirtualTopologyVmIE(inOutDc.getName(),"Ingress", inDc));
            }

            for(ServerFunctionChain.InOutDc inOutDc : sfcWorkload.getEgress()){
                nodes.add(new VirtualTopologyVmIE(inOutDc.getName(),"Egress", outDc));
            }
        }


        // Generate serverFunction
        for(String sf : ServerFunction.serverFunctionMap.keySet()){
            // generate two case for each serverFunction
            nodes.add(new VirtualTopologyVmSF(
                    sf+"_1", sf,1000,1,500,128
            ));
            nodes.add(new VirtualTopologyVmSF(
                    sf+"_2",sf,1000,1,500,128
            ));
        }
    }

    public void generateLinks(List<ServerFunctionChain> serverFunctionChains){
        for(ServerFunctionChain serverFunctionChain : serverFunctionChains) {
            int count = 1;
            for (ServerFunctionChain.InOutDc ingress : serverFunctionChain.getIngressDCs()) {
                int count_ingress = 1;
                for (ServerFunctionChain.InOutDc egerss : serverFunctionChain.getEgressDCs()) {
                    int count_egerss = 1;
                    VirtualTopologyLink virtualTopologyLink = new VirtualTopologyLink(
                            ingress.getName()+"-"+egerss.getName(),
                            ingress.getName(),
                            egerss.getName(),
                            1000
                    );
                    count_egerss++;
                }
                count_ingress++;
            }
        }

    }

    public void generatePolicies(List<ServerFunctionChain> serverFunctionChains){
        for(ServerFunctionChain serverFunctionChain : serverFunctionChains){
            int count = 1;
            for(ServerFunctionChain.InOutDc ingress : serverFunctionChain.getIngressDCs()){
                for(ServerFunctionChain.InOutDc egerss : serverFunctionChain.getEgressDCs()){
                    VirtualTopologyPolicy virtualTopologyPolicy = new VirtualTopologyPolicy(
                            serverFunctionChain.getName()+"_"+count, ingress.getName(), serverFunctionChain.getName(), egerss.getName(),
                            ingress.getName()+"-"+egerss.getName(),serverFunctionChain.getCreateTime(),
                            serverFunctionChain.getDestroyTime()- serverFunctionChain.getCreateTime(),
                            serverFunctionChain.getChain()
                    );
                    policies.add(virtualTopologyPolicy);
                    count++;
                }
            }
        }


    }
}
