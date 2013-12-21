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

package com.thezorro266.bukkit.srm;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.helpers.WorldGuardDomain;
import com.thezorro266.bukkit.srm.helpers.WorldGuardPlayer;

public class WorldGuardManager {

	WorldGuardPlugin worldguardPlugin;

	public void load() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			throw new UnknownDependencyException("No WorldGuard installed or WorldGuard not enabled");
		} else {
			worldguardPlugin = (WorldGuardPlugin) plugin;
		}
	}

	public WorldGuardPlugin getWorldGuard() {
		return worldguardPlugin;
	}

	public void addMember(ProtectedRegion protectedRegion, OfflinePlayer player) {
		WorldGuardDomain.addPlayer(protectedRegion.getMembers(), new WorldGuardPlayer(player));
	}

	public void addOwner(ProtectedRegion protectedRegion, OfflinePlayer player) {
		WorldGuardDomain.addPlayer(protectedRegion.getOwners(), new WorldGuardPlayer(player));
	}

	public void removeMember(ProtectedRegion protectedRegion, OfflinePlayer player) {
		WorldGuardDomain.removePlayer(protectedRegion.getMembers(), new WorldGuardPlayer(player));
	}

	public void removeOwner(ProtectedRegion protectedRegion, OfflinePlayer player) {
		WorldGuardDomain.removePlayer(protectedRegion.getOwners(), new WorldGuardPlayer(player));
	}

	public void removeAllMembers(ProtectedRegion protectedRegion) {
		WorldGuardDomain.removeAll(protectedRegion.getMembers());
	}

	public void removeAllOwners(ProtectedRegion protectedRegion) {
		WorldGuardDomain.removeAll(protectedRegion.getOwners());
	}

	public boolean isPlayerMember(ProtectedRegion protectedRegion, OfflinePlayer player) {
		return WorldGuardDomain.containsPlayer(protectedRegion.getMembers(), new WorldGuardPlayer(player));
	}

	public boolean isPlayerOwner(ProtectedRegion protectedRegion, OfflinePlayer player) {
		return WorldGuardDomain.containsPlayer(protectedRegion.getOwners(), new WorldGuardPlayer(player));
	}

	public OfflinePlayer[] getMembers(ProtectedRegion protectedRegion) {
		OfflinePlayer[] list;
		Set<String> playerSet = WorldGuardDomain.getPlayers(protectedRegion.getMembers());
		list = new OfflinePlayer[playerSet.size()];
		int index = 0;
		for (String playerName : playerSet) {
			list[index] = Bukkit.getOfflinePlayer(playerName);
			++index;
		}
		return list;
	}

	public OfflinePlayer[] getOwners(ProtectedRegion protectedRegion) {
		OfflinePlayer[] list;
		Set<String> playerSet = WorldGuardDomain.getPlayers(protectedRegion.getOwners());
		list = new OfflinePlayer[playerSet.size()];
		int index = 0;
		for (String playerName : playerSet) {
			list[index] = Bukkit.getOfflinePlayer(playerName);
			++index;
		}
		return list;
	}

	public ProtectedRegion getProtectedRegion(World world, String region) {
		if (world != null) {
			return worldguardPlugin.getRegionManager(world).getRegion(region);
		}
		return null;
	}

	public LocalPlayer wrapPlayer(Player player) {
		if (player != null) {
			return worldguardPlugin.wrapPlayer(player);
		}
		return null;
	}
}
