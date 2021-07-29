package org.cloudbus.cloudsim.sdn.nos;

// import javax.security.auth.login.Configuration;
import org.cloudbus.cloudsim.sdn.Configuration;
import org.cloudbus.cloudsim.distributions.NormalDistr;

public class PerformanceJitter {
	protected NormalDistr nd = null;

	// Processing requests

	public PerformanceJitter() {
		// this.nd = new NormalDistr();

	}

	// Jason: Per-Event sampling : This function should be called by the Channel
	// Manager during each transmission.
	public double sampleTransmissionPerformance(double link_bw) {
		double sigma = Configuration.NETWORK_JITTER_SIGMA;
		if (Configuration.ENABLE_TRANSMISSION_JITTER) {
			this.nd = new NormalDistr(link_bw, sigma);
			System.out.println("Jitter!!!!!! Check!!!!!!!");
			return nd.sample(link_bw / 1.5, link_bw * 1.2);

		} else
			return link_bw;
	}

	// Jason: Discuss!! Performance Jitter should be implemented inside the VM ?
	// Or I just leave it here ?

	public double sampleComputationPerformance(double mips, double jitter_sigma) {
		if (Configuration.ENABLE_COMPUTATION_JITTER) {
			this.nd = new NormalDistr(mips, jitter_sigma);
			// return Math.min(nd.sample(), mips);
			System.out.println("Jitter!!!!!! Check!!!!!!!");
			return nd.sample(mips / 1.5, mips * 1.2);
		} else
			return mips;
	}
}
