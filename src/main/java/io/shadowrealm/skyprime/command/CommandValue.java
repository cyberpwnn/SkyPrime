package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.Config;
import io.shadowrealm.skyprime.SkyMaster;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandValue extends MortarCommand
{
	public CommandValue()
	{
		super("value", "val");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant check value on an island you dont have. Use /sky create");
			return true;
		}

		if(SkyMaster.hasIslandLoaded(sender.player()) && SkyMaster.getIsland(sender.player()).getWorld().equals(sender.player().getWorld()))
		{
			sender.sendMessage("Island Size: " + C.BOLD + C.WHITE + F.f((int) SkyMaster.getIsland(sender.player()).getWorld().getWorldBorder().getSize()) + " of " + F.f((long) SkyMaster.getIsland(sender.player()).getIsland().getMaxSize()));
			sender.sendMessage("Island Value: " + C.BOLD + C.GREEN + F.f((long) (SkyMaster.getIsland(sender.player()).getIsland().getValue() / Config.VALUE_DIVISOR)));
			sender.sendMessage("Island Level: " + C.BOLD + C.AQUA + F.f((long) (SkyMaster.getIsland(sender.player()).getIsland().getLevel() / Config.LEVEL_DIVISOR)));
		}

		else
		{
			sender.sendMessage("Island Value: " + C.BOLD + C.GREEN + F.f((long) (SkyMaster.getIslandConfig(sender.player()).getValue() / Config.VALUE_DIVISOR)));
			sender.sendMessage("Island Level: " + C.BOLD + C.AQUA + F.f((long) (SkyMaster.getIslandConfig(sender.player()).getLevel() / Config.LEVEL_DIVISOR)));
		}

		return true;
	}
}
