package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.VirtualIsland;
import com.volmit.volume.bukkit.command.Command;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.bukkit.util.text.C;

public class CommandSkyPrime extends PawnCommand
{
	@Command
	private CommandReload reload;

	@Command
	private CommandSetSpawn setspawn;

	@Command
	private CommandSpawn spawn;

	@Command
	private CommandWarp warp;

	@Command
	private CommandSetWarp setwarp;

	@Command
	private CommandVoltage voltage;

	@Command
	private CommandValue value;

	@Command
	private CommandSave save;

	@Command
	private CommandReboot reboot;

	@Command
	private CommandConfig config;

	@Command
	private CommandDelete delete;

	@Command
	private CommandCreate create;

	@Command
	private CommandRecreate recreate;

	@Command
	private CommandVisit visit;

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
			if(SkyMaster.hasIsland(sender.player()))
			{
				SkyMaster.ensureIslandLoaded(sender.player());
				VirtualIsland is = SkyMaster.getIsland(sender.player());

				if(!is.getWorld().equals(sender.player().getWorld()))
				{
					SkyMaster.getIsland(sender.player()).spawn(sender.player());

					new S(20)
					{
						@Override
						public void run()
						{
							sender.sendMessage("Welcome back to your island!");
						}
					};
				}

				else
				{
					sender.sendMessage("/sky value - Check your island value");
					sender.sendMessage("/sky voltage - Check your island voltage");
					sender.sendMessage("/sky config - Configure your island");
					sender.sendMessage("/sky save - Save your island");
					sender.sendMessage("/sky warp - Teleport to public warp");
					sender.sendMessage("/sky setwarp - Set your island public warp");
					sender.sendMessage("/sky spawn - Teleport to island spawn");
					sender.sendMessage("/sky setspawn - Set your island spawn");
					sender.sendMessage(C.YELLOW + "/sky reboot - Reboot your island");
					sender.sendMessage(C.RED + "/sky delete - Delete your island");
					sender.sendMessage(C.RED + "/sky recreate - Delete and create a new island");
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
