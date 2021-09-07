/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.sdn.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.cloudbus.cloudsim.ParameterException;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;
import org.cloudbus.cloudsim.sdn.workload.FREvent;
import org.cloudbus.cloudsim.sdn.workload.WorkloadResultWriter;

/**
 * Parse [AvailabilityEvent].csv file.
 * 
 * File format : event_arrival_time(1), HostName(1), FailureOrRecovery(1),
 * event_arrival_time(2), HostName(2), FailureOrRecovery(2)
 * 
 * @author Jason Sun
 * @since 20201111 v1.0
 */

public class FREventParser {
    private static final int NUM_PARSE_EACHTIME = 200;

    private double forcedStartTime = -1;
    private double forcedFinishTime = Double.POSITIVE_INFINITY;

    private Map<String, Integer> hostNames;
    private String file;
    private int userId;

    private List<FREvent> parsedFREvents;

    private WorkloadResultWriter resultWriter = null;

    private int FREventNum = 0;

    private BufferedReader bufReader = null;

    /**
     * 
     * @param file
     * @param userId
     * @param hostIdMap
     */
    public FREventParser(String file) {
        this.file = file;
        String result_file = getResultFileName(this.file);
        resultWriter = new WorkloadResultWriter(result_file);
        openFile();
        hostNames = new HashMap<String, Integer>();
        buildHostNames2IdMap();
    }

    public void buildHostNames2IdMap() {

        for (SDNHost h : PhysicalTopologyParser.deployedHosts.values()) {
            String host_name = h.getName();
            int host_id = h.getId();
            hostNames.put(host_name, host_id);
        }
    }

    public void forceStartTime(double forcedStartTime) {
        this.forcedStartTime = forcedStartTime;
    }

    public void forceFinishTime(double forcedFinishTime) {
        this.forcedFinishTime = forcedFinishTime;
    }

    public static String getResultFileName(String fileName) {
        String result_file = null;
        int indexSlash = fileName.lastIndexOf("/");
        if (indexSlash != -1) {
            String path_folder = fileName.substring(0, indexSlash + 1);
            String path_file = fileName.substring(indexSlash + 1);
            result_file = path_folder + "result_" + path_file;
        } else {
            result_file = "result_" + fileName;
        }
        return result_file;
    }

    public void parseNextFREvents() {
        this.parsedFREvents = new ArrayList<FREvent>();
        try {
            parseNext(NUM_PARSE_EACHTIME);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    public List<FREvent> getParsedFREvents() {
        return this.parsedFREvents;
    }

    public WorkloadResultWriter getResultWriter() {
        return resultWriter;
    }

    private int getHostId(String hostName) {
        Integer hostId = this.hostNames.get(hostName);
        if (hostId == null) {
            System.err.println("In Event Parser-- Cannot find Host name:" + hostName);
            return -1;
        }
        return hostId;
    }

    // Cloud_Len -> /FlowId/ -> ToVmId -> PktSize
    // Iterative calling this function

    private void openFile() {
        try {
            bufReader = new BufferedReader(new FileReader(Configuration.workingDirectory + file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            @SuppressWarnings("unused")
            String head = bufReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseNext(int numPerBatch) throws ParameterException {
        String line;

        try {
            while (((line = bufReader.readLine()) != null) && (parsedFREvents.size() < numPerBatch)) {
                // System.out.println("parsing:"+line);
                FREvent ae = new FREvent(FREventNum++);

                String[] splitLine = line.split(",");
                Queue<String> lineitems = new LinkedList<String>(Arrays.asList(splitLine));

                // start time
                ae.setTime(Double.parseDouble(lineitems.poll()));
                // For debug only
                if (ae.getTime() < this.forcedStartTime || ae.getTime() > this.forcedFinishTime) // Skip Workloads
                                                                                                 // before the set
                    continue;

                // host information
                String hostName = lineitems.poll();
                int hostID = getHostId(hostName);
                ae.setHostID(hostID);

                double failureTime = Double.parseDouble(lineitems.poll());
                double recoveryTime = Double.parseDouble(lineitems.poll());

                if (failureTime > recoveryTime)
                    throw new ParameterException("failure time should less than recovery time!!");
                ae.setFailureTime(failureTime);
                ae.setRecoveryTime(recoveryTime);

                parsedFREvents.add(ae);

                // String inputEventType = lineitems.poll();
                // event type
                // for (AvailabilityEvent.AvailabilityEventType event :
                // AvailabilityEvent.AvailabilityEventType.values()) {
                // if (event.getType().equals(inputEventType)) {
                // ae.setAvailabilityEventType(event);
                // break;
                // }
                // }

                // Jason: Todo! complete this part

                // ae.submitVmId = getHostId(vmName);

                // ae.submitPktSize = Integer.parseInt(lineitems.poll());

                // ae.request = parseRequest(tr.submitVmId, lineitems);

                // parsedAvailabilityEvents.add(tr);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * private String getOutputFilename(String filename) { String ext =
     * LogWriter.getExtension(this.file); String dir = LogWriter.getPath(this.file);
     * String name = "result_"+LogWriter.getBaseName(this.file);
     * System.err.println(dir+"/"+name+"."+ext); return dir+"/"+name+"."+ext; }
     */

    public int getFREventNum() {
        return FREventNum;
    }

}
