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
import com.thezorro266.bukkit.srm.exceptions.NotEnoughPermissionsException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;

public abstract class Permissions {
	protected boolean enabled = false;

	public boolean isEnabled() {
		return enabled;
	}

	public void checkPermission(CommandSender sender, String permNode) throws NotEnoughPermissionsException {
		if (!hasPermission(sender, permNode)) {
			throw new NotEnoughPermissionsException(sender, permNode);
		}
	}

	public abstract boolean hasPermission(CommandSender sender, String node);
	public abstract boolean hasPermission(Player player, String node);
	public abstract boolean hasPermissionListSupport();
	public abstract ArrayList<String> getPermissionList(Player player);
}
