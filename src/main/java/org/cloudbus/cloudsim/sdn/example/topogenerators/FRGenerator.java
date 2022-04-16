/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn.example.topogenerators;

import org.cloudbus.cloudsim.Host;
// import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
import org.cloudbus.cloudsim.distributions.PoiDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.sdn.LogWriter;
import org.cloudbus.cloudsim.sdn.parsers.PhysicalTopologyParser;
import org.cloudbus.cloudsim.sdn.physicalcomponents.SDNHost;

import java.util.ArrayList;

import java.util.Comparator;

import java.util.LinkedList;
import java.util.List;
// import org.cloudbus.cloudsim.sdn.example.topogenerators.VirtualTopologyGeneratorVmTypesSFC;

// import javax.security.auth.login.Configuration;

/**
 * This class creates FailureRecoveryEvents.
 * 
 * @author Jason Sun
 * @since 2020/12/07 // generate FailureRecoveryEvents related information.
 * 
 */
public class FRGenerator extends SFCWorkloadGenerator {

    // 固定配置： 最小时间单位、最短故障恢复时间、
    // 输入：VM的availability值， timeline开始节点、终止节点，

    // p2p request; data imbalance;
    // 起点、终点要指定位置 -- very important!!

    // request workload (cloudlet) 在0.5k-1k之间（平均值0.75）
    // request per second -> 4
    // queue size: 6* workload平均值

    private static double minRecoveryTime = 20;

    private LogWriter out = null;
    private List<Host> hosts = null;
    private boolean isHeadPrinted;

    private double timelineStart = 0;
    private double timelineEnd;

    private double minMttr = 0;
    private double maxMttr = 0;

    public FRGenerator(String filename, List<Host> hosts, double timelineEnd) {

        out = LogWriter.getLogger(filename);
        this.hosts = hosts;
        this.timelineEnd = timelineEnd;
    }

    public FRGenerator(String filename) {
        out = LogWriter.getLogger(filename);
    }

    private class FREventSortor implements Comparator<FREvent> {

        @Override
        public int compare(FREvent fre1, FREvent fre2) {
            // TODO Auto-generated method stub
            if (fre1.eventArrivalTime != fre2.eventArrivalTime)
                return (int) (fre1.eventArrivalTime - fre2.eventArrivalTime);
//            if (fre1.failureTime != fre2.failureTime)
            else if (fre1.failureTime != fre2.failureTime)
                return (int) (fre1.failureTime - fre2.failureTime);
            else if (fre1.recoveryTime != fre2.recoveryTime)
                return (int) (fre1.recoveryTime - fre2.recoveryTime);
            else
                return 0;
        }

    }

    // 南屏晚钟、雷峰夕照、花港观鱼、双峰插云、断桥残雪、苏堤春晓、三潭印月、平湖秋月、柳浪闻莺、曲苑风荷
    public class FREvent {
        String hostName = "";
        double eventArrivalTime;
        double failureTime;
        double recoveryTime;

        public FREvent(String hostName, double event_arrival_time, double failure_time, double recovery_time) {
            this.hostName = hostName;
            this.eventArrivalTime = event_arrival_time;
            this.failureTime = failure_time;
            this.recoveryTime = recovery_time;
        }

        @Override
        public String toString() {
            String str = this.eventArrivalTime + "," + this.hostName + "," + this.failureTime + "," + this.recoveryTime;
            return str;
        }
    }

    /**
     * 
     * @param host
     * @param start
     * @param end
     * @param total_failure_time
     * @param remainingFailureCount
     * @return
     * @throws Exception
     */
    public List<FREvent> genHostFREventByAvailability(Host host, double start, double end, double total_failure_time,
            int remainingFailureCount) throws Exception {
        double avail = 0;
        List<FREvent> fre_list = null;
        if (host instanceof SDNHost) {
            SDNHost sdn_host = (SDNHost) host;
            FREvent fre = new FREvent(sdn_host.getName(), CloudSim.clock(), 0, 0); // Jason: todo! change
                                                                                   // time
            double totalTimeLine = end - start;

            avail = sdn_host.getAvailability();
            if (total_failure_time == 0)
                total_failure_time = avail * totalTimeLine;
            double failure_time = randomDouble(start, end);
            // double time_interval = randomEvenDouble(failure_time,end);
            double time_interval = randomDouble(
                    Integer.parseInt(new java.text.DecimalFormat("x").format(minRecoveryTime)));

            System.out.println(time_interval);
            System.out.println(minRecoveryTime);

            double recovery_time = time_interval > minRecoveryTime ? (failure_time + time_interval)
                    : (failure_time + minRecoveryTime);
            // 确定recovery_time
            if (end - failure_time <= total_failure_time) { // 总时间不够，则将恢复时间改为仿真结束时间end
                recovery_time = end;
            } else if (time_interval < total_failure_time && --remainingFailureCount > 0) // 总时间足够，但是失效时间不够
            {
                fre_list.addAll(genHostFREventByAvailability(host, recovery_time, end,
                        total_failure_time - time_interval, --remainingFailureCount));
            }

            if (recovery_time - failure_time <= total_failure_time) {
            }

            fre_list.add(fre);
        }

        else {
            throw new Exception();

        }
        return fre_list;
        // int flowSizeSeed = 5000;
        // for (int i = 0; i < 10; i++) {
        // int flowSize = (int) Math.round(randomDouble(flowSizeSeed));
        // int SFClength = new Random().nextInt(10);
        // System.out.println("==================");
        // System.out.println("flowSize = " + flowSize + ", SFClength= " + SFClength);
        // }
    }

