package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.storage.Island;
import io.shadowrealm.skyprime.storage.Visibility;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandPrivacy extends MortarCommand
{
	public CommandPrivacy()
	{
		super("privacy", "lock", "public", "private");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant configure an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant configure your island. It isnt loaded. Use /sky.");
			return true;
		}

		try {
			Island is = SkyMaster.getIsland(sender.player()).getIsland();
			is.getProtection().setPublicVisbility(! is.getProtection().isPublicVisibility());
			sender.sendMessage("Changed your island's privacy to " + is.getProtection().getVisibility().toString().toLowerCase());
			SkyMaster.getIsland(sender.player()).saveConfig(sender);
		} catch (Exception ex) {
			sender.sendMessage("&cInternal error has occurred. Please panic.");
			ex.printStackTrace();
		}

		return true;
	}
}
