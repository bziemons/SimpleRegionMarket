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

package com.thezorro266.bukkit.srm.templates.interfaces;

import org.bukkit.OfflinePlayer;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;

/**
 * An interface that gives a template the general methods to manage owners and members and being able to set a region occupied.
 * 
 * @author theZorro266
 */
public interface OwnableTemplate {
	/**
	 * Checks if the given player is an owner of the specific region.
	 * 
	 * @param player as the {@link OfflinePlayer}
	 * @param region as the {@link Region} to be checked
	 * @return true, if the player is an owner of the region
	 */
	public boolean isRegionOwner(OfflinePlayer player, Region region);

	/**
	 * Checks if the given player is a member of the specific region.
	 * 
	 * @param player as the {@link OfflinePlayer}
	 * @param region as the {@link Region} to be checked
	 * @return true, if the player is a member of the region
	 */
	public boolean isRegionMember(OfflinePlayer player, Region region);

	/**
	 * Returns all owners of the given region. The owners will be put into an {@link OfflinePlayer} array.
	 * 
	 * @param region as the {@link Region} of which the owners should be returned
	 * @return the owners in an {@link OfflinePlayer} array
	 */
	public OfflinePlayer[] getRegionOwners(Region region);

	/**
	 * Returns all members of the given region. The members will be put into an {@link OfflinePlayer} array.
	 * 
	 * @param region as the {@link Region} of which the members should be returned
	 * @return the members in an {@link OfflinePlayer} array
	 */
	public OfflinePlayer[] getRegionMembers(Region region);

	/**
	 * Sets the owners of a region. The owners are in an {@link OfflinePlayer} array.
	 * 
	 * @param region as the {@link Region} of which the owners should be set
	 * @param owners in an {@link OfflinePlayer} array as the new owners of that region
	 * @return true, if successful
	 */
	public boolean setRegionOwners(Region region, OfflinePlayer[] owners);

	/**
	 * Sets the members of a region. The members are in an {@link OfflinePlayer} array.
	 * 
	 * @param region as the {@link Region} of which the members should be set
	 * @param members in an {@link OfflinePlayer} array as the new members of that region
	 * @return true, if successful
	 */
	public boolean setRegionMembers(Region region, OfflinePlayer[] members);

	/**
	 * Adds a player to the list of owners of a region.
	 * 
	 * @param region as the {@link Region} to which an owner should be added
	 * @param player as the {@link OfflinePlayer} to be added as an owner to the {@link Region}
	 * @return true, if successful
	 */
	public boolean addRegionOwner(Region region, OfflinePlayer player);

	/**
	 * Adds a player to the list of members of a region.
	 * 
	 * @param region as the {@link Region} to which a member should be added
	 * @param player as the {@link OfflinePlayer} to be added as a member to the {@link Region}
	 * @return true, if successful
	 */
	public boolean addRegionMember(Region region, OfflinePlayer player);

	/**
	 * Removes an owner from the region.
	 * 
	 * @param region as the {@link Region} of which an owner should be removed
	 * @param player as the {@link OfflinePlayer}, who should be removed from the list of owners
	 * @return true, if successful
	 */
	public boolean removeRegionOwner(Region region, OfflinePlayer player);

	/**
	 * Removes a member from the region.
	 * 
	 * @param region as the {@link Region} of which a member should be removed
	 * @param player as the {@link OfflinePlayer}, who should be removed from the list of members
	 * @return true, if successful
	 */
	public boolean removeRegionMember(Region region, OfflinePlayer player);

	/**
	 * Checks from the template if a region is occupied.
	 * 
	 * @param region as the {@link Region} to be checked
	 * @return true, if the given region is occupied by someone
	 */
	public boolean isRegionOccupied(Region region);

	/**
	 * Sets a region to be occupied or not.
	 * 
	 * @param region as the {@link Region}, which state should be changed
	 * @param isOccupied is true, if the region should be set as occupied
	 * @return true, if successful
	 */
	public boolean setRegionOccupied(Region region, boolean isOccupied);

	/**
	 * Clears a region. This should remove all owners and members and should set the {@link Region} to a non-occupied state again.
	 * 
	 * @param region as the {@link Region}, which should be cleared
	 * @return true, if successful
	 */
	public boolean clearRegion(Region region);

	/**
	 * Clears only the owners and members from the region.
	 * 
	 * @param region as the {@link Region}, which should be cleared
	 * @return true, if successful
	 */
	public boolean clearOwnershipOfRegion(Region region);

	/**
	 * Gets the main owner, who is registered with SRM.
	 *
	 * @param region as the {@link Region} from which the owner should be get
	 * @return the name of the owner
	 */
	public String getMainOwner(Region region);
}