    public boolean genMTBFMTTRWithAvailability(Host host, double mttr_min, double mttr_max) {
        if (host instanceof SDNHost) {
            SDNHost sdn_host = (SDNHost) host;

            double availability = sdn_host.getAvailability();
            double mttr = this.randomDouble(mttr_min, mttr_max);

            double mtbf = mttr / (1 - availability);

            sdn_host.setMtbf(mtbf);
            sdn_host.setMttr(mttr);
            return true;
        }
        return false;
    }

    public List<FREvent> genHostFREventWithMTBF(Host host, double start, double end, double mttr_min, double mttr_max) {

        List<FREvent> fre_list = null;
        if (host instanceof SDNHost) {
            fre_list = new ArrayList<>();
            SDNHost sdn_host = (SDNHost) host;
            double mtbf = sdn_host.getMtbf();
            double mttr = sdn_host.getMttr();
            // double remaining_failure_time =
            ExponentialDistr failure_expdis = new ExponentialDistr(mtbf);
            ExponentialDistr recovery_expdis = new ExponentialDistr(mttr);

            double time_to_failure = failure_expdis.sample();
            double time_to_recovery = recovery_expdis.sample(mttr_min, mttr_max);

            // while (time_to_recovery > mttr_max || time_to_recovery < mttr_min)
            // time_to_recovery = recovery_expdis.sample(); // Jason:
            // 控制time_to_recovery的上限下限值

            double next_failure_time = start + time_to_failure;
            double next_recovery_time = start + time_to_recovery;
            double current_time = next_failure_time;

            while (current_time < end) {
                if (next_recovery_time > end)
                    next_recovery_time = end;
                fre_list.add(new FREvent(sdn_host.getName(), CloudSim.clock(), next_failure_time, next_recovery_time));
                time_to_failure = failure_expdis.sample();

                // time_to_recovery = recovery_expdis.sample();

                time_to_recovery = recovery_expdis.sample(mttr_min, mttr_max);

                // while (time_to_recovery > mttr_max || time_to_recovery < mttr_min)
                // time_to_recovery = recovery_expdis.sample(); // Jason:
                // 控制time_to_recovery的上限下限值

                next_failure_time = current_time + time_to_failure;
                current_time = next_failure_time;
                next_recovery_time = current_time + time_to_recovery;
            }
        }
        return fre_list;
    }

    public List<FREvent> genHostFREventWithAvaMTBF(Host host, double start, double end) {

        List<FREvent> fre_list = null;
        if (host instanceof SDNHost) {
            fre_list = new ArrayList<>();
            SDNHost sdn_host = (SDNHost) host;
            double mtbf = sdn_host.getMtbf();
            double mttr = sdn_host.getMttr();
            // double remaining_failure_time =
            ExponentialDistr failure_expdis = new ExponentialDistr(mtbf);
            // ExponentialDistr recovery_expdis = new ExponentialDistr(mttr);
            double recovery_min = mttr * 0.8;
            double recovery_max = mttr * 1.2;
            UniformDistr recovery_expdis = new UniformDistr(recovery_min, recovery_max);

            double time_to_failure = failure_expdis.sample();
            // double time_to_recovery = recovery_expdis.sample(mttr_min, mttr_max);

            // while (time_to_recovery > mttr_max || time_to_recovery < mttr_min)
            double time_to_recovery = recovery_expdis.sample(); // Jason:
            // 控制time_to_recovery的上限下限值

            double next_failure_time = start + time_to_failure;
            double next_recovery_time = next_failure_time + time_to_recovery;
            double current_time = next_failure_time;

            while (current_time < end) {
                if (next_recovery_time > end)
                    next_recovery_time = end;
                fre_list.add(new FREvent(sdn_host.getName(), CloudSim.clock(), next_failure_time, next_recovery_time));
                time_to_failure = failure_expdis.sample();

                // time_to_recovery = recovery_expdis.sample();

                time_to_recovery = recovery_expdis.sample();

                next_failure_time = current_time + time_to_failure;
                current_time = next_failure_time;
                next_recovery_time = current_time + time_to_recovery;
                current_time = next_recovery_time;
            }
        }
        return fre_list;
    }

