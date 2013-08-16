/*
 * SimpleRegionMarket
 * Copyright (C) 2013  theZorro266 <http://www.thezorro266.com>
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

package com.thezorro266.bukkit.srm.templates;

import static com.thezorro266.bukkit.srm.helpers.Sign.SIGN_LINE_COUNT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.Sign;

public abstract class IntelligentSignTemplate extends Template {
	protected String[] signInput;
	protected Map<String, String[]> signOutput;

	public IntelligentSignTemplate(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		signInput = new String[SIGN_LINE_COUNT];
		for (int i = 0; i < SIGN_LINE_COUNT; i++) {
			signInput[i] = templateConfigSection.getString("input." + (i + 1));
		}
		signOutput = new HashMap<String, String[]>(4);
		for (String outputType : templateConfigSection.getConfigurationSection("output").getKeys(false)) {
			String[] tempOutput = new String[SIGN_LINE_COUNT];
			for (int i = 0; i < SIGN_LINE_COUNT; i++) {
				tempOutput[i] = templateConfigSection.getString("output." + outputType + "." + (i + 1));
			}
			signOutput.put(outputType, tempOutput);
		}
	}

	public abstract void replacementMap(HashMap<String, String> replacementMap);

	@Override
	public boolean changeSign(Player player, Location location, String[] lines) {
		if (lines[0].equalsIgnoreCase(signInput[0])) {
			Map<String, String> inputMap = getSignInput(this, lines);

			// Change Lines
		}
		return false;
	}

	protected void updateSign(Sign sign) {
		if (sign.getRegion().getTemplate().equals(this)) {
			String[] lines = signOutput.get(state).clone();
			for (int i = 0; i < SIGN_LINE_COUNT; i++) {
				lines[i] = replaceTokens(lines[i], sign.getRegion().getReplacementMap());
			}
			sign.setContent(lines);
		}
	}

	public static String replaceTokens(String text, Map<String, String> replacementMap) {
		final Pattern pattern = Pattern.compile("\\[\\[(.+?)\\]\\]");
		final Matcher matcher = pattern.matcher(text);
		final StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			try {
				final String replacement = replacementMap.get(matcher.group(1));
				if (replacement != null) {
					matcher.appendReplacement(buffer, "");
					buffer.append(replacement);
				}
			} catch (final Exception e) {
				SimpleRegionMarket.getInstance().getLogger().info("Replacement map has a misconfiguration at " + matcher.group(1));
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	public static Map<String, String> getSignInput(IntelligentSignTemplate signTemplate, String[] lines) {
		final HashMap<String, String> outputMap = new HashMap<String, String>();
		for (int i = 0; i < SIGN_LINE_COUNT; i++) {
			final String inputLine = signTemplate.signInput[i];
			if (inputLine != null && !inputLine.isEmpty()) {

				Pattern pattern = Pattern.compile("\\[\\[(.+?)\\]\\]");
				Matcher matcher = pattern.matcher(inputLine);
				String newPattern = "";
				final ArrayList<String> keys = new ArrayList<String>();
				while (matcher.find()) {
					for (int u = 0; u < matcher.groupCount(); u++) {
						keys.add(matcher.group(u + 1));
					}
				}
				newPattern = matcher.replaceAll("(.+)");

				pattern = Pattern.compile(newPattern);
				matcher = pattern.matcher(lines[i]);
				final ArrayList<String> vars = new ArrayList<String>();
				while (matcher.find()) {
					for (int u = 0; u < matcher.groupCount(); u++) {
						vars.add(matcher.group(u + 1));
					}
				}
				for (int u = 0; u < keys.size(); u++) {
					if (u < vars.size()) {
						outputMap.put(keys.get(u), vars.get(u));
					} else {
						outputMap.put(keys.get(u), "");
					}
				}
			}
		}
		return outputMap;
	}

	public static String getSignTime(long time) {
		time = time / 1000; // From ms to sec
		final int days = (int) (time / (24 * 60 * 60));
		time = time % (24 * 60 * 60);
		final int hours = (int) (time / (60 * 60));
		time = time % (60 * 60);
		final int minutes = (int) (time / 60);
		time = time % (60);
		final int seconds = (int) time;
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

	public static long parseSignTime(String timestring) {
		long time = 0;
		int i, u;

		i = timestring.indexOf("d");
		if (i > 0) {
			if (timestring.charAt(i - 1) == ' ' && i > 1) {
				i--;
			}
			u = i - 1;
			while (u > 0 && Character.isDigit(timestring.charAt(u - 1))) {
				u--;
			}
			time += Long.parseLong(timestring.substring(u, i)) * 24 * 60 * 60 * 1000;
		}

		i = timestring.indexOf("h");
		if (i > 0) {
			if (timestring.charAt(i - 1) == ' ' && i > 1) {
				i--;
			}
			u = i - 1;
			while (u > 0 && Character.isDigit(timestring.charAt(u - 1))) {
				u--;
			}
			time += Long.parseLong(timestring.substring(u, i)) * 60 * 60 * 1000;
		}

		i = timestring.indexOf("m");
		if (i > 0) {
			if (timestring.charAt(i - 1) == ' ' && i > 1) {
				i--;
			}
			u = i - 1;
			while (u > 0 && Character.isDigit(timestring.charAt(u - 1))) {
				u--;
			}
			time += Long.parseLong(timestring.substring(u, i)) * 60 * 1000;
		}

		return time;
	}
}
