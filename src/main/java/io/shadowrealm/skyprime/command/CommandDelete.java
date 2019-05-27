package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyMaster;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandDelete extends MortarCommand
{
	public CommandDelete()
	{
		super("delete", "destroy");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant destroy an island you dont have. Use /sky create");
			return true;
		}

		if(SkyMaster.hasIslandLoaded(sender.player()))
		{
			SkyMaster.getIsland(sender.player()).delete();
		}

		else
		{
			SkyMaster.coldDelete(sender.player());
		}

		sender.sendMessage("Island Deleted!");

		return true;
	}
}
