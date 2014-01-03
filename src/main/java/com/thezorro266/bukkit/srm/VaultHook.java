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
import lombok.Getter;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
	public static final String VAULT_PLGUIN_NAME = "Vault"; // NON-NLS
	@Getter
	private Permission permission = null;
	@Getter
	private Economy economy = null;

	public void load() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(VAULT_PLGUIN_NAME);

		if (plugin == null || !(plugin instanceof Vault)) {
			SimpleRegionMarket.getInstance().getLogger().info(LanguageSupport.instance.getString("vault.notfound"));
		} else {
			SimpleRegionMarket.getInstance().getLogger().info(LanguageSupport.instance.getString("vault.found"));
			permission = setupPermissions();
			if (permission != null) {
				SimpleRegionMarket
						.getInstance()
						.getLogger()
						.info(MessageFormat.format(
								LanguageSupport.instance.getString("vault.detected.permissionssystem"),
								permission.getName()));
			} else {
				SimpleRegionMarket.getInstance().getLogger()
						.info(LanguageSupport.instance.getString("vault.notdetected.permissionssystem"));
			}
			economy = setupEconomy();
			if (economy != null) {
				SimpleRegionMarket
						.getInstance()
						.getLogger()
						.info(MessageFormat.format(LanguageSupport.instance.getString("vault.detected.economysystem"),
								economy.getName()));
			} else {
				SimpleRegionMarket.getInstance().getLogger()
						.info(LanguageSupport.instance.getString("vault.notdetected.economysystem"));
			}
		}
	}

	private Permission setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			return permissionProvider.getProvider();
		}
		return null;
	}

	private Economy setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			return economyProvider.getProvider();
		}
		return null;
	}

	public String toPermissionString(com.thezorro266.bukkit.srm.helpers.Permission perm) {
		return String.format("simpleregionmarket.%s", perm.toString().replaceAll("_", ".").toLowerCase());
	}

	public boolean hasPermission(Player player, com.thezorro266.bukkit.srm.helpers.Permission perm) {
		return hasPermission(player, toPermissionString(perm));
	}

	public boolean hasPermission(Player player, String perm) {
		if (getPermission() != null) {
			return getPermission().has(player, perm);
		} else {
			return player.hasPermission(perm);
		}
	}
}
