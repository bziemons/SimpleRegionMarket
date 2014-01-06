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

import static com.thezorro266.bukkit.srm.factories.SignFactory.Sign.SIGN_LINE_COUNT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.exceptions.TemplateFormatException;
import com.thezorro266.bukkit.srm.exceptions.ThisShouldNeverHappenException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.factories.RegionFactory.Region;
import com.thezorro266.bukkit.srm.templates.Template;
import com.thezorro266.bukkit.srm.templates.TemplateRent;
import com.thezorro266.bukkit.srm.templates.TemplateSell;

public class TemplateManager {
	private static final String AGENTS_FILENAME = "agents.yml"; //NON-NLS
	private static final String TEMPLATE_CONFIG_FILENAME = "templates.yml"; //NON-NLS
	private static final int TEMPLATE_VERSION = 1;
	public static final String REGIONS_FOLDER = "regions"; //NON-NLS
	public static final String REGIONS_YML_FORMAT_STRING = "%s.yml"; //NON-NLS

	@Getter
	private final List<Template> templateList = new ArrayList<Template>();

	public void load() throws TemplateFormatException, IOException {
		File templateFile = new File(SimpleRegionMarket.getInstance().getDataFolder(), TEMPLATE_CONFIG_FILENAME);

		if (templateFile.exists()) {
			if (templateFile.canRead()) {
				YamlConfiguration templateYaml = YamlConfiguration.loadConfiguration(templateFile);
				if (templateYaml.getInt("version", 0) != TEMPLATE_VERSION) {
					update(templateFile, templateYaml);
				} else {
					synchronized (templateList) {
						templateList.clear();
						for (String templateId : templateYaml.getRoot().getKeys(false)) {
							if (templateYaml.isSet(templateId + ".type")) {
								Template template = Template.load(templateYaml.getConfigurationSection(templateId));
								if (templateList.contains(template)) {
									throw new TemplateFormatException("Duplicate id in templates configuration file");
								}
								templateList.add(template);
							}
						}
					}
					templateYaml.save(templateFile);
				}
			} else {
				throw new IOException("Cannot read " + TEMPLATE_CONFIG_FILENAME);
			}
		} else {
			// Load very old agents file, if exist
			File agentsFile = new File(SimpleRegionMarket.getInstance().getDataFolder(), AGENTS_FILENAME);
			if (agentsFile.exists()) {
				if (agentsFile.canRead()) {
					YamlConfiguration agentsYaml = YamlConfiguration.loadConfiguration(agentsFile);
					updateAgents(agentsYaml);
				} else {
					throw new IOException("Cannot read " + AGENTS_FILENAME);
				}
			} else {
				loadDefault();
			}
		}
	}

	private void loadDefault() throws TemplateFormatException, IOException {
		SimpleRegionMarket.getInstance().saveResource(TEMPLATE_CONFIG_FILENAME, false);
		load();
	}

