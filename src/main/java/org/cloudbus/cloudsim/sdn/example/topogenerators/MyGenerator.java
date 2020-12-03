/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn.example.topogenerators;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
import org.cloudbus.cloudsim.distributions.PoiDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.sdn.LogWriter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
// import org.cloudbus.cloudsim.sdn.example.topogenerators.VirtualTopologyGeneratorVmTypesSFC;
import java.util.Map;
import java.util.Random;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

import javax.sql.rowset.WebRowSet;

/**
 * This class creates Virtual Environment for SFC experiments.
 * 
 * @author Jason Sun
 * @since 2020/12/01
 */
public class MyGenerator extends SFCWorkloadGenerator {

    // 1. generate raw flow size and SFC graph demands

    // 1. get end-point VM pairs from hashmap
    // 2. select links for the VM pair

    // 要生成的东西：
    // 1、拓扑文件
    // 2、request
    // 3、由资源量计算得到 -> sfc结构

    // p2p request; data imbalance;
    // 起点、终点要指定位置

    // vm mips 在2k-5k之间,2k,3k,4k,5k;
    // host mips 5k or 10k
    // vnf mipoper 在1k-1.5k之间

    // request workload (cloudlet) 在0.5k-1k之间（平均值0.75）
    // request per second -> 4
    // queue size: 6* workload平均值

    private int vmMipsBase;
    private int hostMipsBase;
    private int requestPerSecondAverage;
    private int vnfMiPOAverage;
    private int workloadSizeMin;
    private int workloadSizeMax;
    private int queueSize;
    private boolean isHeadPrinted = false;
    private LogWriter out = null;

    private double minTimeSpan;
    List<Double> computeWL = new ArrayList<>();
    List<Double> transmissionWL = new ArrayList<>();
    Map<Integer, Integer> vmMap = new HashMap<>();

    Map<Integer, List<String>> SFCs = new HashMap<>();

    public MyGenerator(int vmMipsBase, int hostMipsBase, int requestPerSecondAverage, int vnfMiPOAverage,
            int workloadSizeMin, int workloadSizeMax, int queueSize) {

        this.vmMipsBase = vmMipsBase;
        this.hostMipsBase = hostMipsBase;
        this.requestPerSecondAverage = requestPerSecondAverage;
        this.vnfMiPOAverage = vnfMiPOAverage;

        this.workloadSizeMin = workloadSizeMin;
        this.workloadSizeMax = workloadSizeMax;
        this.queueSize = queueSize;
    }

    public MyGenerator(String filename) {
        out = LogWriter.getLogger(filename);
    }

    public List<Integer> genVMOffers(int vmMipsBase, int numberOfTypes) {
        List<Integer> offers = new ArrayList<Integer>();
        for (int i = 1; i < numberOfTypes + 1; i++) {
            offers.add(i * vmMipsBase);
        }
        return offers;

    }

    public List<Integer> genHostQuotas(int hostMipsBase, int numberOfTypes) {
        List<Integer> quotas = new ArrayList<Integer>();
        for (int i = 1; i < numberOfTypes + 1; i++) {
            quotas.add(i * vmMipsBase);
        }
        return quotas;

    }

    public boolean genWorkLoad() {
        // 1. generate raw flow size and SFC graph demands

        int flowSizeSeed = 5000;
        for (int i = 0; i < 10; i++) {
            int flowSize = (int) Math.round(randomDouble(flowSizeSeed));
            int SFClength = new Random().nextInt(10);
            System.out.println("==================");
            System.out.println("flowSize = " + flowSize + ",  SFClength= " + SFClength);
        }
        // 1. get end-point VM pairs from hashmap
        // 2. select links for the VM pair
        return false;
    }

