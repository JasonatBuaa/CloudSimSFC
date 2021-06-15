package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sdn.nos.NetworkOperatingSystem;
import org.cloudbus.cloudsim.sdn.parsers.PhysicalTopologyParser;
import org.cloudbus.cloudsim.sfc.parser.FileParser;

import java.util.Map;

class FileParserTest {
    @org.junit.jupiter.api.Test
    public void init(){
        FileParser fileParser = new FileParser();
        fileParser.parser();
        fileParser.generate();
        String physicalTopologyFile = "SFCExampleConfig/PhysicalResource.json";

        Map<String, NetworkOperatingSystem> dcNameNOS = PhysicalTopologyParser
                .loadPhysicalTopologyMultiDC(physicalTopologyFile);

        fileParser.print();

    }

}