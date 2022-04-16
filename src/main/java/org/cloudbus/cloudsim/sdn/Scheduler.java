package org.cloudbus.cloudsim.sdn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.sdn.example.StartAvailability;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystem;
import org.cloudbus.cloudsim.sdn.parsers.DeploymentFileParser;
import org.cloudbus.cloudsim.sdn.parsers.FREventParser;
import org.cloudbus.cloudsim.sdn.parsers.PhysicalTopologyParser;
import org.cloudbus.cloudsim.sdn.parsers.VirtualTopologyParser;
import org.cloudbus.cloudsim.sdn.parsers.WorkloadParser;
// import org.cloudbus.cloudsim.sdn.physicalcomponents.PhysicalTopology;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNDatacenter;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunction;
import org.cloudbus.cloudsim.sdn.sfc.ServiceFunctionChainPolicy;
import org.cloudbus.cloudsim.sdn.virtualcomponents.FlowConfig;
import org.cloudbus.cloudsim.sdn.virtualcomponents.SDNVm;
import org.cloudbus.cloudsim.sdn.workload.FREvent;
import org.cloudbus.cloudsim.sdn.workload.Request;
import org.cloudbus.cloudsim.sdn.workload.Workload;
import org.cloudbus.cloudsim.sdn.workload.WorkloadResultWriter;

/**
 * Broker class for CloudSimSDN example. This class represents a broker (Service
 * Provider) who uses the Cloud data center.
 * 
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */
public class Scheduler extends SimEntity {

    public static Map<String, SDNDatacenter> datacenters = new HashMap<String, SDNDatacenter>();
    private static Map<Integer, SDNDatacenter> vmIdToDc = new HashMap<Integer, SDNDatacenter>();

    // private List<String> availEventFileNames = null;
    private List<String> FRFileNames = null;

    public Scheduler(String name) throws Exception {
        super(name);

        this.FRFileNames = new ArrayList<String>();
    }

    @Override
    public void startEntity() {
        // sendNow(getId(), CloudSimTagsSDN.APPLICATION_SUBMIT,
        // this.applicationFileName);
    }

    @Override
    public void shutdownEntity() {

    }

    public void submitDeployApplication(SDNDatacenter dc, String filename) {
        SDNBroker.datacenters.put(dc.getName(), dc); // default DC
    }

    @Override
    public void processEvent(SimEvent ev) {
        int tag = ev.getTag();

        switch (tag) {
            case CloudSimTagsSDN.PROCESS_SFC_DEMAND:
                decidePlacement(ev.getSource(), (String) ev.getData());

                break;
            case CloudSimTagsSDN.REQUEST_COMPLETED:

                break;
            default:
                System.out.println("Unknown event received by " + super.getName() + ". Tag:" + ev.getTag());
                break;
        }
    }

    /**
     * Jason: initilize the regarding data structures, including: VMs, arcs, NFC
     * policy, nos
     * 
     * @param userId
     * @param vmsFileName
     */
    private void decidePlacement(int userId, String vmsFileName) {
        Object placementDecision = null;

        SDNDatacenter defaultDC = SDNBroker.datacenters.entrySet().iterator().next().getValue();
        DeploymentFileParser parser = new DeploymentFileParser(defaultDC.getName(), vmsFileName, userId);

        for (String dcName : SDNBroker.datacenters.keySet()) {
            SDNDatacenter dc = SDNBroker.datacenters.get(dcName);
            NetworkOperatingSystem nos = dc.getNOS();

            for (SDNVm vm : parser.getVmList(dcName)) {
                nos.addVm(vm);
                if (vm instanceof ServiceFunction) {
                    ServiceFunction sf = (ServiceFunction) vm;
                    sf.setNetworkOperatingSystem(nos);
                }
                vmIdToDc.put(vm.getId(), dc);
            }
            // System.out.println("test");
        }

        for (FlowConfig arc : parser.getArcList()) {
            SDNDatacenter srcDc = vmIdToDc.get(arc.getSrcId());
            SDNDatacenter dstDc = vmIdToDc.get(arc.getDstId());

            if (srcDc.equals(dstDc)) {
                // Intra-DC traffic: create a virtual flow inside the DC
                srcDc.getNOS().addFlow(arc);
            } else {
                // Inter-DC traffic: Create it in inter-DC N.O.S.
                srcDc.getNOS().addFlow(arc);
                dstDc.getNOS().addFlow(arc);
            }
        }

        // Add parsed ServiceFunctionChainPolicy
        for (ServiceFunctionChainPolicy policy : parser.getSFCPolicyList()) {
            SDNDatacenter srcDc = vmIdToDc.get(policy.getSrcId());
            SDNDatacenter dstDc = vmIdToDc.get(policy.getDstId());
            if (srcDc.equals(dstDc)) {
                // Intra-DC traffic: create a virtual flow inside the DC
                srcDc.getNOS().addSFCPolicy(policy);
            } else {
                // Inter-DC traffic: Create it in inter-DC N.O.S.
                srcDc.getNOS().addSFCPolicy(policy);
                dstDc.getNOS().addSFCPolicy(policy);
            }
        }

        for (String dcName : SDNBroker.datacenters.keySet()) {
            SDNDatacenter dc = SDNBroker.datacenters.get(dcName);
            NetworkOperatingSystem nos = dc.getNOS();
            nos.startDeployApplicatoin();
        }

        // send(userId, 0, CloudSimTagsSDN.APPLICATION_SUBMIT_ACK, vmsFileName);
        sendNow(userId, CloudSimTagsSDN.SFC_DEPLOY, placementDecision);
    }

    public static SDNDatacenter getDataCenterByName(String dcName) {
        return SDNBroker.datacenters.get(dcName);
    }

    public static SDNDatacenter getDataCenterByVmID(int vmId) {
        return vmIdToDc.get(vmId);
    }

}
