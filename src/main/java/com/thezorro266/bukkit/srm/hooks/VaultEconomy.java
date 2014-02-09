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
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.MessageFormat;

public class VaultEconomy extends Economy {
	private net.milkbowl.vault.economy.Economy economy = null;

	public VaultEconomy() {
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		if (economy != null) {
			SimpleRegionMarket
					.getInstance()
					.getLogger()
					.info(MessageFormat.format(LanguageSupport.instance.getString("vault.detected.economysystem"),
							economy.getName()));

			// Set enabled
			enabled = true;
		} else {
			SimpleRegionMarket.getInstance().getLogger()
					.info(LanguageSupport.instance.getString("vault.notdetected.economysystem"));
		}
	}

	@Override
	public boolean isEnabled() {
		try {
			return economy.isEnabled();
		} catch (NullPointerException t) {
			return super.isEnabled();
		}
	}

	@Override
	public boolean isValidAccount(String account) {
		try {
			return economy.hasAccount(account);
		} catch (NullPointerException t) {
			SimpleRegionMarket.getInstance().getLogger().warning(MessageFormat.format(LanguageSupport.instance.getString("vault.economy.problem"), t.toString()));
			return false;
		}
	}

	@Override
	public boolean hasEnough(String account, double money) {
		try {
			return economy.has(account, money);
		} catch (NullPointerException t) {
			SimpleRegionMarket.getInstance().getLogger().warning(MessageFormat.format(LanguageSupport.instance.getString("vault.economy.problem"), t.toString()));
			return false;
		}
	}

	@Override
	public boolean subtractMoney(String account, double money) {
		if (money < 0) {
			return addMoney(account, -money);
		} else if (money > 0) {
			try {
				EconomyResponse er = economy.withdrawPlayer(account, money);
				if (er.type != EconomyResponse.ResponseType.SUCCESS) {
					SimpleRegionMarket.getInstance().getLogger().warning(MessageFormat.format(LanguageSupport.instance.getString("vault.economy.problem"), er.type.toString() + ": " + er.errorMessage));
				} else {
					return true;
				}
			} catch (NullPointerException t) {
				SimpleRegionMarket.getInstance().getLogger().warning(MessageFormat.format(LanguageSupport.instance.getString("vault.economy.problem"), t.toString()));
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean addMoney(String account, double money) {
		if (money < 0) {
			return subtractMoney(account, -money);
		} else if (money > 0) {
			try {
				EconomyResponse er = economy.withdrawPlayer(account, money);
				if (er.type != EconomyResponse.ResponseType.SUCCESS) {
					SimpleRegionMarket.getInstance().getLogger().warning(MessageFormat.format(LanguageSupport.instance.getString("vault.economy.problem"), er.type.toString() + ": " + er.errorMessage));
				} else {
					return true;
				}
			} catch (NullPointerException t) {
				SimpleRegionMarket.getInstance().getLogger().warning(MessageFormat.format(LanguageSupport.instance.getString("vault.economy.problem"), t.toString()));
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String format(double money) {
		if (isEnabled()) {
			try {
				return economy.format(money);
			} catch (NullPointerException t) {
				SimpleRegionMarket.getInstance().getLogger().warning(MessageFormat.format(LanguageSupport.instance.getString("vault.economy.problem"), t.toString()));
			}
		}
		return super.format(money);
	}
}
