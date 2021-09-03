package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator.ServiceFunctionChainGenerator;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName ServiceFunctionChainGeneratorTest.java
 * @Description TODO
 * @createTime 2021-07-24 17:07
 */
public class ServiceFunctionChainGeneratorTest {
    @org.junit.jupiter.api.Test
    public void testRun() {
        ServiceFunctionChainGenerator serviceFunctionChainGenerator = new ServiceFunctionChainGenerator();
        serviceFunctionChainGenerator.run();
    }
}
