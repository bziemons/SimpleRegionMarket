/*
 * SimpleRegionMarket
 * Copyright (C) 2014  theZorro266 <http://www.thezorro266.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.thezorro266.bukkit.srm;

/**
 * Class with several util functions.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class Utils {
	/**
	 * Generates a string, which represents the time left until the given time.
	 * 
	 * @param time in seconds
	 * @return the generated string
	 */
	public static String getTimeLeft(int time) {
		return getTimeString(time - (int) (System.currentTimeMillis() / 1000));
	}

	/**
	 * Generates a time string for the passed time in seconds.
	 *
	 * @param time as seconds
	 * @return the time representated as string
	 */
	public static String getTimeString(int time) {
		final int days = time / (24 * 60 * 60);
		time = time % (24 * 60 * 60);
		final int hours = time / (60 * 60);
		time = time % (60 * 60);
		final int minutes = time / 60;
		time = time % (60);
		final int seconds = time;

		StringBuilder sb = new StringBuilder();
		if (days > 0) {
			sb.append(days);
			if (hours > 0) {
				sb.append("+");
			}
			sb.append(" day");
			if (days > 1) {
				sb.append("s");
			}
		} else if (hours > 0) {
			sb.append(hours);
			if (minutes > 0) {
				sb.append("+");
			}
			sb.append(" hour");
			if (hours > 1) {
				sb.append("s");
			}
		} else if (minutes > 0) {
			sb.append(minutes);
			if (seconds > 0) {
				sb.append("+");
			}
			sb.append(" min");
			if (minutes > 1) {
				sb.append("s");
			}
		} else {
			sb.append("< 1 min");
		}
		return sb.toString();
	}

	/**
	 * Parses the given string for a time.
	 * 
	 * @param timeString the time as a string
	 * @return the time in seconds
	 */
	public static int parseTime(String timeString) {
		int time = 0;
		int i, u;

		i = timeString.indexOf("d");
		if (i > 0) {
			if (timeString.charAt(i - 1) == ' ' && i > 1) {
				i--;
			}
			u = i - 1;
			while (u > 0 && Character.isDigit(timeString.charAt(u - 1))) {
				u--;
			}
			time += Integer.parseInt(timeString.substring(u, i)) * 24 * 60 * 60;
		}

		i = timeString.indexOf("h");
		if (i > 0) {
			if (timeString.charAt(i - 1) == ' ' && i > 1) {
				i--;
			}
			u = i - 1;
			while (u > 0 && Character.isDigit(timeString.charAt(u - 1))) {
				u--;
			}
			time += Integer.parseInt(timeString.substring(u, i)) * 60 * 60;
		}

		i = timeString.indexOf("m");
		if (i > 0) {
			if (timeString.charAt(i - 1) == ' ' && i > 1) {
				i--;
			}
			u = i - 1;
			while (u > 0 && Character.isDigit(timeString.charAt(u - 1))) {
				u--;
			}
			time += Integer.parseInt(timeString.substring(u, i)) * 60;
		}

		return time;
	}

	public static class TimeMeasurement {
		private long start;

		public TimeMeasurement() {
			start = System.nanoTime();
		}

		public int diff() {
			return (int) ((System.nanoTime() - start) / 1000000L);
		}
	}
}
