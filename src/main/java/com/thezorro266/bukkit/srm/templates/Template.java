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

import java.lang.reflect.InvocationTargetException;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.thezorro266.bukkit.srm.SimpleRegionMarket;

public abstract class Template {

	public static final String ID_NONE = "none";
	public static final String TYPE_UNKNOWN = "Unknown";
	public static final String STATE_UNKNOWN = "unknown";

	@Getter
	protected String id = ID_NONE;
	@Getter
	protected String type = TYPE_UNKNOWN;
	@Getter
	protected String state = STATE_UNKNOWN;

	public Template(ConfigurationSection templateConfigSection) {
		id = templateConfigSection.getName();
		type = TYPE_UNKNOWN;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Template) {
			if (this.getId().equals(((Template) other).getId())) {
				return true;
			}
		}
		return false;
	}

	abstract public boolean signCreated(Player player, Location location, String[] lines);

	abstract public boolean signDestroyed(Player player, Sign sign);

	abstract public void signClicked(Player player, Sign sign);

	public static Template load(ConfigurationSection templateConfigSection) throws TemplateFormatException {
		String id = templateConfigSection.getName();
		if (templateConfigSection.isSet("type")) {
			if (id.equals(ID_NONE)) {
				throw new TemplateFormatException("Template using reserved id " + id);
			}

			String type = templateConfigSection.getString("type");
			if (!type.contains(".")) {
				type = "com.thezorro266.bukkit.srm.templates." + type;
			}
			Class<?> templateClass;
			try {
				templateClass = SimpleRegionMarket.class.getClassLoader().loadClass(type);
			} catch (ClassNotFoundException e) {
				throw new TemplateFormatException("Template class " + type + " on id " + id + " not found");
			}

			Template template;
			try {
				template = Template.class.cast(templateClass.getConstructor(new Class[] { ConfigurationSection.class }).newInstance(templateConfigSection));
			} catch (IllegalArgumentException e) {
				throw new TemplateFormatException("Template class " + type + " on id " + id + " is not valid", e);
			} catch (IllegalAccessException e) {
				throw new TemplateFormatException("Template class " + type + " on id " + id + " is not valid", e);
			} catch (SecurityException e) {
				throw new TemplateFormatException(e);
			} catch (InstantiationException e) {
				throw new TemplateFormatException("Template class " + type + " on id " + id + " is not valid", e);
			} catch (InvocationTargetException e) {
				throw new TemplateFormatException("Template class " + type + " on id " + id + " is not valid", e);
			} catch (NoSuchMethodException e) {
				throw new TemplateFormatException("Template class " + type + " on id " + id + " is not valid", e);
			} catch (ClassCastException e) {
				throw new TemplateFormatException("Template class " + type + " on id " + id + " is not valid", e);
			}

			return template;
		} else {
			throw new TemplateFormatException("No type defined on template " + id);
		}
	}
}
