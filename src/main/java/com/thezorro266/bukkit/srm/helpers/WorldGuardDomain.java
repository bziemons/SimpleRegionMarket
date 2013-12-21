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

import java.util.Set;

import com.sk89q.worldguard.domains.DefaultDomain;

public class WorldGuardDomain {
	public static void addPlayer(DefaultDomain dd, WorldGuardPlayer player) {
		dd.addPlayer(player);
	}
	
	public static void removePlayer(DefaultDomain dd, WorldGuardPlayer player) {
		dd.removePlayer(player);
	}
	
	public static void addGroup(DefaultDomain dd, String group) {
		dd.addGroup(group);
	}
	
	public static void removeGroup(DefaultDomain dd, String group) {
		dd.removeGroup(group);
	}
	
	public static Set<String> getPlayers(DefaultDomain dd) {
		return dd.getPlayers();
	}
	
	public static void removeAll(DefaultDomain dd) {
		dd.removaAll();
	}
	
	public static boolean containsPlayer(DefaultDomain dd, WorldGuardPlayer player) {
		return dd.contains(player);
	}
}
