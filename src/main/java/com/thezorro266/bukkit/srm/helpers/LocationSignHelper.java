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

package com.thezorro266.bukkit.srm.helpers;

import java.util.ArrayList;
import com.thezorro266.bukkit.srm.factories.SignFactory.Sign;

public class LocationSignHelper {
	private final ArrayList<Sign> signList = new ArrayList<Sign>();
	private final ArrayList<Location> locationList = new ArrayList<Location>();

	public Sign getSign(Location location) {
		int index = locationList.indexOf(location);

		if (index >= 0) {
			return signList.get(index);
		} else {
			return null;
		}
	}

	public void addSignAndLocation(Sign sign) {
		signList.add(sign);
		locationList.add(sign.getLocation());
	}

	public void removeSignAndLocation(Sign sign) {
		int index = signList.indexOf(sign);

		if (index >= 0) {
			signList.remove(index);
			locationList.remove(index);
		}
	}
}
