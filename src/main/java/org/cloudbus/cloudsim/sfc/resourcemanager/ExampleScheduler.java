package org.cloudbus.cloudsim.sfc.resourcemanager;

import org.cloudbus.cloudsim.Host;
// import org.cloudbus.cloudsim.sfc.monitor.impl.MonitorImpl;
import org.cloudbus.cloudsim.sfc.parser.Resource;
import org.cloudbus.cloudsim.sfc.parser.SFCWorkload;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunction;
import org.cloudbus.cloudsim.sfc.parser.ServiceFunctionChain;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.DeploymentScheduler;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.VirtualTopologyVmSF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.util.concurrent.Monitor;

/**
 * @author chengr
 * @version 1.0.0
 * @ClassName Scheduler.java
 * @Description TODO
 * @createTime 2021-07-24 22:20
 */
// 继承DeploymentScheduler避免重复开发,后面需要自定义产生link和policy的方法的话,可以把继承去了重写
public class ExampleScheduler extends DeploymentScheduler {
    private ServiceFunctionChain needSchedule;
    private MonitorInterface monitor;

    public ExampleScheduler(ServiceFunctionChain needSchedule, MonitorInterface monitor,
            List<ServiceFunctionChain> serverFunctionChains, List<SFCWorkload> sfcWorkloads, List<Resource> resources) {
        super(serverFunctionChains, sfcWorkloads, resources);
        this.needSchedule = needSchedule;
        this.monitor = monitor;
    }

    @Override
    public void generateNodes(List<SFCWorkload> sfcWorkloads, List<Resource> resources,
            List<ServiceFunctionChain> serviceFunctionChains) {
        scheduleByGreedy();
    }

    public void scheduleByGreedy() {
        double inOutRatio = 1.0;
        List<String> chain = needSchedule.getChain();
        int index = 0;
        for (; index < chain.size();) {
            List<ServiceFunction> serviceFunctionTogether = new ArrayList<>();
            inOutRatio = 1.0;
            // 如果chain的某一个SF 出/进>1,就继续循环,找下一个捆绑部署,直到 出/进 < 1;如果某一个SF 出/进 < 1，就单独部署
            do {
                ServiceFunction serviceFunction = ServiceFunction.serverFunctionMap.get(chain.get(index));
                inOutRatio *= serviceFunction.getOutputRate() / serviceFunction.getInputRate();
                serviceFunctionTogether.add(serviceFunction);
                index++;
            } while (inOutRatio > 1 && index < chain.size());

            String ingressDc = needSchedule.getIngressDCs().get(0).getDC();
            String selectDc = "";
            // 优先在ingress中尝试部署
            if (isIngressSuitable(ingressDc, serviceFunctionTogether)) {
                selectDc = ingressDc;
            } else {
                selectDc = findOtherSuitableDC(serviceFunctionTogether);
            }
            String physicalChainName = needSchedule.getName() + "_psfc";
            for (ServiceFunction serviceFunction : serviceFunctionTogether) {
                String sfInstanceName = physicalChainName + serviceFunction.getName();
                nodes.add(new VirtualTopologyVmSF(sfInstanceName, serviceFunction.getName(), 1000, 1, 500, 128,
                        selectDc));
            }
        }
    }

    public boolean isIngressSuitable(String ingressDc, List<ServiceFunction> serviceFunctions) {
        List<Host> hosts = monitor.getRemainingResourcesByDC(ingressDc);
        return isSatisfy(serviceFunctions, hosts);
    }

    public String findOtherSuitableDC(List<ServiceFunction> serviceFunctions) {
        Map<String, List<Host>> dcMap = monitor.getRemainingResources();
        for (String dcName : dcMap.keySet()) {
            List<Host> resource = dcMap.get(dcName);
            if (isSatisfy(serviceFunctions, resource)) {
                return dcName;
            }
        }
        return null;
    }

    public boolean isSatisfy(List<ServiceFunction> serviceFunctions, List<Host> hosts) {
        // todo 支持更多类型的资源,暂时SF定义里没有找到更多的资源参数
        double mipsNeed = 0, mipsHas = 0;
        for (ServiceFunction serviceFunction : serviceFunctions) {
            mipsNeed += serviceFunction.getPerformance() * 100;
        }

        for (Host host : hosts) {
            mipsHas += host.getAvailableMips();
        }

        if (mipsHas < mipsNeed) {
            return false;
        }
        return true;
    }
}