    public List<FREvent> genFREventForAllHosts(List<Host> host_list, double start, double end) {
        List<FREvent> allfre_list = new LinkedList();

        for (Host host : host_list) {
            List<FREvent> fre_list = genHostFREventWithAvaMTBF(host, start, end);
            if (fre_list != null)
                allfre_list.addAll(fre_list);
            else
                System.out.println("Warning!!! empty FailureRecoveryEvents list detected!!!");
        }

        allfre_list.sort(new FREventSortor());

        return allfre_list;

    }

//    public void generate() {
//        FRGenerator fg = new FRGenerator("FREvents.csv");
//        List<Host> host_list = new ArrayList<>();
//        for (Host h : PhysicalTopologyParser.deployedHosts.values()) {
//            host_list.add(h);
//        }
//        // List<Host> host_list = new
//        // ArrayList<>(PhysicalTopologyParser.deployedHosts.get("dc1"));
//
//        double mttr_min = 1.5;
//        double mttr_max = 3;
//        for (Host host : host_list) {
//            fg.genMTBFMTTRWithAvailability(host, mttr_min, mttr_max);
//        }
//
//        double start = 0;
//        double end = Configuration.SIMULATION_DURATION;
//        List<FREvent> allfre_list = fg.genFREventForAllHosts(host_list, start, end);
//        fg.writeFREventIntoFile(allfre_list);
//    }

    public void generate() {
        List<Host> host_list = new ArrayList<>();
        for (Host h : PhysicalTopologyParser.deployedHosts.values()) {
            host_list.add(h);
        }
        // List<Host> host_list = new
        // ArrayList<>(PhysicalTopologyParser.deployedHosts.get("dc1"));

        double mttr_min = 1.5;
        double mttr_max = 3;
        for (Host host : host_list) {
            this.genMTBFMTTRWithAvailability(host, mttr_min, mttr_max);
        }

        double start = 0;
        double end = Configuration.SIMULATION_DURATION;
        List<FREvent> allfre_list = this.genFREventForAllHosts(host_list, start, end);
        this.writeFREventIntoFile(allfre_list);
    }

    public static void main(String[] argv) {

        FRGenerator fg = new FRGenerator("FREvents.csv");
        List<Host> host_list = new ArrayList<>(PhysicalTopologyParser.deployedHosts.get("dc1"));

        // double mttr_min = 1.5;
        // double mttr_max = 3;
        // for (Host host : host_list) {
        // fg.genMTBFMTTRWithAvailability(host, mttr_min, mttr_max);
        // }

        double start = 0;
        double end = Configuration.SIMULATION_DURATION;
        List<FREvent> allfre_list = fg.genFREventForAllHosts(host_list, start, end);
        fg.writeFREventIntoFile(allfre_list);
    }

    /**
     * 
     * @param averagePerSecond
     * @param timeSpan
     * @return
     */
    public List<Integer> genRequestAmountBySecond(int averagePerSecond, int timeSpan) {
        List<Integer> requestAmountEverySecond = new ArrayList<>(); // Poisson Distribution
        PoiDistribution pd = new PoiDistribution(averagePerSecond);
        for (int i = 0; i < timeSpan; i++)
            requestAmountEverySecond.add(pd.sample());
        return requestAmountEverySecond;
    }

    /**
     * Jason: Generate one line of workload.
     * 
     * @param mu
     * @param sigma
     * @param sampleSize
     * @return
     */

    public double randomDouble(Double min, Double max) {
        UniformDistr dis = new UniformDistr(min, max);
        return dis.sample();
    }

    public double randomDouble(int min, int max) {
        UniformDistr dis = new UniformDistr(min, max);
        return dis.sample();
    }

    public int randomEvenInt(int min, int max) {
        return (int) Math.round(randomDouble(min, max));
    }

    private String printFREventHeadForDebug() {
        String headString = "event_arrival_time,HostName,failure_time,recovery_time";
        out.printLine(headString);
        return headString;

    }

    private void writeFREventIntoFile(List<FREvent> FRE_list) {
        if (!isHeadPrinted) {
            printFREventHeadForDebug();
            isHeadPrinted = true;
        }

        for (FREvent fre : FRE_list) {
            this.out.printLine(fre.toString());
        }

    }

}