package com.volmit.skyprime;

import java.io.File;

import com.volmit.phantom.api.lang.D;
import com.volmit.phantom.api.module.Color;
import com.volmit.phantom.api.module.Command;
import com.volmit.phantom.api.module.Instance;
import com.volmit.phantom.api.module.Module;
import com.volmit.phantom.api.module.Permission;
import com.volmit.phantom.api.module.Start;
import com.volmit.phantom.api.module.Stop;
import com.volmit.phantom.api.sheduler.SR;
import com.volmit.phantom.util.text.C;
import com.volmit.skyprime.command.CommandSkyPrime;
import com.volmit.skyprime.permissions.PermissionSky;
import com.volmit.skyprime.storage.FileStorageEngine;

@Color(C.AQUA)
public class SkyPrime extends Module
{
	@Permission
	public static PermissionSky perm;

	@Instance
	public static SkyPrime instance;

	@Command
	private CommandSkyPrime commandsp;

	@Start
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

	@Stop
	public void stop()
	{
		D.as("SkyPrime").l("Unloading Virtual Islands");
		SkyMaster.saveAllWorlds();
	}
}
