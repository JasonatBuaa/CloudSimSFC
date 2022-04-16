package org.cloudbus.cloudsim.distributions;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.cloudbus.cloudsim.sdn.Configuration;

/**
 * 
 * @author
 * @since
 */
public class NormalDistr implements ContinuousDistribution {

	/** The internal Log-normal pseudo random number generator. */
	// private final LogNormalDistribution numGen;
	private final NormalDistribution numGen;

	/**
	 * Instantiates a new Log-normal pseudo random number generator.
	 * 
	 * @param seed  the seed
	 * @param shape the shape
	 * @param scale the scale
	 */
	public NormalDistr(Random seed, double mean, double variance) {
		this(mean, variance);
		numGen.reseedRandomGenerator(seed.nextLong());
	}

	/**
	 * Instantiates a new Log-normal pseudo random number generator.
	 * 
	 * @param shape the shape
	 * @param scale the scale
	 */
	public NormalDistr(double mean, double variance) {
		numGen = new NormalDistribution(mean, variance);
		Configuration.normal_dist_call_frequency +=1;
	}

	@Override
	public double sample() {
		return numGen.sample();
	}

	/**
	 * 
	 * Jason: The sample result is controlled by the min/max threshold
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public double sample(double min, double max) {
		double result = numGen.sample();
		while (result < min || result > max)
			result = numGen.sample();
		return result;
	}

	@Override
	public double sampleInInterval(double min, double max) {
		double result = sample();
		while (result < min || result > max)
			result = sample();
		return result;
	}



}
