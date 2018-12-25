package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.task.S;

public class CommandReboot extends PawnCommand
{
	public CommandReboot()
	{
		super("reboot", "restart");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant reboot an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant reboot your island. It isnt loaded. Use /sky.");
			return true;
		}

		sender.sendMessage("Rebooting your island, please wait.");
		SkyMaster.getIsland(sender.player()).unload();

		new S(30)
		{
			@Override
			public void run()
			{
				Bukkit.dispatchCommand(sender.player(), "sky");
			}
		};

		return true;
	}
}
