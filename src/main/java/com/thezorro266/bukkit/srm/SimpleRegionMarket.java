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

import com.thezorro266.bukkit.srm.helpers.RegionFactory;
import com.thezorro266.bukkit.srm.helpers.WorldHelper;
import com.thezorro266.bukkit.srm.templates.Template;
import com.thezorro266.bukkit.srm.templates.TemplateFormatException;
import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class SimpleRegionMarket extends JavaPlugin {
	public static final boolean PRINT_STACKTRACE = false;

	@Getter
	private static SimpleRegionMarket instance = null;
	@Getter
	private final WorldHelper worldHelper;
	@Getter
	private final RegionFactory regionFactory;
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
		worldHelper = new WorldHelper();
		regionFactory = new RegionFactory();
		templateManager = new TemplateManager();
		worldGuardManager = new WorldGuardManager();
		vaultHook = new VaultHook();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	@Override
	public void onLoad() {
		long start = System.currentTimeMillis();
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
		getLogger().info(String.format("Loaded %d templates in %dms", templateManager.getTemplateList().size(), System.currentTimeMillis() - start));
	}

	@Override
	public void onEnable() {
		try {
			vaultHook.load();
			worldGuardManager.load();
		} catch (Throwable e) {
			except(e);
		}

		if (disable) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		loading = false;

		// TODO template region load (I guess)

		new EventListener();

		getCommand("regionmarket").setExecutor(new CommandHandler());

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

	private void except(Throwable t) {
		disable = true;
		getLogger().severe("We got a problem here. Disabling plugin..");
		if (PRINT_STACKTRACE) {
			t.printStackTrace();
		} else {
			for (StackTraceElement element : t.getStackTrace()) {
				getLogger().severe(element.toString());
			}
		}

		if (!loading) {
			getPluginLoader().disablePlugin(this);
		}
	}

	public static String getCopyright() {
		return "(c) 2013  theZorro266 and SRM Team";
	}
}
