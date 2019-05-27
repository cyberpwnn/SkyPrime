package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.SkyPrime;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandReload extends MortarCommand
{
	public CommandReload()
	{
		super("reload", "rld");
		requiresPermission(SkyPrime.perm.admin.reload);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		sender.sendMessage("Reloaded Configs.");
		SkyMaster.loadConfig();
		return true;
	}
}
