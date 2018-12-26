package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandReload extends PawnCommand
{
	public CommandReload()
	{
		super("reload", "rld");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(!sender.hasPermission("sky.reload"))
		{
			return true;
		}

		sender.sendMessage("Reloaded Configs.");
		SkyMaster.loadConfig();
		return true;
	}
}
