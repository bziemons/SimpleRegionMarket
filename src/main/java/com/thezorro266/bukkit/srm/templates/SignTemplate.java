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

package com.thezorro266.bukkit.srm.templates;

import static com.thezorro266.bukkit.srm.factories.SignFactory.Sign.SIGN_LINE_COUNT;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.factories.SignFactory.Sign;
import com.thezorro266.bukkit.srm.helpers.Location;

public abstract class SignTemplate extends Template {
	protected final String[] signInput = new String[SIGN_LINE_COUNT];
	protected final HashMap<String, String[]> signOutput = new HashMap<String, String[]>();

	public SignTemplate(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		for (int i = 0; i < SIGN_LINE_COUNT; i++) {
			signInput[i] = templateConfigSection.getString("input." + (i + 1));
		}

		for (String outputType : templateConfigSection.getConfigurationSection("output").getKeys(false)) {
			String[] tempOutput = new String[SIGN_LINE_COUNT];
			for (int i = 0; i < SIGN_LINE_COUNT; i++) {
				tempOutput[i] = templateConfigSection.getString("output." + outputType + "." + (i + 1));
			}
			signOutput.put(outputType, tempOutput);
		}
	}

	private static String replaceTokens(String text, Map<String, String> replacementMap) {
		Matcher matcher = Pattern.compile("(\\[\\[\\w+\\]\\])").matcher(text);
		if (matcher.find()) {
			ArrayList<SimpleEntry<Integer, String>> entryList = new ArrayList<SimpleEntry<Integer, String>>();
			do {
				entryList.add(new SimpleEntry<Integer, String>(matcher.end(), matcher.group()));
			} while (matcher.find());

			for (int i = entryList.size() - 1; i >= 0; --i) {
				SimpleEntry<Integer, String> entry = entryList.get(i);
				StringBuilder newText = new StringBuilder();
				newText.append(text.substring(0, entry.getKey() - entry.getValue().length()));
				newText.append(replacementMap.get(entry.getValue().substring(2, entry.getValue().length() - 2)));
				newText.append(text.substring(entry.getKey()));
				text = newText.toString();
			}
		}
		return text;
	}

	private static HashMap<String, String> getSignInput(SignTemplate signTemplate, String[] lines) {
		HashMap<String, String> outputMap = new HashMap<String, String>();

		for (int i = 0; i < SIGN_LINE_COUNT; i++) {
			String inputLine = signTemplate.signInput[i];
			if (inputLine != null && !inputLine.isEmpty()) {
				Matcher matcher = Pattern.compile("(\\[\\[\\w+\\]\\])").matcher(inputLine);

				if (matcher.find()) {
					ArrayList<String> paramList = new ArrayList<String>();
					do {
						String param = matcher.group();
						paramList.add(param.substring(2, param.length() - 2));
					} while (matcher.find());

					String newPattern = String.format("\\Q%s\\E", matcher.replaceAll("\\\\E(.*)\\\\Q")).replaceAll(
							"\\\\Q\\\\E", "");

					int matchCount = 0;
					matcher = Pattern.compile(newPattern).matcher(lines[i]);
					if (matcher.matches()) {
						for (int j = 1; j <= matcher.groupCount(); ++j) {
							if (!matcher.group(j).isEmpty()) {
								outputMap.put(paramList.get(matchCount), matcher.group(j));
							}
							++matchCount;
						}
					}
				}
			}
		}
		return outputMap;
	}

	public abstract void replacementMap(Region region, HashMap<String, String> replacementMap);

	protected abstract Sign makeSign(Player player, Block block, HashMap<String, String> inputMap);

	@Override
	public boolean isSignApplicable(Location location, String[] lines) {
		return lines[0].equalsIgnoreCase(signInput[0]);
	}

	@Override
	public boolean createSign(Player player, Block block, String[] lines) {
		if (lines == null || lines.length != SIGN_LINE_COUNT) {
			throw new IllegalArgumentException("Lines array must be in the correct format and must not be null");
		}

		if (lines[0].equalsIgnoreCase(signInput[0])) {
			HashMap<String, String> inputMap = getSignInput(this, lines);
			Sign sign = makeSign(player, block, inputMap);
			if (sign != null) {
				updateSignLines(sign.getRegion(), lines);
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateSign(Sign sign) {
		if (sign == null) {
			throw new IllegalArgumentException("Sign must not be null");
		}

		String[] lines = new String[SIGN_LINE_COUNT];
		updateSignLines(sign.getRegion(), lines);
		sign.setContent(lines);
	}

	private void updateSignLines(Region region, String[] lines) {
		if (region.getTemplate().equals(this)) {
			if (region.getOptions().exists("state")) {
				String state = (String) region.getOptions().get("state");

				String[] outputLines = signOutput.get(state);
				if (outputLines != null) {

					// Set lines in the input String array
					for (int i = 0; i < SIGN_LINE_COUNT; i++) {
						lines[i] = replaceTokens(outputLines[i], region.getReplacementMap());
					}
				}
			}
		}
	}
}
