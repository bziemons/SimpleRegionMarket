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

import com.thezorro266.bukkit.srm.SimpleRegionMarket;

public abstract class Economy {
	protected boolean enabled = false;

	public boolean isEnabled() {
		return enabled;
	}

	public abstract boolean isValidAccount(String account);
	public abstract boolean hasEnough(String account, double money);
	public abstract boolean subtractMoney(String account, double money);
	public abstract boolean addMoney(String account, double money);

	public String format(double money) {
		if (isEnabled()) {
			return String.format("%.2f", money); //NON-NLS
		}
		return "";
	}
}
