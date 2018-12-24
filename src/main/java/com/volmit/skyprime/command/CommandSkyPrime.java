package com.volmit.skyprime.command;

import java.util.UUID;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.Command;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.task.S;

public class CommandSkyPrime extends PawnCommand
{
	@Command
	private CommandReload reload;

	@Command
	private CommandDelete delete;

	@Command
	private CommandCreate create;

	@Command
	private CommandGen gen;

	public CommandSkyPrime()
	{
		super("skyprime", "sky", "sp", "sprime");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(sender.isPlayer())
		{
			UUID island = sender.player().getUniqueId();

			if(SkyMaster.hasPersonalIsland(island))
			{
				UUID is = SkyMaster.getIsland(island).getId();
				
				if(!SkyMaster.isIslandLoaded(is))
				{
					sender.sendMessage("Loading your island!");
					SkyMaster.loadIsland(is);
				}

				if(!SkyMaster.getWorld(is).equals(sender.player().getWorld()))
				{
					sender.player().teleport(SkyMaster.getWorld(is).getSpawnLocation());

					new S(20)
					{
						@Override
						public void run()
						{
							sender.sendMessage("Welcome back to your new island!");
						}
					};
				}

				else
				{
					// TODO SHOW HELP
				}
			}

			else
			{
				sender.sendMessage("Create your own island with /sky create");
			}
		}

		return true;
	}
}
