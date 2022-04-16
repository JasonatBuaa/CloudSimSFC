package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sfc.scenariomanager.ScenarioParser;
import org.cloudbus.cloudsim.sfc.scenariomanager.CompScenarioParser;

class ScenarioGenerator {
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

//         scenarioParser.setRootPath(Configuration.ROOT_FOLDER);
//         scenarioParser.generateSFCDemands();
//         scenarioParser.parse();
//         scenarioParser.generate();

        /**
         * 
         * Perform static scheduling: using a fixed scenario to evaluate different
         * scheduling algorithms.
         */

//        scenarioParser.setRootPath(Configuration.ROOT_FOLDER);
        scenarioParser.setRootPath("Scenario2-1");
        scenarioParser.parse();
         String scenario = "scenario1";
//        String scenario = "compare_test";
        try {
            scenarioParser.deploymentSchedule(scenario);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
            // TODO: handle exception
        }

        scenarioParser.print();

    }

    public static void main(String[] args) {
        ScenarioParserTest fpt = new ScenarioParserTest();
        fpt.init();
    }

}