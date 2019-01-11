package com.volmit.skyprime.command;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.imp.command.PhantomCommand;
import com.volmit.phantom.util.text.C;
import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.SkyPrime;

public class CommandStatus extends PhantomCommand
{
	public CommandStatus()
	{
		super("status", "stats", "admin");
		requiresPermission(SkyPrime.perm.admin);
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		sender.sendMessage("Loaded Islands: " + C.WHITE + ((int) SkyMaster.getIslandsCount()));
		sender.sendMessage("Voltage: " + C.WHITE + SkyMaster.getVoltageSummary());

		return true;
	}
}
