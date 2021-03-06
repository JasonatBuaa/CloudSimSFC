/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2017, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn;

import java.util.Iterator;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

public class CloudSimEx extends CloudSim {
	private static long startTime;

	private static void setStartTimeMillis(long startedTime) {
		startTime = startedTime;
	}

	/**
	 * Jason: set system variable starttime in currentTimeMillis
	 */
	public static void setStartTime() {
		setStartTimeMillis(System.currentTimeMillis());
	}

	/**
	 * Jason: 返回经过的毫秒数
	 * 
	 * @return 经过的时间（毫秒/1000=秒）
	 */
	public static long getElapsedTimeSec() {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - startTime;
		elapsedTime /= 1000;

		return elapsedTime;
	}

	/**
	 * Jason:
	 * 
	 * @return String hour: minute: second
	 */
	public static String getElapsedTimeString() {
		String ret = "";
		long elapsedTime = getElapsedTimeSec();
		ret = "" + elapsedTime / 3600 + ":" + (elapsedTime / 60) % 60 + ":" + elapsedTime % 60;

		return ret;
	}

	public static int getNumFutureEvents() {
		return future.size() + deferred.size();
	}

	public static boolean hasMoreEvent(int excludeEventTag) {
		if (future.size() > 0) {
			Iterator<SimEvent> fit = future.iterator();
			while (fit.hasNext()) {
				SimEvent ev = fit.next();
				if (ev.getTag() != excludeEventTag)
					return true;
			}
		}
		if (deferred.size() > 0) {
			Iterator<SimEvent> fit = deferred.iterator();
			while (fit.hasNext()) {
				SimEvent ev = fit.next();
				if (ev.getTag() != excludeEventTag)
					return true;
			}
		}
		return false;
	}

	public static double getNextEventTime() {
		if (future.size() > 0) {
			Iterator<SimEvent> fit = future.iterator();
			SimEvent first = fit.next();
			if (first != null)
				return first.eventTime();
		}
		return -1;
	}
}
