/**
 * SimpleRegionMarket
 * Copyright (C) 2013-2014  theZorro266 <http://www.thezorro266.com>
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
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.factories.SignFactory;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class TemplateLease extends TemplateSell implements TimedTemplate {
	public TemplateLease(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		type = "lease";
	}

	@Override
	public void schedule() {
		for (RegionFactory.Region region : regionList) {
			if (isRegionOccupied(region)) {
				int currentSecs = (int) (System.nanoTime() / 1000000000);

				// TODO: Check for expiration, try to expand the time, send the owner a message, and clearRegion, if expanding is not possible
			}
		}
	}

	@Override
	public boolean cancel(RegionFactory.Region region, Player player) {
		return false;
	}

	@Override
	public boolean setRegionOccupied(RegionFactory.Region region, boolean isOccupied) {
		if (!isOccupied) {
			// TODO: Unset renttime
			region.setOption("owner", null);
		}
		region.setOption("state", (isOccupied ? "occupied" : "free"));
		return true;
	}

	@Override
	public void clickSign(Player player, SignFactory.Sign sign) {
		RegionFactory.Region region = sign.getRegion();
		if (isRegionOccupied(region)) {
			if (isRegionOwner(player, region)) {
				player.sendMessage("This is your region.");
			} else {
				player.sendMessage("This region is already sold.");
			}
		} else {
			// TODO: Player permissions
			// TODO: Player money
			clearRegion(region);
			if (buyerIsOwner) {
				setRegionOwners(region, new OfflinePlayer[] { player });
			} else {
				setRegionMembers(region, new OfflinePlayer[] { player });
			}

			// TODO: Set renttime
			region.setOption("owner", player.getName());
			setRegionOccupied(region, true);

			try {
				SimpleRegionMarket.getInstance().getTemplateManager().saveRegion(region);
			} catch (ContentSaveException e) {
				player.sendMessage(ChatColor.RED + "Could not save region");
				SimpleRegionMarket.getInstance().getLogger().severe("Could not save region " + region.getName());
				SimpleRegionMarket.getInstance().printError(e);
			}

			player.sendMessage("You're now the owner of this region");
		}
		region.updateSigns();
	}

	@Override
	public void replacementMap(RegionFactory.Region region, HashMap<String, String> replacementMap) {
		super.replacementMap(region, replacementMap);

		// TODO: Add renttime, owner, etc. to the replacement map
	}

	@Override
	public SignFactory.Sign makeSign(Player player, Block block, HashMap<String, String> inputMap) {
		ProtectedRegion worldguardRegion = RegionFactory.getProtectedRegionFromLocation(Location.fromBlock(block), inputMap.remove("region"));

		if (worldguardRegion != null) {
			RegionFactory.Region region = null;
			for (RegionFactory.Region regionEntry : regionList) {
				if (regionEntry.getWorldguardRegion().equals(worldguardRegion)) {
					region = regionEntry;
					break;
				}
			}

			if (region == null) {
				region = RegionFactory.instance.createRegion(this, block.getWorld(), worldguardRegion);

				double price;
				if (SimpleRegionMarket.getInstance().getVaultHook().getEconomy() != null) {
					String priceString = inputMap.remove("price");
					if (priceString != null) {
						try {
							price = Double.parseDouble(priceString);
						} catch (final Exception e) {
							player.sendMessage("Price not found.");
							return null;
						}
					} else {
						price = priceMin;
					}
				} else {
					price = 0;
				}

				if (priceMin > price && (priceMax == -1 || price < priceMax)) {
					String priceMinString;
					String priceMaxString;
					try {
						priceMinString = SimpleRegionMarket.getInstance().getVaultHook().getEconomy().format(priceMin);
						priceMaxString = SimpleRegionMarket.getInstance().getVaultHook().getEconomy().format(priceMax);
					} catch (Throwable e) {
						priceMinString = String.format("%.2f", priceMin);
						priceMaxString = String.format("%.2f", priceMax);
					}
					player.sendMessage(String.format(ChatColor.RED + "The price must be between %s and %s", priceMinString, priceMaxString));
					return null;
				}

				String account = player.getName();
				{
					String accountString = inputMap.remove("account");
					if (accountString != null) {
						if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, String.format("simpleregionmarket.%s.setaccount", getId()))) {
							if (accountString.equalsIgnoreCase("none")) {
								account = "";
							} else {
								account = accountString;
							}
						}
					}
				}

				// TODO: Check for renttime

				region.setOption("price", price);
				region.setOption("account", account);
				clearRegion(region);
			}

			SignFactory.Sign sign = region.addBlockAsSign(block);

			try {
				SimpleRegionMarket.getInstance().getTemplateManager().saveRegion(region);
			} catch (ContentSaveException e) {
				SimpleRegionMarket.getInstance().getLogger().severe("Could not save region " + region.getName());
				SimpleRegionMarket.getInstance().printError(e);
			}

			return sign;
		} else {
			player.sendMessage(ChatColor.RED + "Could not find the region.");
		}
		return null;
	}
}
