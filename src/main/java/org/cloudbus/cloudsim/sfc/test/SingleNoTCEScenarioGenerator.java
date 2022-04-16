package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sfc.scenariomanager.SingleNoTCEScenarioParser;
import org.cloudbus.cloudsim.sfc.scenariomanager.SingleScenarioParser;

import java.util.Arrays;
import java.util.List;

class SingleNoTCEScenarioGenerator {
    @org.junit.jupiter.api.Test
    public void init() {

        Integer[] ns = {1};
        List<Integer> numbers = Arrays.asList(ns);
        String path = "CompareSingleChainNoTCE/";

        String subFolder = String.valueOf(1) + "demands/";
        SingleNoTCEScenarioParser scenarioParser = new SingleNoTCEScenarioParser(path, subFolder);

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

        SingleNoTCEScenarioGenerator gen = new SingleNoTCEScenarioGenerator();
        gen.init();
    }

}