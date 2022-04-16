package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sfc.scenariomanager.CompScenarioParser;
import org.cloudbus.cloudsim.sfc.scenariomanager.SingleScenarioParser;

import java.util.Arrays;
import java.util.List;

class SingleScenarioGenerator {
    @org.junit.jupiter.api.Test
    public void init() {

        Integer[] ns = {1};
        List<Integer> numbers = Arrays.asList(ns);
        String path = "CompareSingleChain/";

        String subFolder = String.valueOf(1) + "demands/";
        SingleScenarioParser scenarioParser = new SingleScenarioParser(path, subFolder);

//        scenarioParser.generateSFCDemands(1);
        scenarioParser.parse();
        scenarioParser.generate();

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

    public static void main(String[] args) {

        SingleScenarioGenerator gen = new SingleScenarioGenerator();
        gen.init();
    }

}