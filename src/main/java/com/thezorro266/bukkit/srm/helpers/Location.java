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

package com.thezorro266.bukkit.srm.helpers;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;

public @Data
class Location {
	final World world;
	final int x;
	final int y;
	final int z;

	public Location(World world, int x, int y, int z) {
		if (world == null) {
			throw new IllegalArgumentException("World must not be null");
		}

		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location(Location loc) {
		this(loc.world, loc.x, loc.y, loc.z);
	}

	public static Location loadFromConfiguration(Configuration config, String path) throws ContentLoadException {
		String worldString = config.getString(path + "world");
		if (worldString == null) {
			throw new ContentLoadException("Failed to load Location, world could not be found");
		}
		World world = Bukkit.getWorld(worldString);

		if (!config.isSet(path + "x") || !config.isSet(path + "y") || !config.isSet(path + "z")) {
			throw new ContentLoadException("Failed to load x, y or z coordinate");
		}
		int x = config.getInt(path + "x");
		int y = config.getInt(path + "y");
		int z = config.getInt(path + "z");
		return new Location(world, x, y, z);
	}

	public static Location fromBlock(Block block) {
		return new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	public Block getBlock() {
		return world.getBlockAt(x, y, z);
	}

	public org.bukkit.Location getBukkitLocation() {
		return getBlock().getLocation();
	}

	public boolean isBlockAt(Block block) {
		return block.getWorld().equals(world) && block.getX() == x && block.getY() == y && block.getZ() == z;
	}

	public void saveToConfiguration(Configuration config, String path) {
		config.set(path + "world", world.getName());
		config.set(path + "x", x);
		config.set(path + "y", y);
		config.set(path + "z", z);
	}

	@SuppressWarnings("HardCodedStringLiteral")
	@Override
	public String toString() {
		return String.format("Location[w:%s,c:(%d,%d,%d)]", world.getName(), x, y, z);
	}
}
