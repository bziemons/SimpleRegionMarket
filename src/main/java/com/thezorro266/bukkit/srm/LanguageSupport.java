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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import lombok.Getter;

public final class LanguageSupport {
	public static final LanguageSupport instance = new LanguageSupport();
	private static final String STANDARD_LANGUAGE = "en"; //NON-NLS

	@Getter
	private Locale locale;
	private ResourceBundle languageResource;

	private LanguageSupport() {
		// Load English as default
		load(STANDARD_LANGUAGE);
	}

	public void load(String language) {
		Locale newLocale = null;
		for (Locale tempLocale : Locale.getAvailableLocales()) {
			if (tempLocale.getLanguage().equalsIgnoreCase(language)) {
				newLocale = tempLocale;
				break;
			}
		}

		if (newLocale != null) {
			try {
				languageResource = ResourceBundle.getBundle("lang.srm_" + language, newLocale); //NON-NLS
				locale = newLocale;
			} catch (MissingResourceException e) {
				throw new IllegalArgumentException("Could not find file", e);
			}
		} else {
			throw new IllegalArgumentException("Could not find locale");
		}
	}

	public String getString(String s) {
		try {
			return languageResource.getString(s);
		} catch (MissingResourceException e) {
			SimpleRegionMarket
					.getInstance()
					.getLogger()
					.warning(
							String.format("language string %s doesnt exist in %s language file", s, //NON-NLS
									locale.getLanguage()));
			SimpleRegionMarket.getInstance().printError(e);
			return s;
		}
	}
}
