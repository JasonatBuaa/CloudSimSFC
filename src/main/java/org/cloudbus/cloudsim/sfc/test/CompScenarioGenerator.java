package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sfc.scenariomanager.CompScenarioParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class CompScenarioGenerator {
    @org.junit.jupiter.api.Test
    public void init() {

        /**
         * The following code creates a brand new scenario
         */
//        Integer[] ns = {10, 20, 30, 40};
//        Integer[] ns = {5, 15, 25, 35};
        Integer[] ns = {45, 50, 55, 60};
        List<Integer> numbers = Arrays.asList(ns);
//        String path = "CompareExperimentMedium/";
//        String path = "CompareExperimentSmall/";
        String path = "CompareExperimentTiny/";
        for (int n : numbers) {
//            String rootFolder = path + String.valueOf(n) + "demands/";
//            CompScenarioParser scenarioParser = new CompScenarioParser(rootFolder);
            String subFolder = String.valueOf(n) + "demands/";
            CompScenarioParser scenarioParser = new CompScenarioParser(path, subFolder);
//            scenarioParser.setRootPath(Configuration.ROOT_FOLDER);
//            scenarioParser.generateSFCDemands();

//            scenarioParser.setRootPath(rootFolder);
            scenarioParser.generateSFCDemands(n);
            scenarioParser.parse();
            scenarioParser.generate();

            /**
             *
             * Perform static scheduling: using a fixed scenario to evaluate different
             * scheduling algorithms.
             */
//        scenarioParser.setRootPath(Configuration.ROOT_FOLDER);
//        scenarioParser.parse();

            // String scenario = "scenario2";

            String scenario = "revisionComparison";
            try {
                scenarioParser.deploymentSchedule(scenario);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
                // TODO: handle exception
            }

            scenarioParser.print();
        }

    }

    public static void main(String[] args) {
//      ScenarioParserTest fpt = new ScenarioParserTest();
//      fpt.init();

        CompScenarioGenerator gen = new CompScenarioGenerator();
        gen.init();
    }

}