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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thezorro266.bukkit.srm.exceptions.NotEnoughPermissionsException;
import com.thezorro266.bukkit.srm.hooks.Economy;
import com.thezorro266.bukkit.srm.hooks.Permissions;
import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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
	protected boolean regionReset = false;

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
		if (templateConfigSection.contains("regionreset")) {
			regionReset = templateConfigSection.getBoolean("regionreset");
		}
	}

	@Override
	public String getMainOwner(Region region) {
		return (String) region.getOptions().get("buyer");
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
		if (super.clearRegion(region)) {
			setRegionOccupied(region, false);
			if (regionReset) {
				SimpleRegionMarket.getInstance().getWorldEditManager().replaceRegionFromSchematic(region);
			}
			return true;
		}
		return false;
	}

	@Override
	public void regionCommand(Region region, String cmd, CommandSender sender, String[] arguments) throws NotEnoughPermissionsException {
		Permissions permissions = SimpleRegionMarket.getInstance().getPermissions();
		Logger logger = SimpleRegionMarket.getInstance().getLogger();

		if (cmd.isEmpty() || cmd.equalsIgnoreCase("help") || cmd.equals("?")) {

		} else if (cmd.equalsIgnoreCase("terminate")) { //NON-NLS
			permissions.checkPermission(sender, "srm.admin.terminate");

			OwnableTemplate ot = (OwnableTemplate) region.getTemplate();
			if (ot.isRegionOccupied(region)) {
				ot.clearRegion(region);
				sender.sendMessage(MessageFormat.format(LanguageSupport.instance.getString("region.in.world.released"), region, region.getWorld()));

				region.updateSigns();

				try {
					SimpleRegionMarket.getInstance().getTemplateManager().saveRegion(region);
				} catch (ContentSaveException e) {
					sender.sendMessage(ChatColor.RED + LanguageSupport.instance.getString("region.save.problem.player"));
					logger.log(Level.SEVERE, MessageFormat.format(LanguageSupport.instance.getString("region.save.problem.console"), region.getName()), e);
				}
			} else {
				sender.sendMessage(MessageFormat.format(LanguageSupport.instance.getString("region.in.world.already.free"), region, region.getWorld()));
			}
		} else if (cmd.equalsIgnoreCase("remove")) { //NON-NLS
			((OwnableTemplate) region.getTemplate()).clearRegion(region);
			SimpleRegionMarket.getInstance().getTemplateManager().removeRegion(region);
			RegionFactory.instance.destroyRegion(region);
			sender.sendMessage(MessageFormat.format(LanguageSupport.instance.getString("region.in.world.removed"), region, region.getWorld()));
		} else if (cmd.equalsIgnoreCase("snapshot") && regionReset) {
			try {
				SimpleRegionMarket.getInstance().getWorldEditManager().saveRegionToSchematic(region);
				sender.sendMessage(MessageFormat.format(LanguageSupport.instance.getString("region.schematic.save.successful"), region.getName()));
			} catch (IOException e) {
				sender.sendMessage(LanguageSupport.instance.getString("region.schematic.save.failure"));
				logger.log(Level.SEVERE, MessageFormat.format(LanguageSupport.instance.getString("region.in.world.schematic.save.failure.console"), region.getName()), e);
			}
		} else {
			sender.sendMessage(LanguageSupport.instance.getString("not.yet.implemented"));
			// TODO: region command help
		}
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
			double price = (Double) region.getOptions().get("price");

			replacementMap.put("price", SimpleRegionMarket.getInstance().getEconomy().format(price));
		} else {
			replacementMap.put("price", "free");
		}

		if (region.getOptions().exists("account")) {
			replacementMap.put("account", region.getOptions().get("account").toString());
		} else {
			replacementMap.put("account", "");
		}

		if (region.getOptions().exists("buyer"))
			replacementMap.put("buyer", region.getOptions().get("buyer").toString());
	}

	@Override
	public Sign makeSign(Player player, Block block, HashMap<String, String> inputMap) {
		ProtectedRegion worldguardRegion = RegionFactory.getProtectedRegionFromLocation(Location.fromBlock(block),
				inputMap.get("region"));

		if (worldguardRegion != null) {
			Region region = SimpleRegionMarket.getInstance().getWorldHelper()
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

	public boolean doesRegionReset() {
		return regionReset;
	}
}
