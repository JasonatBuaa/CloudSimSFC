/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2017, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn;

public class Configuration_Old {
    // public static String workingDirectory = "./jason";
    public static String workingDirectory = "./";
    public static String experimentName = "";

//	public static String ROOT_FOLDER = "Compare/";

//	public static String RESULT_FOLDER = ROOT_FOLDER + "results/";

    public static String ROOT_FOLDER = null;
    public static String RESULT_FOLDER = null;

    public static void setRootFolder(String rootFolder) {
        Configuration_Old.ROOT_FOLDER = rootFolder;
        Configuration_Old.RESULT_FOLDER = ROOT_FOLDER + "result/";
    }


    // Monitoring setup

    /*
     * /////////////////////////////////FOR TEST ONLY public static final double
     * monitoringTimeInterval = 1; // every 60 seconds, polling utilization.
     *
     * public static final double overbookingTimeWindowInterval =
     * monitoringTimeInterval; // Time interval between points public static final
     * double overbookingTimeWindowNumPoints = 5; // How many points to track
     *
     * public static final double migrationTimeInterval =
     * overbookingTimeWindowInterval*overbookingTimeWindowNumPoints; // every 1
     * seconds, polling utilization.
     *
     * public static final double OVERBOOKING_RATIO_MAX = 1.0; public static final
     * double OVERBOOKING_RATIO_MIN = 0.1; // Guarantee 10% public static final
     * double OVERBOOKING_RATIO_INIT = 0.5;
     *
     * public static final double OVERLOAD_THRESHOLD = 0.70; public static final
     * double OVERLOAD_THRESHOLD_ERROR = 1.0 - OVERLOAD_THRESHOLD; public static
     * final double OVERLOAD_THRESHOLD_BW_UTIL = 0.9;
     *
     * public static final double UNDERLOAD_THRESHOLD_HOST = 0.5; public static
     * final double UNDERLOAD_THRESHOLD_HOST_BW = 0.5; public static final double
     * UNDERLOAD_THRESHOLD_VM = 0.1;
     *
     * public static final double DECIDE_SLA_VIOLATION_GRACE_ERROR = 1.03; //
     * Expected time + 5% is accepted as SLA provided
     *
     * public static final double OVERBOOKING_RATIO_UTIL_PORTION = 0.5; //Give 10%
     * more than historical utilization public static final double
     * OVERLOAD_HOST_PERCENTILE_THRESHOLD = 0.3; // If 5% of time is overloaded,
     * host is overloaded
     *
     * public static final double CPU_REQUIRED_MIPS_PER_WORKLOAD_PERCENT = 0.20; //
     * 20% of CPU resource is required to process 1 workload
     *
     * ///*
     *
     * //////////////////////////// FOR Overbooking EXPERIMENT public static final
     * double monitoringTimeInterval = 180; // every 60 seconds, polling
     * utilization.
     *
     * public static final double overbookingTimeWindowInterval =
     * monitoringTimeInterval; // Time interval between points public static final
     * double overbookingTimeWindowNumPoints = 10; // How many points to track
     *
     * public static final double migrationTimeInterval =
     * overbookingTimeWindowInterval*overbookingTimeWindowNumPoints; // every 1
     * seconds, polling utilization.
     *
     * public static final double OVERBOOKING_RATIO_MAX = 1.0; public static final
     * double OVERBOOKING_RATIO_MIN = 0.9; //public static final double
     * OVERBOOKING_RATIO_MIN = 0.4; public static double OVERBOOKING_RATIO_INIT =
     * 0.7;
     *
     * public static final double OVERLOAD_THRESHOLD = 0.70; public static final
     * double OVERLOAD_THRESHOLD_ERROR = 1.0 - OVERLOAD_THRESHOLD; public static
     * final double OVERLOAD_THRESHOLD_BW_UTIL = 0.7;
     *
     * public static final double UNDERLOAD_THRESHOLD_HOST = 0.5; public static
     * final double UNDERLOAD_THRESHOLD_HOST_BW = 0.5; public static final double
     * UNDERLOAD_THRESHOLD_VM = 0.1;
     *
     * public static final double DECIDE_SLA_VIOLATION_GRACE_ERROR = 1.03; //
     * Expected time + 5% is accepted as SLA provided
     *
     * public static final double OVERBOOKING_RATIO_UTIL_PORTION =
     * (OVERBOOKING_RATIO_MAX - OVERBOOKING_RATIO_MIN)*0.2; public static final
     * double OVERLOAD_HOST_PERCENTILE_THRESHOLD = 0.3; // If 5% of time is
     * overloaded, host is overloaded
     *
     * public static final double CPU_REQUIRED_MIPS_PER_WORKLOAD_PERCENT =
     * 0.2;//0.05; // 20% of CPU resource is required to process 1 workload
     *
     * public static final double HOST_ACTIVE_AVERAGE_UTIL_THRESHOLD = 0.1;
     *
     * //
     */
    //////////////////////////// Default value
    public static final int MINUTE = 60;
    public static final int HOUR = MINUTE * 60;
    public static final int DAY = HOUR * 24;

