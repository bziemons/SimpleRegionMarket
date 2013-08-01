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

package com.thezorro266.bukkit.srm.templates;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;

public class TemplateSell extends IntelligentSignTemplate implements OwnableTemplate {

	public TemplateSell(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		type = "sell";
	}

	@Override
	public boolean signCreated(Player player, Location location, String[] lines) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean signDestroyed(Player player, Sign sign) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void signClicked(Player player, Sign sign) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedule() {
		// Do nothing
		System.out.println("Schedule here");
	}

	@Override
	public boolean isRegionOwner(OfflinePlayer player, Region region) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRegionMember(OfflinePlayer player, Region region) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OfflinePlayer[] getRegionOwners(Region region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OfflinePlayer[] getRegionMembers(Region region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRegionOccupied(Region region) {
		// TODO Auto-generated method stub
		return false;
	}
}
