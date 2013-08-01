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

package com.thezorro266.bukkit.srm;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardManager {

	public WorldGuardPlugin getWorldGuard() {
		final Plugin wgPlugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin) || !wgPlugin.isEnabled()) {
			SimpleRegionMarket.getInstance().getLogger().severe("MAIN.ERROR.NO_WORLDGUARD");
			return null;
		}

		return (WorldGuardPlugin) wgPlugin;
	}

	public void addMember(Player player, ProtectedRegion protectedRegion) {
		if (protectedRegion != null && player != null) {
			protectedRegion.getMembers().addPlayer(getWorldGuard().wrapPlayer(player));
		}
	}

	public void addOwner(Player player, ProtectedRegion protectedRegion) {
		if (protectedRegion != null && player != null) {
			protectedRegion.getOwners().addPlayer(getWorldGuard().wrapPlayer(player));
		}
	}

	public void removeMember(Player player, ProtectedRegion protectedRegion) {
		if (protectedRegion != null && player != null) {
			protectedRegion.getMembers().removePlayer(getWorldGuard().wrapPlayer(player));
		}
	}

	public void removeOwner(Player player, ProtectedRegion protectedRegion) {
		if (protectedRegion != null && player != null) {
			protectedRegion.getOwners().removePlayer(getWorldGuard().wrapPlayer(player));
		}
	}

	public ProtectedRegion getProtectedRegion(World worldWorld, String region) {
		if (worldWorld != null) {
			final WorldGuardPlugin wgPlugin = getWorldGuard();
			if (wgPlugin != null) {
				return wgPlugin.getRegionManager(worldWorld).getRegion(region);
			}
		}
		return null;
	}

	public LocalPlayer wrapPlayer(Player player) {
		final WorldGuardPlugin wgPlugin = getWorldGuard();
		if (wgPlugin != null) {
			return wgPlugin.wrapPlayer(player);
		}
		return null;
	}
}
