package io.shadowrealm.skyprime;

import java.io.File;

import io.shadowrealm.skyprime.command.CommandSkyPrime;
import io.shadowrealm.skyprime.controller.*;
import io.shadowrealm.skyprime.dependencies.LeaderHeads;
import io.shadowrealm.skyprime.dependencies.PlaceholderAPI;
import io.shadowrealm.skyprime.permissions.PermissionSky;
import io.shadowrealm.skyprime.storage.FileStorageEngine;
import lombok.Getter;
import mortar.api.sched.SR;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.Permission;
import mortar.bukkit.plugin.Control;
import mortar.bukkit.plugin.Instance;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.bukkit.plugin.commands.DelayedController;
import mortar.util.text.C;
import mortar.util.text.D;
import mortar.util.text.TXT;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SkyPrime extends MortarPlugin
{
	@Permission
	public static PermissionSky perm;

	@Instance
	public static SkyPrime instance;

	@Command
	private CommandSkyPrime commandsp;

	@Control
	public DelayedController delayedController;

	@Control
	public IslandChatController islandChatController;

	@Control
	public IslandController islandController;

	@Control
	private IslandProtectionController islandProtectionController;

	@Control
	public IslandRankController islandRankController;

	private PlaceholderAPI papi;

	@Getter
	private LeaderHeads leaderHeads;

	@Override
	public void start()
	{
		final File storageBin = new File("skydata/islands");

		SkyMaster.setStorageEngine(new FileStorageEngine(storageBin));
		SkyMaster.deleteIslands();

		islandRankController.setStorage(storageBin);

		this.registerDependencies();

		new SR(0)
		{
			@Override
			public void run()
			{
				delayedController.tick();
				islandController.tick();
				islandChatController.tick();
				islandRankController.tick();
				SkyMaster.tick();
			}
		};
	}

	@Override
	public void stop()
	{
		D.as("SkyPrime").l("Unloading Virtual Islands");
		SkyMaster.saveAllWorlds();
		SkyMaster.getStorageEngine().shutdown();
	}

	@Override
	public String getTag(String subTag)
	{
		if(subTag.isEmpty())
		{
			return TXT.makeTag(C.GRAY, C.AQUA, C.GRAY, "Sky");
		}

		else
		{
			return TXT.makeTag(C.GRAY, C.AQUA, C.GRAY, "Sky - " + subTag);
		}
	}

	private void registerDependencies()
	{
		this.papi = new PlaceholderAPI(this);
		this.papi.register();

		Plugin p = Bukkit.getPluginManager().getPlugin("LeaderHeads");
		if (p != null && p instanceof me.robin.leaderheads.LeaderHeads) {
			this.leaderHeads = new LeaderHeads(this);
		}
	}
}
