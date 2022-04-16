package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sfc.scenariomanager.ScenarioParser;

class ScenarioResourceGenerator {
    @org.junit.jupiter.api.Test
    public void init() {
        ScenarioParser scenarioParser = new ScenarioParser();

        /**
         * The following code creates a brand new scenario
         */

         scenarioParser.setRootPath("Scenario2-4/");
        Configuration.ROOT_FOLDER = "Scenario2-2/";
         scenarioParser.parse();
         scenarioParser.generatePhysicalResource();

    }

    public static void main(String[] args) {
        ScenarioResourceGenerator fpt = new ScenarioResourceGenerator();
        fpt.init();
    }

}