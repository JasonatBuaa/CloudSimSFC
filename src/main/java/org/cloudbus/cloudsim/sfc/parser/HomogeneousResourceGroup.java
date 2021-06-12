package org.cloudbus.cloudsim.sfc.parser;

import java.util.List;

public class HomogeneousResourceGroup {
    private String Name;
    private List<PhysicalResource> PhysicalResources;

    public HomogeneousResourceGroup(String name, List<PhysicalResource> physicalResources) {
        Name = name;
        PhysicalResources = physicalResources;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<PhysicalResource> getPhysicalResources() {
        return PhysicalResources;
    }

    public void setPhysicalResources(List<PhysicalResource> physicalResources) {
        PhysicalResources = physicalResources;
    }

    @Override
    public String toString() {
        String resource = "";
        for(PhysicalResource physicalResource : PhysicalResources){
            resource += physicalResource.toString() + ',';
        }
        return "HomogeneousResourceGroup[" +
                resource + ']';
    }
}
