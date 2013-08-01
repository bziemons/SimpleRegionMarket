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

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Region {

	ProtectedRegion worldGuardRegion;
	ArrayList<Sign> signs;
	ArrayList<OfflinePlayer> owners;
	ArrayList<OfflinePlayer> members;

	public Region() {
		// TODO Auto-generated constructor stub
	}

	public void addSign(Location location) {
		signs.add(new Sign(this, location));
	}

	/*
	public Map<String, String> getReplacementMap() {
		if (checkTemplate()) {
			if (world != null && entries.containsKey(world) && region != null && entries.get(world).containsKey(region)) {
				final World worldWorld = Bukkit.getWorld(world);
				if (worldWorld != null) {
					final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(worldWorld, region);
					if (protectedRegion != null) {
						final HashMap<String, String> replacementMap = new HashMap<String, String>();
						replacementMap.put("id", id);
						replacementMap.put("id_out", Utils.getOptionString(this, "output.id"));
						replacementMap.put("id_taken", Utils.getOptionString(this, "taken.id"));
						replacementMap.put("world", world.toLowerCase());
						replacementMap.put("region", region.toLowerCase());
						if (!SimpleRegionMarket.econManager.isEconomy() || Utils.getEntryDouble(this, world, region, "price") == 0) {
							replacementMap.put("price", "FREE");
						} else {
							replacementMap.put("price", SimpleRegionMarket.econManager.econFormat(Utils.getEntryDouble(this, world, region, "price")));
						}
						replacementMap.put("account", Utils.getEntryString(this, world, region, "account"));
						if (Utils.getEntry(this, world, region, "owner") == null || Utils.getEntryString(this, world, region, "owner").isEmpty()) {
							replacementMap.put("player", "No owner");
						} else {
							replacementMap.put("player", Utils.getEntryString(this, world, region, "owner"));
						}
						replacementMap.put("x", Integer.toString(Math.abs((int) protectedRegion.getMaximumPoint().getX()
								- (int) (protectedRegion.getMinimumPoint().getX() - 1))));
						replacementMap.put("y", Integer.toString(Math.abs((int) protectedRegion.getMaximumPoint().getY()
								- (int) (protectedRegion.getMinimumPoint().getY() - 1))));
						replacementMap.put("z", Integer.toString(Math.abs((int) protectedRegion.getMaximumPoint().getZ()
								- (int) (protectedRegion.getMinimumPoint().getZ() - 1))));

						return replacementMap;
					}
				}
			}
		}
		return null;
	}
	*/
}
