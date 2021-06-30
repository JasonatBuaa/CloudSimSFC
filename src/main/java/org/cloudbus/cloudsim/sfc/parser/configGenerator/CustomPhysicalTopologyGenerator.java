package org.cloudbus.cloudsim.sfc.parser.configGenerator;

import org.cloudbus.cloudsim.sfc.parser.HomogeneousResourceGroup;
import org.cloudbus.cloudsim.sfc.parser.PhysicalResource;
import org.cloudbus.cloudsim.sfc.parser.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomPhysicalTopologyGenerator {
    public List<PhysicalTopologyDatacenter> datacenters;
    public List<PhysicalTopologyNode> nodes;
    public List<PhysicalTopologyLink> links;
    private HashMap<String,List<Host>> nodeMap;
    private HashMap<String,Resource> resourceHashMap;
    private HashMap<String,Switch> CoreSwitch;
    private HashMap<String,Switch> DCSwitch;
    private Switch GlobalSwitch;

    public CustomPhysicalTopologyGenerator(){
        datacenters = new ArrayList<>();
        nodes = new ArrayList<>();
        links = new ArrayList<>();
        nodeMap = new HashMap<>();
        resourceHashMap = new HashMap<>();
        CoreSwitch = new HashMap<>();
        DCSwitch = new HashMap<>();
    }

    public void generate(List<Resource> resources){
        generateDatacenter(resources);
        generateNode(resources);
        generateLink(resources);
    }

    private void generateNode(List<Resource> resources){
        generateHostNode(resources);
        generateSwitchNode(resources);
        System.out.println(nodes.size());
    }
    private void generateLink(List<Resource> resources){
        double latency = 1.0;
        // link gatewaySwitch and interCloudSwitch
        for(String  dc : DCSwitch.keySet()){
            Switch sw = DCSwitch.get(dc);
            links.add(new PhysicalTopologyLink(sw.name,GlobalSwitch.name,latency));
            links.add(new PhysicalTopologyLink(GlobalSwitch.name,sw.name,latency));

            //link coreSwitch and gatewaySwitch
            for(HomogeneousResourceGroup homogeneousResourceGroup : resourceHashMap.get(dc).getHomogeneousResourceGroups()){
                Switch core = CoreSwitch.get(homogeneousResourceGroup.getName());
                links.add(new PhysicalTopologyLink(core.name,sw.name,latency));
                links.add(new PhysicalTopologyLink(sw.name,core.name,latency));

                //link server and coreSwitch
                for(Host host : nodeMap.get(homogeneousResourceGroup.getName())){
                    links.add(new PhysicalTopologyLink(host.name,core.name,latency));
                    links.add(new PhysicalTopologyLink(core.name,host.name,latency));
                }
            }
        }

        System.out.println(links.size());
    }

    private void generateDatacenter(List<Resource> resources){
        for(Resource resource : resources) {
            String name = resource.getName();
            String type = resource.getType();
            PhysicalTopologyDatacenter physicalTopologyDatacenter = new PhysicalTopologyDatacenter(name, type);
            datacenters.add(physicalTopologyDatacenter);
            resourceHashMap.put(name,resource);
        }
    }

    private void generateHostNode(List<Resource> resources){
        for(Resource resource : resources){
            List<HomogeneousResourceGroup> homogeneousResourceGroups = resource.getHomogeneousResourceGroups();
            for(HomogeneousResourceGroup homogeneousResourceGroup : homogeneousResourceGroups){
                nodeMap.put(homogeneousResourceGroup.getName(),new ArrayList<Host>());
            }

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
                        Host newHost = new Host(name, type, datacenter, pes, mips, ram, storage, bw);
                        nodes.add(newHost);
                        nodeMap.get(homogeneousResourceGroup.getName()).add(newHost);
                    }
                }
            }
        }
    }

    /**
     * @description generate Switch for HomogeneousResourceGroup and DataCenter
     * @author chengr
     * @updateTime 2021/6/28 19:21
     */
    private void generateSwitchNode(List<Resource> resources){
        String datacenter, name, type;
        long iops = Long.MAX_VALUE;
        int upports = 1;
        int downports = 2;
        long bw = Long.MAX_VALUE;
        //Generate global unique InterCloud Switch
        datacenter = "Global";
        name = "InterCloud";
        type = "intercloud";
        GlobalSwitch = new Switch(name, type, datacenter, iops, upports, downports, bw);
        nodes.add(GlobalSwitch);

        Switch newSwitch;
        for(Resource resource : resources){
            //Generate gateway Switch for DataCenter
            datacenter = resource.getName();
            name = datacenter + "-gateway";
            type = "gateway";
            newSwitch = new Switch(name, type, datacenter, iops, upports, downports, bw);
            nodes.add(newSwitch);
            DCSwitch.put(datacenter, newSwitch);
            for(int i = 0; i<resource.getHomogeneousResourceGroups().size(); i++){
                HomogeneousResourceGroup homogeneousResourceGroup = resource.getHomogeneousResourceGroups().get(i);
                datacenter = resource.getName();
                name = datacenter + '-' + homogeneousResourceGroup.getName() + "-core" + (i+1);
                type = "core";
                newSwitch = new Switch(name, type, datacenter, iops, upports, downports, bw);
                nodes.add(newSwitch);
                CoreSwitch.put(homogeneousResourceGroup.getName(), newSwitch);

            }
        }
    }


}
