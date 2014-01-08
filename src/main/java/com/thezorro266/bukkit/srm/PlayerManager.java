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

package com.thezorro266.bukkit.srm;

import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.helpers.RegionOwner;
import com.thezorro266.bukkit.srm.templates.Template;
import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerManager implements Listener {
	private final ArrayList<RegionOwner> ownerList = new ArrayList<RegionOwner>();

	public void registerEvents() {
		SimpleRegionMarket.getInstance().getServer().getPluginManager()
				.registerEvents(this, SimpleRegionMarket.getInstance());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ArrayList<RegionFactory.Region> tempRegions = new ArrayList<RegionFactory.Region>();
		synchronized (SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
			for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
				if (template instanceof OwnableTemplate) {
					OwnableTemplate ownableTemplate = (OwnableTemplate) template;
					synchronized (template.getRegionList()) {
						for (RegionFactory.Region region : template.getRegionList()) {
							String mainOwner = ownableTemplate.getMainOwner(region);
							if (mainOwner != null && mainOwner.equalsIgnoreCase(event.getPlayer().getName())) {
								tempRegions.add(region);
							}
						}
					}
				}
			}
		}
		synchronized (ownerList) {
			for (RegionFactory.Region region : tempRegions) {
				ownerList.add(new RegionOwner(event.getPlayer(), region));
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		synchronized (ownerList) {
			for (Iterator<RegionOwner> iterator = ownerList.iterator(); iterator.hasNext(); ) {
				RegionOwner regionOwner = iterator.next();
				if (regionOwner.getPlayer().equals(event.getPlayer())) {
					iterator.remove();
				}
			}
		}
	}

	public ArrayList<RegionFactory.Region> getPlayerRegions(Player player) {
		ArrayList<RegionFactory.Region> tempRegions = new ArrayList<RegionFactory.Region>();
		synchronized (ownerList) {
			for (Iterator<RegionOwner> iterator = ownerList.iterator(); iterator.hasNext(); ) {
				RegionOwner regionOwner = iterator.next();
				Player owner = regionOwner.getPlayer();
				if (owner == null) {
					iterator.remove();
					continue;
				}

				if (owner.equals(player)) {
					RegionFactory.Region region = regionOwner.getRegion();
					if (region == null) {
						iterator.remove();
						continue;
					}

					tempRegions.add(regionOwner.getRegion());
				}
			}
		}
		return tempRegions;
	}
}
