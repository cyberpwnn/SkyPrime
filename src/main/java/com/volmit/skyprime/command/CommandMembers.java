package com.volmit.skyprime.command;

import java.util.UUID;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.api.module.Command;
import com.volmit.phantom.api.service.SVC;
import com.volmit.phantom.imp.command.PhantomCommand;
import com.volmit.phantom.lib.service.MojangProfileSVC;
import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.storage.Island;

public class CommandMembers extends PhantomCommand
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
	public boolean handle(PhantomSender sender, String[] args)
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
