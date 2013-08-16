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

import lombok.Data;

import org.bukkit.World;
import org.bukkit.block.Block;

public @Data
class Location {
	final World world;
	final int x;
	final int y;
	final int z;

	public Block getBlock() {
		return world.getBlockAt(x, y, z);
	}

	public boolean isBlockAt(Block block) {
		return block.getWorld().equals(world) && block.getX() == x && block.getY() == y && block.getZ() == z;
	}

	public static Location fromBlock(Block block) {
		return new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}
}
