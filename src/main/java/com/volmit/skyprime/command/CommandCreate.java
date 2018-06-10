package com.volmit.skyprime.command;

import java.util.UUID;

import com.volmit.skyprime.api.SkyWorld;
import com.volmit.skyprime.service.SPWorldSVC;
import com.volmit.volume.bukkit.U;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.util.text.C;

public class CommandCreate extends PawnCommand
{
	public CommandCreate()
	{
		super("create", "new");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(args.length == 2)
		{
			String name = "test-" + UUID.randomUUID().toString();
			sender.sendMessage("Creating skyworld " + C.WHITE + name);
			SkyWorld sw = U.getService(SPWorldSVC.class).createWorld(name, Integer.valueOf(args[0]), Integer.valueOf(args[0]));
			sender.sendMessage("Done!");
			sender.player().teleport(sw.getWorld().getChunkAt(0, 0).getBlock(0, 250, 0).getLocation());
			sender.player().setAllowFlight(true);
			sender.player().setFlying(true);
		}

		else
		{
			sender.sendMessage("/skyprime create <gridsize> <spawnsize>");
		}

		return true;
	}
}
