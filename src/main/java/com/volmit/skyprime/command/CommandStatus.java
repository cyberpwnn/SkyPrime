package com.volmit.skyprime.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.phantom.text.C;
import com.volmit.skyprime.SkyMaster;

public class CommandStatus extends PhantomCommand
{
	public CommandStatus()
	{
		super("status", "stats", "admin");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!sender.hasPermission("sky.admin"))
		{
			return true;
		}

		sender.sendMessage("Loaded Islands: " + C.WHITE + ((int) SkyMaster.getIslandsCount()));
		sender.sendMessage("Voltage: " + C.WHITE + SkyMaster.getVoltageSummary());

		return true;
	}
}
