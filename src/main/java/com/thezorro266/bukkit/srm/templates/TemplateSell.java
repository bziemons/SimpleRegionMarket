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

import java.text.MessageFormat;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.LanguageSupport;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.factories.SignFactory;
import com.thezorro266.bukkit.srm.factories.SignFactory.Sign;
import com.thezorro266.bukkit.srm.helpers.Location;

public class TemplateSell extends OwnableRegionTemplate {
	protected double priceMin = 0;
	protected double priceMax = -1;
	protected boolean removeSigns = true;
	protected boolean buyerIsOwner = true;

	public TemplateSell(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);

		type = "sell";

		if (templateConfigSection.contains("price.min")) {
			priceMin = templateConfigSection.getDouble("price.min");
		}
		if (templateConfigSection.contains("price.max")) {
			priceMax = templateConfigSection.getDouble("price.max");
		}
		if (templateConfigSection.contains("removesigns")) {
			removeSigns = templateConfigSection.getBoolean("removesigns");
		}
		if (templateConfigSection.contains("buyer")) {
			String buyer = templateConfigSection.getString("buyer");
			if (buyer.equalsIgnoreCase("owner")) {
				buyerIsOwner = true;
			} else if (buyer.equalsIgnoreCase("member")) {
				buyerIsOwner = false;
			}
		}
	}

	@Override
	public boolean isRegionOccupied(Region region) {
		return region.getOptions().get("state").equals("occupied");
	}

	@Override
	public boolean setRegionOccupied(Region region, boolean isOccupied) {
		if (!isOccupied) {
			region.getOptions().set("buyer", null);
		}
		region.getOptions().set("state", (isOccupied ? "occupied" : "free"));
		return true;
	}

	@Override
	public boolean clearRegion(Region region) {
		setRegionOccupied(region, false);
		return super.clearRegion(region);
	}

	@Override
	public boolean breakSign(Player player, Sign sign) {
		if (sign.getRegion().getSignList().size() > 1 || removeSigns) {
			SignFactory.instance.destroySign(sign);
			return true;
		} else {
			player.sendMessage(LanguageSupport.instance.getString("sign.break.not.allowed"));
			return false;
		}
	}

	@Override
	public void clickSign(Player player, Sign sign) {
		Region region = sign.getRegion();
		if (isRegionOccupied(region)) {
			if (isRegionOwner(player, region)) {
				player.sendMessage(LanguageSupport.instance.getString("region.yours"));
			} else {
				player.sendMessage(LanguageSupport.instance.getString("region.already.sold"));
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

			region.getOptions().set("buyer", player.getName());
			setRegionOccupied(region, true);

			try {
				SimpleRegionMarket.getInstance().getTemplateManager().saveRegion(region);
			} catch (ContentSaveException e) {
				player.sendMessage(ChatColor.RED + LanguageSupport.instance.getString("region.save.problem.player"));
				SimpleRegionMarket
						.getInstance()
						.getLogger()
						.severe(MessageFormat.format(LanguageSupport.instance.getString("region.save.problem.console"),
								region.getName()));
				SimpleRegionMarket.getInstance().printError(e);
			}

			player.sendMessage(LanguageSupport.instance.getString("region.new.owner"));
		}
		region.updateSigns();
	}

	@Override
	public void replacementMap(Region region, HashMap<String, String> replacementMap) {
		if (region.getOptions().exists("price")) {
			String strPrice;
			Double price = (Double) region.getOptions().get("price");
			try {
				strPrice = SimpleRegionMarket.getInstance().getVaultHook().getEconomy().format(price);
			} catch (Throwable e) {
				strPrice = String.format("%.2f", price);
			}
			replacementMap.put("price", strPrice);
		}

		if (region.getOptions().exists("account"))
			replacementMap.put("account", region.getOptions().get("account").toString());

		if (region.getOptions().exists("buyer"))
			replacementMap.put("buyer", region.getOptions().get("buyer").toString());
	}

	@Override
	public Sign makeSign(Player player, Block block, HashMap<String, String> inputMap) {
		ProtectedRegion worldguardRegion = RegionFactory.getProtectedRegionFromLocation(Location.fromBlock(block),
				inputMap.remove("region"));

		if (worldguardRegion != null) {
			Region region = null;
			for (Region regionEntry : regionList) {
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
							player.sendMessage(LanguageSupport.instance.getString("price.not.found"));
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
					player.sendMessage(MessageFormat.format(
							ChatColor.RED + LanguageSupport.instance.getString("price.must.between"), priceMinString,
							priceMaxString));
					return null;
				}

				String account = player.getName();
				{
					String accountString = inputMap.remove("account");
					if (accountString != null) {
						if (SimpleRegionMarket.getInstance().getVaultHook()
								.hasPermission(player, String.format("simpleregionmarket.%s.setaccount", getId()))) {
							if (accountString.equalsIgnoreCase("none")) {
								account = "";
							} else {
								account = accountString;
							}
						}
					}
				}

				region.getOptions().set("price", price);
				region.getOptions().set("account", account);
				clearRegion(region);
			}

			Sign sign = region.addBlockAsSign(block);

			try {
				SimpleRegionMarket.getInstance().getTemplateManager().saveRegion(region);
			} catch (ContentSaveException e) {
				SimpleRegionMarket
						.getInstance()
						.getLogger()
						.severe(MessageFormat.format(LanguageSupport.instance.getString("region.save.problem.console"),
								region.getName()));
				SimpleRegionMarket.getInstance().printError(e);
			}

			return sign;
		} else {
			player.sendMessage(ChatColor.RED + LanguageSupport.instance.getString("sign.make.region.not.found"));
		}
		return null;
	}
}