    //	public static final double SIMULATION_DURATION = 10001;
    public static final double SIMULATION_DURATION = 1100;

    public static final double CPU_SIZE_MULTIPLY = 1; // Multiply all the CPU size for scale. Default =1 (No amplify)
    public static final double NETWORK_PACKET_SIZE_MULTIPLY = 1024; // kB Multiply all the network packet size. Default =1
//    public static final double NETWORK_PACKET_SIZE_MULTIPLY = 1; // kB Multiply all the network packet size. Default =1
    // (No
    // amplify)
//	public static final int QUEUE_SPACE_MULTIPLY = 30;
	public static final int QUEUE_SPACE_MULTIPLY = 1024; //virtual 里面queue size的单位是MB，乘以此因子后换算为 KB
//    public static final int QUEUE_SPACE_MULTIPLY = 1; //virtual 里面queue size的单位改为KB
    // public static final int WORKLOAD_MULTIPLY = 10;

    public static double monitoringTimeInterval = 5; // every 1800 seconds, polling utilization.

    public static final double overbookingTimeWindowInterval = monitoringTimeInterval; // Time interval between points
    public static final double overbookingTimeWindowNumPoints = 1;// Double.POSITIVE_INFINITY; // No migration. How many
    // points to track
    // public static final double overbookingTimeWindowNumPoints = 10; // How many
    // points to track

    public static double migrationTimeInterval = overbookingTimeWindowInterval * overbookingTimeWindowNumPoints; // every
    // 1
    // seconds,
    // polling
    // utilization.

    public static final double OVERBOOKING_RATIO_MAX = 1.0;
    public static final double OVERBOOKING_RATIO_MIN = 1.0; // No overbooking
    public static double OVERBOOKING_RATIO_INIT = 1.0; // No overbooking

    public static final double OVERLOAD_THRESHOLD = 1.0;
    public static final double OVERLOAD_THRESHOLD_ERROR = 1.0 - OVERLOAD_THRESHOLD;
    public static final double OVERLOAD_THRESHOLD_BW_UTIL = 1.0;

    public static final double UNDERLOAD_THRESHOLD_HOST = 0;
    public static final double UNDERLOAD_THRESHOLD_HOST_BW = 0.3;
    public static final double UNDERLOAD_THRESHOLD_VM = 0.3;

    public static final double DECIDE_SLA_VIOLATION_GRACE_ERROR = 100.0; // Expected time + 5% is accepted as SLA
    // provided

    public static final double OVERBOOKING_RATIO_UTIL_PORTION = (OVERBOOKING_RATIO_MAX - OVERBOOKING_RATIO_MIN) * 0.2;
    public static final double OVERLOAD_HOST_PERCENTILE_THRESHOLD = 0.0; // If 5% of time is overloaded, host is
    // overloaded