	private void updateAgents(YamlConfiguration agentsYaml) throws TemplateFormatException, IOException {
		loadDefault();

		TemplateSell tokenAgent = null;
		TemplateRent tokenHotel = null;
		synchronized (templateList) {
			for (Template template : templateList) {
				if (template.getId().equalsIgnoreCase("SELL")) {
					tokenAgent = (TemplateSell) template;
				} else if (template.getId().equalsIgnoreCase("HOTEL")) {
					tokenHotel = (TemplateRent) template;
				}
			}
		}

		if (tokenHotel == null || tokenAgent == null) {
			SimpleRegionMarket.getInstance().getLogger()
					.severe(LanguageSupport.instance.getString("config.import.old.failed"));
			throw new RuntimeException("There were no templates with the IDs SELL and HOTEL found");
		}

		ConfigurationSection path;
		for (final String world : agentsYaml.getKeys(false)) {
			World realWorld = Bukkit.getWorld(world);
			if (realWorld == null) {
				continue;
			}
			path = agentsYaml.getConfigurationSection(world);
			for (final String region : path.getKeys(false)) {
				ProtectedRegion protectedRegion = SimpleRegionMarket.getInstance().getWorldGuardManager()
						.getProtectedRegion(realWorld, region);
				if (protectedRegion == null) {
					continue;
				}
				path = agentsYaml.getConfigurationSection(world).getConfigurationSection(region);
				for (final String signnr : path.getKeys(false)) {
					path = agentsYaml.getConfigurationSection(world).getConfigurationSection(region)
							.getConfigurationSection(signnr);
					if (path == null) {
						continue;
					}

					if (path.getInt("Mode") == 1) { // HOTEL

						/*
						if (!tokenHotel.entries.containsKey(world)) {
							tokenHotel.entries.put(world, new HashMap<String, HashMap<String, Object>>());
						}
						if (!tokenHotel.entries.get(world).containsKey(region)) {
							tokenHotel.entries.get(world).put(region, new HashMap<String, Object>());
							Utils.setEntry(tokenHotel, world, region, "price", path.getInt("Price"));
							Utils.setEntry(tokenHotel, world, region, "account", path.getInt("Account"));
							Utils.setEntry(tokenHotel, world, region, "renttime", path.getLong("RentTime"));
							if (path.isSet("ExpireDate")) {
								Utils.setEntry(tokenHotel, world, region, "taken", true);
								Utils.setEntry(tokenHotel, world, region, "owner", path.getString("RentBy"));
								Utils.setEntry(tokenHotel, world, region, "expiredate", path.getLong("ExpireDate"));
							} else {
								Utils.setEntry(tokenHotel, world, region, "taken", false);
							}
						}

						final ArrayList<Location> signLocations = Utils.getSignLocations(tokenHotel, world, region);
						signLocations.add(new Location(realWorld, path.getDouble("X"), path.getDouble("Y"), path.getDouble("Z")));
						if (signLocations.size() == 1) {
							Utils.setEntry(tokenHotel, world, region, "signs", signLocations);
						}
						*/
					} else { // SELL
						/*
						if (!tokenAgent.entries.containsKey(world)) {
							tokenAgent.entries.put(world, new HashMap<String, HashMap<String, Object>>());
						}
						if (!tokenAgent.entries.get(world).containsKey(region)) {
							tokenAgent.entries.get(world).put(region, new HashMap<String, Object>());
							Utils.setEntry(tokenAgent, world, region, "price", path.getInt("Price"));
							Utils.setEntry(tokenAgent, world, region, "account", path.getInt("Account"));
							Utils.setEntry(tokenAgent, world, region, "renttime", path.getLong("RentTime"));
							Utils.setEntry(tokenAgent, world, region, "taken", false);
						}

						final ArrayList<Location> signLocations = Utils.getSignLocations(tokenAgent, world, region);
						signLocations.add(new Location(realWorld, path.getDouble("X"), path.getDouble("Y"), path.getDouble("Z")));
						if (signLocations.size() == 1) {
							Utils.setEntry(tokenAgent, world, region, "signs", signLocations);
						}
						*/
					}
				}
			}
		}
		load();
	}

