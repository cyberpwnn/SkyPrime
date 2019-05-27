package io.shadowrealm.skyprime.command;

import org.bukkit.Bukkit;

import io.shadowrealm.skyprime.SkyMaster;
import mortar.api.sched.S;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandRecreate extends MortarCommand
{
	public CommandRecreate()
	{
		super("recreate");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant destroy an island you dont have. Use /sky create");
			return true;
		}

		if(SkyMaster.hasIslandLoaded(sender.player()))
		{
			SkyMaster.getIsland(sender.player()).delete();
		}

		else
		{
			SkyMaster.coldDelete(sender.player());
		}

		new S(5)
		{
			@Override
			public void run()
			{
				Bukkit.dispatchCommand(sender.player(), "sky new");
			}
		};

		return true;
	}
}