    public static void main(String[] argv) {

        MyGenerator mg = new MyGenerator("testFile.csv");

        // mg.out.print("abc");

        // mg.out.printLine("abcabc");

        Integer[] a = { 1, 2, 3 };
        List<Integer> workload = Arrays.asList(a);

        int vmMipsBase = 2000;
        int vmType = 2;
        int timeSpan = 100; // 时间长度

        List<Integer> vmMips = mg.genVMOffers(vmMipsBase, vmType);

        int hostMipsBase = 5000;
        int hostType = 2;
        List<Integer> hostMips = mg.genHostQuotas(hostMipsBase, hostType);

        // int requestPerSecondAverage = 4;

        int requestPerSecondAverage = 3;
        int fwMipo = 1250;
        int lbMipo = 500;
        int filterMipo = 1500;

        int workloadSizeMin = 500;
        int workloadSizeMax = 1000;

        int workloadSizeAverage = 1500; // use normal distribution
        int workloadSizeDeviation = 100;

        // int SFClength = 2;

        int sfcid = 0;
        String[] sfc0 = { "vm01", "vm02" };
        mg.SFCs.put(sfcid, Arrays.asList(sfc0));

        int SFC0_length = mg.SFCs.get(0).size();

        // mg.genWorkLoads(timeSpan, SFClength, workloadSizeAverage,
        // workloadSizeDeviation, requestPerSecondAverage);

        List<Double> timePoints = mg.genTimePoints(timeSpan, requestPerSecondAverage);
        List<Map> allWorkLoads = mg.genWorkLoads(timePoints.size(), SFC0_length, workloadSizeAverage,
                workloadSizeDeviation, requestPerSecondAverage);
        Map<Integer, List<Integer>> clWorkload = allWorkLoads.get(0);
        Map<Integer, List<Integer>> wlTransmission = allWorkLoads.get(1);

        mg.writeWorkloadIntoFile(timePoints, clWorkload, wlTransmission, mg.SFCs.get(0));

        // public List<List<Integer>> genWorkloads(List<Integer>
        // requestAmountEverySecond, int workloadEverage, int mu, int sigma) {

        // mg.genWorkload(workloadSizeAverage, workloadSizeDeviation, 2);

        // List<Integer> requestAmountEverySecond =
        // mg.genRequestAmountInEverySecond(requestPerSecondAverage, timeSpan);

        // List<Integer> workLoads = mg.newWorkload(requestAmountEverySecond,
        // workloadSizeAverage, workloadSizeDeviation);

        List<Double> allTimePoints = mg.genTimePoints(timeSpan, requestPerSecondAverage);

        int numberOfTimePoints = allTimePoints.size();

        int queueSize = (int) Math.round(workloadSizeAverage * requestPerSecondAverage * 1.5);

        // vm mips 在2k-5k之间 {2k,4k};
        // host mips 在5k-10k之间 {5k,10k};

        // vnf mipoper 在1k-1.5k之间

        // request workload (cloudlet) 在0.5k-1k之间（平均值0.75）
        // request per second -> 4
        // queue size: 6* workload平均值

        // MyGenerator mg = new MyGenerator(requestPerSecondAverage, vnfMiPOAverage,
        // workloadSizeMin, workloadSizeMax,
        // queueSize);

        // MyGenerator mg =new MyGenerator(vmMipsBase, hostMipsBase,
        // requestPerSecondAverage, vnfMiPOAverage, workloadSizeMin, workloadSizeMax,
        // queueSize)
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        SFCWorkloadGenerator sfcWorkLoadGen = new SFCWorkloadGenerator();
        // sfcWorkLoadGen.generateLarge3TierTopologySFC("sfc.virtual.json", noscale);
        new SFCWorkloadGenerator().genWorkLoad();
    }

    int vmNum = 0;

    enum VMtype {
        WebServer, AppServer, DBServer, Proxy, Firewall
    }

    public List<String> getChainedVNFNames(int chainID) {
        if (this.SFCs.containsKey(chainID)) {
            return this.SFCs.get(chainID);
        } else
            return null;
    }

    public boolean saveChainIntoMap(int chainID, List<String> ChainedVNFs) {
        if (!this.SFCs.containsKey(chainID)) {
            this.SFCs.put(chainID, ChainedVNFs);
        }

        return false;
    }

    private List<Integer> genTransmissionWorkload(List<Integer> toWorkloads) {
        // List<List<Integer>> transWorkloads = new ArrayList<>();
        // for (List<Integer> workload : toWorkloads) {
        // List<Integer> transWorkload = new ArrayList<>();
        // Iterator wit = workload.iterator();
        // while (wit.hasNext()) {
        // int tworkload = (int) Math.round((int) wit.next() / 3.0);
        // transWorkload.add(tworkload);
        // }
        // transWorkloads.add(transWorkload);
        // transWorkload.clear();
        // }

        List<Integer> transWorkloads = new ArrayList<>();
        for (int workload : toWorkloads) {
            int tworkload = (int) Math.round((int) workload / 3.0);
            transWorkloads.add(tworkload);
        }
        return transWorkloads;
    }

