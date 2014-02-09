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

package com.thezorro266.bukkit.srm.factories;

import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.Options;

public class SignFactory {
	public static final SignFactory instance = new SignFactory();
	@Getter
	private int signCount = 0;

	private SignFactory() {
	}

	public Sign createSign(Region region, Location location, boolean isWallSign, BlockFace direction) {
		// Check for sign on location
		Sign oldSign = SimpleRegionMarket.getInstance().getLocationSignHelper().getSign(location);
		if (oldSign != null) {
			throw new IllegalArgumentException("Location already has a sign");
		}

		// Create new sign
		Sign sign = new Sign(region, location, isWallSign, direction);

		region.getSignList().add(sign);
		SimpleRegionMarket.getInstance().getLocationSignHelper().addSignAndLocation(sign);

		++signCount;

		return sign;
	}

	public void destroySign(Sign sign) {
		sign.getRegion().getSignList().remove(sign);
		SimpleRegionMarket.getInstance().getLocationSignHelper().removeSignAndLocation(sign);

		--signCount;
	}

	public boolean isSign(Block block) {
		return block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST);
	}

	public Sign getSignFromLocation(Location location) {
		return SimpleRegionMarket.getInstance().getLocationSignHelper().getSign(location);
	}

	public void loadFromConfiguration(Configuration config, Region region, String path) throws ContentLoadException {
		String regionName = region.getName();
		String configRegionName = config.getString(path + "region");
		if (regionName.equals(configRegionName)) {
			Location location;
			try {
				location = Location.loadFromConfiguration(config, path + "location.");
			} catch (ContentLoadException e) {
				throw new ContentLoadException("Could not load location", e);
			} catch (IllegalArgumentException e) {
				throw new ContentLoadException("Could not create location", e);
			}
			boolean isWallSign = config.getBoolean(path + "is_wall_sign");
			BlockFace direction = BlockFace.valueOf(config.getString(path + "direction"));

			Sign sign;
			try {
				sign = createSign(region, location, isWallSign, direction);
			} catch (IllegalArgumentException e) {
				throw new ContentLoadException("Could not create sign", e);
			}

			// Check, if there are options
			if (config.isSet(path + "options")) {
				// Set sign options from values from options path
				Set<Map.Entry<String, Object>> optionEntrySet = config.getConfigurationSection(path + "options")
						.getValues(true).entrySet();
				for (Map.Entry<String, Object> optionEntry : optionEntrySet) {
					if (!(optionEntry.getValue() instanceof ConfigurationSection)) {
						sign.getOptions().set(optionEntry.getKey(), optionEntry.getValue());
					}
				}
			}

			region.getTemplate().updateSign(sign);
		} else {
			throw new ContentLoadException("Region string in sign config did not match the outer region");
		}
	}

	public @Data
	class Sign {
		public static final int SIGN_LINE_COUNT = 4;
		final Region region;
		final Location location;
		final boolean isWallSign;
		final BlockFace direction;
		private final Options options;

		private Sign(Region region, Location location, boolean isWallSign, BlockFace direction) {
			if (region == null) {
				throw new IllegalArgumentException("Region must not be null");
			}
			if (location == null) {
				throw new IllegalArgumentException("Location must not be null");
			}
			if (direction == null) {
				throw new IllegalArgumentException("Direction must not be null");
			}

			this.region = region;
			this.location = new Location(location);
			this.isWallSign = isWallSign;
			this.direction = direction;
			options = new Options();
		}

		public void clear() {
			setContent(new String[SIGN_LINE_COUNT]);
		}

		public void setContent(String[] lines) {
			if (lines == null || lines.length != SIGN_LINE_COUNT) {
				clear();
				throw new IllegalArgumentException("Lines array must be in the correct format and must not be null");
			}

			Block block = location.getBlock();
			if (!isSign(block)) {
				block.setType(isWallSign ? Material.WALL_SIGN : Material.SIGN_POST);
			}
			org.bukkit.block.Sign signBlock = (org.bukkit.block.Sign) block.getState();
			org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) signBlock.getData();
			if (!signMaterial.getFacing().equals(direction)) {
				signMaterial.setFacingDirection(direction);
			}

			for (int i = 0; i < SIGN_LINE_COUNT; i++) {
				signBlock.setLine(i, lines[i]);
			}
			signBlock.update(true, false);
		}

		public void saveToConfiguration(Configuration config, String path) {
			config.set(path + "region", region.getName());
			location.saveToConfiguration(config, path + "location.");
			config.set(path + "is_wall_sign", isWallSign);
			config.set(path + "direction", direction.toString());
			saveOptions(config, path + "options.");
		}

		private void saveOptions(Configuration config, String path) {
			synchronized (options) {
				for (Map.Entry<String, Object> optionEntry : options) {
					config.set(path + optionEntry.getKey(), optionEntry.getValue());
				}
			}
		}

		@SuppressWarnings("HardCodedStringLiteral")
		@Override
		public String toString() {
			return String.format("Sign[r:%s,l:%s]", region.getName(), location);
		}
	}
}
