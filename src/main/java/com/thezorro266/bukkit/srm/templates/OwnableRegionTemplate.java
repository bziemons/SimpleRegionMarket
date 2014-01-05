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

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.WorldGuardManager;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;

public abstract class OwnableRegionTemplate extends SignTemplate implements OwnableTemplate {
	public OwnableRegionTemplate(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
	}

	@Override
	public boolean isRegionOwner(OfflinePlayer player, RegionFactory.Region region) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		return wgm.getOwnable(region).isPlayerOwner(wgm.wrapPlayer(player));
	}

	@Override
	public boolean isRegionMember(OfflinePlayer player, RegionFactory.Region region) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		return wgm.getOwnable(region).isPlayerMember(wgm.wrapPlayer(player));
	}

	@Override
	public OfflinePlayer[] getRegionOwners(RegionFactory.Region region) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		return wgm.getOwnable(region).getOwners();
	}

	@Override
	public OfflinePlayer[] getRegionMembers(RegionFactory.Region region) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		return wgm.getOwnable(region).getMembers();
	}

	@Override
	public boolean setRegionOwners(RegionFactory.Region region, OfflinePlayer[] owners) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		WorldGuardManager.WorldGuardOwnable wgo = wgm.getOwnable(region);

		wgo.removeAllOwners();
		for (OfflinePlayer player : owners) {
			wgo.addOwner(wgm.wrapPlayer(player));
		}

		try {
			wgo.saveChanges();
			return true;
		} catch (ProtectionDatabaseException e) {
			return false;
		}
	}

	@Override
	public boolean setRegionMembers(RegionFactory.Region region, OfflinePlayer[] members) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		WorldGuardManager.WorldGuardOwnable wgo = wgm.getOwnable(region);

		wgo.removeAllMembers();
		for (OfflinePlayer player : members) {
			wgo.addMember(wgm.wrapPlayer(player));
		}

		try {
			wgo.saveChanges();
			return true;
		} catch (ProtectionDatabaseException e) {
			return false;
		}
	}

	@Override
	public boolean addRegionOwner(RegionFactory.Region region, OfflinePlayer player) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		WorldGuardManager.WorldGuardOwnable wgo = wgm.getOwnable(region);

		wgo.addOwner(wgm.wrapPlayer(player));

		try {
			wgo.saveChanges();
			return true;
		} catch (ProtectionDatabaseException e) {
			return false;
		}
	}

	@Override
	public boolean addRegionMember(RegionFactory.Region region, OfflinePlayer player) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		WorldGuardManager.WorldGuardOwnable wgo = wgm.getOwnable(region);

		wgo.addMember(wgm.wrapPlayer(player));

		try {
			wgo.saveChanges();
			return true;
		} catch (ProtectionDatabaseException e) {
			return false;
		}
	}

	@Override
	public boolean removeRegionOwner(RegionFactory.Region region, OfflinePlayer player) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		WorldGuardManager.WorldGuardOwnable wgo = wgm.getOwnable(region);

		wgo.removeOwner(wgm.wrapPlayer(player));

		try {
			wgo.saveChanges();
			return true;
		} catch (ProtectionDatabaseException e) {
			return false;
		}
	}

	@Override
	public boolean removeRegionMember(RegionFactory.Region region, OfflinePlayer player) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		WorldGuardManager.WorldGuardOwnable wgo = wgm.getOwnable(region);

		wgo.removeMember(wgm.wrapPlayer(player));

		try {
			wgo.saveChanges();
			return true;
		} catch (ProtectionDatabaseException e) {
			return false;
		}
	}

	@Override
	public boolean clearRegion(RegionFactory.Region region) {
		return clearOwnershipOfRegion(region);
	}

	@Override
	public boolean clearOwnershipOfRegion(RegionFactory.Region region) {
		WorldGuardManager wgm = SimpleRegionMarket.getInstance().getWorldGuardManager();
		WorldGuardManager.WorldGuardOwnable wgo = wgm.getOwnable(region);

		wgo.removeAllMembers();
		wgo.removeAllOwners();

		try {
			wgo.saveChanges();
			return true;
		} catch (ProtectionDatabaseException e) {
			return false;
		}
	}
}
