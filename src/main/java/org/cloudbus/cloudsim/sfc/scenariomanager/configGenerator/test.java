package org.cloudbus.cloudsim.sfc.scenariomanager.configGenerator;
import java.util.Map;
import java.util.HashMap;

public class test extends basic{
    public static void main(String[] args) {
        for(int i=0;i<3;i++) {
            double d = Math.random() * 3 + 1;
            int a = (int)d;
            System.out.println(d);
            System.out.println(a);

            test t = new test();

            t.abc();

        }
        Map<String, double[]> test = new HashMap<>();
        test.put("a", new double[] {1,2});
        double[] haha = test.get("a");
        System.out.println(test.get("a")[0] + "," + test.get("a")[1]);

        haha[0] = 3;

        System.out.println(test.get("a")[0] + "," + test.get("a")[1]);
    }


    public void abc(){
        super.schedule();
    }

    @Override
    public void printHello(){
        System.out.println("hello from test");
    }

}

