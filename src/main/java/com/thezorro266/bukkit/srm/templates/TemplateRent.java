/*
 * SimpleRegionMarket
 * Copyright (C) 2013  theZorro266 <http://www.thezorro266.com>
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

package com.thezorro266.bukkit.srm.templates;

import org.bukkit.configuration.ConfigurationSection;

public class TemplateRent extends TemplateLet {

	public TemplateRent(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		// TODO Auto-generated constructor stub
	}

	/*
	@Override
	public void ownerClicksTakenSign(String world, String region) {
		final long newExpiredate = Utils.getEntryLong(this, world, region, "expiredate") + Utils.getEntryLong(this, world, region, "renttime");
		final Player owner = Bukkit.getPlayer(Utils.getEntryString(this, world, region, "owner"));
		if (Utils.getOptionLong(this, "renttime.max") == -1 || (newExpiredate - System.currentTimeMillis()) < Utils.getOptionLong(this, "renttime.max")) {
			if (AlphaRegionMarket.instance.econManager.isEconomy()) {
				String account = Utils.getEntryString(this, world, region, "account");
				if (account.isEmpty()) {
					account = null;
				}
				final double price = Utils.getEntryDouble(this, world, region, "price");
				if (AlphaRegionMarket.instance.econManager.moneyTransaction(Utils.getEntryString(this, world, region, "owner"), account, price)) {
					Utils.setEntry(this, world, region, "expiredate", newExpiredate);
					tokenManager.updateSigns(this, world, region);
					langHandler.playerNormalOut(owner, "PLAYER.REGION.ADDED_RENTTIME", null);
				}
			} else {
				Utils.setEntry(this, world, region, "expiredate", newExpiredate);
				tokenManager.updateSigns(this, world, region);
				langHandler.playerNormalOut(owner, "PLAYER.REGION.ADDED_RENTTIME", null);
			}
		} else {
			langHandler.playerErrorOut(owner, "PLAYER.ERROR.RERENT_TOO_LONG", null);
		}
	}

	@Override
	public void schedule(String world, String region) {
		if (Utils.getEntryBoolean(this, world, region, "taken")) {
			if (Utils.getEntryLong(this, world, region, "expiredate") < System.currentTimeMillis()) {
				if (Utils.getEntry(this, world, region, "owner") != null) {
					final Player player = Bukkit.getPlayer(Utils.getEntryString(this, world, region, "owner"));
					if (player != null) {
						final ArrayList<String> list = new ArrayList<String>();
						list.add(region);
						langHandler.playerNormalOut(player, "PLAYER.REGION.EXPIRED", list);
					}
				}
				releaseRegion(world, region);
			}
		}
	}
	*/
}
