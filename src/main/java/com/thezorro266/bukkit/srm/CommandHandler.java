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

import com.thezorro266.bukkit.srm.exceptions.NotEnoughPermissionsException;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.hooks.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

public class CommandHandler implements CommandExecutor {
	private Logger logger;
	private Permissions permissions;

	public CommandHandler() {
		this.logger = SimpleRegionMarket.getInstance().getLogger();
		this.permissions = SimpleRegionMarket.getInstance().getPermissions();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			try {
				command("", sender, new String[]{});
			} catch (NotEnoughPermissionsException e) {
				sender.sendMessage(ChatColor.RED + MessageFormat.format(LanguageSupport.instance.getString("not.enough.permissions.message"), e.getPermNode()));
			}
		}

		String[] realArguments = new String[args.length - 1];
		System.arraycopy(args, 1, realArguments, 0, args.length - 1);

		try {
			command(args[0], sender, realArguments);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().isEmpty()) {
				return false;
			} else {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		} catch (NotEnoughPermissionsException e) {
			sender.sendMessage(ChatColor.RED + MessageFormat.format(LanguageSupport.instance.getString("not.enough.permissions.message"), e.getPermNode()));
		}
		return true;
	}

	public void command(String cmd, CommandSender sender, String[] arguments) throws NotEnoughPermissionsException {
		Player player = (sender instanceof Player ? (Player) sender : null);

		if (cmd.isEmpty() || cmd.equalsIgnoreCase("help") || cmd.equals("?")) {
			permissions.checkPermission(sender, "srm.help");
			sender.sendMessage("No help for you!");

		} else if (cmd.equalsIgnoreCase("version") || cmd.equalsIgnoreCase("v")) {
			permissions.checkPermission(sender, "srm.version");
			String versionString = MessageFormat.format(LanguageSupport.instance.getString("command.version"), SimpleRegionMarket.getInstance().getDescription().getVersion());
			String copyrightString = SimpleRegionMarket.getCopyright();
			sender.sendMessage(ChatColor.YELLOW + String.format("%s, %s", versionString, copyrightString)); //NON-NLS
		} else if (cmd.equalsIgnoreCase("language") || cmd.equalsIgnoreCase("lang")) {
			permissions.checkPermission(sender, "srm.admin.language");

		} else if (cmd.equalsIgnoreCase("reload")) {
			permissions.checkPermission(sender, "srm.admin.reload");

		} else if (cmd.equalsIgnoreCase("info")) {
			permissions.checkPermission(sender, "srm.t.template.info");

		} else if (cmd.equalsIgnoreCase("tp")) {
			permissions.checkPermission(sender, "srm.t.template.tp");

		} else if (cmd.equalsIgnoreCase("list")) {
			permissions.checkPermission(sender, "srm.t.template.list");

		} else if (cmd.equalsIgnoreCase("region")) {
			Region region = null;
			if (player != null) {
				ArrayList<Region> playerRegions = SimpleRegionMarket.getInstance().getPlayerManager().getPlayerRegions((Player) sender);

				if (playerRegions.size() == 1) {
					region = playerRegions.get(0);
				}
			}

			String[] regionArguments = null;

			String regionString = "";
			if (region == null) {
				if (arguments.length > 2) {
					regionString = arguments[1];
					region = SimpleRegionMarket.getInstance().getWorldHelper().getRegion(regionString, null);
					if (region == null && player != null) {
						region = SimpleRegionMarket.getInstance().getWorldHelper().getRegion(regionString, player.getWorld());
					}
					if (region != null) {
						regionArguments = new String[arguments.length - 2];
						System.arraycopy(arguments, 2, regionArguments, 0, arguments.length - 2);
					}
				}
			}

			if (region != null) {
				if (regionArguments == null) {
					regionArguments = new String[arguments.length - 1];
					System.arraycopy(arguments, 1, regionArguments, 0, arguments.length - 1);
				}

				region.getTemplate().regionCommand(region, arguments[0], sender, regionArguments);
			} else {
				if (regionString.isEmpty()) {
					throw new IllegalArgumentException(LanguageSupport.instance.getString("region.specify"));
				} else {
					throw new IllegalArgumentException(MessageFormat.format(LanguageSupport.instance.getString("region.not.found"), regionString));
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