	@SuppressWarnings("HardCodedStringLiteral")
	private void update(File templateFile, YamlConfiguration templateYaml) throws TemplateFormatException, IOException {

		// Old
		if (templateYaml.isSet("templates_version")) {
			for (String templateId : templateYaml.getRoot().getKeys(false)) {
				if (templateYaml.isSet(templateId + ".type")) {
					String type = templateYaml.getString(templateId + ".type");
					if (type.equalsIgnoreCase("sell")) {
						templateYaml.set(templateId + ".type", "TemplateSell");
					} else if (type.equalsIgnoreCase("let")) {
						templateYaml.set(templateId + ".type", "TemplateLease");
					} else if (type.equalsIgnoreCase("rent")) {
						templateYaml.set(templateId + ".type", "TemplateRent");
					} else if (type.equalsIgnoreCase("bid")) {
						templateYaml.set(templateId + ".type", "TemplateAuction");
					}
				}

				if (templateYaml.isSet(templateId + ".bidtime")) {
					if (templateYaml.isSet(templateId + ".bidtime.min")) {
						templateYaml.set(templateId + ".bidtime.min",
								templateYaml.getInt(templateId + ".bidtime.min") / 1000);
					}
					if (templateYaml.isSet(templateId + ".bidtime.max")) {
						templateYaml.set(templateId + ".bidtime.max",
								templateYaml.getInt(templateId + ".bidtime.max") / 1000);
					}
					templateYaml.set(templateId + ".auctiontime", templateYaml.get(templateId + ".bidtime"));
					templateYaml.set(templateId + ".bidtime", null);
				}

				if (templateYaml.isSet(templateId + ".renttime.min")) {
					templateYaml.set(templateId + "renttime.min",
							templateYaml.getInt(templateId + ".renttime.min") / 1000);
				}
				if (templateYaml.isSet(templateId + ".renttime.max")) {
					templateYaml.set(templateId + "renttime.max",
							templateYaml.getInt(templateId + ".renttime.max") / 1000);
				}

				if (templateYaml.isSet(templateId + ".input.id")) {
					templateYaml.set(templateId + ".input.1", templateYaml.getString(templateId + ".input.id"));
					templateYaml.set(templateId + ".input.id", null);
				}
				if (templateYaml.isSet(templateId + ".output.1")) {
					if (templateYaml.getString(templateId + ".output.1").equals("[[id_out]]")) {
						templateYaml.set(templateId + ".output.1", templateYaml.getString(templateId + ".output.id"));
					}
					templateYaml.set(templateId + ".output.id", null);
				}
				if (templateYaml.isSet(templateId + ".output")) {
					for (int i = 0; i < SIGN_LINE_COUNT; i++) {
						templateYaml.set(templateId + ".output.free." + (i + 1),
								templateYaml.get(templateId + ".output." + (i + 1)));
						templateYaml.set(templateId + ".output." + (i + 1), null);
					}
				}
				if (templateYaml.isSet(templateId + ".taken.1")) {
					if (templateYaml.getString(templateId + ".taken.1").equals("[[id_taken]]")) {
						templateYaml.set(templateId + ".taken.1", templateYaml.getString(templateId + ".taken.id"));
					}
					templateYaml.set(templateId + ".taken.id", null);
				}
				if (templateYaml.isSet(templateId + ".taken")) {
					for (int i = 0; i < SIGN_LINE_COUNT; i++) {
						templateYaml.set(templateId + ".output.occupied." + (i + 1),
								templateYaml.get(templateId + ".taken." + (i + 1)));
					}
					templateYaml.set(templateId + ".taken", null);
				}

				if (templateYaml.isSet(templateId + ".input")) {
					for (int i = 0; i < SIGN_LINE_COUNT; ++i) {
						String path = templateId + ".input." + (i + 1);
						if (templateYaml.isSet(path)) {
							templateYaml.set(path, templateYaml.getString(path).replaceAll("\\Q[[price:time]]\\E", "[[price]]:[[time]]"));
						}
					}
				}

				String type = templateYaml.getString(templateId + ".type");
				if (templateYaml.isSet(templateId + ".output.occupied")) {
					for (int i = 1; i <= SIGN_LINE_COUNT; ++i) {
						String tempPath = String.format("%s.output.occupied.%d", templateId, i);
						String str = templateYaml.getString(tempPath);
						if (type.equalsIgnoreCase("TemplateSell")) {

							// occupied [[player]] => [[buyer]]
							str = str.replaceAll("[[player]]", "[[buyer]]");

						} else if (type.equalsIgnoreCase("TemplateLease")) {

							// occupied [[player]] => [[owner]]
							str = str.replaceAll("[[player]]", "[[owner]]");

						} else if (type.equalsIgnoreCase("TemplateRent")) {

							// occupied [[player]] => [[owner]]
							str = str.replaceAll("[[player]]", "[[owner]]");

						} else if (type.equalsIgnoreCase("TemplateAuction")) {

							// occupied [[player]] => [[highestbidder]]
							str = str.replaceAll("[[player]]", "[[highestbidder]]");

						}
						templateYaml.set(tempPath, str);
					}
				}
			}
			templateYaml.set("templates_version", null);
			templateYaml.set("version", 1);
		}

		// Very old
		else if (!templateYaml.isSet("version")) {
			templateYaml.set("templates_version", "1.0.1");
		}

		// New
		else if (templateYaml.getInt("version") != TEMPLATE_VERSION) {
			int version = templateYaml.getInt("version");
			switch (version) {
			default:
				throw new TemplateFormatException("Unknown template version");
			}
		}

		templateYaml.save(templateFile);
		load();
	}

	public Template getTemplateFromId(String id) {
		if (id != null) {
			synchronized (templateList) {
				for (Template template : templateList) {
					if (template.getId().equalsIgnoreCase(id)) {
						return template;
					}
				}
			}
		}
		return null;
	}

