/**
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
public class Utils {
	/**
	 * Generates a string, which represents the given time.
	 * 
	 * @param time in seconds
	 * @return the generated string
	 */
	public static String getTimeString(int time) {
		final int days = time / (24 * 60 * 60);
		time = time % (24 * 60 * 60);
		final int hours = time / (60 * 60);
		time = time % (60 * 60);
		final int minutes = time / 60;
		time = time % (60);
		final int seconds = time;
		String strReturn = "< 1 min";
		if (days > 0) {
			strReturn = Integer.toString(days);
			if (hours > 0) {
				strReturn += "+";
			}
			if (days == 1) {
				strReturn += " day";
			} else {
				strReturn += " days";
			}
		} else if (hours > 0) {
			strReturn = Integer.toString(hours);
			if (minutes > 0) {
				strReturn += "+";
			}
			if (hours == 1) {
				strReturn += " hour";
			} else {
				strReturn += " hours";
			}
		} else if (minutes > 0) {
			strReturn = Integer.toString(minutes);
			if (seconds > 0) {
				strReturn += "+";
			}
			if (minutes == 1) {
				strReturn += " min";
			} else {
				strReturn += " mins";
			}
		}
		return strReturn;
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
}
