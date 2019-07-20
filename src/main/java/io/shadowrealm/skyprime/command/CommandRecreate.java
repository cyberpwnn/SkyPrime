package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyPrime;
import mortar.bukkit.plugin.commands.DelayedCommand;
import mortar.util.text.C;
import org.bukkit.Bukkit;

import io.shadowrealm.skyprime.SkyMaster;
import mortar.api.sched.S;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import org.bukkit.entity.Player;

public class CommandRecreate extends MortarCommand
{
	public CommandRecreate()
	{
		super("recreate");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant destroy an island you dont have. Use /sky create");
			return true;
		}

		sender.sendMessage("Please confirm your decision to rebuild your island. Use /sky confirm or /sky cancel");

		SkyPrime.instance.delayedController.register(
			new DelayedCommand(
				"Island rebuild",
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

					new S(5)
					{
						@Override
						public void run()
						{
							Bukkit.dispatchCommand(sender.player(), "skyprime new");
						}
					};
				}
			)
		);


		return true;
	}
}
