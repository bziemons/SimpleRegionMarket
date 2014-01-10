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
import net.milkbowl.vault.Vault;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class VaultHook {
	private static final String VAULT_PLGUIN_NAME = "Vault"; //NON-NLS

	private boolean vaultEnabled;

	public void load() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(VAULT_PLGUIN_NAME);

		if (plugin == null || !(plugin instanceof Vault)) {
			SimpleRegionMarket.getInstance().getLogger().info(LanguageSupport.instance.getString("vault.notfound"));
			vaultEnabled = false;
		} else {
			SimpleRegionMarket.getInstance().getLogger().info(LanguageSupport.instance.getString("vault.found"));
			vaultEnabled = true;
		}
	}

	public boolean isVaultEnabled() {
		return vaultEnabled;
	}
}
