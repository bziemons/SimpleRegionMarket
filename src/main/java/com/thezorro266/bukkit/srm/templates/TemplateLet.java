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

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class TemplateLet extends IntelligentSignTemplate implements TimedTemplate {

	public TemplateLet(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean signCreated(Player player, Location location, String[] lines) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean signDestroyed(Player player, Sign sign) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void signClicked(Player player, Sign sign) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedule() {
		// TODO Auto-generated method stub

	}

	/*
	@Override
	public void takeRegion(Player newOwner, String world, String region) {
		final ProtectedRegion protectedRegion = AlphaRegionMarket.instance.wgManager.getProtectedRegion(Bukkit.getWorld(world), region);

		if (Utils.getEntryBoolean(this, world, region, "taken")) {
			final Player oldOwner = Bukkit.getPlayer(Utils.getEntryString(this, world, region, "owner"));
			final ArrayList<String> list = new ArrayList<String>();
			list.add(region);
			list.add(newOwner.getName());
			langHandler.playerNormalOut(oldOwner, "PLAYER.REGION.JUST_TAKEN_BY", list);
			releaseRegion(world, region);
		} else {
			// Clear Members and Owners
			protectedRegion.setMembers(new DefaultDomain());
			protectedRegion.setOwners(new DefaultDomain());
		}

		protectedRegion.getMembers().addPlayer(AlphaRegionMarket.instance.wgManager.wrapPlayer(newOwner));

		Utils.setEntry(this, world, region, "taken", true);
		Utils.setEntry(this, world, region, "owner", newOwner.getName());
		Utils.setEntry(this, world, region, "expiredate", System.currentTimeMillis() + Utils.getEntryLong(this, world, region, "renttime"));

		final ArrayList<String> list = new ArrayList<String>();
		list.add(region);
		langHandler.playerNormalOut(newOwner, "PLAYER.REGION.RENT", list);

		tokenManager.updateSigns(this, world, region);
	}

	@Override
	public void releaseRegion(String world, String region) {
		final ProtectedRegion protectedRegion = AlphaRegionMarket.instance.wgManager.getProtectedRegion(Bukkit.getWorld(world), region);

		// Clear Members and Owners
		protectedRegion.setMembers(new DefaultDomain());
		protectedRegion.setOwners(new DefaultDomain());

		Utils.setEntry(this, world, region, "taken", false);
		Utils.removeEntry(this, world, region, "owner");
		Utils.removeEntry(this, world, region, "expiredate");

		tokenManager.updateSigns(this, world, region);
	}

	@Override
	public boolean signCreated(Player player, String world, ProtectedRegion protectedRegion, Location signLocation, HashMap<String, String> input,
			String[] lines) {
		final String region = protectedRegion.getId();

		if (!entries.containsKey(world) || !entries.get(world).containsKey(region)) {
			final double priceMin = Utils.getOptionDouble(this, "price.min");
			final double priceMax = Utils.getOptionDouble(this, "price.max");
			double price;
			if (AlphaRegionMarket.instance.econManager.isEconomy()) {
				if (input.get("price") != null) {
					try {
						price = Double.parseDouble(input.get("price"));
					} catch (final Exception e) {
						langHandler.playerErrorOut(player, "PLAYER.ERROR.NO_PRICE", null);
						return false;
					}
				} else {
					price = priceMin;
				}
			} else {
				price = 0;
			}

			if (priceMin > price && (priceMax == -1 || price < priceMax)) {
				final ArrayList<String> list = new ArrayList<String>();
				list.add(String.valueOf(priceMin));
				list.add(String.valueOf(priceMax));
				langHandler.playerErrorOut(player, "PLAYER.ERROR.PRICE_LIMIT", list);
				return false;
			}

			final long renttimeMin = Utils.getOptionLong(this, "renttime.min");
			final long renttimeMax = Utils.getOptionLong(this, "renttime.max");
			long renttime;
			if (!input.get("time").isEmpty()) {
				try {
					renttime = Utils.parseSignTime(input.get("time"));
				} catch (final Exception e) {
					langHandler.playerErrorOut(player, "PLAYER.ERROR.NO_RENTTIME", null);
					return false;
				}
			} else {
				langHandler.playerErrorOut(player, "PLAYER.ERROR.NO_RENTTIME", null);
				return false;
			}

			if (renttimeMin > renttime && (renttimeMax == -1 || renttime < renttimeMax)) {
				final ArrayList<String> list = new ArrayList<String>();
				list.add(String.valueOf(renttimeMin));
				list.add(String.valueOf(renttimeMax));
				langHandler.playerErrorOut(player, "PLAYER.ERROR.RENTTIME_LIMIT", null);
				return false;
			}

			Utils.setEntry(this, world, region, "price", price);
			Utils.setEntry(this, world, region, "renttime", renttime);
			Utils.setEntry(this, world, region, "account", player.getName());
			Utils.setEntry(this, world, region, "taken", false);
			Utils.removeEntry(this, world, region, "owner");
		}

		final ArrayList<Location> signLocations = Utils.getSignLocations(this, world, region);
		signLocations.add(signLocation);
		if (signLocations.size() == 1) {
			Utils.setEntry(this, world, region, "signs", signLocations);
		}

		tokenManager.updateSigns(this, world, region);
		return true;
	}

	@Override
	public Map<String, String> getReplacementMap(String world, String region) {
		final HashMap<String, String> replacementMap = (HashMap<String, String>) super.getReplacementMap(world, region);
		if (replacementMap != null) {
			replacementMap.put("time", Utils.getSignTime(Utils.getEntryLong(this, world, region, "renttime")));
			if (Utils.getEntry(this, world, region, "expiredate") != null) {
				replacementMap.put("timeleft", Utils.getSignTime(Utils.getEntryLong(this, world, region, "expiredate") - System.currentTimeMillis()));
			}
		}
		return replacementMap;
	}

	@Override
	public boolean canAddOwner() {
		return false;
	}

	@Override
	public void schedule(String world, String region) {
		if (Utils.getEntryBoolean(this, world, region, "taken")) {
			if (Utils.getEntryLong(this, world, region, "expiredate") < System.currentTimeMillis()) {
				if (Utils.getEntry(this, world, region, "owner") != null) {
					final String owner = Utils.getEntryString(this, world, region, "owner");
					final String account = Utils.getEntryString(this, world, region, "account");
					final Double price = Utils.getEntryDouble(this, world, region, "price");
					final Player player = Bukkit.getPlayer(owner);
					if (AlphaRegionMarket.instance.econManager.econHasEnough(owner, price)) {
						if (AlphaRegionMarket.instance.econManager.moneyTransaction(owner, account, price)) {
							if (player != null) {
								final ArrayList<String> list = new ArrayList<String>();
								list.add(region);
								langHandler.playerNormalOut(player, "PLAYER.REGION.AUTO_EXPANDED", list);
							}
							return;
						}
					}
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
