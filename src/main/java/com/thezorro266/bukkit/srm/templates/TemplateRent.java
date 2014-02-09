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

import com.thezorro266.bukkit.srm.LanguageSupport;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.Utils;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.factories.SignFactory;
import com.thezorro266.bukkit.srm.hooks.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class TemplateRent extends TemplateLease {
	public TemplateRent(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		type = "rent";
	}

	@Override
	public void clickSign(Player player, SignFactory.Sign sign) {
		RegionFactory.Region region = sign.getRegion();
		if (isRegionOccupied(region)) {
			if (isRegionOwner(player, region)) {
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

				int time = (Integer) region.getOptions().get("time");
				int newtime = (Integer) region.getOptions().get("renttime") + time;
				if (newtime - (int) (System.currentTimeMillis() / 1000) <= maxTime) {
					region.getOptions().set("renttime", newtime);

					player.sendMessage(MessageFormat.format(LanguageSupport.instance.getString("region.expanded"),
							Utils.getTimeString(time)));
				} else {
					player.sendMessage(LanguageSupport.instance.getString("region.expand.maxtime.reached"));
				}
			} else {
				player.sendMessage(LanguageSupport.instance.getString("region.already.rented"));
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
	public void schedule() {
		synchronized (regionList) {
			for (RegionFactory.Region region : regionList) {
				if (isRegionOccupied(region)) {
					int currentSecs = (int) (System.currentTimeMillis() / 1000);

					if (currentSecs > (Integer) region.getOptions().get("renttime")) {
						OfflinePlayer op = Bukkit.getOfflinePlayer((String) region.getOptions().get("owner"));

						clearRegion(region);

						if (op.isOnline()) {
							op.getPlayer().sendMessage(
									MessageFormat.format(
											LanguageSupport.instance.getString("region.lease.expired"),
											region.getName()));
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
}
