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
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.RegionFactory;
import com.thezorro266.bukkit.srm.helpers.RegionFactory.Region;
import com.thezorro266.bukkit.srm.helpers.Sign;
import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;

public class TemplateSell extends SignTemplate implements OwnableTemplate {
	protected double priceMin = 0;
	protected double priceMax = -1;
	protected boolean removeSigns = true;
	protected boolean buyerIsOwner = true;

	public TemplateSell(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);

		type = "sell";
		
		if(templateConfigSection.contains("price.min")) {
			priceMin = templateConfigSection.getDouble("price.min");
		}
		if(templateConfigSection.contains("price.max")) {
			priceMax = templateConfigSection.getDouble("price.max");
		}
		if(templateConfigSection.contains("removesigns")) {
			removeSigns = templateConfigSection.getBoolean("removesigns");
		}
		if(templateConfigSection.contains("buyer")) {
			String buyer = templateConfigSection.getString("buyer");
			if(buyer.equalsIgnoreCase("owner")) {
				buyerIsOwner = true;
			} else if(buyer.equalsIgnoreCase("member")) {
				buyerIsOwner = false;
			}
		}
	}

	@Override
	public boolean isRegionOwner(OfflinePlayer player, Region region) {
		return SimpleRegionMarket.getInstance().getWorldGuardManager().isPlayerOwner(region.getWorldguardRegion(), player);
	}

	@Override
	public boolean isRegionMember(OfflinePlayer player, Region region) {
		return SimpleRegionMarket.getInstance().getWorldGuardManager().isPlayerMember(region.getWorldguardRegion(), player);
	}

	@Override
	public OfflinePlayer[] getRegionOwners(Region region) {
		return SimpleRegionMarket.getInstance().getWorldGuardManager().getOwners(region.getWorldguardRegion());
	}

	@Override
	public OfflinePlayer[] getRegionMembers(Region region) {
		return SimpleRegionMarket.getInstance().getWorldGuardManager().getMembers(region.getWorldguardRegion());
	}

	@Override
	public void setRegionOwners(Region region, OfflinePlayer[] owners) {
		SimpleRegionMarket.getInstance().getWorldGuardManager().removeAllOwners(region.getWorldguardRegion());
		for (OfflinePlayer player : owners) {
			addRegionOwner(region, player);
		}
	}

	@Override
	public void setRegionMembers(Region region, OfflinePlayer[] members) {
		SimpleRegionMarket.getInstance().getWorldGuardManager().removeAllMembers(region.getWorldguardRegion());
		for (OfflinePlayer player : members) {
			addRegionMember(region, player);
		}
	}

	@Override
	public void addRegionOwner(Region region, OfflinePlayer player) {
		SimpleRegionMarket.getInstance().getWorldGuardManager().addOwner(region.getWorldguardRegion(), player);
	}

	@Override
	public void addRegionMember(Region region, OfflinePlayer player) {
		SimpleRegionMarket.getInstance().getWorldGuardManager().addMember(region.getWorldguardRegion(), player);
	}

	@Override
	public void removeRegionOwner(Region region, OfflinePlayer player) {
		SimpleRegionMarket.getInstance().getWorldGuardManager().removeOwner(region.getWorldguardRegion(), player);
	}

	@Override
	public void removeRegionMember(Region region, OfflinePlayer player) {
		SimpleRegionMarket.getInstance().getWorldGuardManager().removeMember(region.getWorldguardRegion(), player);
	}

	@Override
	public boolean isRegionOccupied(Region region) {
		return region.getOption("state").equals("occupied");
	}

	@Override
	public void setRegionOccupied(Region region, boolean isOccupied) {
		region.setOption("state", (isOccupied ? "occupied" : "free"));
	}

	@Override
	public void clearRegion(Region region) {
		if (isRegionOccupied(region)) {
			setRegionOccupied(region, false);
			setRegionMembers(region, new OfflinePlayer[] {});
			setRegionOwners(region, new OfflinePlayer[] {});
		}
	}

	@Override
	public boolean isSignApplicable(Location location, String[] lines) {
		return super.isSignApplicable(location, lines);
	}

	@Override
	public boolean breakSign(Player player, Sign sign) {
		if(sign.getRegion().getSignList().size() > 1 || removeSigns) {
			sign.getRegion().getSignList().remove(sign);
			return true;
		} else {
			player.sendMessage("You're not allowed to break this sign");
			return false;
		}
	}

	@Override
	public void clickSign(Player player, Sign sign) {
		Region r = sign.getRegion();
		if (isRegionOccupied(r)) {
			if (isRegionOwner(player, r)) {
				player.sendMessage("This is your region.");
			} else {
				player.sendMessage("This region is already sold.");
			}
		} else {
			// TODO: Player permissions
			// TODO: Player money
			// TODO: WG Region Owner/Member question
			clearRegion(r);
			if(buyerIsOwner) {
				setRegionOwners(r, new OfflinePlayer[] { player });
			} else {
				setRegionMembers(r, new OfflinePlayer[] { player });
			}
			
			r.setOption("price", null);
			r.setOption("account", null);
			r.setOption("buyer", player.getName());
			setRegionOccupied(r, true);

			player.sendMessage("You're now the owner of this region");
		}
		r.updateSigns();
	}

	@Override
	public void replacementMap(Region region, HashMap<String, String> replacementMap) {
		if (region.isOption("price")) {
			String strPrice;
			Double price = (Double) region.getOption("price");
			try {
				strPrice = SimpleRegionMarket.getInstance().getVaultHook().getEconomy().format(price);
			} catch (Throwable e) {
				strPrice = String.format("%.2f", price);
			}
			replacementMap.put("price", strPrice);
		}

		if (region.isOption("account"))
			replacementMap.put("account", region.getOption("account").toString());

		if (region.isOption("buyer"))
			replacementMap.put("buyer", region.getOption("buyer").toString());
	}

	@Override
	public Sign makeSign(Player player, Block block, HashMap<String, String> inputMap) {
		ProtectedRegion worldguardRegion = RegionFactory.getProtectedRegionFromLocation(Location.fromBlock(block), inputMap.remove("region"));

		if (worldguardRegion != null) {
			boolean existentRegion = false;
			for (Region regionEntry : regionList) {
				if (regionEntry.getWorldguardRegion().equals(worldguardRegion)) {
					existentRegion = true;
					break;
				}
			}

			if (!existentRegion) {
				Region region = SimpleRegionMarket.getInstance().getRegionFactory().createRegion(this, block.getWorld(), worldguardRegion);

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

				region.setOption("price", price);
				region.setOption("account", account);
				setRegionOccupied(region, false);

				return region.addBlockAsSign(block);
			}
		} else {
			player.sendMessage(ChatColor.RED + "Could not find the region.");
		}
		return null;
	}
}
