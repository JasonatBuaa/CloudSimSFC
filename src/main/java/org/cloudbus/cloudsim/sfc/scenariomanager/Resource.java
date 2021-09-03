package org.cloudbus.cloudsim.sfc.scenariomanager;

import java.util.List;

/**
 * ResourceParser parser resource description file for CloudSimSFC
 *
 * @author Rui Cheng
 * @date 2021/6/12
 */
public class Resource {
    private String Type;
    private String Name;
    private int InterCloudBW;
    private List<HomogeneousResourceGroup> HomogeneousResourceGroups;

    public Resource(String type, String name, int interCloudBW, List<HomogeneousResourceGroup> homogeneousResourceGroups) {
        Type = type;
        Name = name;
        InterCloudBW = interCloudBW;
        HomogeneousResourceGroups = homogeneousResourceGroups;
    }

    public int getInterCloudBW() {
        return InterCloudBW;
    }

    public void setInterCloudBW(int interCloudBW) {
        InterCloudBW = interCloudBW;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<HomogeneousResourceGroup> getHomogeneousResourceGroups() {
        return HomogeneousResourceGroups;
    }

    public void setHomogeneousResourceGroups(List<HomogeneousResourceGroup> homogeneousResourceGroups) {
        HomogeneousResourceGroups = homogeneousResourceGroups;
    }
    public void addHomogeneousResourceGroup(HomogeneousResourceGroup homogeneousResourceGroup) {
        HomogeneousResourceGroups.add(homogeneousResourceGroup);
    }

    @Override
    public String toString() {
        String groups = "";
        for(HomogeneousResourceGroup homogeneousResourceGroup : HomogeneousResourceGroups){
            groups += homogeneousResourceGroup.toString()+",";
        }
        groups = "{" + groups + "}";
        return "Resource{" +
                "Type='" + Type + '\'' +
                ", Name='" + Name + '\'' +
                ", InterCloudBW='"+ InterCloudBW + '\'' +
                ", HomogeneousResourceGroups=" + groups +
                '}';
    }
}