    public List<Integer> genVNFWorkloads(int workloadAmounts, int mu, int sigma) {
        List<Integer> workloads = new ArrayList<>();
        // for (int n : requestAmountInEverySecond) {
        // workloads.add(new ArrayList<>());
        // for (int j = 0; j < n; j++) {
        // workloads.get(j).addAll(genWorkload(mu, sigma, n));
        // }
        // }

        workloads.addAll(newWorkload(mu, sigma, workloadAmounts));

        // String name[] = { "张三", "李四", "王五", "孙刘", "赵强", "李明", "赵强", "汪汪" };
        // List<String> stringList = Arrays.stream(name).filter(x ->
        // !x.equals("赵强")).collect(toList());

        return workloads;
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
    public List<Integer> newWorkload(int mu, int sigma, int sampleSize) {
        NormalDistribution nd = new NormalDistribution(mu, sigma);
        // Arrays.asList(nd.sample(sampleSize)).forEach(sample);
        // List<Integer> workload =
        //
        // Arrays.stream(nd.sample(sampleSize)).filter(x->(int)Math.round(x)).collect(supplier,
        // accumulator, combiner)

        // List<Integer> workload =
        // Arrays.asList(nd.sample(sampleSize)).stream().map(x->(int)
        // Math.round(x)).collect(Collectors.toList());

        // List<Integer> workload = Arrays.stream(nd.sample(sampleSize)).map(x ->
        // Integer.valueOf(((int) Math.round(x))))
        // .boxed().collect(Collectors.toList());

        // List<Double> workload = Arrays.stream(nd.sample(sampleSize)).map(x ->
        // Integer.valueOf((int) Math.round(x)))
        // .boxed().collect(Collectors.toList());

        List<Integer> workload = Arrays.stream(nd.sample(sampleSize)).map(x -> Math.round(x)).boxed()
                .map(Double::intValue).collect(Collectors.toList());

        // List<Integer> workloada = Arrays.stream(nd.sample(sampleSize)).map(x ->
        // Math.round(x)).boxed()
        // .mapToInt(Double::intValue).boxed().collect(Collectors.toList());

        // int[] a = { 1, 2, 3 };
        // Arrays.asList(a);
        // Double aa = new Double(1);
        // // long aa = 1;
        // Integer aaa = aa.intValue();

        // // Arrays.asList({1,2,3});
        // List<Long> test = new ArrayList(Arrays.asList(new int[] { 1, 2, 3 }));
        // List<Integer> haha = test.stream().map(x -> (Integer)
        // x).collect(Collectors.toList());

        // Jason: for debug !!!
        // System.out.println(workload);

        // Arrays.asList(Math.round(nd.sample(sampleSize)));
        return workload;
    }

    public double randomEvenDouble(int min, int max) {
        UniformDistr dis = new UniformDistr(min, max);
        return dis.sample();
    }

    public int randomEvenInt(int min, int max) {
        return (int) Math.round(randomEvenDouble(min, max));
    }

    public int generateWorkLoadSize() {
        return this.randomEvenInt(this.workloadSizeMin, this.workloadSizeMax);
    }

    private List<Double> genTimePoints(int timeSpan, int requestPerSecondAverage) {
        List<Integer> requestAmountEverySecond = this.genRequestAmountBySecond(requestPerSecondAverage, timeSpan);
        System.out.println(requestAmountEverySecond.size());
        List<Double> allTimePoints = new ArrayList<>();
        for (int start = 0; start < timeSpan; start++) {
            int amountps = requestAmountEverySecond.get(start);
            double[] timePoint = new double[amountps];
            for (int k = 0; k < amountps; k++) {
                timePoint[k] = randomEvenDouble(start, start + 1);
            }
            List<Double> timePoints = Arrays.stream(timePoint).sorted().boxed().collect(Collectors.toList());
            allTimePoints.addAll(timePoints);
        }
        System.out.println(allTimePoints.size());
        return allTimePoints;
    }

    private List<Map> genWorkLoads(int amountOfWorkloads, int SFClength, int workloadSizeAverage,
            int workloadSizeDeviation, int requestPerSecondAverage) {

        // List<Integer> requestAmountEverySecond =
        // this.genRequestAmountInEverySecond(requestPerSecondAverage, timeSpan);

        // List<Double> allTimePoints = new ArrayList<>();
        // for (int start = 0; start < timeSpan; start++) {
        // int amountps = requestAmountEverySecond.get(start);
        // double[] timePoint = new double[amountps];
        // for (int k = 0; k < amountps; k++) {
        // timePoint[k] = randomEvenDouble(start, start + 1);
        // }
        // List<Double> timePoints =
        // Arrays.stream(timePoint).sorted().boxed().collect(Collectors.toList());
        // allTimePoints.addAll(timePoints);
        // }

        if (SFClength <= 0 || amountOfWorkloads <= 0 || workloadSizeAverage <= 0 || workloadSizeDeviation <= 0
                || requestPerSecondAverage <= 0) {
            System.out.println("invalid variables in genWorkLoads!!!");
            return null;
        }
        Map<Integer, List<Integer>> clWorkload = new HashMap<>();
        Map<Integer, List<Integer>> wlTransmission = new HashMap<>();
        List<Integer> workLoadsFirstVNF = this.genVNFWorkloads(amountOfWorkloads, workloadSizeAverage,
                workloadSizeDeviation);

        // List<Double> allTimePoints = genTimePoints(timeSpan,
        // requestPerSecondAverage);

        List<Integer> VNFwlforFirstTransmission = this.genVNFWorkloads(amountOfWorkloads, 0, 1);
        wlTransmission.put(0, genTransmissionWorkload(VNFwlforFirstTransmission));

        clWorkload.put(0, workLoadsFirstVNF);

        for (int i = 1; i < SFClength; i++) {
            List<Integer> workLoadsNextVNF = this.genVNFWorkloads(amountOfWorkloads, workloadSizeAverage,
                    workloadSizeDeviation);
            clWorkload.put(i, workLoadsNextVNF);
            // wlTransmission.put(i - 1, genTransmissionWorkload(workLoadsNextVNF));
            // //Jason: change transmission

            // Jason: change transmission: the transmission happens before every
            // corresponding clworkload, i.e., transmission is the upload of the next
            // clworkload, it happens when we upload a clworkload to a VNF.

            wlTransmission.put(i, genTransmissionWorkload(workLoadsNextVNF));
        }

        List<Map> allWorkLoads = new ArrayList<>();
        allWorkLoads.add(clWorkload);
        allWorkLoads.add(wlTransmission);

        return allWorkLoads;
    }

    private String printWorkloadHeadForDebug(int SFClength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < SFClength; i++) {
            sb.append("cloudlet." + i);
            sb.append(",");
            sb.append("transmission." + i);
            sb.append(",");
        }
        sb.append("cloudlet." + SFClength);

        isHeadPrinted = true;
        out.printLine(sb.toString());
        return sb.toString();

    }

