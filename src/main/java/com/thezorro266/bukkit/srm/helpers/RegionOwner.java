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

import com.thezorro266.bukkit.srm.factories.RegionFactory;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class RegionOwner {
	private WeakReference<Player> player;
	private WeakReference<RegionFactory.Region> region;

	public RegionOwner(Player player, RegionFactory.Region region) {
		this.player = new WeakReference<Player>(player);
		this.region = new WeakReference<RegionFactory.Region>(region);
	}

	public Player getPlayer() {
		return player.get();
	}
	
	public RegionFactory.Region getRegion() {
		return region.get();
	}
}
