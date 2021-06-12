package org.cloudbus.cloudsim.sfc.test;

import org.cloudbus.cloudsim.sfc.parser.FileParser;

class FileParserTest {
    @org.junit.jupiter.api.Test
    public void init(){
        FileParser fileParser = new FileParser();
        fileParser.parser();
        fileParser.print();
    }

}