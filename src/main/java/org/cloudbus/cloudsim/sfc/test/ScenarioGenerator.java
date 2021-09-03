package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sfc.scenariomanager.ScenarioParser;

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

        /**
         * 
         * Perform static scheduling: using a fixed scenario to evaluate different
         * scheduling algorithms.
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

        scenarioParser.print();

    }

    public static void main(String[] args) {
        ScenarioParserTest fpt = new ScenarioParserTest();
        fpt.init();
    }

}