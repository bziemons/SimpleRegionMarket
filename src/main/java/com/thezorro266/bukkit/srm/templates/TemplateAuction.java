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

package com.thezorro266.bukkit.srm.templates;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class TemplateAuction extends TemplateSell implements TimedTemplate {
	public TemplateAuction(ConfigurationSection templateConfigSection) {
		super(templateConfigSection);
		type = "bid";
	}

	@Override
	public void schedule() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean cancel(RegionFactory.Region region, Player player) {
		return false;
	}

	/*
	@Override
	public void otherClicksSign(Player player, String world, String region) {
	    String pr = playersBid.get(player.getName());
	    if (playersBid.containsKey(player.getName()) && pr != region) {
	        LangHandler.NormalOut(player, "PLAYER.LIMITS.ONLY_ONE_BID", null);
	        return;
	    } else {
	        if (bidMode.equalsIgnoreCase("real")) {
	            if (SimpleRegionMarket.permManager.canPlayerUseSign(player, "bid")) {
	                Double priceMax = Utils.getOptionDouble(this, "price.max");
	                if (priceMax == -1 || priceMax > Utils.getEntryInteger(this, world, region, "price")) {
	                    if (SimpleRegionMarket.econManager.isEconomy()) {
	                        Double price = Utils.getEntryDouble(this, world, region, "startingprice") + Utils.getEntryDouble(this, world, region, "user." + player.getName());
	                        String account = Utils.getEntryString(this, world, region, "account");
	                        if (SimpleRegionMarket.econManager.econHasEnough(player.getName(), price)) {
	                            if (SimpleRegionMarket.econManager.moneyTransaction(player.getName(), account, price)) {
	                                Utils.setEntry(this, world, region, "user." + player.getName(), price);
	                                if (Utils.getEntryDouble(this, world, region, "price") <= price) {
	                                    Utils.setEntry(this, world, region, "owner", player.getName());
	                                    Utils.setEntry(this, world, region, "price", price);
	                                }
	                                //save bid region to hashmap
	                                playersBid.put(player.getName(), region);

	                                SimpleRegionMarket.statisticManager.onMoneysUse(this.id, world, price, account, player.getName());

	                                final ArrayList<String> list = new ArrayList<String>();
	                                list.add(String.valueOf(Utils.getEntryDouble(this, world, region, "startingprice")));
	                                list.add(String.valueOf(price));
	                                LangHandler.NormalOut(player, "PLAYER.REGION.BID_ADD", list);
	                            }
	                        }
	                    } else {
	                        Double price = Utils.getEntryDouble(this, world, region, "startingprice") + Utils.getEntryDouble(this, world, region, "user." + player.getName());
	                        Utils.setEntry(this, world, region, "user." + player.getName(), price);
	                        if (Utils.getEntryDouble(this, world, region, "price") < price) {
	                            Utils.setEntry(this, world, region, "owner", player.getName());
	                            Utils.setEntry(this, world, region, "price", price);
	                        }
	                    }
	                    tokenManager.updateSigns(this, world, region);
	                } else {
	                    final ArrayList<String> list = new ArrayList<String>();
	                    list.add(String.valueOf(Utils.getOptionDouble(this, "price.min")));
	                    list.add(String.valueOf(Utils.getOptionDouble(this, "price.max")));
	                    LangHandler.NormalOut(player, "PLAYER.ERROR.PRICE_LIMIT", list);
	                }
	            }
	        }
	    }
	}

	@Override
	public Map<String, String> getReplacementMap(String world, String region) {
	    final HashMap<String, String> replacementMap = (HashMap<String, String>) super.getReplacementMap(world, region);
	    if (replacementMap != null) {
	        replacementMap.put("highestpay", Utils.getEntryString(this, world, region, "price"));
	        replacementMap.put("account", Utils.getEntryString(this, world, region, "account"));
	        if (Utils.getEntry(this, world, region, "owner") == null || Utils.getEntryString(this, world, region, "owner").isEmpty()) {
	            replacementMap.put("player", "No owner");
	        } else {
	            replacementMap.put("player", Utils.getEntryString(this, world, region, "owner"));
	        }
	        if (Utils.getEntry(this, world, region, "expiredate") != null) {
	            replacementMap.put("timeleft", Utils.getSignTime(Utils.getEntryLong(this, world, region, "expiredate") - System.currentTimeMillis()));
	        }
	    }
	    return replacementMap;
	}

	@Override
	public void schedule(String world, String region) {
	    if (!Utils.getEntryBoolean(this, world, region, "taken")) {
	        if (Utils.getEntryLong(this, world, region, "expiredate") < System.currentTimeMillis()) {
	            if (Utils.getEntry(this, world, region, "owner") != null) {
	                final Player player = Bukkit.getPlayer(Utils.getEntryString(this, world, region, "owner"));
	                if (player != null) {
	                    takeRegion(player, world, region);
	                    SimpleRegionMarket.statisticManager.onSignClick(this.id, world, Utils.getEntryString(this, world, region, "account"), player.getName());
	                }

	                final ArrayList<String> list = new ArrayList<String>();
	                list.add(region);
	                list.add(Utils.getEntryString(this, world, region, "owner"));
	                messageToAllBid(region, "PLAYER.REGION.BID_END", list);
	                Utils.removeEntry(this, world, region, "user");
	            } else {
	                messageToAllBid(region, "PLAYER.REGION.BID_END_NO_WINER", null);
	            }

	            if (SimpleRegionMarket.configurationHandler.getString("Auto_Removing_Regions").contains(id)) {
	                Utils.setEntry(this, world, region, "hidden", true);
	            }
	        }
	    }
	}

	private void messageToAllBid(String region, String message, ArrayList<String> args) {
	    for (String playerName : playersBid.keySet().toArray(new String[playersBid.keySet().size()])) {
	        if (playersBid.get(playerName) == region) {
	            Player player = Bukkit.getPlayer(playerName);
	            if (player != null) {
	                LangHandler.NormalOut(player, message, args);
	            }
	            playersBid.remove(playerName);
	        }
	    }
	}

	public boolean signCreated(Player player, String world, ProtectedRegion protectedRegion, Location signLocation, HashMap<String, String> input,
	        String[] lines) {
	    final String region = protectedRegion.getId();

	    if (!entries.containsKey(world) || !entries.get(world).containsKey(region)) {
	        final double priceMin = Utils.getOptionDouble(this, "price.min");
	        final double priceMax = Utils.getOptionDouble(this, "price.max");
	        double price;
	        if (SimpleRegionMarket.econManager.isEconomy()) {
	            if (input.get("price") != null) {
	                try {
	                    price = Double.parseDouble(input.get("price"));
	                } catch (final Exception e) {
	                    LangHandler.ErrorOut(player, "PLAYER.ERROR.NO_PRICE", null);
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
	            LangHandler.ErrorOut(player, "PLAYER.ERROR.PRICE_LIMIT", list);
	            return false;
	        }

	        long time = Utils.parseSignTime(input.get("time"));
	        final long timeMin = Utils.getOptionLong(this, "bidtime.min");
	        final long timeMax = Utils.getOptionLong(this, "bidtime.max");

	        if (timeMin > time && (timeMax == -1 || time < timeMax)) {
	            final ArrayList<String> list = new ArrayList<String>();
	            list.add(String.valueOf(timeMin));
	            list.add(String.valueOf(timeMax));
	            LangHandler.ErrorOut(player, "PLAYER.ERROR.RENTTIME_LIMIT", null);
	            return false;
	        }

	        time = time + System.currentTimeMillis();

	        String account = "";
	        if (input.get("account") != null && !input.get("account").isEmpty()) {
	            account = input.get("account");
	            if (SimpleRegionMarket.permManager.hadAdminPermissions(player)) {
	                if (input.get("account").equalsIgnoreCase("none")) {
	                    account = "";
	                }
	            }
	        } else {
	            if (SimpleRegionMarket.configurationHandler.getBoolean("Player_Line_Empty")) {
	                account = player.getName();
	            } else {
	                account = SimpleRegionMarket.configurationHandler.getString("Default_Economy_Account");
	            }
	        }

	        Utils.setEntry(this, world, region, "startingprice", price);
	        Utils.setEntry(this, world, region, "expiredate", time);
	        Utils.setEntry(this, world, region, "price", 0);
	        Utils.setEntry(this, world, region, "account", account);
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
	*/
}