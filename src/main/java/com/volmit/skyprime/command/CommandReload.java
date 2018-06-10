package com.volmit.skyprime.command;

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
		sender.sendMessage("Reloaded. JK.");
		return true;
	}
}
