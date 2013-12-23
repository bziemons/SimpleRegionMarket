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

import org.bukkit.OfflinePlayer;

import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;

public class WorldGuardPlayer extends LocalPlayer {
	private OfflinePlayer player;

	public WorldGuardPlayer(OfflinePlayer player) {
		if (player == null) {
			throw new IllegalArgumentException("Player must not be null");
		}

		this.player = player;
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public boolean hasGroup(String group) {
		return PermissionsResolverManager.getInstance().inGroup(player, group);
	}

	@Override
	public Vector getPosition() {
		if (player.isOnline()) {
			org.bukkit.Location loc = player.getPlayer().getLocation();
			return new Vector(loc.getX(), loc.getY(), loc.getZ());
		} else {
			SimpleRegionMarket.getInstance().getLogger().warning("WorldGuard tried to get a position of an offline player");
			return new Vector(0, 0, 0);
		}
	}

	@Override
	public void kick(String msg) {
		if (player.isOnline()) {
			player.getPlayer().kickPlayer(msg);
		} else {
			SimpleRegionMarket.getInstance().getLogger().warning("WorldGuard tried to kick an offline player");
		}
	}

	@Override
	public void ban(String msg) {
		if (player.isOnline()) {
			player.getPlayer().setBanned(true);
			player.getPlayer().kickPlayer(msg);
		} else {
			SimpleRegionMarket.getInstance().getLogger().warning("WorldGuard tried to ban an offline player");
		}
	}

	@Override
	public void printRaw(String msg) {
		if (player.isOnline()) {
			player.getPlayer().sendMessage(msg);
		} else {
			SimpleRegionMarket.getInstance().getLogger().warning("WorldGuard tried to message an offline player");
		}
	}

	@Override
	public String[] getGroups() {
		return PermissionsResolverManager.getInstance().getGroups(player);
	}

	@Override
	public boolean hasPermission(String perm) {
		if (player.isOnline()) {
			return SimpleRegionMarket.getInstance().getWorldGuardManager().getWorldGuard().hasPermission(player.getPlayer(), perm);
		} else {
			SimpleRegionMarket.getInstance().getLogger().warning("WorldGuard tried to get the permissions of an offline player");
			return true;
		}
	}

}
