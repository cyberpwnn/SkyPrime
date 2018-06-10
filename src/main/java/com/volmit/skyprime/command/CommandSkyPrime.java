package com.volmit.skyprime.command;

import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandSkyPrime extends PawnCommand
{
	public CommandSkyPrime()
	{
		super("skyprime", "sky", "sp", "sprime");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		sender.sendMessage("Hello");
		return true;
	}
}
