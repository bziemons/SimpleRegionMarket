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

package com.thezorro266.bukkit.srm.exceptions;

import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;

public class ContentSaveException extends Exception {

	private static final long serialVersionUID = 2941771316730435551L;

	public ContentSaveException(String message) {
		super(message);
	}

	public ContentSaveException(Region region) {
		super(errorMessageFromRegion(region));
	}

	public ContentSaveException(Region region, Throwable cause) {
		super(errorMessageFromRegion(region), cause);
	}

	private static String errorMessageFromRegion(Region region) {
		return String.format("Could not save %s", region);
	}
}