    private void writeWorkloadIntoFile(List<Double> timePoints, Map<Integer, List<Integer>> clWorkload,
            Map<Integer, List<Integer>> wlTransmission, List<String> SFC) {
        if (wlTransmission.size() != clWorkload.size() || timePoints.size() != clWorkload.get(0).size()) {
            System.out.println("error workload size or error timePoint number!!!!");
            return;
        }
        int totalAmountofWorkloads = timePoints.size();

        if (!isHeadPrinted) {
            printWorkloadHeadForDebug(clWorkload.size());
            isHeadPrinted = true;
        }

        // atime,VMname.1,submitted packet
        // size(zeros),CloudletLen1,link.1.2,VMname.2,p.1.2,CloudletLen2
        // 0.009217527,vm01,0,4931,default,vm02,4642800,43663

        for (int wlIndex = 0; wlIndex < totalAmountofWorkloads; wlIndex++) {
            StringBuilder perWorkload = new StringBuilder();
            perWorkload.append(timePoints.get(wlIndex));

            perWorkload.append(",");
            perWorkload.append(SFC.get(0)); // vnf_0
            perWorkload.append(",");
            perWorkload.append("0"); // submitted_packets

            int firstWLVNfj = clWorkload.get(0).get(wlIndex); // clworkload_0
            perWorkload.append(",");
            perWorkload.append(firstWLVNfj);

            for (int node = 1; node < clWorkload.size(); node++) {
                // perWorkload.append(",");
                // perWorkload.append(SFC.get(node));
                // perWorkload.append(",");
                // perWorkload.append(",");
                // perWorkload.append(SFC.get(0)); // vnf0
                // perWorkload.append(",");
                // perWorkload.append("0"); // submitted_packets

                // int ithWLVNfj = clWorkload.get(node).get(wlIndex); // clworkload_0
                // perWorkload.append(",");
                // perWorkload.append(ithWLVNfj);
                // perWorkload.append(",");

                // perWorkload.append("l12");// link_0_1

                // perWorkload.append(",");

                // int ithWLtransmissionj = wlTransmission.get(node).get(wlIndex);
                // perWorkload.append(ithWLtransmissionj);
                // }

                // perWorkload.append(",");
                // perWorkload.append(clWorkload.get(clWorkload.size() - 1).get(wlIndex));

                // // Log.print(perWorkload);
                // this.out.printLine(perWorkload.toString());

                // // out.print(perWorkload);

                perWorkload.append(",");

                perWorkload.append("l12");// link_i-1_i

                perWorkload.append(",");
                perWorkload.append(SFC.get(0)); // vnf_i
                perWorkload.append(",");
                // perWorkload.append("0"); // packet_size_i

                int ithWLtransmissionj = wlTransmission.get(node).get(wlIndex);
                perWorkload.append(ithWLtransmissionj); // packet_size_i

                perWorkload.append(",");
                int ithWLVNfj = clWorkload.get(node).get(wlIndex); // clworkload_i
                perWorkload.append(ithWLVNfj);

            }

            // perWorkload.append(",");
            // perWorkload.append(clWorkload.get(clWorkload.size() - 1).get(wlIndex));

            // // Log.print(perWorkload);
            this.out.printLine(perWorkload.toString());

            // out.print(perWorkload);
        }

    }

