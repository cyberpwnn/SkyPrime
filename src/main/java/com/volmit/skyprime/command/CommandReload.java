package com.volmit.skyprime.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
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
