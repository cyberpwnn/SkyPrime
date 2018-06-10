package com.volmit.skyprime.command;

import com.volmit.volume.bukkit.command.Command;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandSkyPrime extends PawnCommand
{
	@Command
	private CommandReload reload;

	@Command
	private CommandCreate create;

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
