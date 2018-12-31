package com.volmit.skyprime;

import java.io.File;

import com.volmit.phantom.lang.D;
import com.volmit.phantom.plugin.Module;
import com.volmit.phantom.plugin.SR;
import com.volmit.phantom.plugin.Scaffold.Command;
import com.volmit.phantom.plugin.Scaffold.Instance;
import com.volmit.phantom.plugin.Scaffold.ModuleInfo;
import com.volmit.phantom.plugin.Scaffold.Permission;
import com.volmit.phantom.plugin.Scaffold.Start;
import com.volmit.phantom.plugin.Scaffold.Stop;
import com.volmit.phantom.text.C;
import com.volmit.skyprime.command.CommandSkyPrime;
import com.volmit.skyprime.permissions.PermissionSky;
import com.volmit.skyprime.storage.FileStorageEngine;

@ModuleInfo(name = "SkyPrime", version = "1.2.11", author = "cyberpwn", color = C.AQUA)
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
