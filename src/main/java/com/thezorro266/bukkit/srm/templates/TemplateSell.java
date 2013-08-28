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

package com.thezorro266.bukkit.srm.templates;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.Region;
import com.thezorro266.bukkit.srm.helpers.Sign;
import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;

public class TemplateSell extends IntelligentSignTemplate implements OwnableTemplate {
	double priceMin;
	double priceMax;

	public TemplateSell(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);

		type = "sell";
	}

	@Override
	public boolean isRegionOwner(OfflinePlayer player, Region region) {
		return false;
	}

	@Override
	public boolean isRegionMember(OfflinePlayer player, Region region) {
		return false;
	}

	@Override
	public OfflinePlayer[] getRegionOwners(Region region) {
		return new OfflinePlayer[] {};
	}

	@Override
	public OfflinePlayer[] getRegionMembers(Region region) {
		return new OfflinePlayer[] {};
	}

	@Override
	public boolean isRegionOccupied(Region region) {
		return region.getOption("state").equals("occupied");
	}

	@Override
	public boolean isSignApplicable(Location location, String[] lines) {
		if (super.isSignApplicable(location, lines)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean breakSign(Player player, Sign sign) {
		return false;
	}

	@Override
	public void clickSign(Player player, Sign sign) {
	}

	@Override
	public void replacementMap(Region region, HashMap<String, String> replacementMap) {
	}

	@Override
	public Sign makeSign(Player player, Block block, HashMap<String, String> inputMap) {
		// TODO Get region from coordinates
		ProtectedRegion worldguardRegion = null;
		
		// TODO if (entry on this region not exists)
			Region region = new Region(this, block.getWorld(), worldguardRegion);
			
			double price;
			// TODO if (SimpleRegionMarket.econManager.isEconomy()) {
				String priceString = inputMap.remove("price");
				if (priceString != null) {
					try {
						price = Double.parseDouble(priceString);
					} catch (final Exception e) {
						// TODO langHandler.playerErrorOut(player, "PLAYER.ERROR.NO_PRICE", null);
						return null;
					}
				} else {
					price = priceMin;
				}
			//} else {
			//	price = 0;
			//}
	
			if (priceMin > price && (priceMax == -1 || price < priceMax)) {
				// TODO Add Vault formatting for price
				player.sendMessage(String.format(ChatColor.RED + "The price must be between %d and %d", priceMin, priceMax)); 
				return null;
			}
	
			String account = player.getName();
			{
				String accountString = inputMap.remove("account");
				if (accountString != null) {
					// TODO if (SimpleRegionMarket.permManager.isAdmin(player)) {
						if (accountString.equalsIgnoreCase("none")) {
							account = "";
						} else {
							account = accountString;
						}
					//}
				}
			}
	
			region.setOption("state", "free");
			region.setOption("price", price);
			region.setOption("account", account);
		//} else {
			// return null;
		//}

		return region.addBlockAsSign(block);
	}
}