    // public void printWorkloadList(List<Workload> wls) {
    // for (Workload wl : wls) {
    // printWorkload(wl);
    // }
    // }

    public void printWorkload(List<Double> wls) {
        if (!isHeadPrinted) {
            int SFClength = wls.size();
            this.printWorkloadHeadForDebug(SFClength);
            isHeadPrinted = true;
        }

        double serveTime;

    }

    /**
     * 
     * @param sp1
     * @param sp2
     * @return
     */
    private List<SFSpec>[] createSFCombination(SFSpec[] sp1, SFSpec[] sp2) {
        int maxNum = Integer.max(sp1.length, sp2.length);

        @SuppressWarnings("unchecked")
        List<SFSpec>[] chains = new List[maxNum];
        for (int i = 0; i < maxNum; i++) {
            chains[i] = new ArrayList<SFSpec>();
            chains[i].add(sp1[i % sp1.length]);
            chains[i].add(sp2[i % sp2.length]);
        }
        return chains;
    }

    /**
     * 
     * @param webs
     * @param apps
     * @param dbs
     * @param startTime
     * @param endTime
     * @param linkBw
     * @param noscale
     */
    public void createSFCPolicy(VMSpec[] webs, VMSpec[] apps, VMSpec[] dbs, TimeGen startTime, TimeGen endTime,
            Long linkBw, boolean noscale) {
        int lb1Num = 1;
        int lb2Num = 1;
        int fwNum = 1;
        int idsNum = 1;

        if (noscale) {
            lb1Num = 1;
            lb2Num = 1;
            fwNum = 3;
            idsNum = 3;
        }

        SFSpec[] lb1s = new SFSpec[lb1Num];
        for (int i = 0; i < lb1Num; i++) {
            lb1s[i] = addSFLoadBalancer("lb1" + i, linkBw, startTime, endTime, noscale);
        }
        SFSpec[] lb2s = new SFSpec[lb2Num];
        for (int i = 0; i < lb2Num; i++) {
            lb2s[i] = addSFLoadBalancer("lb2" + i, linkBw, startTime, endTime, noscale);
        }
        SFSpec[] fws = new SFSpec[fwNum];
        for (int i = 0; i < fwNum; i++) {
            fws[i] = addSFFirewall("fw" + i, linkBw, startTime, endTime, noscale);
        }
        SFSpec[] idss = new SFSpec[idsNum];
        for (int i = 0; i < idsNum; i++) {
            idss[i] = addSFIntrusionDetectionSystem("ids" + i, linkBw, startTime, endTime, noscale);
        }

        // Policy for Web -> App
        {
            List<SFSpec>[] chains = createSFCombination(fws, lb1s);
            double expTime = 1.0;
            addSFCPolicyCollective(webs, apps, chains, expTime);
        }

        // Policy for App -> DB
        {
            List<SFSpec>[] chains = createSFCombination(lb2s, idss);
            double expTime = 1.0;
            addSFCPolicyCollective(apps, dbs, chains, expTime);
        }

        // Policy for DB -> App
        {
            List<SFSpec>[] chains = createSFCombination(idss, lb2s);
            double expTime = 1.0;
            addSFCPolicyCollective(dbs, apps, chains, expTime);
        }

        // Policy for App -> Web
        {
            List<SFSpec> chain = new ArrayList<SFSpec>();
            chain.add(lb1s[0]);
            @SuppressWarnings("unchecked")
            List<SFSpec>[] chains = new List[1];
            chains[0] = chain;
            double expTime = 1.0;
            addSFCPolicyCollective(apps, webs, chains, expTime);
        }
    }

