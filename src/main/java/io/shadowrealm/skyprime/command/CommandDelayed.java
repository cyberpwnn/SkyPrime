package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyPrime;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandDelayed extends MortarCommand
{
	public CommandDelayed()
	{
		super("confirm", "accept", "deny", "cancel", "can", "reject");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if (sender.getCommand().equalsIgnoreCase("confirm") || sender.getCommand().equalsIgnoreCase("accept")) {
			if (!SkyPrime.instance.delayedController.confirm(sender)) {
				sender.sendMessage("You don't have any pending confirmations");
			}
		} else {
			if (!SkyPrime.instance.delayedController.cancel(sender)) {
				sender.sendMessage("You don't have any pending confirmations");
			}
		}
		return true;
	}
}
