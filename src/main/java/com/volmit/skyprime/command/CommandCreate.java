package com.volmit.skyprime.command;

import org.bukkit.Location;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.lang.collections.Callback;

public class CommandCreate extends PawnCommand
{
	public CommandCreate()
	{
		super("create", "new");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(SkyMaster.hasPersonalIsland(sender.player().getUniqueId()))
		{
			sender.sendMessage("You already have an island! Use /sky");
			return true;
		}

		sender.sendMessage("Creating a new Island for you!");
		SkyMaster.builder().streamProgress(sender.player()).onComplete(new Callback<Location>()
		{
			@Override
			public void run(Location t)
			{
				t.getWorld().setSpawnLocation(t);
				t.getWorld().save();
				sender.player().teleport(t);

				new S(20)
				{
					@Override
					public void run()
					{
						sender.sendMessage("Welcome to your new island!");
					}
				};
			}
		}).create();

		return true;
	}
}
