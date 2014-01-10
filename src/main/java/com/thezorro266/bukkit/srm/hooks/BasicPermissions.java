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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BasicPermissions extends Permissions {
	public BasicPermissions() {
		enabled = true;
	}

	@Override
	public boolean hasPermission(CommandSender sender, String node) {
		return isEnabled() ? sender.hasPermission(node) : sender.isOp();
	}

	@Override
	public boolean hasPermission(Player player, String node) {
		return isEnabled() ? player.hasPermission(node) : player.isOp();
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
