package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystem;
import org.cloudbus.cloudsim.sdn.parsers.PhysicalTopologyParser;
import org.cloudbus.cloudsim.sfc.parser.FileParser;

import java.util.Calendar;
import java.util.Map;

class FileParserTest {
    @org.junit.jupiter.api.Test
    public void init(){
        FileParser fileParser = new FileParser();
        fileParser.parser();
        fileParser.generate();
        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false; // mean trace events
        CloudSim.init(num_user, calendar, trace_flag);
        String physicalTopologyFile = "SFCExampleConfig/PhysicalResource.json";

        Map<String, NetworkOperatingSystem> dcNameNOS = PhysicalTopologyParser
                .loadPhysicalTopologyMultiDC(physicalTopologyFile);

        fileParser.print();

    }

}