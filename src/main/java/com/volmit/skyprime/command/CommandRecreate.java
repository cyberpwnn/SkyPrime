package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.task.S;

public class CommandRecreate extends PawnCommand
{
	public CommandRecreate()
	{
		super("recreate");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
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
