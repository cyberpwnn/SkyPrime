package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.storage.Island;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.format.F;

public class CommandMembers extends PawnCommand
{
	public CommandMembers()
	{
		super("members", "admins");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant check value on an island you dont have. Use /sky create");
			return true;
		}

		Island is = SkyMaster.getIslandConfig(sender.player());

		for()

			return true;
	}
}
