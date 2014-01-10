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

package com.thezorro266.bukkit.srm.hooks;

import com.thezorro266.bukkit.srm.LanguageSupport;
import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.MessageFormat;
import java.util.ArrayList;

public class VaultPermissions extends Permissions {
	private Permission permission = null;

	public VaultPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}

		if (permission != null) {
			SimpleRegionMarket
					.getInstance()
					.getLogger()
					.info(MessageFormat.format(
							LanguageSupport.instance.getString("vault.detected.permissionssystem"),
							permission.getName()));

			// Set enabled
			enabled = true;
		} else {
			SimpleRegionMarket.getInstance().getLogger()
					.info(LanguageSupport.instance.getString("vault.notdetected.permissionssystem"));
		}
	}

	@Override
	public boolean hasPermission(CommandSender sender, String node) {
		return isEnabled() ? permission.has(sender, node) : sender.isOp();
	}

	@Override
	public boolean hasPermission(Player player, String node) {
		return isEnabled() ? permission.has(player, node) : player.isOp();
	}

	@Override
	public boolean hasPermissionListSupport() {
		return false;
	}

	@Override
	public ArrayList<String> getPermissionList(Player player) {
		return null;
	}
}
