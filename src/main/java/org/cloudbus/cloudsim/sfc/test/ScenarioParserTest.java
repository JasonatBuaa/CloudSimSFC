package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sfc.scenariomanager.ScenarioParser;

class ScenarioParserTest {
    @org.junit.jupiter.api.Test
    public void init() {
        ScenarioParser scenarioParser = new ScenarioParser();

        /**
         * The following code creates a brand new scenario
         */
        // fileParser.setRootPath(Configuration.ROOT_FOLDER);
        // fileParser.generateSFCDemands();
        // fileParser.parse();
        // fileParser.generate();

        /**
         * iterate algorithms : ) the following only change scheduling algorithms,
         * maintains the scenario to be consistent.
         */

        scenarioParser.setRootPath(Configuration.ROOT_FOLDER);
        scenarioParser.parse();
        // String scenario = "scenario2";
        String scenario = "scenario4";
        try {
            scenarioParser.deploymentSchedule(scenario);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
            // TODO: handle exception
        }

        // int num_user = 1; // number of cloud users
        // Calendar calendar = Calendar.getInstance();
        // boolean trace_flag = false; // mean trace events
        // CloudSim.init(num_user, calendar, trace_flag);
        // String physicalTopologyFile = "Scenario1/PhysicalResource.json";

        // Map<String, NetworkOperatingSystem> dcNameNOS = PhysicalTopologyParser
        // .loadPhysicalTopologyMultiDC(physicalTopologyFile);

        scenarioParser.print();

    }

    public static void main(String[] args) {
        ScenarioParserTest fpt = new ScenarioParserTest();
        fpt.init();
    }

}