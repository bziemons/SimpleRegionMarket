
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
