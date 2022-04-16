package org.cloudbus.cloudsim.sdn.nos;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.NormalDistr;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.virtualcomponents.Channel;

import java.util.HashMap;
import java.util.Map;


/**
 * Simulate the performance instability throughout Computation/Transmission task processing
 */
public class PerformanceJitter_old {
    protected NormalDistr nd = null;

    // Processing requests
    private Map<Channel, ContinuousDistribution> channelToDistDict = new HashMap<>();
    private ContinuousDistribution channelDist = null;
    private Map<Vm, ContinuousDistribution> vmToDistDict = new HashMap<>();

    private Map<Channel, double[]> channelBWTimeDict = new HashMap<>();
    private Map<Vm, double[]> vmPerfTimeDict = new HashMap<>();

    Class trDistModel = null;
    Class cpDistModel = null;

    public PerformanceJitter_old() {
//        String trDistStr = Configuration.TR_JITTER_MODEL.toLowerCase();
//        if (trDistStr.contains("normal"))
//            trDistStr = "NormalDist";
//        else if (trDistStr.contains("even") || trDistStr.contains("uniform"))
//            trDistStr = "UniformDist";
//        else if (trDistStr.contains("pareto"))
//            trDistStr = "ParetoDist";
//        else
//            throw new IllegalArgumentException();
//
//
//        String cpDistStr = Configuration.CP_JITTER_MODEL.toLowerCase();
//        if (cpDistStr.contains("normal"))
//            cpDistStr = "NormalDist";
//        else if (cpDistStr.contains("even") || cpDistStr.contains("uniform"))
//            cpDistStr = "UniformDist";
//        else if (cpDistStr.contains("pareto"))
//            cpDistStr = "ParetoDist";
//        else
//            throw new IllegalArgumentException();

//		try {
//			cpDistModel = Class.forName(cpDistStr);
//			Constructor c = cpDistModel.getConstructor(int.class,);
//		}
//		catch (ClassNotFoundException e){
//			e.printStackTrace();
//			System.exit(-1);
//		}

    }

    // Jason: Per-Event sampling : This function should be called by the Channel
    // Manager during each transmission.

    /**
     * @param staticBw allocated bandwidth
     * @param ch       the channel
     * @return
     */
    public double sampleTransmissionPerformance(double staticBw, Channel ch) {
        if((int)staticBw == 0)
            return staticBw;

        double jitter_sigma = Configuration.NETWORK_JITTER_SIGMA;
        if (Configuration.ENABLE_TRANSMISSION_JITTER) {

            ContinuousDistribution dist = this.channelToDistDict.getOrDefault(ch, null);
            if (dist == null) {
                dist = new NormalDistr(staticBw, jitter_sigma);
                this.channelToDistDict.put(ch, dist);
//                double bw = dist.sampleInInterval(0, staticBw * 1.5);
                double bw = dist.sampleInInterval(0.5 * staticBw, staticBw * 1.1);
                this.channelBWTimeDict.put(ch, new double[]{bw, CloudSim.clock()});
                return bw;
            }
//            return dist.sampleInInterval(0, staticBw * 1.5);
            double[] bwTime = this.channelBWTimeDict.get(ch);
            if (CloudSim.clock() - bwTime[1] > Configuration.TR_JITTER_INTERVAL) {
//                double bw = dist.sampleInInterval(0, staticBw * 1.5);
                double bw = dist.sampleInInterval(0.5 * staticBw, staticBw * 1.1);
                bwTime[0] = bw;
                bwTime[1] = CloudSim.clock();
                return bw;
            } else
                return bwTime[0];
        } else
            return staticBw;
    }


    public double sampleTransmissionPerformance(double staticBw) {
        Configuration.total_bw_samplings +=1;

        double jitter_sigma = Configuration.NETWORK_JITTER_SIGMA;
        if (Configuration.ENABLE_TRANSMISSION_JITTER) {

            if (this.channelDist == null) {
                this.channelDist = new NormalDistr(1, jitter_sigma);
                Configuration.channel_freq += 1;
            }

//            double bw = this.channelDist.sampleInInterval(0.6, 1.15);
            double bw = this.channelDist.sampleInInterval(0, 3);
//            double bw = this.channelDist.sampleInInterval(0.5, 1.1);

            bw = bw * staticBw;
            return bw;
        } else
            return staticBw;
    }


//    public double sampleTransmissionPerformance(double staticBw, Channel ch) {
//
//        double jitter_sigma = Configuration.NETWORK_JITTER_SIGMA;
//        if (Configuration.ENABLE_TRANSMISSION_JITTER) {
//            return staticBw;
//        } else
//            return staticBw;
//    }


    // Jason: Discuss!! Performance Jitter should be implemented inside the VM ?
    // Or I just leave it here ?

    /**
     * @param staticMips  allocated mips
     * @param jitterSigma
     * @param vm          target vm
     * @return
     */
    public double sampleComputationPerformance(double staticMips, double jitterSigma, Vm vm) {
        if((int) staticMips == 0)
            return staticMips;

        if (Configuration.ENABLE_COMPUTATION_JITTER) {
            // this.nd = new NormalDistr(mips, mips * jitter_sigma);
//            double jitter = staticMips * 0.05;
//            this.nd = new NormalDistr(staticMips, jitter);

            ContinuousDistribution dist = this.vmToDistDict.getOrDefault(vm, null);
            if (dist == null) { // initialize vmToDistDict and vmPerfTimeDict
                dist = new NormalDistr(staticMips, jitterSigma);
                Configuration.vm_freq += 1;
                this.vmToDistDict.put(vm, dist);
                double perf = dist.sampleInInterval(0, staticMips * 3);
//                double perf = dist.sampleInInterval(0, staticMips * 1.5);
//                double perf = dist.sampleInInterval(staticMips * 0.55, staticMips * 1.15);
                double[] perfTime = {perf, CloudSim.clock()};
                this.vmPerfTimeDict.put(vm, perfTime);
                return perf;
            }
            double[] perfTime = this.vmPerfTimeDict.get(vm);
            if (CloudSim.clock() - perfTime[1] > Configuration.CP_JITTER_INTERVAL) {
                double perf = dist.sampleInInterval(0, staticMips * 3);
                //                double perf = dist.sampleInInterval(0, staticMips * 1.5);
//                double perf = dist.sampleInInterval(staticMips * 0.55, staticMips * 1.15);
                perfTime[0] = perf;
                perfTime[1] = CloudSim.clock();
                Configuration.total_mips_samplings += 1;
                return perf;
            } else
                return perfTime[0];
            // return nd.sample(mips / 5, mips * 2);
//            return dist.sampleInInterval(0, staticMips * 1.5);
        } else
            return staticMips;
    }
}

//    public double sampleComputationPerformance(double staticMips, double jitterSigma, Vm vm) {
//        if (Configuration.ENABLE_TRANSMISSION_JITTER) {
//            return staticMips;
//            // return nd.sample(mips / 5, mips * 2);
////            return dist.sampleInInterval(0, staticMips * 1.5);
//        } else
//            return staticMips;
//    }

