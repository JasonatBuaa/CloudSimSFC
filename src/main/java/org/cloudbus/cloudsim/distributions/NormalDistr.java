package org.cloudbus.cloudsim.distributions;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

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
	 * @param seed the seed
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
	}

	@Override
	public double sample() {
		return numGen.sample();
	}

}
