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
