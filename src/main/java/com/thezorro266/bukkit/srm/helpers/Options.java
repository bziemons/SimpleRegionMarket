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

package com.thezorro266.bukkit.srm.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Options implements Iterable<Map.Entry<String, Object>> {
	private HashMap<String, Object> optionMap;

	public Options() {
		optionMap = new HashMap<String, Object>(4);
	}

	public synchronized boolean exists(String key) {
		return optionMap.get(key) != null;
	}

	public synchronized Object get(String key) {
		return optionMap.get(key);
	}

	public synchronized void set(String key, Object value) {
		if (value == null) {
			optionMap.remove(key);
		} else {
			optionMap.put(key, value);
		}
	}

	/**
	 * Not thread safe.
	 * 
	 * @return a set of entries with the key as the option key and the value as the object
	 */
	public synchronized Set<Map.Entry<String, Object>> entrySet() {
		return optionMap.entrySet();
	}

	/**
	 * Not thread safe.
	 * 
	 * @return a iterator to the options entry set
	 */
	@Override
	public Iterator<Map.Entry<String, Object>> iterator() {
		return entrySet().iterator();
	}

	@Override
	public String toString() {
		return optionMap.toString();
	}
}
