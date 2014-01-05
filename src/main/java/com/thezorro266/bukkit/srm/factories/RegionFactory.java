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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.factories.SignFactory.Sign;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.Options;
import com.thezorro266.bukkit.srm.templates.SignTemplate;
import com.thezorro266.bukkit.srm.templates.Template;

public class RegionFactory {
	public static final RegionFactory instance = new RegionFactory();
	@Getter
	private int regionCount = 0;

	private RegionFactory() {
	}

	public static ProtectedRegion getProtectedRegionFromLocation(Location loc, String region) {
		ProtectedRegion protectedRegion = null;
		final RegionManager worldRegionManager = SimpleRegionMarket.getInstance().getWorldGuardManager()
				.getWorldGuard().getRegionManager(loc.getWorld());
		if (region == null) {
			ApplicableRegionSet regionSet = worldRegionManager.getApplicableRegions(loc.getBukkitLocation());
			if (regionSet.size() == 1) {
				protectedRegion = regionSet.iterator().next();
			} else {
				System.out.println("More than one region detected at " + loc.toString());
				// TODO Take child region or region with highest priority
			}
		} else {
			protectedRegion = worldRegionManager.getRegion(region);
		}
		return protectedRegion;
	}

	public Region createRegion(Template template, World world, ProtectedRegion worldguardRegion) {
		Region region = new Region(template, world, worldguardRegion);

		synchronized (template.getRegionList()) {
			template.getRegionList().add(region);
		}
		SimpleRegionMarket.getInstance().getWorldHelper().putRegion(region, world);

		++regionCount;

		return region;
	}

	public void destroyRegion(Region region) {
		{
			Sign[] signArray = new Sign[region.getSignList().size()];
			signArray = region.getSignList().toArray(signArray);
			for (Sign sign : signArray) {
				sign.clear();
				SignFactory.instance.destroySign(sign);
			}
		}

		synchronized (region.getTemplate().getRegionList()) {
			region.getTemplate().getRegionList().remove(region);
		}

		--regionCount;
	}

	public void loadFromConfiguration(Configuration config, String path) throws ContentLoadException {
		Template template = SimpleRegionMarket.getInstance().getTemplateManager()
				.getTemplateFromId(config.getString(path + "template_id"));
		World world = Bukkit.getWorld(config.getString(path + "world"));
		ProtectedRegion worldguardRegion = SimpleRegionMarket.getInstance().getWorldGuardManager()
				.getProtectedRegion(world, config.getString(path + "worldguard_region"));

		Region region;
		try {
			region = createRegion(template, world, worldguardRegion);
		} catch (IllegalArgumentException e) {
			throw new ContentLoadException("Could not create region", e);
		}

		// Check if there are options
		if (config.isSet(path + "options")) {
			// Set region options from values from options path
			Set<Entry<String, Object>> optionEntrySet = config.getConfigurationSection(path + "options")
					.getValues(true).entrySet();
			for (Entry<String, Object> optionEntry : optionEntrySet) {
				if (!(optionEntry.getValue() instanceof ConfigurationSection)) {
					region.getOptions().set(optionEntry.getKey(), optionEntry.getValue());
				}
			}
		}

		ConfigurationSection signSection = config.getConfigurationSection(path + "signs");
		if (signSection != null) {
			for (String signKey : signSection.getKeys(false)) {
				try {
					SignFactory.instance.loadFromConfiguration(config, region,
							path + String.format("signs.%s.", signKey));
				} catch (IllegalArgumentException e) {
					throw new ContentLoadException("Could not create sign " + signKey, e);
				}
			}
		}
	}

	public class Region {
		@Getter
		final Template template;
		@Getter
		final World world;
		@Getter
		final ProtectedRegion worldguardRegion;
		@Getter
		ArrayList<Sign> signList;

		@Getter
		private final Options options;

		private Region(Template template, World world, ProtectedRegion worldguardRegion) {
			if (template == null) {
				throw new IllegalArgumentException("Template must not be null");
			}
			if (world == null) {
				throw new IllegalArgumentException("World must not be null");
			}
			if (worldguardRegion == null) {
				throw new IllegalArgumentException("WorldGuard region must not be null");
			}

			this.template = template;
			this.world = world;
			this.worldguardRegion = worldguardRegion;
			signList = new ArrayList<Sign>();
			options = new Options();
		}

		public String getName() {
			return worldguardRegion.getId();
		}

		public Sign addBlockAsSign(Block block) {
			if (SignFactory.instance.isSign(block)) {
				org.bukkit.material.Sign signMat = (org.bukkit.material.Sign) block.getState().getData();
				return SignFactory.instance.createSign(this, Location.fromBlock(block),
						block.getType().equals(Material.WALL_SIGN), signMat.getFacing());
			}
			return null;
		}

		public void updateSigns() {
			for (Sign sign : signList) {
				template.updateSign(sign);
			}
		}

		public HashMap<String, String> getReplacementMap() {
			if (!(template instanceof SignTemplate)) {
				throw new IllegalStateException(String.format("Template '%s' is not a sign template", template.getId())); //NON-NLS
			}

			HashMap<String, String> replacementMap = new HashMap<String, String>();
			replacementMap.put("region", getName());
			replacementMap.put("world", world.getName());
			if (getWorldguardRegion() instanceof ProtectedCuboidRegion) {
				replacementMap.put(
						"x",
						Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getX()
								- (int) (worldguardRegion.getMinimumPoint().getX() - 1))));
				replacementMap.put(
						"y",
						Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getY()
								- (int) (worldguardRegion.getMinimumPoint().getY() - 1))));
				replacementMap.put(
						"z",
						Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getZ()
								- (int) (worldguardRegion.getMinimumPoint().getZ() - 1))));
			}

			((SignTemplate) template).replacementMap(this, replacementMap);

			return replacementMap;
		}

		public void saveToConfiguration(Configuration config, String path) {
			config.set(path + "template_id", template.getId());
			config.set(path + "world", world.getName());
			config.set(path + "worldguard_region", worldguardRegion.getId());
			saveOptions(config, path + "options.");

			int signCount = 0;
			for (Sign sign : signList) {
				sign.saveToConfiguration(config, String.format("signs.%d.", signCount));
				++signCount;
			}
		}

		private void saveOptions(Configuration config, String path) {
			synchronized (options) {
				for (Entry<String, Object> optionEntry : options) {
					config.set(path + optionEntry.getKey(), optionEntry.getValue());
				}
			}
		}

		@SuppressWarnings("HardCodedStringLiteral")
		@Override
		public String toString() {
			return String.format("Region[%s,w:%s,t:%s]", getName(), world.getName(), template.toString());
		}
	}
}
