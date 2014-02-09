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

import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;

public class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		Boolean isConsole = true;
		if (sender instanceof Player) {
			player = (Player) sender;
			isConsole = false;
		}

		if (args.length < 1) {
			return false;
		}

		if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("v")) { //NON-NLS
			String versionString = MessageFormat.format(LanguageSupport.instance.getString("command.version"),
					SimpleRegionMarket.getInstance()
							.getDescription().getVersion());
			String copyrightString = SimpleRegionMarket.getCopyright();
			sender.sendMessage(ChatColor.YELLOW + String.format("%s, %s", versionString, copyrightString)); //NON-NLS
		} else if (args[0].equalsIgnoreCase("release")) { //NON-NLS
			if (args.length < 2) {
				sender.sendMessage(LanguageSupport.instance.getString("command.release"));
				return true;
			} else {
				final String region = args[1];
				String world = "";
				if (args.length > 2) {
					world = args[2];
				} else {
					if (!isConsole) {
						world = player.getWorld().getName();
					}
				}
				World realWorld = Bukkit.getWorld(world);
				Region realRegion = SimpleRegionMarket.getInstance().getWorldHelper().getRegion(region, realWorld);
				if (realRegion == null) {
					if (realWorld == null) {
						sender.sendMessage(MessageFormat.format(
								LanguageSupport.instance.getString("region.not.found"), region));
					} else {
						sender.sendMessage(MessageFormat.format(
								LanguageSupport.instance.getString("region.in.world.not.found"), region, world));
					}
				} else {
					if (realRegion.getTemplate() instanceof OwnableTemplate) {
						OwnableTemplate ot = (OwnableTemplate) realRegion.getTemplate();
						if (ot.isRegionOccupied(realRegion)) {
							ot.clearRegion(realRegion);
							sender.sendMessage(MessageFormat.format(
									LanguageSupport.instance.getString("region.in.world.released"), region, world));

							realRegion.updateSigns();

							try {
								SimpleRegionMarket.getInstance().getTemplateManager().saveRegion(realRegion);
							} catch (ContentSaveException e) {
								sender.sendMessage(ChatColor.RED
										+ LanguageSupport.instance.getString("region.save.problem.player"));
								SimpleRegionMarket
										.getInstance()
										.getLogger()
										.severe(MessageFormat.format(
												LanguageSupport.instance.getString("region.save.problem.console"),
												realRegion.getName()));
								SimpleRegionMarket.getInstance().printError(e);
							}
						} else {
							sender.sendMessage(MessageFormat.format(
									LanguageSupport.instance.getString("region.in.world.already.free"), region,
									world));
						}
					} else {
						sender.sendMessage(MessageFormat.format(
								LanguageSupport.instance.getString("region.in.world.cannot.be.owned"), region,
								world));
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("remove")) { //NON-NLS
			if (args.length < 2) {
				sender.sendMessage(LanguageSupport.instance.getString("command.remove"));
				return true;
			} else {
				final String region = args[1];
				String world = "";
				if (args.length > 2) {
					world = args[2];
				} else {
					if (!isConsole) {
						world = player.getWorld().getName();
					}
				}
				World realWorld = Bukkit.getWorld(world);
				Region realRegion = SimpleRegionMarket.getInstance().getWorldHelper().getRegion(region, realWorld);
				if (realRegion == null) {
					if (realWorld == null) {
						sender.sendMessage(MessageFormat.format(
								LanguageSupport.instance.getString("region.not.found"), region));
					} else {
						sender.sendMessage(MessageFormat.format(
								LanguageSupport.instance.getString("region.in.world.not.found"), region, world));
					}
				} else {
					if (realRegion.getTemplate() instanceof OwnableTemplate) {
						((OwnableTemplate) realRegion.getTemplate()).clearRegion(realRegion);
					}

					SimpleRegionMarket.getInstance().getTemplateManager().removeRegion(realRegion);
					RegionFactory.instance.destroyRegion(realRegion);
					sender.sendMessage(MessageFormat.format(
							LanguageSupport.instance.getString("region.in.world.removed"), region, world));
				}
			}
		} else if (args[0].equalsIgnoreCase("region")) { //NON-NLS
			Region region = null;
			if (!isConsole) {
				ArrayList<Region> playerRegions = SimpleRegionMarket.getInstance().getPlayerManager().getPlayerRegions((Player) sender);

				if (playerRegions.size() == 1) {
					region = playerRegions.get(0);
				}
			}

			int skip = 1;
			String regionString = "";
			if (region == null) {
				if (args.length > 2) {
					skip = 2;
					regionString = args[1];
					region = SimpleRegionMarket.getInstance().getWorldHelper().getRegion(regionString, null);
					if (region == null && !isConsole) {
						region = SimpleRegionMarket.getInstance().getWorldHelper().getRegion(regionString, player.getWorld());
					}
				}
			}

			if (region != null) {
				String[] realArgs = new String[args.length - skip];
				System.arraycopy(args, skip, realArgs, 0, realArgs.length);

				region.getTemplate().regionCommand(region, sender, realArgs);
			} else {
				if (regionString.isEmpty()) {
					sender.sendMessage(LanguageSupport.instance.getString("region.specify"));
				} else {
					sender.sendMessage(MessageFormat.format(LanguageSupport.instance.getString("region.not.found"), regionString));
				}
			}
		} else {
			sender.sendMessage(LanguageSupport.instance.getString("not.yet.implemented"));
			return false;
		}
		return true;
	}
}
