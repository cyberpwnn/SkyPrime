package com.volmit.skyprime.command;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.imp.command.PhantomCommand;
import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.SkyPrime;

public class CommandReload extends PhantomCommand
{
	public CommandReload()
	{
		super("reload", "rld");
		requiresPermission(SkyPrime.perm.admin.reload);
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		sender.sendMessage("Reloaded Configs.");
		SkyMaster.loadConfig();
		return true;
	}
}
