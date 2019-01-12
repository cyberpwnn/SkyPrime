package com.volmit.skyprime.command;

import com.volmit.phantom.api.command.PhantomCommand;
import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.api.lang.F;
import com.volmit.phantom.util.text.C;
import com.volmit.skyprime.SkyMaster;

public class CommandValue extends PhantomCommand
{
	public CommandValue()
	{
		super("value", "val");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant check value on an island you dont have. Use /sky create");
			return true;
		}

		if(SkyMaster.hasIslandLoaded(sender.player()) && SkyMaster.getIsland(sender.player()).getWorld().equals(sender.player().getWorld()))
		{
			sender.sendMessage("Island Size: " + C.BOLD + C.WHITE + F.f((int) SkyMaster.getIsland(sender.player()).getWorld().getWorldBorder().getSize()) + " of " + F.f((long) SkyMaster.getIsland(sender.player()).getIsland().getMaxSize()));
			sender.sendMessage("Island Value: " + C.BOLD + C.GREEN + F.f((long) (SkyMaster.getIsland(sender.player()).getIsland().getValue() / 20D)));
			sender.sendMessage("Island Level: " + C.BOLD + C.AQUA + F.f((long) (SkyMaster.getIsland(sender.player()).getIsland().getLevel() / 20D)));
		}

		else
		{
			sender.sendMessage("Island Value: " + C.BOLD + C.GREEN + F.f((long) (SkyMaster.getIslandConfig(sender.player()).getValue() / 20D)));
			sender.sendMessage("Island Level: " + C.BOLD + C.AQUA + F.f((long) (SkyMaster.getIslandConfig(sender.player()).getLevel() / 20D)));
		}

		return true;
	}
}
