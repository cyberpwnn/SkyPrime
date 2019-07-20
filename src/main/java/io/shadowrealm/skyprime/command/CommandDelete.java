package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.SkyPrime;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.commands.DelayedCommand;

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

		sender.sendMessage("Please confirm your decision to delete your island. Use /sky confirm or /sky cancel");

		SkyPrime.instance.delayedController.register(
			new DelayedCommand(
				"Island deletion",
				sender,
				() -> {
					if(SkyMaster.hasIslandLoaded(sender.player()))
					{
						SkyMaster.getIsland(sender.player()).delete();
					}
					else
					{
						SkyMaster.coldDelete(sender.player());
					}

					sender.sendMessage("Island Deleted!");
				}
			)
		);

		return true;
	}
}