    public void addSFCPolicyCollective(VMSpec[] srcList, VMSpec[] dstList, List<SFSpec>[] sfChains,
            double expectedTime) {
        int maxNum = Integer.max(srcList.length, dstList.length);
        for (int i = 0; i < maxNum; i++) {
            VMSpec src = srcList[i % srcList.length];
            VMSpec dest = dstList[i % dstList.length];
            List<SFSpec> sfChain = sfChains[i % sfChains.length];
            String linkname = getAutoLinkName(src, dest);
            String policyname = "sfc-" + linkname;

            addSFCPolicy(policyname, src, dest, linkname, sfChain, expectedTime);
        }
    }

    /**
     * 
     * @param name
     * @param linkBw
     * @param startTime
     * @param endTime
     * @param noscale
     * @return
     */
    public SFSpec addSFFirewall(String name, long linkBw, TimeGen startTime, TimeGen endTime, boolean noscale) {
        int pes = 8; // for AutoScale
        if (noscale)
            pes = 16; // for fixed number : total mips = 3*8000 = 24,000. MI/op = 25. -> 960
                      // operations / sec
        long mips = 10000;
        int ram = 8;
        long storage = 8;
        long bw = linkBw;
        // long miPerOperation = 25;
        long miPerOperation = 800;
        SFSpec sf = addSF(name, pes, mips, ram, storage, bw, startTime.getStartTime(), endTime.getEndTime(),
                miPerOperation, "Firewall");

        return sf;
    }

    /**
     * 
     * @param name
     * @param linkBw
     * @param startTime
     * @param endTime
     * @param noscale
     * @return
     */
    public SFSpec addSFLoadBalancer(String name, long linkBw, TimeGen startTime, TimeGen endTime, boolean noscale) {
        int pes = 2; // for AutoScale
        if (noscale)
            pes = 10; // for fixed number : total mips = 5*8000 = 40,000. MI/op = 10. -> 4,000
                      // operations / sec
        long mips = 10000;
        int ram = 8;
        long storage = 8;
        long bw = linkBw;
        long miPerOperation = 20; // 10
        SFSpec sf = addSF(name, pes, mips, ram, storage, bw, startTime.getStartTime(), endTime.getEndTime(),
                miPerOperation, "LoadBalancer");

        return sf;
    }

    /**
     * 
     * @param name
     * @param linkBw
     * @param startTime
     * @param endTime
     * @param noscale
     * @return
     */
    public SFSpec addSFIntrusionDetectionSystem(String name, long linkBw, TimeGen startTime, TimeGen endTime,
            boolean noscale) {
        int pes = 6; // for AutoScale
        if (noscale)
            pes = 12; // for fixed number : total mips = 5*8000 = 40,000. MI/op = 30. -> 1333.3333
                      // operations / sec
        long mips = 10000;
        int ram = 8;
        long storage = 8;
        long bw = linkBw;
        long miPerOperation = 200;// 30;
        SFSpec sf = addSF(name, pes, mips, ram, storage, bw, startTime.getStartTime(), endTime.getEndTime(),
                miPerOperation, "IDS");

        return sf;
    }

    public double getMinTimeSpan() {
        return minTimeSpan;
    }

    public void setMinTimeSpan(double minTimeSpan) {
        this.minTimeSpan = minTimeSpan;
    }

}
