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

package com.thezorro266.bukkit.srm.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import org.bukkit.World;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;

public class WorldHelper {
	private final WeakHashMap<Region, World> regionMap = new WeakHashMap<Region, World>();

	public Region[] getRegions(World world) {
		ArrayList<Region> list = new ArrayList<Region>();

		for (Iterator<Entry<Region, World>> iterator = regionMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Region, World> entry = iterator.next();

			// Check cache Validation
			if (!entry.getKey().getTemplate().getRegionList().contains(entry.getKey())) {
				iterator.remove();
			}

			if (entry.getValue().equals(world)) {
				list.add(entry.getKey());
			}
		}

		return list.toArray(new Region[list.size()]);
	}

	public Region getRegion(String name, World world) {
		Region found = null;
		String lowerName = name.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (Iterator<Region> iterator = regionMap.keySet().iterator(); iterator.hasNext();) {
			Region region = iterator.next();

			// Check cache validation
			if (!region.getTemplate().getRegionList().contains(region)) {
				iterator.remove();
				continue;
			}

			if (region.getName().toLowerCase().startsWith(lowerName)
					&& (world == null || regionMap.get(region).equals(world))) {
				int curDelta = region.getName().length() - lowerName.length();
				if (curDelta < delta) {
					found = region;
					delta = curDelta;
				}
				if (curDelta == 0)
					break;
			}
		}
		return found;
	}

	public Region getRegionExact(String name, World world) {
		Region region;
		for (Iterator<Region> iterator = regionMap.keySet().iterator(); iterator.hasNext();) {
			region = iterator.next();

			// Check cache validation
			if (!region.getTemplate().getRegionList().contains(region)) {
				iterator.remove();
				continue;
			}

			if (region.getName().equals(name)) {
				return region;
			}
		}
		return null;
	}

	public void putRegion(Region region, World world) {
		if (region == null) {
			throw new IllegalArgumentException();
		}
		if (world == null) {
			throw new IllegalArgumentException();
		}

		if (region.getTemplate().getRegionList().contains(region)) {
			regionMap.put(region, world);
		}
	}
}
