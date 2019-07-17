package io.shadowrealm.skyprime.command;

import java.util.UUID;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.storage.Island;
import mortar.api.sched.J;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.Mortar;
import mortar.lib.control.MojangProfileController;

public class CommandMembers extends MortarCommand
{
	@Command
	public CommandAddMember add;

	@Command
	public CommandRemoveMember del;

	public CommandMembers()
	{
		super("members", "admins");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant check members on an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant configure your island. It isnt loaded. Use /sky.");
			return true;
		}

		Island is = SkyMaster.getIsland(sender.player()).getIsland();
		sender.sendMessage("/sky members add <player>");
		sender.sendMessage("/sky members remove <player>");
		sender.sendMessage("Members: " + is.getMembers().size() + " of " + is.getMaximumMembers());

		for(UUID i : is.getMembers())
		{
			J.a(() -> sender.sendMessage("- " + Mortar.getController(MojangProfileController.class).getOnlineNameFor(i)));
		}

		return true;
	}
}
