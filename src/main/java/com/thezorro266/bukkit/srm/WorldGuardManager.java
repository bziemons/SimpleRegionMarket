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

package com.thezorro266.bukkit.srm;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.factories.RegionFactory;

public class WorldGuardManager {
	private static final String WORLD_GUARD_PLUGIN_NAME = "WorldGuard"; // NON-NLS
	private WorldGuardPlugin worldguardPlugin;
	private WeakHashMap<RegionFactory.Region, WeakReference<WorldGuardOwnable>> ownableMap = new WeakHashMap<RegionFactory.Region, WeakReference<WorldGuardOwnable>>();

	public WorldGuardOwnable getOwnable(RegionFactory.Region region) {
		WorldGuardOwnable wgo;
		if (ownableMap.containsKey(region)) {
			WeakReference<WorldGuardOwnable> wr = ownableMap.get(region);
			wgo = wr.get();
			if (wgo != null) {
				return wgo;
			}
		}

		wgo = new WorldGuardOwnable(region);
		ownableMap.put(region, new WeakReference<WorldGuardOwnable>(wgo));
		return wgo;
	}

	public void load() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(WORLD_GUARD_PLUGIN_NAME);

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			throw new UnknownDependencyException(LanguageSupport.instance.getString("worldguard.notloaded"));
		} else {
			worldguardPlugin = (WorldGuardPlugin) plugin;
		}
	}

	public WorldGuardPlugin getWorldGuard() {
		return worldguardPlugin;
	}

	public ProtectedRegion getProtectedRegion(World world, String region) {
		if (world != null) {
			return worldguardPlugin.getRegionManager(world).getRegion(region);
		}
		return null;
	}

	public LocalPlayer wrapPlayer(OfflinePlayer player) {
		if (player != null) {
			return new WorldGuardPlayer(player);
		}
		return null;
	}

	public LocalPlayer wrapPlayer(Player player) {
		if (player != null) {
			return worldguardPlugin.wrapPlayer(player);
		}
		return null;
	}

	public class WorldGuardOwnable {
		private World world;
		private DefaultDomain owners;
		private DefaultDomain members;

		private WorldGuardOwnable(RegionFactory.Region region) {
			this.world = region.getWorld();
			this.owners = region.getWorldguardRegion().getOwners();
			this.members = region.getWorldguardRegion().getMembers();
		}

		public void addMember(LocalPlayer localPlayer) {
			members.addPlayer(localPlayer);
		}

		public void addMemberGroup(String group) {
			members.addGroup(group);
		}

		public void addOwner(LocalPlayer localPlayer) {
			owners.addPlayer(localPlayer);
		}

		public void addOwnerGroup(String group) {
			owners.addGroup(group);
		}

		public void removeMember(LocalPlayer localPlayer) {
			members.removePlayer(localPlayer);
		}

		public void removeMemberGroup(String group) {
			members.removeGroup(group);
		}

		public void removeOwner(LocalPlayer localPlayer) {
			owners.removePlayer(localPlayer);
		}

		public void removeOwnerGroup(String group) {
			owners.removeGroup(group);
		}

		public void removeAllMembers() {
			members.removeAll();
		}

		public void removeAllOwners() {
			owners.removeAll();
		}

		public boolean isPlayerMember(LocalPlayer localPlayer) {
			return members.contains(localPlayer);
		}

		public boolean isPlayerOwner(LocalPlayer localPlayer) {
			return owners.contains(localPlayer);
		}

		public OfflinePlayer[] getMembers() {
			OfflinePlayer[] list;
			Set<String> playerSet = members.getPlayers();
			list = new OfflinePlayer[playerSet.size()];
			int index = 0;
			for (String playerName : playerSet) {
				list[index] = Bukkit.getOfflinePlayer(playerName);
				++index;
			}
			return list;
		}

		public OfflinePlayer[] getOwners() {
			OfflinePlayer[] list;
			Set<String> playerSet = owners.getPlayers();
			list = new OfflinePlayer[playerSet.size()];
			int index = 0;
			for (String playerName : playerSet) {
				list[index] = Bukkit.getOfflinePlayer(playerName);
				++index;
			}
			return list;
		}

		public void saveChanges() throws ProtectionDatabaseException {
			worldguardPlugin.getRegionManager(world).save();
		}
	}

	public class WorldGuardPlayer extends LocalPlayer {
		private OfflinePlayer player;

		private WorldGuardPlayer(OfflinePlayer player) {
			if (player == null) {
				throw new IllegalArgumentException("Player must not be null");
			}

			this.player = player;
		}

		@Override
		public String getName() {
			return player.getName();
		}

		@Override
		public boolean hasGroup(String group) {
			return PermissionsResolverManager.getInstance().inGroup(player, group);
		}

		@Override
		public Vector getPosition() {
			if (player.isOnline()) {
				org.bukkit.Location loc = player.getPlayer().getLocation();
				return new Vector(loc.getX(), loc.getY(), loc.getZ());
			} else {
				SimpleRegionMarket.getInstance().getLogger()
						.warning(LanguageSupport.instance.getString("worldguard.offlineplayer.getposition"));
				return new Vector(0, 0, 0);
			}
		}

		@Override
		public void kick(String msg) {
			if (player.isOnline()) {
				player.getPlayer().kickPlayer(msg);
			} else {
				SimpleRegionMarket.getInstance().getLogger()
						.warning(LanguageSupport.instance.getString("worldguard.offlineplayer.kick"));
			}
		}

		@Override
		public void ban(String msg) {
			if (player.isOnline()) {
				player.getPlayer().setBanned(true);
				player.getPlayer().kickPlayer(msg);
			} else {
				SimpleRegionMarket.getInstance().getLogger()
						.warning(LanguageSupport.instance.getString("worldguard.offlineplayer.ban"));
			}
		}

		@Override
		public void printRaw(String msg) {
			if (player.isOnline()) {
				player.getPlayer().sendMessage(msg);
			} else {
				SimpleRegionMarket.getInstance().getLogger()
						.warning(LanguageSupport.instance.getString("worldguard.offlineplayer.message"));
			}
		}

		@Override
		public String[] getGroups() {
			return PermissionsResolverManager.getInstance().getGroups(player);
		}

		@Override
		public boolean hasPermission(String perm) {
			if (player.isOnline()) {
				return SimpleRegionMarket.getInstance().getWorldGuardManager().getWorldGuard()
						.hasPermission(player.getPlayer(), perm);
			} else {
				SimpleRegionMarket.getInstance().getLogger()
						.warning(LanguageSupport.instance.getString("worldguard.offlineplayer.getpermission"));
				return true;
			}
		}

	}
}
