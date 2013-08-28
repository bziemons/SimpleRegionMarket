/**
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
import java.util.HashMap;

import lombok.Getter;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.templates.IntelligentSignTemplate;
import com.thezorro266.bukkit.srm.templates.Template;

public class Region {
	@Getter
	final Template template;
	final World world;
	final ProtectedRegion worldguardRegion;

	@Getter
	ArrayList<Sign> signList;
	HashMap<String, Object> options;

	public Region(Template template, World world, ProtectedRegion worldguardRegion) {
		this.template = template;
		this.world = world;
		this.worldguardRegion = worldguardRegion;
	}
	
	@Override
	public String toString() {
		return String.format("region %s in world %s, template %s", worldguardRegion.getId(), world.getName(), template.toString());
	}

	public Sign addBlockAsSign(Block block) {
		if (Sign.isSign(block)) {
			int direction = 0;
			// TODO direction
			System.out.println(block.getData());
			Sign sign = new Sign(this, Location.fromBlock(block), block.getType().equals(Material.WALL_SIGN), direction);
			signList.add(sign);
			return sign;
		}
		return null;
	}

	public Object getOption(String optionAlias) {
		return options.get(optionAlias);
	}

	public void setOption(String optionAlias, Object value) {
		options.put(optionAlias, value);
	}

	public HashMap<String, String> getReplacementMap() {
		if (!(template instanceof IntelligentSignTemplate)) {
			throw new IllegalStateException(String.format("Template '%s' is not an intelligent sign template", template.getId()));
		}

		HashMap<String, String> replacementMap = new HashMap<String, String>();
		replacementMap.put("region", worldguardRegion.getId());
		replacementMap.put("world", world.getName());
		replacementMap.put("x",
				Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getX() - (int) (worldguardRegion.getMinimumPoint().getX() - 1))));
		replacementMap.put("y",
				Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getY() - (int) (worldguardRegion.getMinimumPoint().getY() - 1))));
		replacementMap.put("z",
				Integer.toString(Math.abs((int) worldguardRegion.getMaximumPoint().getZ() - (int) (worldguardRegion.getMinimumPoint().getZ() - 1))));

		((IntelligentSignTemplate) template).replacementMap(this, replacementMap);

		return replacementMap;
	}
}
