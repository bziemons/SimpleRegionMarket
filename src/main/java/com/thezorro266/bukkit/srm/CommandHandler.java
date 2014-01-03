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

package com.thezorro266.bukkit.srm;

import java.text.MessageFormat;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.helpers.Permission;
import com.thezorro266.bukkit.srm.templates.Template;
import com.thezorro266.bukkit.srm.templates.interfaces.OwnableTemplate;

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
			if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, Permission.COMMAND_RELEASE)) {
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
			} else {
				sender.sendMessage(LanguageSupport.instance.getString("player.no.permission"));
			}
		} else if (args[0].equalsIgnoreCase("remove")) { //NON-NLS
			if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, Permission.COMMAND_REMOVE)) {
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
			} else {
				sender.sendMessage(LanguageSupport.instance.getString("player.no.permission"));
			}
		} else if (args[0].equalsIgnoreCase("list")) { //NON-NLS TODO Can list own and rented regions
			if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, Permission.COMMAND_LIST)) {
				sender.sendMessage(LanguageSupport.instance.getString("not.yet.implemented"));
			} else {
				sender.sendMessage(LanguageSupport.instance.getString("player.no.permission"));
			}
		} else if (args[0].equalsIgnoreCase("addmember")) { //NON-NLS
			if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, Permission.COMMAND_ADDMEMBER_OWN)
					|| SimpleRegionMarket.getInstance().getVaultHook()
							.hasPermission(player, Permission.COMMAND_ADDMEMBER_OTHER)) {
				if (args.length < 3) {
					sender.sendMessage(LanguageSupport.instance.getString("command.addmember"));
				} else {
					final Player givenPlayer = Bukkit.getPlayer(args[1]);
					if (givenPlayer == null) {
						sender.sendMessage(LanguageSupport.instance.getString("player.not.found"));
						return true;
					}
					final String region = args[2];
					String world;
					if (args.length > 3) {
						world = args[3];
					} else {
						if (isConsole) {
							sender.sendMessage("You have to type the world in the console");
							return true;
						} else {
							world = player.getWorld().getName();
						}
					}
					final World worldWorld = Bukkit.getWorld(world);
					if (worldWorld == null) {
						sender.sendMessage("The given world was not found.");
						return true;
					}
					final ProtectedRegion protectedRegion = SimpleRegionMarket.getInstance().getWorldGuardManager()
							.getProtectedRegion(worldWorld, region);
					if (protectedRegion == null) {
						final ArrayList<String> list = new ArrayList<String>();
						list.add(region);
						list.add(world);
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
						return true;
					}

					Boolean found = false;
					for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
						/*
						if (Utils.getEntry(token, world, region, "taken") != null) {
							if (Utils.getEntryBoolean(token, world, region, "taken")) {
								if (token.canAddMember()) {
									if (isConsole || SimpleRegionMarket.permManager.canPlayerAddMember(player, token)
											|| SimpleRegionMarket.permManager.isAdmin(player)) { // Permission
										if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
												|| SimpleRegionMarket.permManager.isAdmin(player)) {
											SimpleRegionMarket.wgManager.addMember(protectedRegion, givenPlayer);
											found = true;
											break;
										} else {
											langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
										}
									} else {
										langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDMEMBER", null);
									}
								} else {
									if (isConsole) {
										langHandler.consoleOut("CMD.ADDMEMBER.NO_ADDMEMBER", Level.SEVERE, null);
									} else {
										langHandler.playerErrorOut(player, "CMD.ADDMEMEBR.NO_ADDMEMBER", null);
									}
								}
							}
						}
						*/
					}

					if (found) {
						sender.sendMessage(String.format("Added %s to the region %s in world %s as member.",
								givenPlayer.getName(), region, world));
					} else {
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
					}
				}
			} else {
				sender.sendMessage("You do not have permission to do this.");
			}
		} else if (args[0].equalsIgnoreCase("remmember") || args[0].equalsIgnoreCase("removemember")) {
			if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, Permission.COMMAND_ADDMEMBER_OWN)
					|| SimpleRegionMarket.getInstance().getVaultHook()
							.hasPermission(player, Permission.COMMAND_ADDMEMBER_OTHER)) {
				if (args.length < 3) {
					sender.sendMessage("Usage: /rm removemember <player> <region> (<world>) - Removes the member from the region");
				} else {
					final Player givenPlayer = Bukkit.getPlayer(args[1]);
					if (givenPlayer == null) {
						sender.sendMessage("The given player was not found.");
						return true;
					}
					final String region = args[2];
					String world;
					if (args.length > 3) {
						world = args[3];
					} else {
						if (isConsole) {
							sender.sendMessage("You have to type the world in the console");
							return true;
						} else {
							world = player.getWorld().getName();
						}
					}
					final World worldWorld = Bukkit.getWorld(world);
					if (worldWorld == null) {
						sender.sendMessage("The given world was not found.");
						return true;
					}
					final ProtectedRegion protectedRegion = SimpleRegionMarket.getInstance().getWorldGuardManager()
							.getProtectedRegion(worldWorld, region);
					if (protectedRegion == null) {
						final ArrayList<String> list = new ArrayList<String>();
						list.add(region);
						list.add(world);
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
						return true;
					}

					Boolean found = false;
					for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
						/*
						if (Utils.getEntry(token, world, region, "taken") != null) {
							if (Utils.getEntryBoolean(token, world, region, "taken")) {
								if (token.canAddMember()) {
									if (isConsole || SimpleRegionMarket.permManager.canPlayerAddMember(player, token)
											|| SimpleRegionMarket.permManager.isAdmin(player)) {
										if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
												|| SimpleRegionMarket.permManager.isAdmin(player)) {
											SimpleRegionMarket.wgManager.removeMember(protectedRegion, givenPlayer);
											found = true;
											break;
										} else {
											langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
										}
									} else {
										langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDMEMBER", null);
									}
								} else {
									if (isConsole) {
										langHandler.consoleOut("CMD.ADDMEMBER.NO_ADDMEMBER", Level.SEVERE, null);
									} else {
										langHandler.playerErrorOut(player, "CMD.ADDMEMEBR.NO_ADDMEMBER", null);
									}
								}
							}
						}
						*/
					}

					if (found) {
						sender.sendMessage(String.format("Removed the member %s from the region %s in world %s.",
								givenPlayer.getName(), region, world));
					} else {
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
					}
				}
			} else {
				sender.sendMessage("You do not have permission to do this.");
			}
		} else if (args[0].equalsIgnoreCase("addowner")) {
			if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, Permission.COMMAND_ADDOWNER_OWN)
					|| SimpleRegionMarket.getInstance().getVaultHook()
							.hasPermission(player, Permission.COMMAND_ADDOWNER_OTHER)) {
				if (args.length < 3) {
					sender.sendMessage("Usage: /rm addowner <player> <region> (<world>) - Adds the player as an owner to the region");
				} else {
					final Player givenPlayer = Bukkit.getPlayer(args[1]);
					if (givenPlayer == null) {
						sender.sendMessage("The given player was not found.");
						return true;
					}
					final String region = args[2];
					String world;
					if (args.length > 3) {
						world = args[3];
					} else {
						if (isConsole) {
							sender.sendMessage("You have to type the world in the console");
							return true;
						} else {
							world = player.getWorld().getName();
						}
					}
					final World worldWorld = Bukkit.getWorld(world);
					if (worldWorld == null) {
						sender.sendMessage("The given world was not found.");
						return true;
					}
					final ProtectedRegion protectedRegion = SimpleRegionMarket.getInstance().getWorldGuardManager()
							.getProtectedRegion(worldWorld, region);
					if (protectedRegion == null) {
						final ArrayList<String> list = new ArrayList<String>();
						list.add(region);
						list.add(world);
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
						return true;
					}

					Boolean found = false;
					for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
						/*
						if (Utils.getEntry(token, world, region, "taken") != null) {
							if (Utils.getEntryBoolean(token, world, region, "taken")) {
								if (token.canAddOwner()) {
									if (isConsole || SimpleRegionMarket.permManager.canPlayerAddOwner(player, token)
											|| SimpleRegionMarket.permManager.isAdmin(player)) {
										if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
												|| SimpleRegionMarket.permManager.isAdmin(player)) {
											SimpleRegionMarket.wgManager.addOwner(protectedRegion, givenPlayer);
											found = true;
											break;
										} else {
											langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
										}
									} else {
										langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDOWNER", null);
									}
								} else {
									if (isConsole) {
										langHandler.consoleOut("CMD.ADDOWNER.NO_ADDOWNER", Level.SEVERE, null);
									} else {
										langHandler.playerErrorOut(player, "CMD.ADDOWNER.NO_ADDOWNER", null);
									}
								}
							}
						}
						*/
					}

					if (found) {
						sender.sendMessage(String.format("Added %s to the region %s in world %s as owner.",
								givenPlayer.getName(), region, world));
					} else {
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
					}
				}
			} else {
				sender.sendMessage("You do not have permission to do this.");
			}
		} else if (args[0].equalsIgnoreCase("remowner") || args[0].equalsIgnoreCase("removeowner")) {
			if (SimpleRegionMarket.getInstance().getVaultHook().hasPermission(player, Permission.COMMAND_ADDOWNER_OWN)
					|| SimpleRegionMarket.getInstance().getVaultHook()
							.hasPermission(player, Permission.COMMAND_ADDOWNER_OTHER)) {
				if (args.length < 3) {
					sender.sendMessage("Usage: /rm removeowner <player> <region> (<world>) - Removes the owner from the region");
				} else {
					final Player givenPlayer = Bukkit.getPlayer(args[1]);
					if (givenPlayer == null) {
						sender.sendMessage("The given player was not found.");
						return true;
					}
					final String region = args[2];
					String world;
					if (args.length > 3) {
						world = args[3];
					} else {
						if (isConsole) {
							sender.sendMessage("You have to type the world in the console");
							return true;
						} else {
							world = player.getWorld().getName();
						}
					}
					final World worldWorld = Bukkit.getWorld(world);
					if (worldWorld == null) {
						sender.sendMessage("The given world was not found.");
						return true;
					}
					final ProtectedRegion protectedRegion = SimpleRegionMarket.getInstance().getWorldGuardManager()
							.getProtectedRegion(worldWorld, region);
					if (protectedRegion == null) {
						final ArrayList<String> list = new ArrayList<String>();
						list.add(region);
						list.add(world);
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
						return true;
					}

					Boolean found = false;
					for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
						/*
						if (Utils.getEntry(token, world, region, "taken") != null) {
							if (Utils.getEntryBoolean(token, world, region, "taken")) {
								if (token.canAddOwner()) {
									if (isConsole || SimpleRegionMarket.permManager.canPlayerAddOwner(player, token)
											|| SimpleRegionMarket.permManager.isAdmin(player)) {
										if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
												|| SimpleRegionMarket.permManager.isAdmin(player)) {
											SimpleRegionMarket.wgManager.removeOwner(protectedRegion, givenPlayer);
											found = true;
											break;
										} else {
											langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
										}
									} else {
										langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDOWNER", null);
									}
								} else {
									if (isConsole) {
										langHandler.consoleOut("CMD.ADDOWNER.NO_ADDOWNER", Level.SEVERE, null);
									} else {
										langHandler.playerErrorOut(player, "CMD.ADDOWNER.NO_ADDOWNER", null);
									}
								}
							}
						}
						*/
					}

					if (found) {
						sender.sendMessage(String.format("Added %s to the region %s in world %s as owner.",
								givenPlayer.getName(), region, world));
					} else {
						sender.sendMessage(String.format("Region %s was not found in world %s.", region, world));
					}
				}
			} else {
				sender.sendMessage("You do not have permission to do this.");
			}
		} else {
			return false;
		}
		return true;
	}
}
