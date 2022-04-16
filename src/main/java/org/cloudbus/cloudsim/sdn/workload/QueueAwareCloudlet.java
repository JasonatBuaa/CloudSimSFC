/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.sdn.workload;

import org.cloudbus.cloudsim.Cloudlet;

import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.core.CloudSim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
// import org.cloudbus.cloudsim.util.gridsim;

/**
 * Cloudlet is an extension to the cloudlet. It stores, despite all the
 * information encapsulated in the Cloudlet, the ID of the VM running it.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @todo The documentation is wrong. Cloudlet isn't extending any class.
 * @since CloudSim Toolkit 1.0
 */
public class QueueAwareCloudlet extends Cloudlet {

    private int queueRequired = Integer.MAX_VALUE;

    public QueueAwareCloudlet(final int cloudletId, final long cloudletLength, final int pesNumber, final long cloudletFileSize,
                              final long cloudletOutputSize, final UtilizationModel utilizationModelCpu,
                              final UtilizationModel utilizationModelRam, final UtilizationModel utilizationModelBw) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
    }


    public QueueAwareCloudlet(final int cloudletId, final long cloudletLength, final int pesNumber, final long cloudletFileSize,
                              final long cloudletOutputSize, final UtilizationModel utilizationModelCpu,
                              final UtilizationModel utilizationModelRam, final UtilizationModel utilizationModelBw, final boolean record,
                              final List<String> fileList) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record, fileList);

    }


    public QueueAwareCloudlet(final int cloudletId, final long cloudletLength, final int pesNumber, final long cloudletFileSize,
                              final long cloudletOutputSize, final UtilizationModel utilizationModelCpu,
                              final UtilizationModel utilizationModelRam, final UtilizationModel utilizationModelBw,
                              final List<String> fileList) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, fileList);

    }


    public QueueAwareCloudlet(final int cloudletId, final long cloudletLength, final int pesNumber, final long cloudletFileSize,
                              final long cloudletOutputSize, final UtilizationModel utilizationModelCpu,
                              final UtilizationModel utilizationModelRam, final UtilizationModel utilizationModelBw,
                              final boolean record) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record);

    }

}
