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

package com.thezorro266.bukkit.srm;

import java.io.IOException;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

import com.thezorro266.bukkit.srm.templates.Template;
import com.thezorro266.bukkit.srm.templates.TemplateFormatException;
import com.thezorro266.bukkit.srm.templates.TemplateManager;
import com.thezorro266.bukkit.srm.templates.interfaces.TimedTemplate;

public class SimpleRegionMarket extends JavaPlugin {
	public static final boolean PRINT_STACKTRACE = false;

	@Getter
	private static SimpleRegionMarket instance = null;
	@Getter
	private final TemplateManager templateManager;
	@Getter
	private final WorldGuardManager worldGuardManager;

	private boolean loading = true;
	private boolean disable = false;

	public SimpleRegionMarket() {
		super();
		instance = this;
		templateManager = new TemplateManager();
		worldGuardManager = new WorldGuardManager();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onLoad() {
		long start = System.currentTimeMillis();
		try {
			templateManager.load();
		} catch (TemplateFormatException e) {
			except(e);
			return;
		} catch (IOException e) {
			except(e);
			return;
		}
		getLogger().info("Loaded " + templateManager.getTemplateList().size() + " templates in " + (System.currentTimeMillis() - start) + "ms");
	}

	@Override
	public void onEnable() {
		if (disable) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		loading = false;

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
			return;
		}
	}

	public static String getCopyright() {
		return "(c) 2013  theZorro266 and SRM Team";
	}
}
