package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.SkyPrime;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.util.text.C;

public class CommandStatus extends MortarCommand
{
	public CommandStatus()
	{
		super("status", "stats", "admin");
		requiresPermission(SkyPrime.perm.admin);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		sender.sendMessage("Loaded Islands: " + C.WHITE + ((int) SkyMaster.getIslandsCount()));
		sender.sendMessage("Voltage: " + C.WHITE + SkyMaster.getVoltageSummary());

		return true;
	}
}
