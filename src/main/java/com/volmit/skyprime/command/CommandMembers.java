package com.volmit.skyprime.command;

import java.util.UUID;

import com.volmit.phantom.api.service.SVC;
import com.volmit.phantom.lib.service.MojangProfileSVC;
import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.storage.Island;

import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

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

		Island is = SkyMaster.getIslandConfig(sender.player());

		sender.sendMessage("Members: " + is.getMembers().size());

		for(UUID i : is.getMembers())
		{
			SVC.get(MojangProfileSVC.class).getOnlineNameFor(i);
		}

		sender.sendMessage("Admins: " + is.getMembers().size());

		for(UUID i : is.getAdmins())
		{
			SVC.get(MojangProfileSVC.class).getOnlineNameFor(i);
		}

		return true;
	}
}
