package io.shadowrealm.skyprime;

import java.io.File;

import io.shadowrealm.skyprime.command.CommandSkyPrime;
import io.shadowrealm.skyprime.permissions.PermissionSky;
import io.shadowrealm.skyprime.storage.FileStorageEngine;
import mortar.api.sched.SR;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.Permission;
import mortar.bukkit.plugin.Instance;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.util.text.C;
import mortar.util.text.D;
import mortar.util.text.TXT;

public class SkyPrime extends MortarPlugin
{
	@Permission
	public static PermissionSky perm;

	@Instance
	public static SkyPrime instance;

	@Command
	private CommandSkyPrime commandsp;

	@Override
	public void start()
	{
		SkyMaster.setStorageEngine(new FileStorageEngine(new File("skydata/islands")));
		SkyMaster.deleteIslands();

		new SR(0)
		{
			@Override
			public void run()
			{
				SkyMaster.tick();
			}
		};
	}

	@Override
	public void stop()
	{
		D.as("SkyPrime").l("Unloading Virtual Islands");
		SkyMaster.saveAllWorlds();
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
}
