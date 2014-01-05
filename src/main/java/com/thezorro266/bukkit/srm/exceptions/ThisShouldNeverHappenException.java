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

public class ThisShouldNeverHappenException extends RuntimeException {

	private static final long serialVersionUID = -5134174393398186655L;

	public ThisShouldNeverHappenException() {
		super();
	}

	public ThisShouldNeverHappenException(String message) {
		super(message);
	}

	public ThisShouldNeverHappenException(String message, Throwable error) {
		super(message, error);
	}
}