    public static final double CPU_REQUIRED_MIPS_PER_WORKLOAD_PERCENT = 1.0;// 0.05; // 20% of CPU resource is required
    // to process 1 workload

    public static final double HOST_ACTIVE_AVERAGE_UTIL_THRESHOLD = 0;

    public static final double SFC_OVERLOAD_THRESHOLD_VM = 0.7;
    public static final double SFC_OVERLOAD_THRESHOLD_BW = 0.7;
    public static final double SFC_UNDERLOAD_THRESHOLD_BW = 0.4;
    public static final double SFC_UNDERLOAD_THRESHOLD_VM = 0.4;

    public static boolean ENABLE_TRANSMISSION_JITTER = true;
    public static boolean ENABLE_COMPUTATION_JITTER = true;

    public static double TR_JITTER_INTERVAL = 1;
    public static double CP_JITTER_INTERVAL = 1;

    public static long normal_dist_call_frequency = 0;
    public static long channel_freq = 0;
    public static long vm_freq = 0;

    public static long total_bw_samplings = 0;
    public static long total_mips_samplings = 0;

    //    public static final double NETWORK_JITTER_SIGMA = 30;
    public static final double NETWORK_JITTER_SIGMA = 3200;
//	public static ContinuousDistribution network_jitter = null;

    public static String TR_JITTER_MODEL = "NormalDist";
    public static String CP_JITTER_MODEL = "NormalDist";

    // public static boolean DISABLE_FAILURE_RECOVERY = true; // True: Do not injuct
    // fail over event.
//	public static boolean DISABLE_FAILURE_RECOVERY = true; // True: Do not injuct fail over event.
    public static boolean DISABLE_FAILURE_RECOVERY = false; //true; // True: Do not injuct fail over event.

    public static double FAILURE_DETECTION_DELAY = 2.0; // Monitoring delay for server failure
    public static double RECOVERY_DETECTION_DELAY = 2.0;// Monitoring delay for server recovery

    // Jason: Do not use queue
    // public static boolean DISABLE_MEMORY_QUEUE = true; // True: Do not use
    // MemoryQueue.

    // Jason: Use queue
    public static boolean DISABLE_MEMORY_QUEUE = false; // True: Do not use MemoryQueue.

    public static final double TIME_OUT = 1000; // Double.POSITIVE_INFINITY; //

    public static final int DEFAULT_FLOW_ID = 0;

    public static boolean ENABLE_SFC = true;

    public static boolean SFC_LATENCY_AWARE_ENABLE;

//	public static boolean SFC_AUTOSCALE_ENABLE = true;
//	public static boolean SFC_AUTOSCALE_ENABLE_BW = true;
//	public static boolean SFC_AUTOSCALE_ENABLE_VM = true;
//	public static boolean SFC_AUTOSCALE_ENABLE_SCALE_DOWN_BW = true;
//	public static boolean SFC_AUTOSCALE_ENABLE_SCALE_DOWN_VM = false;
//	public static boolean SFC_AUTOSCALE_ENABLE_VM_VERTICAL = true;

    public static boolean SFC_AUTOSCALE_ENABLE = false;
    public static boolean SFC_AUTOSCALE_ENABLE_BW = false;
    public static boolean SFC_AUTOSCALE_ENABLE_VM = false;
    public static boolean SFC_AUTOSCALE_ENABLE_SCALE_DOWN_BW = false;
    public static boolean SFC_AUTOSCALE_ENABLE_SCALE_DOWN_VM = false;
    public static boolean SFC_AUTOSCALE_ENABLE_VM_VERTICAL = false;

    public static boolean DEBUG_RESULT_WRITE_DETAIL = true;
    public static boolean DEBUG_PRINT_DETAIL_SIZE_TIME = true;
    public static boolean DEBUG_CHECK_OVER_TIME_REQUESTS = false;

    // */
}
