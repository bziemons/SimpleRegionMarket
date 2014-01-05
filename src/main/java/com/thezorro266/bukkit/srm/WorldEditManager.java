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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.thezorro266.bukkit.srm.exceptions.ThisShouldNeverHappenException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;

public class WorldEditManager {
	private static final String WORLD_EDIT_PLUGIN_NAME = "WorldEdit"; //NON-NLS
	private static final String REGIONS_SCHEMATIC_FORMAT_STRING = "%s.schematic"; //NON-NLS
	@Getter
	private WorldEdit worldedit;
	private WorldEditPlugin worldEditPlugin;

	public void load() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(WORLD_EDIT_PLUGIN_NAME);

		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			throw new UnknownDependencyException(LanguageSupport.instance.getString("worldedit.notloaded"));
		} else {
			worldEditPlugin = (WorldEditPlugin) plugin;
			worldedit = WorldEdit.getInstance();
		}
	}

	public File getSchematicFile(RegionFactory.Region region) {
		return new File(
				new File(
						new File(
								new File(
										SimpleRegionMarket.getInstance().getDataFolder(),
										TemplateManager.REGIONS_FOLDER
								),
								region.getTemplate().getId().toLowerCase()
						),
						region.getWorld().getName()
				),
				String.format(REGIONS_SCHEMATIC_FORMAT_STRING, region.getName()));
	}

	public void replaceRegionFromSchematic(RegionFactory.Region region) {
		if (region.getWorldguardRegion() instanceof ProtectedCuboidRegion) {
			File schematicFile = getSchematicFile(region);

			CuboidClipboard clipboard;
			try {
				clipboard = SchematicFormat.MCEDIT.load(schematicFile);
			} catch (IOException e) {
				SimpleRegionMarket
						.getInstance()
						.getLogger()
						.warning(
								MessageFormat.format(
										LanguageSupport.instance.getString("region.schematic.load.failure"),
										schematicFile.getPath()));
				SimpleRegionMarket.getInstance().printError(e);
				return;
			} catch (DataException e) {
				SimpleRegionMarket
						.getInstance()
						.getLogger()
						.warning(
								MessageFormat.format(
										LanguageSupport.instance.getString("region.schematic.load.failure"),
										schematicFile.getPath()));
				SimpleRegionMarket.getInstance().printError(e);
				return;
			}

			EditSession session = worldedit.getEditSessionFactory().getEditSession(
					BukkitUtil.getLocalWorld(region.getWorld()), -1);
			session.enableQueue();

			Vector pos = clipboard.getOrigin();
			try {
				clipboard.place(session, pos, false);
			} catch (MaxChangedBlocksException e) {
				throw new ThisShouldNeverHappenException("We can change infinite blocks", e);
			}
			clipboard.pasteEntities(pos);

			ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) region.getWorldguardRegion();

			BlockVector min = cuboid.getMinimumPoint();
			BlockVector max = cuboid.getMaximumPoint();

			// Get stuck players free
			for (Player player : region.getWorld().getPlayers()) {
				BukkitPlayer bp = worldEditPlugin.wrapPlayer(player);
				if (bp.getBlockIn().containedWithin(min.subtract(0, 1, 0), max)) {
					// TODO: Does not work, seems to be a WorldEdit problem
					bp.findFreePosition();
				}
			}

			session.flushQueue();
		}
	}

	public void saveRegionToSchematic(RegionFactory.Region region) throws IOException {
		if (region.getWorldguardRegion() instanceof ProtectedCuboidRegion) {
			File schematicFile = getSchematicFile(region);

			CuboidClipboard clipboard = getClipboardFromRegion(region);

			if (!schematicFile.exists()) {
				if (!schematicFile.getParentFile().exists()) {
					schematicFile.getParentFile().mkdirs();
				}
				schematicFile.createNewFile();
			}
			try {
				SchematicFormat.MCEDIT.save(clipboard, schematicFile);
			} catch (DataException e) {
				throw new ThisShouldNeverHappenException("There was a data exception from WorldEdit's internal format",
						e);
			}
		}
	}

	public CuboidClipboard getClipboardFromRegion(RegionFactory.Region region) {
		if (region.getWorldguardRegion() instanceof ProtectedCuboidRegion) {
			ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) region.getWorldguardRegion();

			BlockVector min = cuboid.getMinimumPoint();
			BlockVector max = cuboid.getMaximumPoint();

			CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(Vector.ONE), min);
			CuboidSelection selection = new CuboidSelection(region.getWorld(), min, max);

			for (int x = 0; x < selection.getWidth(); ++x) {
				for (int y = 0; y < selection.getHeight(); ++y) {
					for (int z = 0; z < selection.getLength(); ++z) {
						BlockVector vector = new BlockVector(x, y, z);
						Block block = region.getWorld().getBlockAt(selection.getMinimumPoint().getBlockX() + x,
								selection.getMinimumPoint().getBlockY() + y,
								selection.getMinimumPoint().getBlockZ() + z);
						BaseBlock baseBlock = new BaseBlock(block.getTypeId(), block.getData());

						clipboard.setBlock(vector, baseBlock);
					}
				}
			}
			return clipboard;
		}
		return null;
	}
}
