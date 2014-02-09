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

package com.thezorro266.bukkit.srm.templates;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import com.thezorro266.bukkit.srm.hooks.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.LanguageSupport;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.Utils;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.factories.SignFactory;
import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class TemplateLease extends TemplateSell implements TimedTemplate {
	protected int minTime = 60;
	protected int maxTime = -1;

	public TemplateLease(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);

		type = "lease";

		if (templateConfigSection.contains("renttime.min")) {
			minTime = templateConfigSection.getInt("renttime.min");
		}
		if (templateConfigSection.contains("renttime.max")) {
			maxTime = templateConfigSection.getInt("renttime.max");
		}
	}

	@Override
	public void schedule() {
		synchronized (regionList) {
			for (RegionFactory.Region region : regionList) {
				if (isRegionOccupied(region)) {
					int currentSecs = (int) (System.currentTimeMillis() / 1000);

					if (currentSecs > (Integer) region.getOptions().get("renttime")) {
						OfflinePlayer op = Bukkit.getOfflinePlayer((String) region.getOptions().get("owner"));

						Economy ec = SimpleRegionMarket.getInstance().getEconomy();
						double price = (Double) region.getOptions().get("price");
						String playerAccount = op.getName();
						String regionAccount = (String) region.getOptions().get("account");
						boolean moneyOkay = true;
						if (ec.isEnabled() && price > 0) {
							if (!ec.isValidAccount(playerAccount)) {
								if (op.isOnline()) {
									op.getPlayer().sendMessage(
											MessageFormat.format(
													LanguageSupport.instance.getString("economy.lease.problem"),
													region.getName()));
									op.getPlayer().sendMessage(
											LanguageSupport.instance.getString("economy.player.no.account"));
								}
								moneyOkay = false;
							}
							if (!ec.hasEnough(playerAccount, price)) {
								if (op.isOnline()) {
									op.getPlayer().sendMessage(
											MessageFormat.format(
													LanguageSupport.instance.getString("economy.lease.problem"),
													region.getName()));
									op.getPlayer().sendMessage(
											LanguageSupport.instance.getString("economy.player.no.money"));
								}
								moneyOkay = false;
							}
						}

						if (moneyOkay) {
							ec.subtractMoney(playerAccount, price);
							if (!regionAccount.isEmpty() && ec.isValidAccount(regionAccount)) {
								ec.addMoney(regionAccount, price);
							}

							int time = (Integer) region.getOptions().get("time");
							region.getOptions().set("renttime", currentSecs + time);

							if (op.isOnline()) {
								op.getPlayer().sendMessage(
										MessageFormat.format(
												LanguageSupport.instance.getString("region.lease.expanded"),
												region.getName(), Utils.getTimeLeft(currentSecs + time)));
							}
						} else {
							clearRegion(region);

							if (op.isOnline()) {
								op.getPlayer().sendMessage(
										MessageFormat.format(
												LanguageSupport.instance.getString("region.lease.expired"),
												region.getName()));
							}
						}

						try {
							SimpleRegionMarket.getInstance().getTemplateManager().saveRegion(region);
						} catch (ContentSaveException e) {
							SimpleRegionMarket
									.getInstance()
									.getLogger()
									.severe(MessageFormat.format(
											LanguageSupport.instance.getString("region.save.problem.console"),
											region.getName()));
							SimpleRegionMarket.getInstance().printError(e);
						}
					}
					region.updateSigns();
				}
			}
		}
	}

	@Override
	public boolean cancel(RegionFactory.Region region, Player player) {
		return false;
	}

	@Override
	public String getMainOwner(RegionFactory.Region region) {
		return (String) region.getOptions().get("owner");
	}

	@Override
	public boolean setRegionOccupied(RegionFactory.Region region, boolean isOccupied) {
		if (!isOccupied) {
			region.getOptions().set("renttime", null);
			region.getOptions().set("owner", null);
		}
		region.getOptions().set("state", (isOccupied ? "occupied" : "free"));
		return true;
	}

	@Override
	public void clickSign(Player player, SignFactory.Sign sign) {
		RegionFactory.Region region = sign.getRegion();
		if (isRegionOccupied(region)) {
			if (isRegionOwner(player, region)) {
				player.sendMessage(LanguageSupport.instance.getString("region.yours"));
			} else {
				player.sendMessage(LanguageSupport.instance.getString("region.already.leased"));
			}
		} else {
			// TODO: Player permissions
			Economy ec = SimpleRegionMarket.getInstance().getEconomy();
			double price = (Double) region.getOptions().get("price");
			String playerAccount = player.getName();
			String regionAccount = (String) region.getOptions().get("account");
			if (ec.isEnabled() && price > 0) {
				if (!ec.isValidAccount(playerAccount)) {
					player.sendMessage(LanguageSupport.instance.getString("economy.player.no.account"));
					return;
				}
				if (!ec.hasEnough(playerAccount, price)) {
					player.sendMessage(LanguageSupport.instance.getString("economy.player.no.money"));
					return;
				}
			}
			ec.subtractMoney(playerAccount, price);
			if (!regionAccount.isEmpty() && ec.isValidAccount(regionAccount)) {
				ec.addMoney(regionAccount, price);
			}

			clearRegion(region);
			if (buyerIsOwner) {
				setRegionOwners(region, new OfflinePlayer[] { player });
			} else {
				setRegionMembers(region, new OfflinePlayer[] { player });
			}

			int currentSecs = (int) (System.currentTimeMillis() / 1000);
			int time = (Integer) region.getOptions().get("time");
			region.getOptions().set("renttime", currentSecs + time);

			region.getOptions().set("owner", player.getName());
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
	public void replacementMap(RegionFactory.Region region, HashMap<String, String> replacementMap) {
		super.replacementMap(region, replacementMap);

		if (region.getOptions().exists("owner"))
			replacementMap.put("owner", (String) region.getOptions().get("owner"));

		if (region.getOptions().exists("time"))
			replacementMap.put(
					"time",
					Utils.getTimeLeft((int) (System.currentTimeMillis() / 1000)
							+ (Integer) region.getOptions().get("time")));

		if (region.getOptions().exists("renttime"))
			replacementMap.put("timeleft", Utils.getTimeLeft((Integer) region.getOptions().get("renttime")));
	}

	@Override
	public SignFactory.Sign makeSign(Player player, Block block, HashMap<String, String> inputMap) {
		ProtectedRegion worldguardRegion = RegionFactory.getProtectedRegionFromLocation(Location.fromBlock(block),
				inputMap.get("region"));

		if (worldguardRegion != null) {
			RegionFactory.Region region = SimpleRegionMarket.getInstance().getWorldHelper()
					.getRegionExact(worldguardRegion.getId(), block.getWorld());

			if (region == null) {
				region = RegionFactory.instance.createRegion(this, block.getWorld(), worldguardRegion);

				if (SimpleRegionMarket.getInstance().getEconomy().isEnabled()) {
					double price;
					String account = player.getName();

					String priceString = inputMap.get("price");
					if (priceString != null) {
						try {
							price = Double.parseDouble(priceString);
						} catch (NullPointerException e) {
							player.sendMessage(LanguageSupport.instance.getString("price.not.found"));
							return null;
						} catch (NumberFormatException e) {
							player.sendMessage(LanguageSupport.instance.getString("price.not.found"));
							return null;
						}
					} else {
						price = priceMin;
					}

					if (priceMin > price || (priceMax != -1 && price > priceMax)) {
						String priceMinString = SimpleRegionMarket.getInstance().getEconomy().format(priceMin);
						String priceMaxString = SimpleRegionMarket.getInstance().getEconomy().format(priceMax);
						player.sendMessage(MessageFormat.format(
								ChatColor.RED + LanguageSupport.instance.getString("price.must.between"), priceMinString,
								priceMaxString));
						return null;
					}

					{
						String accountString = inputMap.get("account");
						if (accountString != null) {
							if (accountString.equalsIgnoreCase("none")) {
								account = "";
							} else {
								account = accountString;
							}
						}
					}

					region.getOptions().set("price", price);
					region.getOptions().set("account", account);
				}

				int time = minTime;
				{
					String timeString = inputMap.get("time");
					if (timeString != null) {
						time = Utils.parseTime(timeString);
						if (minTime > time || (maxTime != -1 && time > maxTime)) {
							player.sendMessage(ChatColor.RED
									+ MessageFormat.format(LanguageSupport.instance.getString("renttime.between"),
											minTime, maxTime));
							return null;
						}
					}
				}

				region.getOptions().set("time", time);
				setRegionOccupied(region, false);
				clearOwnershipOfRegion(region);

				if (regionReset) {
					try {
						SimpleRegionMarket.getInstance().getWorldEditManager().saveRegionToSchematic(region);
					} catch (IOException e) {
						player.sendMessage(LanguageSupport.instance.getString("region.schematic.save.failure"));
						SimpleRegionMarket
								.getInstance()
								.getLogger()
								.severe(MessageFormat.format(LanguageSupport.instance
										.getString("region.in.world.schematic.save.failure.console"), region.getName(),
										region.getWorld().getName()));
						SimpleRegionMarket.getInstance().printError(e);
					}
				}
			} else if (region.getTemplate() != this) {
				player.sendMessage(LanguageSupport.instance.getString("sign.create.different.template"));
				return null;
			}

			SignFactory.Sign sign = region.addBlockAsSign(block);

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
