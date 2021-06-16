package org.cloudbus.cloudsim.sfc.parser.configGenerator;

import org.cloudbus.cloudsim.sfc.parser.HomogeneousResourceGroup;
import org.cloudbus.cloudsim.sfc.parser.PhysicalResource;
import org.cloudbus.cloudsim.sfc.parser.Resource;

import java.util.ArrayList;
import java.util.List;

public class CustomPhysicalTopologyGenerator {
    public List<PhysicalTopologyDatacenter> datacenters;
    public List<PhysicalTopologyNode> nodes;
    public List<PhysicalTopologyLink> links;
    public CustomPhysicalTopologyGenerator(){
        datacenters = new ArrayList<>();
        nodes = new ArrayList<>();
        links = new ArrayList<>();
    }

    public void generate(List<Resource> resources){
        generateDatacenter(resources);
        generateNode(resources);
        generateLink();
    }

    private void generateNode(List<Resource> resources){
        generateHostNode(resources);
        generateSwitchNode();
        System.out.println(nodes.size());
    }
    private void generateLink(){
        //test: generate link randomly
        int count = 0;
        double latency = 1.0;
        for(PhysicalTopologyNode node_s : nodes){
            for(PhysicalTopologyNode node_d : nodes){
                if(node_d == node_s){
                    continue;
                }
                links.add(new PhysicalTopologyLink(node_s.name, node_d.name, latency));
                count ++;
                if(count>=50){
                    return;
                }
            }
        }


    }

    private void generateDatacenter(List<Resource> resources){
        for(Resource resource : resources) {
            String name = resource.getName();
            String type = resource.getType();
            datacenters.add(new PhysicalTopologyDatacenter(name, type));
        }
    }

    private void generateHostNode(List<Resource> resources){
        for(Resource resource : resources){
            List<HomogeneousResourceGroup> homogeneousResourceGroups = resource.getHomogeneousResourceGroups();
            for(HomogeneousResourceGroup homogeneousResourceGroup : homogeneousResourceGroups){
                List<PhysicalResource> physicalResources = homogeneousResourceGroup.getPhysicalResources();
                for(int i =0; i < physicalResources.size(); i++){
                    PhysicalResource physicalResource = physicalResources.get(i);
                    int size = physicalResource.getServerNumber();
                    for(int j =0; j< size; j++){
                        String name = resource.getName() + '-' + physicalResource.getName() + '-' + (j+1);
                        String type = "host";
                        String datacenter = resource.getName();
                        //todo need pes config
                        long pes = 1;
                        long mips = physicalResource.getMIPS();
                        long ram = physicalResource.getFastMem();
                        //todo need storage config
                        long storage = physicalResource.getFastMem() * 100;
                        long bw = physicalResource.getBW();
                        nodes.add(new Host(name, type, datacenter, pes, mips, ram, storage, bw));
                    }
                }
            }
        }

    }

    private void generateSwitchNode(){
        String datacenter = "intercloud";
        String name = "gateway";
        String type = "gateway";
        long iops = Long.MAX_VALUE;
        int upports = 1;
        int downports = 2;
        long bw = Long.MAX_VALUE;
        for(int i = 0; i < 20; i++){
            nodes.add(new Switch(name + "-" + (i+1), type, datacenter, iops, upports, downports, bw));
        }

    }


}