	public void loadContent() throws ContentLoadException {
		FilenameFilter ymlFiles = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".yml");
			}
		};

		synchronized (templateList) {
			for (Template template : templateList) {
				File templateDir = new File(new File(SimpleRegionMarket.getInstance().getDataFolder(), REGIONS_FOLDER),
						template.getId().toLowerCase());

				// Search through template dir, if exists
				if (templateDir.exists()) {
					for (String worldStr : templateDir.list()) {
						File worldDir = new File(templateDir, worldStr);
						if (worldDir.isDirectory()) {
							// Check the world and get the world object from Bukkit
							World world = Bukkit.getWorld(worldStr);

							if (world != null) {
								File[] files = worldDir.listFiles(ymlFiles);
								if (files != null) {
									for (File regionFile : files) {
										// Create a fresh YamlConfiguration
										YamlConfiguration regionConfig = new YamlConfiguration();

										// Open the region configuration file and try to load it
										try {
											regionConfig.load(regionFile);
										} catch (FileNotFoundException e) {
											throw new ThisShouldNeverHappenException(
													"The region file does not exist, but it did a few nanoseconds ago");
										} catch (IOException e) {
											throw new ContentLoadException("Problem when loading region file", e);
										} catch (InvalidConfigurationException e) {
											throw new ContentLoadException("Yaml region file with wrong configuration",
													e);
										}

										// Let the RegionFactory do the rest
										try {
											RegionFactory.instance.loadFromConfiguration(regionConfig, "");
										} catch (ContentLoadException e) {
											throw new ContentLoadException(
													"There was a problem reading the region file "
															+ regionFile.getPath());
										}
									}
								}
							} else {
								SimpleRegionMarket
										.getInstance()
										.getLogger()
										.warning(
												MessageFormat.format(
														LanguageSupport.instance
																.getString("template.load.world.not.found"),
														worldStr, template.getId()));
							}
						}
					}
				}
			}
		}
	}

	//	public void saveContent() throws ContentSaveException {
	//		for (World world : Bukkit.getWorlds()) {
	//			saveContent(world);
	//		}
	//	}
	//
	//	public void saveContent(World world) throws ContentSaveException {
	//		if (world == null) {
	//			throw new IllegalArgumentException("World cannot be null");
	//		}
	//
	//		synchronized (templateList) {
	//			for (Template template : templateList) {
	//				File templateDir = new File(new File(SimpleRegionMarket.getInstance().getDataFolder(), REGIONS_FOLDER),
	//						template.getId().toLowerCase());
	//
	//				// Get the world folder
	//				File worldFolder = new File(templateDir, world.getName());
	//				File[] regionFiles = worldFolder.listFiles();
	//				ArrayList<File> files;
	//				if (regionFiles != null) {
	//					files = new ArrayList<File>(Arrays.asList(regionFiles));
	//				} else {
	//					// empty ArrayList
	//					files = new ArrayList<File>(0);
	//				}
	//
	//				for (Region region : template.getRegionList()) {
	//					if (region.getWorld().equals(world)) {
	//						File regionFile = new File(worldFolder, String.format(REGIONS_YML_FORMAT_STRING,
	//								region.getName()));
	//						files.remove(regionFile);
	//						saveRegion(region, regionFile);
	//					}
	//				}
	//
	//				for (File toDelete : files) {
	//					if (!toDelete.delete()) {
	//						SimpleRegionMarket
	//								.getInstance()
	//								.getLogger()
	//								.warning(
	//										MessageFormat.format(
	//												LanguageSupport.instance.getString("region.save.could.not.remove.file"),
	//												toDelete.getPath()));
	//					}
	//				}
	//			}
	//		}
	//	}

	public File getRegionFile(Region region) {
		return new File(
				new File(
						new File(
								new File(
										SimpleRegionMarket.getInstance().getDataFolder(),
										REGIONS_FOLDER
								),
								region.getTemplate().getId().toLowerCase()
						),
						region.getWorld().getName()
				),
				String.format(REGIONS_YML_FORMAT_STRING, region.getName()));
	}

	public void saveRegion(Region region) throws ContentSaveException {
		if (region == null) {
			throw new IllegalArgumentException("Region cannot be null");
		}

		saveRegion(region, getRegionFile(region));
	}

	public void removeRegion(Region region) {
		if (region == null) {
			throw new IllegalArgumentException("Region cannot be null");
		}

		if (region.getTemplate() instanceof TemplateSell) {
			TemplateSell sellTemplate = (TemplateSell) region.getTemplate();
			File schematicFile = SimpleRegionMarket.getInstance().getWorldEditManager().getSchematicFile(region);
			if (sellTemplate.doesRegionReset()) {
				if (!schematicFile.delete()) {
					SimpleRegionMarket.getInstance().getLogger()
							.warning("Could not remove region schematic file " + schematicFile.getPath());
				}
			}
		}

		File regionFile = getRegionFile(region);
		if (!regionFile.delete()) {
			SimpleRegionMarket
					.getInstance()
					.getLogger()
					.warning(
							MessageFormat.format(
									LanguageSupport.instance.getString("region.save.could.not.remove.file"),
									regionFile.getPath()));
		}
	}

	public void saveRegion(Region region, File file) throws ContentSaveException {
		if (region == null) {
			throw new IllegalArgumentException("Region cannot be null");
		}
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}

		// Create folders leading to the region config file
		file.getParentFile().mkdirs();

		YamlConfiguration regionConfig = new YamlConfiguration();

		// Let the region put the stuff in
		region.saveToConfiguration(regionConfig, "");

		// Save
		try {
			regionConfig.save(file);
		} catch (IOException e) {
			throw new ContentSaveException(region, e);
		}
	}
}
