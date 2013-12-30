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

package com.thezorro266.bukkit.srm;

import java.io.IOException;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.exceptions.ContentSaveException;
import com.thezorro266.bukkit.srm.exceptions.TemplateFormatException;
import com.thezorro266.bukkit.srm.factories.RegionFactory;
import com.thezorro266.bukkit.srm.helpers.LocationSignHelper;
import com.thezorro266.bukkit.srm.helpers.WorldHelper;
import com.thezorro266.bukkit.srm.templates.Template;
import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class SimpleRegionMarket extends JavaPlugin {
	private static final boolean PRINT_STACKTRACE = false;

	@Getter
	private static SimpleRegionMarket instance = null;
	@Getter
	private final LocationSignHelper locationSignHelper;
	@Getter
	private final WorldHelper worldHelper;
	@Getter
	private final TemplateManager templateManager;
	@Getter
	private final WorldGuardManager worldGuardManager;
	@Getter
	private final VaultHook vaultHook;

	private boolean loading = true;
	private boolean disable = false;

	public SimpleRegionMarket() {
		super();
		instance = this;
		locationSignHelper = new LocationSignHelper();
		worldHelper = new WorldHelper();
		templateManager = new TemplateManager();
		worldGuardManager = new WorldGuardManager();
		vaultHook = new VaultHook();
	}

	@Override
	public void onDisable() {
		try {
			saveRegions();
		} catch (ContentSaveException e) {
			e.printStackTrace();
		}
		instance = null;
	}

	@Override
	public void onLoad() {
		long start = System.nanoTime();
		{
			try {
				templateManager.load();
			} catch (TemplateFormatException e) {
				except(e);
				return;
			} catch (IOException e) {
				except(e);
				return;
			}
		}
		getLogger().info(String.format("Loaded %d templates in %dms", templateManager.getTemplateList().size(), (System.nanoTime() - start) / 1000000L));
	}

	@Override
	public void onEnable() {
		// Try to load dependencies
		try {
			vaultHook.load();
			worldGuardManager.load();
		} catch (Throwable e) {
			except(e);
		}

		// Load regions in templates
		long start = System.nanoTime();
		{
			try {
				templateManager.loadContent();
			} catch (ContentLoadException e) {
				except(e);
			}
		}
		getLogger().info(String.format("Loaded %d regions in %dms", RegionFactory.instance.getRegionCount(), (System.nanoTime() - start) / 1000000L));

		// Check if the plugin should be disabled because of an exception
		if (disable) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		loading = false;

		// Register events
		new EventListener();

		// Set command executor
		getCommand("regionmarket").setExecutor(new CommandHandler());

		// Set up async timer
		getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				for (Template template : templateManager.getTemplateList()) {
					if (template instanceof TimedTemplate) {
						((TimedTemplate) template).schedule();
					}
				}
			}
		}, 1200L, 1200L);
	}

	void saveRegions() throws ContentSaveException {
		if (!loading) {
			long start = System.nanoTime();
			{
				templateManager.saveContent();
			}
			getLogger().info(String.format("Saved %d regions in %dms", RegionFactory.instance.getRegionCount(), (System.nanoTime() - start) / 1000000L));
		} else {
			getLogger().info("Not saving anything, because I didn't even load");
		}
	}

	private void except(Throwable t) {
		disable = true;
		getLogger().severe("We got a problem here. Disabling plugin..");
        printError(t);

		if (!loading) {
			getPluginLoader().disablePlugin(this);
		}
	}

    public void printError(Throwable t) {
        if (PRINT_STACKTRACE) {
            t.printStackTrace();
        } else {
            getLogger().severe(t.toString());
            for (StackTraceElement element : t.getStackTrace()) {
                getLogger().severe(element.toString());
            }

            Throwable cause = t.getCause();
            if(cause != null) {
                getLogger().severe("=== Caused by:");
                printError(cause);
            }
        }
    }

	public static String getCopyright() {
		return "(c) 2013  theZorro266 and SRM Team";
	}
}
