/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * A pseudo random number generator following the
 * <a href="https://en.wikipedia.org/wiki/Exponential_distribution">Exponential
 * distribution</a>.
 * 
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public class ExponentialDistr implements ContinuousDistribution {

	/** The internal exponential number generator. */
	private final ExponentialDistribution numGen;

	/**
	 * Creates a new exponential pseudo random number generator.
	 * 
	 * @param seed the seed to be used.
	 * @param mean the mean for the distribution.
	 */
	public ExponentialDistr(long seed, double mean) {
		this(mean);
		numGen.reseedRandomGenerator(seed);
	}

	/**
	 * Creates a new exponential pseudo random number generator.
	 * 
	 * @param mean the mean for the distribution.
	 */
	public ExponentialDistr(double mean) {
		numGen = new ExponentialDistribution(mean);
	}

	@Override
	public double sample() {
		return numGen.sample();
	}

	public double sample(double min, double max) {
		double value = sample();
		while (value > max || value < min) {
			value = sample();
		}
		return value;
	}

	@Override
	public double sampleInInterval(double min, double max) {
		double result = sample();
		while (result < min || result > max)
			result = sample();
		return result;
	}

}
