/**
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

import lombok.Data;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.templates.Template;

public @Data
class Sign {
	public static final int SIGN_LINE_COUNT = 4;

	final Region region;
	final Location location;
	final boolean wallSign;
	final int direction;

	public void clear() {
		setContent(new String[] { "", "", "", "" });
	}

	public void setContent(String[] lines) {
		Block signBlock = location.getBlock();
		if (!isSign(signBlock)) {
			signBlock.setType(wallSign ? Material.WALL_SIGN : Material.SIGN_POST);
		}
		org.bukkit.block.Sign signBlockState = (org.bukkit.block.Sign) signBlock.getState();
		for (int i = 0; i < SIGN_LINE_COUNT; i++) {
			signBlockState.setLine(i, lines[i]);
		}
	}

	public boolean isBlockThisSign(Block block) {
		if (isSign(block)) {
			if (location.isBlockAt(block)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSign(Block block) {
		if (block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST)) {
			return true;
		}
		return false;
	}

	public static Sign getSignFromBlock(Block block) {
		if (isSign(block)) {
			for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
				for (Region region : template.getRegionList()) {
					for (Sign sign : region.getSignList()) {
						if (sign.isBlockThisSign(block)) {
							return sign;
						}
					}
				}
			}
		}
		return null;
	}
}
