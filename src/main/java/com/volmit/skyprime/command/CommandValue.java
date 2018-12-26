package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.format.F;

public class CommandValue extends PawnCommand
{
	public CommandValue()
	{
		super("value", "val");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
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
		}

		else
		{
			sender.sendMessage("Island Value: " + C.BOLD + C.GREEN + F.f((long) (SkyMaster.getIslandConfig(sender.player()).getValue() / 20D)));
		}

		return true;
	}
}
